package edu.colorado.phet.membranediffusion.model;

import java.awt.Color;

import edu.colorado.phet.membranediffusion.MembraneChannelsConstants;


public class SodiumIon extends Particle {
	
	@Override
	public ParticleType getType() {
		return ParticleType.SODIUM_ION;
	}

	@Override
	public Color getRepresentationColor() {
		return MembraneChannelsConstants.SODIUM_COLOR;
	}
}
