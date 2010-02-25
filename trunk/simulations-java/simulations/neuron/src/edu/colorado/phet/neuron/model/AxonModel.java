/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Random;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.neuron.NeuronConstants;

/**
 * This class represents the main class for modeling the axon.  It acts as the
 * central location where the interaction between the membrane, the particles
 * (i.e. ions), and the gates is all governed.
 *
 * @author John Blanco
 */
public class AxonModel implements IParticleCapture {
    
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	private static final Random RAND = new Random();
	
	private static final double MAX_PARTICLE_VELOCITY = 500; // In nano meters per second.
	
	// The following constant defines how frequently particle motion is updated.
	// A value of 1 means every clock tick for every particle, 2 means every other
	// particle on each tick, etc.
	private static final int PARTICLE_UPDATE_INCREMENT = 4;
	
	// The following constants define the boundaries for the motion of the
	// particles.  These boundaries are intended to be outside the view port,
	// so that it is not apparent to the user that they exist.  We may at some
	// point want to make these bounds dynamic and set by the view so that the
	// user never encounters a situation where these can be seen.
	private static final double MODEL_HEIGHT = 130; // In nanometers.
	private static final double MODEL_WIDTH = 180; // In nanometers.
	private static final Rectangle2D PARTICLE_BOUNDS = new Rectangle2D.Double(-MODEL_WIDTH / 2, -MODEL_HEIGHT / 2,
			MODEL_WIDTH, MODEL_HEIGHT);
	
	// Center of the model.
	private static final Point2D CENTER_POS = new Point2D.Double(0, 0);
	
	// Constant that controls how often we calculate the membrane potential
	// for internal use, i.e. particle motion calculations.
	private static final int MEMBRANE_POTENTIAL_UPDATE_COUNT = 10;
	
	// Countdown used for preventing the axon from receiving stimuli that
	// are too close together.
	private static final double STIM_LOCKOUT_TIME = 0.01;  // Milliseconds of sim time.
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private final ConstantDtClock clock;
    private final AxonMembrane axonMembrane = new AxonMembrane();
    private ArrayList<Particle> particles = new ArrayList<Particle>();
    private ArrayList<MembraneChannel> channels = new ArrayList<MembraneChannel>();
    private final double crossSectionInnerRadius;
    private final double crossSectionOuterRadius;
    private int particleUpdateOffset = 0;
    private EventListenerList listeners = new EventListenerList();
    private ConcentrationTracker concentrationTracker = new ConcentrationTracker();
    private int membranePotentialUpdateCounter = 0;
    private int membranePotentialSnapshot;
    private HodgkinHuxleyModel hodgkinHuxleyModel = new HodgkinHuxleyModel();
    private boolean potentialChartVisible;
    private double stimLockoutCountdownTime;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AxonModel( NeuronClock clock ) {
    	
        this.clock = clock;
        
        crossSectionInnerRadius = (axonMembrane.getCrossSectionDiameter() - axonMembrane.getMembraneThickness()) / 2; 
        crossSectionOuterRadius = (axonMembrane.getCrossSectionDiameter() + axonMembrane.getMembraneThickness()) / 2;
        
        clock.addClockListener(new ClockAdapter(){
			@Override
			public void clockTicked(ClockEvent clockEvent) {
				stepInTime( clockEvent.getSimulationTimeChange() );
			}
        });
        
        // Listen to the membrane for events that indicate that a traveling
        // action potential has arrived at the location of the transverse
        // cross section.
        axonMembrane.addListener(new AxonMembrane.Adapter() {
			public void travelingActionPotentialEnded() {
				// The action potential has arrived, so stimulate the model
				// the simulates the action potential voltages and current
				// flows.
				hodgkinHuxleyModel.stimulate();
			}
		});
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public ConstantDtClock getClock() {
        return clock;
    }    
    
    public ArrayList<Particle> getParticles(){
    	return particles;
    }
    
    public AxonMembrane getAxonMembrane(){
    	return axonMembrane;
    }
    
    public Shape getBodyShape(){
    	return new GeneralPath();
    }
    
    public ArrayList<MembraneChannel> getMembraneChannels(){
    	return new ArrayList<MembraneChannel>(channels);
    }
    
    public int getNumMembraneChannels(MembraneChannelTypes channelType){
    	
    	int numChannels = 0;
    	
    	for (MembraneChannel channel : channels){
    		if (channel.getChannelType() == channelType){
    			numChannels++;
    		}
    	}
    	
    	return numChannels;
    }
    
    public int getNumMembraneChannels(){
    	return channels.size();
    }
    
    /**
     * Get the membrane potential in volts.
     * 
     * @return
     */
    public double getMembranePotential(){
    	return hodgkinHuxleyModel.getMembraneVoltage();
    }
    
    /**
     * Get a reference to the first Hodgkins-Huxley model.  This is used
     * primarily for debugging purposes.
     */
    public HodgkinHuxleyModel getHodgkinHuxleyModel(){
    	return hodgkinHuxleyModel;
    }

    public void setNumMembraneChannels(MembraneChannelTypes channelType, int desiredNumChannesl){
    	if (desiredNumChannesl > NeuronConstants.MAX_CHANNELS_PER_TYPE){
    		System.err.println(getClass().getName() + "- Warning: Attempt to set too many channels.");
    		assert false;
    		return;
    	}
    	
    	if (desiredNumChannesl < getNumMembraneChannels(channelType)){
    		// Need to remove one or more channels.
    		while (desiredNumChannesl < getNumMembraneChannels(channelType)){
    			removeChannel(channelType);
    		}
    	}
    	else if (desiredNumChannesl > getNumMembraneChannels(channelType)){
    		// Need to add one or more channels.
    		while (desiredNumChannesl > getNumMembraneChannels(channelType)){
    			addChannel(channelType);
    		}
    	}
    	else{
    		// Don't need to do nuthin'.
    	}
    }
    
    public boolean isPotentialChartVisible(){
    	return potentialChartVisible;
    }
    
    public void setPotentialChartVisible(boolean isVisible){
    	if (potentialChartVisible != isVisible){
    		potentialChartVisible = isVisible;
    		notifyPotentialChartVisibilityChanged();
    	}
    }
    
    //----------------------------------------------------------------------------
    // Other Methods
    //----------------------------------------------------------------------------
    
    public void reset(){
    	
    	// Reset the HH model.
    	hodgkinHuxleyModel.reset();
    	
    	// Reset the stimulation lockout time.
    	stimLockoutCountdownTime = 0;
    	
    	// Remove all particles.  This is done by telling the particle to send
    	// out notifications of its removal from the model.  All listeners,
    	// including this class, should remove their references in response.
    	ArrayList<Particle> particlesCopy = new ArrayList<Particle>(particles);
    	for (Particle particle : particlesCopy){
    		particle.notifyRemoved();
    	}
    	
    	// Remove all channels.
    	ArrayList<MembraneChannel> tempChannelList = new ArrayList<MembraneChannel>(channels);
    	for ( MembraneChannel channel : tempChannelList){
    		removeChannel(channel.getChannelType());
    	}
    	
    	// Add the initial particles.
    	addParticles(ParticleType.SODIUM_ION, ParticlePosition.INSIDE_MEMBRANE, 10);
    	addParticles(ParticleType.SODIUM_ION, ParticlePosition.OUTSIDE_MEMBRANE, 100);
    	addParticles(ParticleType.POTASSIUM_ION, ParticlePosition.INSIDE_MEMBRANE, 100);
    	addParticles(ParticleType.POTASSIUM_ION, ParticlePosition.OUTSIDE_MEMBRANE, 5);

    	// Add the initial channels.
    	for (int i = 0; i < 4; i++){
    		addChannel(MembraneChannelTypes.SODIUM_GATED_CHANNEL);
    	}
    	for (int i = 0; i < 4; i++){
    		addChannel(MembraneChannelTypes.POTASSIUM_GATED_CHANNEL);
    	}
    	for (int i = 0; i < 4; i++){
    		addChannel(MembraneChannelTypes.POTASSIUM_LEAKAGE_CHANNEL);
    	}
    	for (int i = 0; i < 2; i++){
    		addChannel(MembraneChannelTypes.SODIUM_LEAKAGE_CHANNEL);
    	}
    	for (int i = 0; i < 4; i++){
    		addChannel(MembraneChannelTypes.SODIUM_GATED_CHANNEL);
    	}
    	for (int i = 0; i < 4; i++){
    		addChannel(MembraneChannelTypes.POTASSIUM_GATED_CHANNEL);
    	}
    	for (int i = 0; i < 2; i++){
    		addChannel(MembraneChannelTypes.SODIUM_GATED_CHANNEL);
    	}
    }
    
    /**
     * Get the potential between the outside and the inside of the membrane in
     * terms of quantized charge.
     * 
     * @return
     */
    public int getQuantizedMembranePotential(){
    	
    	int quantizedInsideCharge = 0;
    	int quantizedOutsideCharge = 0;
    	
    	for (Particle particle : particles){
    		if (isParticleInside(particle)){
    			quantizedInsideCharge += particle.getCharge();
    		}
    		else{
    			quantizedOutsideCharge += particle.getCharge();
    		}
    	}
    	
    	// Add in the charges from any particles that are in channels.  Note
    	// that particles that are in channels are assumed to be inside the
    	// membrane.
    	for (MembraneChannel channel : channels){
    		ArrayList<Particle> particlesInChannel = channel.getOwnedAtomsRef();
        	for (Particle particle : particlesInChannel){
       			quantizedInsideCharge += particle.getCharge();
        	}
    	}
    	
    	return quantizedInsideCharge - quantizedOutsideCharge;
    }
    
    /**
     * Add the specified particles to the model.
     * 
     * @param particleType
     * @param position
     */
    public void addParticles(ParticleType particleType, ParticlePosition position, int numberToAdd){
    	Particle newParticle = null;
    	for (int i = 0; i < numberToAdd; i++){
    		newParticle = createParticle(particleType);
    		
    		if (position == ParticlePosition.INSIDE_MEMBRANE){
    			positionParticleInsideMembrane(newParticle);
    		}
    		else{
    			positionParticleOutsideMembrane(newParticle);
    		}
        	concentrationTracker.updateParticleCount(newParticle.getType(), position, 1);
    	}
    }

    /**
     * Get the proportion of particles of a given type that are inside the
     * axon membrane.  A value of 1 indicates that all particles are inside, 0
     * means that none are inside.
     */
    public double getProportionOfParticlesInside(ParticleType particleType){
    	return concentrationTracker.getProportion(particleType, ParticlePosition.INSIDE_MEMBRANE);
    }
    
    /**
     * 
     * @return
     */
    public Rectangle2D getParticleMotionBounds(){
    	return PARTICLE_BOUNDS;
    }
    
    /**
     * Set the proportion of particles inside the axon membrane.  A value of 0
     * indicates that all particles of this type should be outside, a value of 1
     * indicates that the should all be inside, and value between...well, you
     * get the idea.
     */
    public void setConcentration(ParticleType particleType, double targetProportion){
    	
    	if (targetProportion > 1 || targetProportion < 0){
    		System.err.println(getClass().getName() + " - Error: Invalid target proportion value = " + targetProportion);
    		assert false;
    		return; 
    	}

    	int targetNumInside = (int)Math.round(targetProportion * 
    			(double)(concentrationTracker.getTotalNumParticles(particleType)));
    	
    	if (targetNumInside > concentrationTracker.getNumParticlesInPosition(particleType, 
    			ParticlePosition.INSIDE_MEMBRANE)){
    		// Move some particles from outside to inside.
    		for (Particle particle : particles){
    			if (particle.getType() == particleType && !isParticleInside(particle)){
    				// Move this guy in.
    				positionParticleInsideMembrane(particle);
    				concentrationTracker.updateParticleCount(particleType, ParticlePosition.INSIDE_MEMBRANE, 1);
    				concentrationTracker.updateParticleCount(particleType, ParticlePosition.OUTSIDE_MEMBRANE, -1);
    				if (concentrationTracker.getNumParticlesInPosition(particleType, ParticlePosition.INSIDE_MEMBRANE) == targetNumInside){
    					break;
    				}
    			}
    		}
    		notifyConcentrationGradientChanged(particleType);
    	}
    	else if (targetNumInside < concentrationTracker.getNumParticlesInPosition(particleType, ParticlePosition.INSIDE_MEMBRANE)){
    		// Move some particles from inside to outside.
    		for (Particle particle : particles){
    			if (particle.getType() == particleType && isParticleInside(particle)){
    				// Move this guy out.
    				positionParticleOutsideMembrane(particle);
    				concentrationTracker.updateParticleCount(particleType, ParticlePosition.INSIDE_MEMBRANE, -1);
    				concentrationTracker.updateParticleCount(particleType, ParticlePosition.OUTSIDE_MEMBRANE, 1);
    				if (concentrationTracker.getNumParticlesInPosition(particleType, ParticlePosition.INSIDE_MEMBRANE) == targetNumInside){
    					break;
    				}
    			}
    		}
    		notifyConcentrationGradientChanged(particleType);
    	}
    }
    
    public void initiateStimulusPulse(){
    	if (!stimulusInitiationLockedOut()){
    		axonMembrane.initiateTravelingActionPotential();
    		notifyStimulusPulseInitiated();
    		stimLockoutCountdownTime = STIM_LOCKOUT_TIME;
    	}
    }
    
    /**
     * Get a boolean value that indicates whether the initiation of a new
     * stimulus (i.e. action potential) is currently locked out.  This is done
     * to prevent the situation where multiple action potentials are moving
     * down the membrane at the same time.
     * 
     * @return
     */
    private boolean stimulusInitiationLockedOut(){
    	// This works be requiring a certain minimum time and then by
    	// looking at the constants that control the conductance values in the
    	// HH model to tell when another action potential can happen.  The
    	// values used for comparison were empirically determined.
    	return (stimLockoutCountdownTime > 0 || hodgkinHuxleyModel.get_n4() > 0.02 || hodgkinHuxleyModel.get_m3h() > 0.01);
    }
    
    public Shape getTravelingActionPotentialShape(){
    	return new Rectangle2D.Double(0, 0, 10, 10);
    }
    
    /**
     * Create a particle of the specified type in the specified capture zone.
     * In general, this method will be used when a particle is or may soon be
     * needed to travel through a membrane channel.
     *  
     * @param particleType
     * @param captureZone
     * @return
     */
    public Particle createParticle(ParticleType particleType, CaptureZone captureZone){
    	
    	final Particle newParticle = Particle.createParticle(particleType);
    	particles.add(newParticle);
    	if (captureZone != null){
    		Point2D location = captureZone.getSuggestedNewParticleLocation();
    		newParticle.setPosition(location);
    	}
    	
    	// Listen to the particle for notification of its removal.
    	newParticle.addListener(new Particle.Adapter(){
    		public void removedFromModel() {
    			particles.remove(newParticle);
    		}
    	});
    	
    	// Send notification that this particle has come into existence.
    	notifyParticleAdded(newParticle);
    	
		return newParticle;
    }
    
    /**
     * Create a particle of the specified type and add it to the model.
     *  
     * @param particleType
     * @return
     */
    public Particle createParticle(ParticleType particleType){
    	
    	return createParticle(particleType, null);
    }
    
    /**
     * Starts a particle of the specified type moving through the
     * specified channel.  If one or more particles of the needed type exist
     * within the capture zone for this channel, one will be chosen and set to
     * move through, and another will be created to essentially take its place
     * (though the newly created one will probably be in a slightly different
     * place for better visual effect).  If none of the needed particles
     * exist, two will be created, and one will move through the channel and
     * the other will just hang out in the zone.
     * 
     * Note that it is not guaranteed that the particle will make it through
     * the channel, since it is possible that the channel could close before
     * the particle goes through it.
     * 
     * @param particleType
     * @param channel
     * @return
     */
    public void requestParticleThroughChannel(ParticleType particleType, MembraneChannel channel, double maxVelocity){

    	/*
    	 * TODO: For an experiment, I am trying to have all requested particles
    	 * be generated and never found, so that existing particles are left
    	 * alone.  
    	// Scan the capture zone for particles of the desired type.
    	CaptureZoneScanResult czsr = scanCaptureZoneForFreeParticles(channel.getCaptureZone(), particleType);
    	Particle particleToCapture = czsr.getClosestFreeParticle();
    	
    	if (czsr.getNumParticlesInZone() == 0){
    		// No particles available in the zone, so create a new one.
    		Particle newParticle = createParticle(particleType, channel.getCaptureZone());
    		newParticle.setFadeStrategy(new TimedFadeInStrategy(0.0005));
   			particleToCapture = newParticle;
    	}
    	*/
    	Particle particleToCapture;
		Particle newParticle = createParticle(particleType, channel.getCaptureZone());
		newParticle.setFadeStrategy(new TimedFadeInStrategy(0.0005));
			particleToCapture = newParticle;
    	particleToCapture.setMotionStrategy(
    			new MembraneChannelTraversalMotionStrategy(channel, particleToCapture.getPosition(), maxVelocity));
    }
    
    private void stepInTime(double dt){
    
    	// Update the value of the membrane potential by stepping the
    	// Hodgkins-Huxley model.
    	hodgkinHuxleyModel.stepInTime( dt );
    	
    	// Step the membrane in time.
    	axonMembrane.stepInTime( dt );
    	
    	// If it is time, update the value of the membrane potential that is
    	// used for internal calculations.
    	if (membranePotentialUpdateCounter++ >= MEMBRANE_POTENTIAL_UPDATE_COUNT){
    		membranePotentialSnapshot = getQuantizedMembranePotential();
    		membranePotentialUpdateCounter = 0;
    	}
    	
    	// Update the stimulus lockout timer.
    	if (stimLockoutCountdownTime >= 0){
    		stimLockoutCountdownTime -= dt;
    	}
    	
    	/*
    	 * TODO: Feb 12 2010 - The paradigm for moving particles around is changing from having
    	 * them controlled by the AxonModel and the channels to having a motion strategy set on
    	 * them and have them move themselves.  This code is being removed as part of that
    	 * effort, and should be deleted or reinstated at some point in time.

    	
    	// Update the velocity of the particles.  For efficiency and because
    	// it looks better, not all particles are updated every time.
    	for (int i = particleUpdateOffset; i < particles.size(); i += PARTICLE_UPDATE_INCREMENT){
    		updateParticleVelocity(particles.get(i));
    	}
    	particleUpdateOffset = (particleUpdateOffset + 1) % PARTICLE_UPDATE_INCREMENT;
    	
    	// Update the particle positions.
    	for (Particle particle : particles){
    		particle.stepInTime(clockEvent.getSimulationTimeChange());
    	}

    	 */
    	
    	// Step the channels.
    	for (MembraneChannel channel : channels){
    		channel.stepInTime( dt );
    	}
    	
    	// Step the particles.  Since particles may remove themselves as a
    	// result of being stepped, we need to copy the list in order to avoid
    	// concurrent modification exceptions.
    	ArrayList<Particle> particlesCopy = new ArrayList<Particle>(particles);
    	for (Particle particle : particlesCopy){
    		particle.stepInTime( dt );
    	}
    }
    
    /**
     * Scan the supplied capture zone for particles of the specified type.
     * 
     * @param zone
     * @param particleType
     * @return
     */
    private CaptureZoneScanResult scanCaptureZoneForFreeParticles(CaptureZone zone, ParticleType particleType){
    	Particle closestFreeParticle = null;
    	double distanceOfClosestParticle = Double.POSITIVE_INFINITY;
    	int totalNumberOfParticles = 0;
    	Point2D captureZoneOrigin = zone.getOriginPoint();
    	
    	for (Particle particle : particles){
    		if ((particle.getType() == particleType) && (particle.isAvailableForCapture()) && (zone.isPointInZone(particle.getPosition()))) {
    			totalNumberOfParticles++;
    			if (closestFreeParticle == null){
    				closestFreeParticle = particle;
    				distanceOfClosestParticle = captureZoneOrigin.distance(closestFreeParticle.getPosition());
    			}
    			else if (captureZoneOrigin.distance(closestFreeParticle.getPosition()) < distanceOfClosestParticle){
    				closestFreeParticle = particle;
    				distanceOfClosestParticle = captureZoneOrigin.distance(closestFreeParticle.getPosition());
    			}
    		}
    	}
    	
    	return new CaptureZoneScanResult(closestFreeParticle, totalNumberOfParticles);
    }
    
	public void addListener(Listener listener){
		listeners.add(Listener.class, listener);
	}
	
	public void removeListener(Listener listener){
		listeners.remove(Listener.class, listener);
	}
	
	private void notifyChannelAdded(MembraneChannel channel){
		for (Listener listener : listeners.getListeners(Listener.class)){
			listener.channelAdded(channel);
		}
	}
	
	private void notifyChannelRemoved(MembraneChannel channel){
		for (Listener listener : listeners.getListeners(Listener.class)){
			listener.channelRemoved(channel);
		}
	}
	
	private void notifyParticleAdded(Particle particle){
		for (Listener listener : listeners.getListeners(Listener.class)){
			listener.particleAdded(particle);
		}
	}
	
	private void notifyConcentrationGradientChanged(ParticleType particleType){
		for (Listener listener : listeners.getListeners(Listener.class)){
			listener.concentrationRatioChanged(particleType);
		}
	}
	
	private void notifyStimulusPulseInitiated(){
		for (Listener listener : listeners.getListeners(Listener.class)){
			listener.stimulusPulseInitiated();
		}
	}
	
	private void notifyPotentialChartVisibilityChanged(){
		for (Listener listener : listeners.getListeners(Listener.class)){
			listener.potentialChartVisibilityChanged();
		}
	}
	
	/*
	 * TODO: Feb 12 2010 - The paradigm for moving particles around is changing from having
	 * them controlled by the AxonModel and the channels to having a motion strategy set on
	 * them and have them move themselves.  This routine is being removed as part of that
	 * effort, and should be deleted or reinstated at some point in time.

    private void updateParticleVelocity(Particle particle){
    	
    	// Convert the position to polar coordinates.
    	double r = Math.sqrt(particle.getX() * particle.getX() + particle.getY() * particle.getY());
    	double theta = Math.atan2(particle.getY(), particle.getX());
    	
    	// Determine the current angle of travel.
    	double previousAngleOfTravel = Math.atan2( particle.getPositionReference().y, particle.getPositionReference().x );
    	
    	double angle = 0;
    	double velocity;

    	// Generate the new angle of travel for the particle.
    	if (r < axonMembrane.getCrossSectionDiameter() / 2){
    		// Particle is inside the membrane.
    		if (crossSectionInnerRadius - r <= particle.getDiameter()){
    			// This particle is near the membrane wall, so should be repelled.
    	    	angle = theta + Math.PI + ((RAND.nextDouble() - 0.5) * Math.PI / 2);
    		}
    		else{
    			// Particle should just do a random walk.
				angle = Math.PI * 2 * RAND.nextDouble();
    		}
    	}
    	else{
    		// Particle is outside the membrane.
    		if (r - crossSectionOuterRadius <= particle.getDiameter()){
    			// This particle is near the membrane wall, so should be repelled.
				angle = Math.PI * RAND.nextDouble() - Math.PI / 2 + theta;
    		}
    		else if (!PARTICLE_BOUNDS.contains(particle.getPositionReference())){
    			// Particle is moving out of bounds, so move it back towards
    			// the center.
    	    	angle = theta + Math.PI + ((RAND.nextDouble() - 0.5) * Math.PI / 2);
    	    	angle = theta + Math.PI;
    		}
    		else{
    			// The particle should do a random walk with some tendency to
    			// move toward or away from the membrane based on its current
    			// potential.
    			// NOTE: This algorithm was empirically determined, so tweak
    			// as needed.
    			double gradientThreshold;
    			if (particle.getCharge() == 0 || membranePotentialSnapshot == 0){
    				// No gradient should exist, so just do a random walk.
    				angle = Math.PI * 2 * RAND.nextDouble();
    			}
    			else{
    				gradientThreshold = 1 - Math.min((double)Math.abs(membranePotentialSnapshot) / 500, 1);
    				double offsetAngle;
    				if (Math.signum(particle.getCharge()) == Math.signum(membranePotentialSnapshot)){
    					// Signs match, which should cause repulsion.
    					offsetAngle = 0;
    				}
    				else{
    					// Attraction, so offset angle is 0.
    					offsetAngle = Math.PI;
    				}
    				if (RAND.nextDouble() > gradientThreshold){
    					// Move in the gradient direction.
    	    	    	angle = theta + ((RAND.nextDouble() - 0.5) * Math.PI / 2) + offsetAngle;
    				}
    				else{
    					// Just do a random walk.
    					angle = Math.PI * 2 * RAND.nextDouble();
    				}
    			}
    		}
    	}
    	
    	// Generate the new overall velocity.
		velocity = MAX_PARTICLE_VELOCITY * RAND.nextDouble();
		
		// Set the particle's new velocity. 
    	particle.setVelocity(velocity * Math.cos(angle), velocity * Math.sin(angle));
    }
    */
    
    private void addChannel(MembraneChannelTypes channelType){
    	MembraneChannel membraneChannel = null;
    	
    	if (getNumMembraneChannels(channelType) > NeuronConstants.MAX_CHANNELS_PER_TYPE){
    		System.err.println(getClass().getName() + " - Warning: Ignoring attempt to add more than max allowed channels.");
    		assert false;
    		return;
    	}
    	
    	switch (channelType){
    	case SODIUM_LEAKAGE_CHANNEL:
    		membraneChannel = new SodiumLeakageChannel(this);
    		break;
    		
    	case SODIUM_GATED_CHANNEL:
    		membraneChannel = new SodiumGatedChannel(hodgkinHuxleyModel, this);
    		break;
    		
    	case POTASSIUM_LEAKAGE_CHANNEL:
    		membraneChannel = new PotassiumLeakageChannel(this);
    		break;
    		
		case POTASSIUM_GATED_CHANNEL:
			membraneChannel = new PotassiumGatedChannel(hodgkinHuxleyModel, this);
			break;
    	}
    	
    	// Find a position for the new channel.
    	
    	/*
    	 * TODO: The following code distributes the channels fairly evenly around the
    	 * membrane based on channel type.  This was commented out on Feb 2 2010
    	 * because the specification changed to having the channels be grouped.
    	 * Remove this or reinstate it once the desired behavior is worked out.

    	double angleOffset = channelType == MembraneChannelTypes.SODIUM_LEAKAGE_CHANNEL ? 0 : 
    		Math.PI / NeuronConstants.MAX_CHANNELS_PER_TYPE;
    	
    	int numChannelsOfThisType = getNumMembraneChannels(channelType);
    	double angle = (double)(numChannelsOfThisType % 2) * Math.PI + 
    		(double)(numChannelsOfThisType / 2) * 2 * Math.PI / NeuronConstants.MAX_CHANNELS_PER_TYPE + angleOffset;
    	double radius = axonMembrane.getCrossSectionDiameter() / 2;
		Point2D newLocation = new Point2D.Double(radius * Math.cos(angle), radius * Math.sin(angle));
		
		*/
    	
    	double angle = Math.PI * 0.3 + getNumMembraneChannels() * Math.PI / 2 + (getNumMembraneChannels() / 4) * Math.PI * 0.075;
    	double radius = axonMembrane.getCrossSectionDiameter() / 2;
		Point2D newLocation = new Point2D.Double(radius * Math.cos(angle), radius * Math.sin(angle));
    	
    	// Position the channel on the membrane.
    	membraneChannel.setRotationalAngle(angle);
    	membraneChannel.setCenterLocation(newLocation);

    	// Add the channel and let everyone know it exists.
    	channels.add(membraneChannel);
    	notifyChannelAdded(membraneChannel);
    }
    
    private void removeChannel(MembraneChannelTypes channelType){
    	// Make sure there is at least one to remove.
    	if (getNumMembraneChannels(channelType) < 1 ){
    		System.err.println(getClass().getName() + ": Error - No channel of this type to remove, type = " + channelType);
    		return;
    	}
    	
    	MembraneChannel channelToRemove = null;
    	// Work backwards through the array so that the most recently added
    	// channel is removed first.  This just looks better visually and
    	// makes the positioning of channels work better.
    	for (int i = channels.size() - 1; i >= 0; i--){
    		if (channels.get(i).getChannelType() == channelType){
    			channelToRemove = channels.get(i);
    			break;
    		}
    	}
    	
    	if (channelToRemove != null){
    		ArrayList<Particle> releasedParticle = channelToRemove.forceReleaseAllParticles(particles);
    		// Since particles in a channel are considered to be inside the
    		// membrane, force any particle that was in the channel when it was
    		// removed to go safely into the interior.
    		if (releasedParticle != null){
    			for (Particle particle : releasedParticle){
    				particles.add(particle);
    				positionParticleInsideMembrane(particle);
    			}
    		}
    		// Remove the channel and send any notifications.
    		channels.remove(channelToRemove);
    		channelToRemove.remove();
    		notifyChannelRemoved(channelToRemove);
    	}
    }
    
    /**
     * Place a particle at a random location inside the axon membrane.
     */
    private void positionParticleInsideMembrane(Particle particle){
    	// Choose any angle.
    	double angle = RAND.nextDouble() * Math.PI * 2;
    	
    	// Choose a distance which is close to but inside the membrane.
    	double distance = crossSectionInnerRadius - particle.getDiameter() * 2 - 
    		RAND.nextDouble() * particle.getDiameter() * 2;
    	
    	/*
    	 * TODO: The code below was used prior to 10/9/2009, which is when it
    	 * was decided that the atoms should be evenly distributed within the
    	 * membrane, not tending towards the outside.  Keep it until we're
    	 * sure we don't need it, then blow it away.
    	double multiplier = 0;
    	if (RAND.nextDouble() < 0.8){
    		multiplier = 1 - (RAND.nextDouble() * 0.25);
    	}
    	else{
    		multiplier = RAND.nextDouble();
    	}
    	double distance = multiplier * (crossSectionInnerRadius - atom.getDiameter());
    	*/
    	particle.setPosition(distance * Math.cos(angle), distance * Math.sin(angle));
    }

    /**
     * Place a particle at a random location outside the axon membrane.
     */
    private void positionParticleOutsideMembrane(Particle particle){
    	// Choose any angle.
    	double angle = RAND.nextDouble() * Math.PI * 2;
    	
    	// Choose a distance which is close to but outside the membrane.
    	double distance = crossSectionOuterRadius + particle.getDiameter() * 2 + 
    		RAND.nextDouble() * particle.getDiameter() * 2;
    	
    	particle.setPosition(distance * Math.cos(angle), distance * Math.sin(angle));
    }
    
    /**
     * Determine whether the given particle is considered to be inside or outside
     * of the axon.  IMPORTANT NOTE - If an particle is in a channel, it is
     * considered to be inside the membrane.
     * 
     * @param particle
     * @return
     */
    private boolean isParticleInside(Particle particle){

    	boolean inside = false;
    	
		for (MembraneChannel channel : channels){
			if (channel.getOwnedParticles().contains(particle)){
				inside = true;
				break;
			}
		}
		
		if (!inside){
			inside = particle.getPositionReference().distance(CENTER_POS) < crossSectionOuterRadius;
		}
    	
    	return inside;
    }
    
    //----------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //----------------------------------------------------------------------------
    
    /**
     * This is a "convenience class" that is used to track the relative
     * concentration of the different particle types.  This was created so that
     * the concentration doesn't need to be completely recalculated at every
     * time step, which would be computationally expensive.
     */
    public static class ConcentrationTracker {

    	HashMap<ParticleType, Integer> mapParticleTypeToNumOutside = new HashMap<ParticleType, Integer>();
    	HashMap<ParticleType, Integer> mapParticleTypeToNumInside = new HashMap<ParticleType, Integer>();
    	
    	public void updateParticleCount(ParticleType particleType, ParticlePosition position, int delta){
    		HashMap<ParticleType, Integer> map = position == ParticlePosition.INSIDE_MEMBRANE ? mapParticleTypeToNumInside :
    			mapParticleTypeToNumOutside;
    		Integer currentCount = map.get(particleType);
    		if (currentCount == null){
    			currentCount = new Integer(0);
    		}
    		Integer newCount = new Integer(currentCount.intValue() + delta);
    		if (newCount.intValue() < 0){
    			System.err.println(getClass().getName()+ "- Error: Negative count for particles in a position.");
    			assert false;
    			newCount = new Integer(0);
    		}
    		map.put(particleType, newCount);
    	}
    	
    	public void resetParticleCount(ParticleType particleType, ParticlePosition position){
    		HashMap<ParticleType, Integer> map = position == ParticlePosition.INSIDE_MEMBRANE ? mapParticleTypeToNumInside :
    			mapParticleTypeToNumOutside;
    		map.put(particleType, new Integer(0));
    	}
    	
    	public int getNumParticlesInPosition(ParticleType particleType, ParticlePosition position){
    		HashMap<ParticleType, Integer> map = position == ParticlePosition.INSIDE_MEMBRANE ? mapParticleTypeToNumInside :
    			mapParticleTypeToNumOutside;
    		Integer currentCount = map.get(particleType);
    		if (currentCount == null){
    			currentCount = new Integer(0);
    		}
    		return currentCount.intValue();
    	}
    	
    	public int getTotalNumParticles(ParticleType particleType){
    		return (getNumParticlesInPosition(particleType, ParticlePosition.INSIDE_MEMBRANE) + 
    				getNumParticlesInPosition(particleType, ParticlePosition.OUTSIDE_MEMBRANE));
    	}
    	
    	public double getProportion(ParticleType particleType, ParticlePosition position){
    		Integer insideCount = mapParticleTypeToNumInside.get(particleType);
    		if (insideCount == null){
    			insideCount = new Integer(0);
    		}
    		Integer outsideCount = mapParticleTypeToNumOutside.get(particleType);
    		if (outsideCount == null){
    			outsideCount = new Integer(0);
    		}

    		if (insideCount.intValue() == outsideCount.intValue() && insideCount.intValue() == 0){
    			return 0;
    		}
    		else if (position == ParticlePosition.INSIDE_MEMBRANE){
    			return insideCount.doubleValue() / (insideCount.doubleValue() + outsideCount.doubleValue());
    		}
    		else {
    			return outsideCount.doubleValue() / (insideCount.doubleValue() + outsideCount.doubleValue());
    		}
    	}
    }

    /**
     * A class for reporting the closest particle to the origin in a capture
     * zone and the total number of particles in the zone.
     */
    public static class CaptureZoneScanResult {
    	final Particle closestFreeParticle;
    	final int numParticlesInZone;
		public CaptureZoneScanResult(Particle closestParticle,
				int numParticlesInZone) {
			super();
			this.closestFreeParticle = closestParticle;
			this.numParticlesInZone = numParticlesInZone;
		}
		protected Particle getClosestFreeParticle() {
			return closestFreeParticle;
		}
		protected int getNumParticlesInZone() {
			return numParticlesInZone;
		}
    }
    
    public interface Listener extends EventListener {
    	/**
    	 * Notification that a channel was added.
    	 * 
    	 * @param channel - Channel that was added.
    	 */
    	public void channelAdded(MembraneChannel channel);
    	
    	/**
    	 * Notification that a channel was removed.
    	 * 
    	 * @param channel - Channel that was removed.
    	 */
    	public void channelRemoved(MembraneChannel channel);
    	
    	/**
    	 * Notification that a particle was added.
    	 * 
    	 * @param particle - Particle that was added.
    	 */
    	public void particleAdded(Particle particle);
    	
    	/**
    	 * Notification that the concentration gradient for the given particle
    	 * type had changed.
    	 * 
    	 * @param particleType - Particle for which the concentration gradient has
    	 * changed.
    	 */
    	public void concentrationRatioChanged(ParticleType particleType);
    	
    	/**
    	 * Notification that a stimulus pulse has been initiated.
    	 */
    	public void stimulusPulseInitiated();
    	
    	/**
    	 * Notification that the setting for the visibility of the membrane
    	 * potential chart has changed.
    	 */
    	public void potentialChartVisibilityChanged();
    	
    }
    
    public static class Adapter implements Listener{
		public void channelAdded(MembraneChannel channel) {}
		public void particleAdded(Particle particle) {}
		public void channelRemoved(MembraneChannel channel) {}
		public void concentrationRatioChanged(ParticleType particleType) {}
		public void stimulusPulseInitiated() {}
		public void potentialChartVisibilityChanged() {}
    }
}
