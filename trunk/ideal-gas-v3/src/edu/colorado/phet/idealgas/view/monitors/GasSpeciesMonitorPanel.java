/*
 * Class: IdealGasMonitorPanel
 * Package: edu.colorado.phet.graphicaldomain.idealgas
 *
 * Created by: Ron LeMaster
 * Date: Oct 30, 2002
 */
package edu.colorado.phet.idealgas.view.monitors;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.idealgas.IdealGasStrings;
import edu.colorado.phet.idealgas.model.GasMolecule;
import edu.colorado.phet.idealgas.model.HeavySpecies;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.model.LightSpecies;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.lang.reflect.Method;
import java.text.NumberFormat;

/**
 *
 */
public class GasSpeciesMonitorPanel extends PhetMonitorPanel implements SimpleObserver {

    private Class speciesClass;
    private Method aveSpeedMethod;
    private Method numMoleculesMethod;

    private JTextField numParticlesTF;
    private NumberFormat aveSpeedFormat = NumberFormat.getInstance();
    private JTextField aveSpeedTF;
    private IdealGasModel model;


    /**
     * Constructor
     */
    public GasSpeciesMonitorPanel( Class speciesClass, String speciesName, IdealGasModel model ) {
        this.model = model;
        this.speciesClass = speciesClass;

        setUpdateInterval( 500 );

        // Sanity check on parameter
        if( !GasMolecule.class.isAssignableFrom( speciesClass ) ) {
            throw new RuntimeException( "Class other than a gas species class sent to constructor for GasSpeciesMonitorPanel" );
        }

        this.setPreferredSize( new Dimension( 400, 60 ) );
        Border border = new TitledBorder( speciesName );
        this.setBorder( border );

        // Set up the readout for the number of gas molecules
        this.add( new JLabel( IdealGasStrings.get( "GasSpeciesMonitorPanel.Number_of_Gas_Molecules") + ": " ) );
        numParticlesTF = new JTextField( 4 );
        numParticlesTF.setEditable( false );
        this.add( numParticlesTF );

        // Set up the average speed readout
        aveSpeedFormat.setMaximumFractionDigits( 2 );
        //aveSpeedFormat.setMinimumFractionDigits( 2 );
        this.add( new JLabel( IdealGasStrings.get( "GasSpeciesMonitorPanel.Average_speed" ) + ": " ) );
        aveSpeedTF = new JTextField( 6 );
        aveSpeedTF.setEditable( false );
        this.add( aveSpeedTF );
    }

    /**
     * Clears the values in the readouts
     */
    public void clear() {
        numParticlesTF.setText( "" );
        aveSpeedTF.setText( "" );
    }

    /**
     *
     */
    Object[] emptyParamArray = new Object[]{};

    public void update() {

        // Get the number of molecules, average speed of the molecules
        double aveSpeed = 0;
        int numMolecules = 0;
            if( HeavySpecies.class.isAssignableFrom( speciesClass )) {
                numMolecules = model.getHeavySpeciesCnt();
                aveSpeed = model.getHeavySpeciesAveSpeed();
            }
            if( LightSpecies.class.isAssignableFrom( speciesClass )) {
                numMolecules = model.getLightSpeciesCnt();
                aveSpeed = model.getLightSpeciesAveSpeed();
            }

        // Track the values we got
        long now = System.currentTimeMillis();
        if( now - getLastUpdateTime() >= getUpdateInterval() ) {

            setLastUpdateTime( now );
            //Display the readings
            numParticlesTF.setText( Integer.toString( numMolecules ));

            if( Double.isNaN( runningAveSpeed ) ) {
            }
            aveSpeedTF.setText( aveSpeedFormat.format( ( runningAveSpeed / sampleCnt ) * s_aveSpeedReadoutFactor ) );
            sampleCnt = 0;
            runningAveSpeed = 0;
        }
        else {
            sampleCnt++;
            runningAveSpeed += aveSpeed;
        }
    }

    private int sampleCnt;
    private double runningAveSpeed;

    /**
     *
     */
    public void paintComponent( Graphics graphics ) {
        super.paintComponent( graphics );
    }

    //
    // Inner classes
    //
    private class GaugePanel extends JPanel {

        public void paintComponent( Graphics graphics ) {
            super.paintComponent( graphics );
            Graphics2D g2 = (Graphics2D)graphics;
        }
    }

    //
    // Static fields and methods
    //
    private double s_pressureReadoutFactor = 1.0 / 100;
    private double s_temperatureReadoutFactor = 1.0 / 1000;
    private double s_aveSpeedReadoutFactor = 10;
}
