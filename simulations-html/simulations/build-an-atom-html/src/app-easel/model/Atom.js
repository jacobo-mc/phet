// Copyright 2002-2012, University of Colorado
define( [
            'underscore'
        ], function ( _ ) {

    function Atom( xPos, yPos ) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.protons = 0;
        this.neutrons = 0;
        this.electrons = 0;
    }

    Atom.prototype.toJSON = function () {
        // hard coded properties for now
        return {
            symbol:"He",
            weight:1,
            number:1,
            charge:0
        };
    };

    var nucleons = [];

    Atom.prototype.addParticle = function ( particle ) {
        if ( particle.type === 'proton' ) {
            particle.setLocation( {x:0, y:0} );
            nucleons.push( particle );
            console.log( "Particle added to atom ");
        }
    }

    return Atom;

} );
