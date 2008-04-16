"""
Basic Glacier Model (Hollywood)
version 4.1

author: Archie Paulson
creation date: March 1 2008
last modified: Mon Apr 14 15:36:10 MDT 2008

This is a "Hollywood" model, based on the finite element code (v3.3).

"""

from pylab import *
from lib import derivative

###############################################################################
# constants

yts = 365*24*60*60. # year to seconds conversion
rho = 1e3  # density of ice, kg/m^3
gg = 9.8    # gravitational acceleration, m/s^2

# from wikipedia:
temp_lapse_rate = 6.5  # degrees C per km elevation

# from Kessler et al (eq 8):
AA = 6.8e-24 * yts  # units: 1/(Pa^3.yr)
# from Kessler et al (eq 9):
Uc = 20.      # reference ice velocity, m/yr
tau_c = 1e5   # reference gravitational stress, Pa

###############################################################################

class Mountain:
    """
    Mountain object describes the valley geometry and basic domain of
    computation.
    """

    def __init__(self,mountain_config,domain_config):
        self.config = mountain_config
        self.num_x = domain_config['num_x']
        self.x_max = domain_config['x_max']
        # set x coordinate, meters
        dx = self.x_max/self.num_x
        x = arange(0.0,self.x_max,dx)
        self.x = x
        # set F and W (valley geometry)
        self.F = self.get_valley_elevation(self.x)
        self.W = self.get_valley_width(self.x)
        # for glacier plots:
        self.madeplot = False

    def get_valley_width(self,x):
        """
        returns W (width of valley) in meters
        x is horizontal coordinate; can be scalar or array
        """
        return 1e3 + self.config['headwater_width']*exp( -((x-5e3)/2e3)**2 ) 

    def get_valley_elevation(self,x):
        """
        returns F (height of valley floor) in meters
        x is horizontal coordinate; can be scalar or array
        """
        F = 4e3 - x/30.         # simple linear slope
        # some extra initial steepness:
        F += exp(-(x-self.config['headwall_steepness'])
                   / self.config['headwall_length'])  
        # put a bump near the top:
        if self.config['bump']:
            F += (x-self.config['scoop'])*(0.5-(1./pi)           \
                    * arctan(x/100-self.config['bump_position']))\
                    / self.config['bump_attenuation']
        return F

    def plot(self,fignum=2):
        figure(fignum)
        plot( self.x/1e3, self.F/1e3, label='valley floor' )
        xlabel('x, horizontal distance (km)')
        ylabel('profile (km)')
        self.madeplot = True
        grid(True)

###############################################################################

class Glacier:

    def __init__(self, m, init_ela=4e3 ):
        """
        init args:
            m is a Mountain object, with information on x-positions and
                elevations of each element
        """
        self.m = m
        self.x = m.x
        self.H = zeros(self.x.shape,'d')
        self.plot = self.plot_profile
        # prep
        self.max_F = max(m.F)  # maximum elevation of valley floor
        #self.x_term_alter_x = self.max_F - 0.15*m.config['headwall_length']
        #self.x_term_diff = -self.x_terminus_bulk(self.max_F) /\
        #                   ( self.max_F - self.x_term_alter_x )**2
        self.x_term_alter_x = 4e3
        self.x_term_0 = self.x_terminus_bulk(self.x_term_alter_x)
        self.x_term_1 = self.x_term_0 / ( self.max_F - self.x_term_alter_x )
        self.h_max_0  = self.h_max_bulk(self.x_term_alter_x)
        self.h_max_1  = self.h_max_0 / ( self.max_F - self.x_term_alter_x )
        # set inital climate and find ela:
        self.set_new_climate(init_ela) # no args => don't change temp or precip

    def h_max_bulk(self,ela):
        """
        This is the H_max (max glacier height) as a function of ELA for all
        cases except when the ELA is near the top of the valley (ie, when the
        glacier is almost gone).
        """
        return 400.-(1.04e-2*ela-23)**2   # old v4.0 function

    def x_terminus_bulk(self,ela):
        """
        This is the x-terminus as a function of ELA for all cases except when
        the ELA is near the top of the valley (ie, when the glacier is almost
        gone).
        """
        return 170.5e3-41.8*ela          # old v4.0 function

    def get_geometry(self,ela):
        """
        This calculates x_terminus and H_max, applying a correction if the ELA
        is very high. The correction makes sure both values go to zero right
        when the ELA hits the maximum height of the valley.
        """
        if ela > self.max_F: return 0.,0.
        x_terminus = self.x_terminus_bulk(ela)
        H_max = self.h_max_bulk(ela)
        if ela > self.x_term_alter_x:
            x_terminus = self.x_term_0- (ela-self.x_term_alter_x)*self.x_term_1
            H_max      = self.h_max_0 - (ela-self.x_term_alter_x)*self.h_max_1
        return x_terminus,H_max

    def set_new_climate(self,ela):
        """
        Set new glacier profile according to ELA.
        """
        self.ela = ela
        x_terminus,H_max = self.get_geometry(ela)
        x_peak = 0.5 * x_terminus
        for i,x in enumerate(self.x):
            if x < x_peak: 
                p = max(42-0.01*ela,1.5)
                f = 1.5
                r = f*x_peak
                self.H[i] = sqrt(r**2 - (x-x_peak)**2) * H_max/r
                self.H[i] *= ( x_peak**p - (abs(x-x_peak)**p))/ x_peak**p
            elif x < x_terminus:
                self.H[i] = sqrt(x_peak**2 - (x-x_peak)**2) * H_max/x_peak
            else:
                self.H[i] = 0.0
        self.terminus_index = searchsorted(self.x,x_terminus)
        self.x_terminus = x_terminus
        self.x_peak = x_peak
        self.H_max = H_max

    def set_ice_velocities( self, tau=None ):
        """
        sets (self.) tau, u_slide, u_deform_ave, u_ave
        depends on H and F only
        """
        # set basal shear stress:  tau = rho.g.H. d(H+F)/dx    units of Pascals
        self.tau = rho*gg*self.H* abs(derivative( self.m.x, self.m.F+self.H ))
        tau1 = clip( self.tau, 1., 9e99 )  # for safe division
        factor = where( self.tau==0, 0.0, exp( 1.-tau_c/tau1 ) )
        # sliding velocity:
        self.u_slide = ones( self.H.shape, 'd' ) * Uc * factor
        # variable (verically-averaged) deformation velocity:
        u0 = 0.4*AA * self.H * (self.tau**3)
        self.u_deform_ave = u0
        # total vertically-averaged velocity:
        self.u_ave = self.u_deform_ave + self.u_slide

    # the following methods are only for plotting and data inpection:

    def __repr__(self):
        "return string representation of object"
        s  = "Glacier object:\n"
        s += "length: ".rjust(20) + '%0.2f km'%(self.x_terminus/1e3)+'\n'
        s += "peak height: ".rjust(20) + '%0.2f m'%(self.H_max)+'\n'
        return s

    def plot_profile(self,fignum=2,label=None):
        figure(fignum)
        if self.m.madeplot: 
            yy = (self.m.F+self.H)/1e3
            y_label = 'glacier/valley profile (km)'
            ylim(3.5,4.7)
        else:               
            yy = array(self.H)
            y_label = 'glacier profile (m)'
        plot( self.m.x/1e3, yy, label='ela=%0.2f'%(self.ela/1e3) )
        #xlim(0,1.2*self.m.x[self.terminus_index]/1e3)
        if 0: #self.m.madeplot: 
            ylim(self.m.F[int(self.terminus_index*1.3)]/1e3,4.7)
        ylabel(y_label)
        xlabel('x, horizontal distance (km)')
        legend()
        grid(True)

    def plot_stress(self,fignum=4,label=None):
        figure(fignum)
        plot( self.m.x/1e3, self.tau/1e5, label=label )
        xlim(0,1.2*self.m.x[self.terminus_index]/1e3)
        ylabel('basal shear stress (bars)')
        xlabel('x, horizontal distance (km)')
        if label: legend()
        grid(True)

    def plot_velocities(self,fignum=3,):
        figure(fignum)
        plot( self.m.x/1e3, self.u_slide, label='sliding' )
        plot( self.m.x/1e3, self.u_deform_ave, label='deformation' )
        plot( self.m.x/1e3, self.u_ave, label='total' )
        xlim(0,1.2*self.m.x[self.terminus_index]/1e3)
        ylabel('velocity (m/yr)')
        xlabel('x, horizontal distance (km)')
        legend()
        grid(True)

###############################################################################

class Climate:
    """
    This object will calculate the local mass balance given
        - t0 (temperature), 
        - p0 (snowfall precipitation), and 
        - pmax (snowfall_max)
    """

    def __init__(self, m, config ):
        self.t0 =   config['default_t0']
        self.p0 =   config['default_p0']
        self.pmax = config['default_pmax']
        self.snow_transition_width = config['snow_transition_width']
        self.melt_v_elev = config['melt_v_elev']   # abl rate per meter elev
        self.melt_v_temp = config['melt_v_temp']   # abl curve shift per celcius
        self.z0,self.z1 = config['z0'],config['z1']
        #  m is a mountain-domain object
        self.m = m 
        self.z = m.F # valley floor evelvations
        # 
        self.set_new_climate()

    def set_new_climate(self, t0=None, p0=None, pmax=None ):
        """
        """
        if t0:   self.t0 = t0
        if p0:   self.p0 = p0
        if pmax is not None: self.pmax = pmax
        # for temperature gauge  (temp vs elevation in celcius):
        self.temp_v_z = self.t0 - temp_lapse_rate*self.z/1e3 
        # set mass balance (ela):
        self._set_mass_balance()

    def _set_mass_balance(self):
        """
        sets (self.) accumulation, ablation, massbalance, ela_index, ela
        """
        # find mass balance at all elevations
        self.ablation = self.get_ablation( self.z )
        self.accumulation = self.get_accumulation( self.z )
        self.mass_balance = self.accumulation - self.ablation
        # find ELA (elevation of zero mass balance)
        self.ela_index = argmin( abs(self.mass_balance) )
        self.ela = self.z[self.ela_index]   

    def get_accumulation(self,z):
        """
        z is elevation in meters (can be scalar or array)
        uses current value of self.p0
        """
        tmp_ac = .5+(1./pi)* arctan((z-self.p0)/self.snow_transition_width)
        return self.pmax*tmp_ac

    def get_ablation(self,z):
        """
        z is elevation in meters (can be scalar or array)
        uses current value of self.t0
        """
        ab = self.melt_v_elev*\
             (1.-sin((z-self.z0-(self.t0-20)*self.melt_v_temp)/
                 ((self.z1-self.z0)*2/pi)) )
        # elev of min ablation
        min_ablation_elev= (self.t0-20)*self.melt_v_temp + self.z1 
        if iterable(z):
            min_abl_index = argmin(abs( min_ablation_elev - self.z ))
            ab[:min_abl_index] *= 0.
        elif z>min_ablation_elev:
            ab = 0.
        offset = arctan((self.t0-20)/2.5)/3.+0.5
        return ab + offset

    def __repr__(self):
        "return string representation of object"
        s  = "ela: ".rjust(20) + '%0.3f km\n'%(self.ela/1e3)
        s += "t0: ".rjust(20) + '%0.1f C\n'%self.t0 
        s += "p0: ".rjust(20) + '%0.1f m\n'%self.p0 
        s += "pmax: ".rjust(20) +'%0.1f m/yr\n'%self.pmax
        return s

    def plot(self,fignum=7):
        figure(fignum)
        yy = self.z/1e3
        plot( self.accumulation, yy, label='accumulation' )
        plot( -self.ablation, yy, label='- ablation' )
        plot( self.mass_balance, yy, label='mass balance' )
        ylabel('elevation (km)')
        xlabel('mass balance (m/yr)')
        text( average(xlim()), self.ela/1e3, '-- ELA %0.1fkm --'%(self.ela/1e3),
              verticalalignment='center',backgroundcolor='white')
        grid(True)
        xlim(ymax=1.5*self.pmax)
        legend(loc='upper left')

###############################################################################
# test code:

climate_config = {
                    'default_t0':20.,      # modern temp at sea level (celcius)
                    'default_p0':4e3,      # elevation of half-max snowfall (m)
                    'default_pmax':2.0,    # max precip (m/yr)
                    'snow_transition_width':300., # (m)
                    'melt_v_elev':30.,
                    'melt_v_temp':200.,
                    'z0':1300., 
                    'z1':4200.,
                  }
mountain_config = {
                    'headwater_width':0.0, 
                    'headwall_length':800., 
                    'headwall_steepness':5e3,
                    'bump':False, 
                    'bump_attenuation':30., 
                    'scoop':500., 
                    'bump_position':30.,
                   }
domain_config =    {
                    'num_x':1000,   # num ice columns in whole domain
                    'x_max':80e3,        # edge of simulation (m)
                   }

if 1:    # if 1:
    m = Mountain( mountain_config, domain_config )
    c = Climate( m, climate_config )
    g = Glacier( m, c.ela )


# save a few mass balance curves
if 0:
    climates = [
                {'t0':19.,  'p0':4e3,   'pmax':2.0 },
                {'t0':18.5, 'p0':3.5e3, 'pmax':2.0 },
                {'t0':18.,  'p0':3e3,   'pmax':2.0 },
                {'t0':15.,  'p0':2e3,   'pmax':2.0 },
               ]
    for i,climate in enumerate(climates):
        c.set_new_climate( t0=climate['t0'], p0=climate['p0'],
                           pmax=climate['pmax'] )
        #c.plot(fignum=i)
        g.set_new_climate( c.ela )
        g.plot()
        title('t0=%0.1f  p0=%0.1f  pmax=%0.1f'%(
             climate['t0'],climate['p0'], climate['pmax']))
        #savefig('glacial_budget_%d.png'%i)


# make mountain plot of aspect ratio 1.0
if 0:  # if 1:
    fignum = 34
    fig = figure(fignum)
    ax = fig.add_subplot(111, aspect='equal')
    if 1:
        g.m.plot(fignum)
        grid(False)
        xlim(0,5)
        ylim(1,6)
        savefig('mountain0_aspect1.svg')
    else:
        plot( g.x[0:875]/1e3, g.m.F[0:875]/1e3 )  # 70km
        grid(False)
        xlim(0,5)
        ylim(1,6)
        savefig('mountain1_aspect1.svg')


# look at x_terminus and H_max as a function of ELA 
if 0:   # if 1:
    # want x_terminus -> 0 right when ELA -> F(0); same for H_max
    ela_ = array(m.F)   # ela proxy
    figure(14)
    dat = array([ g.get_geometry(ee) for ee in ela_ ]) # term,hmax
    if 0:  # xterm
        old_term = [ g.x_terminus_bulk(ee) for ee in ela_ ]
        plot( ela_, dat[:,0], label='model terminus' )
        plot( ela_, old_term, label='old model terminus' )
        ylabel('x_terminus')
    else:  # hmax
        old_hmax = [ g.h_max_bulk(ee) for ee in ela_ ]
        plot( ela_, dat[:,1], label='model H_max' )
        plot( ela_, old_hmax, label='old H_max' )
        ylabel('H_max')
    xlabel('ELA')
    grid()
    legend()

