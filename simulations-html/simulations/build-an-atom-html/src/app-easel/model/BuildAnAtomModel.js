// Copyright 2002-2012, University of Colorado
define( [
            'underscore',
            'common/SharedConstants',
            'common/Utils',
            'model/atom',
            'model/Bucket',
            'model/particle'
        ], function ( _, SharedConstants, Utils, Atom, Bucket, Particle ) {

    var NUM_PROTONS = 10;
    var PROTON_COLOR = "red";
    var NUM_NEUTRONS = 13;
    var NEUTRON_COLOR = "gray";
    var NUM_ELECTRONS = 10;
    var ELECTRON_COLOR = "rgb( 175, 238, 238)";
    var NUCLEON_CAPTURE_RADIUS = 100;
    var ELECTRON_CAPTURE_RADIUS = Atom.OUTER_ELECTRON_SHELL_RADIUS * 1.1;
    var BUCKET_WIDTH = 150;

    /**
     * Constructor for main model object.
     *
     * @constructor
     */
    function BuildAnAtomModel() {

        this.atom = new Atom( 0, 0 );

        this.buckets = {
            protonBucket:new Bucket( -200, 300, BUCKET_WIDTH, SharedConstants.NUCLEON_RADIUS, "Protons" ),
            neutronBucket:new Bucket( 0, 300, BUCKET_WIDTH, SharedConstants.NUCLEON_RADIUS, "Neutrons" ),
            electronBucket:new Bucket( 200, 300, BUCKET_WIDTH, SharedConstants.ELECTRON_RADIUS, "Electrons" )
        };

        this.nucleons = [];
        this.electrons = [];
        var self = this;

        // Add the protons.
        _.times( NUM_PROTONS, function () {
            var proton = new Particle( self.buckets.protonBucket.x, self.buckets.protonBucket.y, PROTON_COLOR, SharedConstants.NUCLEON_RADIUS, "proton" );
            self.nucleons.push( proton );
            self.buckets.protonBucket.addParticle( proton );
            proton.events.on( 'userReleased', function () {
                if ( Utils.distanceBetweenPoints( self.atom.xPos, self.atom.yPos, proton.x, proton.y ) < NUCLEON_CAPTURE_RADIUS ) {
                    self.atom.addParticle( proton );
                }
                else {
                    self.buckets.protonBucket.addParticle( proton );
                }
            } );
        } );

        // Add the neutrons.
        _.times( NUM_NEUTRONS, function () {
            var neutron = new Particle( self.buckets.neutronBucket.x, self.buckets.protonBucket.y, NEUTRON_COLOR, SharedConstants.NUCLEON_RADIUS, "neutron" );
            self.nucleons.push( neutron );
            self.buckets.neutronBucket.addParticle( neutron );
            neutron.events.on( 'userReleased', function () {
                if ( Utils.distanceBetweenPoints( self.atom.xPos, self.atom.yPos, neutron.x, neutron.y ) < NUCLEON_CAPTURE_RADIUS ) {
                    self.atom.addParticle( neutron );
                }
                else {
                    self.buckets.neutronBucket.addParticle( neutron );
                }
            } );
        } );

        // Add the electrons.
        _.times( NUM_ELECTRONS, function () {
            var electron = new Particle( self.buckets.electronBucket.x, self.buckets.electronBucket.y, ELECTRON_COLOR, SharedConstants.ELECTRON_RADIUS, "electron" );
            self.electrons.push( electron );
            self.buckets.electronBucket.addParticle( electron );
            electron.events.on( 'userReleased', function () {
                if ( Utils.distanceBetweenPoints( self.atom.xPos, self.atom.yPos, electron.x, electron.y ) < ELECTRON_CAPTURE_RADIUS ) {
                    self.atom.addParticle( electron );
                }
                else {
                    self.buckets.electronBucket.addParticle( electron );
                }
            } );
        } );
    }

    return BuildAnAtomModel;
} );
