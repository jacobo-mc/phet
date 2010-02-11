/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.neuron.NeuronConstants;

public class PotassiumGatedChannel extends AbstractGatedChannel {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	private static final double CHANNEL_HEIGHT = AxonMembrane.MEMBRANE_THICKNESS * 1.4; // In nanometers.
	private static final double CHANNEL_WIDTH = AxonMembrane.MEMBRANE_THICKNESS * 0.70; // In nanometers.
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	private HodgkinHuxleyModel hodgekinHodgkinHuxleyModel;
	
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
	public PotassiumGatedChannel(HodgkinHuxleyModel hodgekinHuxleyModel) {
		super(CHANNEL_WIDTH, CHANNEL_HEIGHT, ParticleType.SODIUM_ION);
		this.hodgekinHodgkinHuxleyModel = hodgekinHuxleyModel;
	}

	@Override
	public Color getChannelColor() {
		return ColorUtils.darkerColor(NeuronConstants.POTASSIUM_COLOR, 0.2);
	}

	@Override
	public Color getEdgeColor() {
		return NeuronConstants.POTASSIUM_COLOR;
	}

	@Override
	public MembraneChannelTypes getChannelType() {
		return MembraneChannelTypes.POTASSIUM_LEAKAGE_CHANNEL;
	}
	
	@Override
	public void stepInTime(double dt) {
		super.stepInTime(dt);
		// Update the openness factor based on the state of the HH model.
		// This is very specific to the model and the type of channel.
		setOpenness(Math.min(Math.abs(hodgekinHodgkinHuxleyModel.get_k_current())/100, 1));
	}
}
