/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.control.panel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.text.MessageFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.model.Battery;


/**
 * BatteryPanel constains the control for the Battery.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BatteryPanel extends FaradayPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model & view components to be controlled.
    private Battery _batteryModel;

    // UI components
    private JSlider _batteryAmplitudeSlider;
    private JLabel _batteryAmplitudeValue;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param batteryModel
     */
    public BatteryPanel( Battery batteryModel ) {

        super();
        assert( batteryModel != null );
        
        _batteryModel = batteryModel;
        
        // Title
        Border border = BorderFactory.createLineBorder( Color.BLACK, 2 );
        String title = SimStrings.get( "BatteryPanel.title" );
        TitledBorder titledBorder = BorderFactory.createTitledBorder( border, title );
        titledBorder.setTitleFont( getTitleFont() );
        setBorder( titledBorder );

        JPanel batteryAmpitudePanel = new JPanel();
        {
            batteryAmpitudePanel.setBorder( BorderFactory.createEtchedBorder() );

            // Range of values
            int max = (int) ( 100.0 * FaradayConfig.BATTERY_AMPLITUDE_MAX );
            int min = (int) ( 100.0 * FaradayConfig.BATTERY_AMPLITUDE_MIN );
            int range = max - min;

            // Slider
            _batteryAmplitudeSlider = new JSlider();
            _batteryAmplitudeSlider.setMaximum( max );
            _batteryAmplitudeSlider.setMinimum( min );
            _batteryAmplitudeSlider.setValue( min );

            // Slider tick marks
            _batteryAmplitudeSlider.setMajorTickSpacing( range );
            _batteryAmplitudeSlider.setMinorTickSpacing( range / 10 );
            _batteryAmplitudeSlider.setSnapToTicks( false );
            _batteryAmplitudeSlider.setPaintTicks( true );
            _batteryAmplitudeSlider.setPaintLabels( true );

            // Value
            _batteryAmplitudeValue = new JLabel( UNKNOWN_VALUE );

            // Layout
            EasyGridBagLayout layout = new EasyGridBagLayout( batteryAmpitudePanel );
            batteryAmpitudePanel.setLayout( layout );
            layout.addAnchoredComponent( _batteryAmplitudeValue, 0, 0, GridBagConstraints.WEST );
            layout.addAnchoredComponent( _batteryAmplitudeSlider, 1, 0, GridBagConstraints.WEST );
        }

        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        layout.addFilledComponent( batteryAmpitudePanel, row++, 0, GridBagConstraints.HORIZONTAL );

        // Wire up event handling.
        EventListener listener = new EventListener();
        _batteryAmplitudeSlider.addChangeListener( listener );

        // Update control panel to match the components that it's controlling.
        _batteryAmplitudeSlider.setValue( (int) ( 100.0 * _batteryModel.getAmplitude() ) );
    }

    //----------------------------------------------------------------------------
    // Event Handling
    //----------------------------------------------------------------------------

    /**
     * EventListener is a nested class that is private to this control panel.
     * It handles dispatching of all events generated by the controls.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     * @version $Revision$
     */
    private class EventListener implements ChangeListener {

        /** Sole constructor */
        public EventListener() {}

        /**
         * ChangeEvent handler.
         * 
         * @param e the event
         * @throws IllegalArgumentException if the event is unexpected
         */
        public void stateChanged( ChangeEvent e ) {
            if ( e.getSource() == _batteryAmplitudeSlider ) {
                // Read the value.
                int percent = _batteryAmplitudeSlider.getValue();
                // Update the model.
                _batteryModel.setAmplitude( percent / 100.0 );
                // Update the label.
                Object[] args = { new Integer( percent ) };
                String text = MessageFormat.format( SimStrings.get( "BatteryPanel.voltage" ), args );
                _batteryAmplitudeValue.setText( text );
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + e );
            }
        }
    }

}