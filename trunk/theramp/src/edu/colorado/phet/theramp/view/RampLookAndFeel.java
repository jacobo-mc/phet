package edu.colorado.phet.theramp.view;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Dec 22, 2004
 * Time: 8:14:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class RampLookAndFeel {
    private final Color myGreen = new Color( 0.0f, 0.8f, 0.1f );
    private Color appliedForceColor = myGreen;
    private Color netForceColor = Color.blue;
    private Color frictionForceColor = Color.red;
    private Color weightColor = Color.yellow;
    private Color normalColor = Color.magenta;
    private Color wallForceColor = Color.orange;

    private Color accelColor = Color.black;
    private Color velColor = Color.black;
    private Color positionColor = Color.black;

    private Color totalEnergyColor = new Color( 100, 0, 160 );//purple
    private Color kineticEnergyColor = new Color( 234, 152, 168 );//light red
    private Color potentialEnergyColor = new Color( 134, 208, 197 );//light blue
    private Color thermalEnergyColor = new Color( 223, 188, 22 );//orange

//    private Color totalEnergyColor = Color.blue;
//    private Color kineticEnergyColor = myGreen;
//    private Color potentialEnergyColor = Color.yellow;
//    private Color thermalEnergyColor = Color.red;

    private Color appliedWorkColor = Color.green;
    private Color frictionWorkColor = Color.red;
    private Color gravityWorkColor = Color.yellow;
    private Color totalWorkColor = Color.blue;

    public Color getAppliedForceColor() {
        return appliedForceColor;
    }

    public Color getNetForceColor() {
        return netForceColor;
    }

    public Color getFrictionForceColor() {
        return frictionForceColor;
    }

    public Color getWeightColor() {
        return weightColor;
    }

    public Color getNormalColor() {
        return normalColor;
    }

    public Color getAccelerationColor() {
        return accelColor;
    }

    public Color getVelocityColor() {
        return velColor;
    }

    public Color getPositionColor() {
        return positionColor;
    }

    public Color getWallForceColor() {
        return wallForceColor;
    }

    public Color getTotalEnergyColor() {
        return totalEnergyColor;
    }

    public Color getKineticEnergyColor() {
        return kineticEnergyColor;
    }

    public Color getPotentialEnergyColor() {
        return potentialEnergyColor;
    }

    public Color getThermalEnergyColor() {
        return thermalEnergyColor;
    }

    public Color getAppliedWorkColor() {
        return appliedWorkColor;
    }

    public Color getFrictionWorkColor() {
        return frictionWorkColor;
    }

    public Color getGravityWorkColor() {
        return gravityWorkColor;
    }

    public Color getTotalWorkColor() {
        return totalWorkColor;
    }
}
