/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.GlaciersStrings;

/**
 * GraphsControlPanel is the control panel for creating graphs.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GraphsControlPanel extends JPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color BACKGROUND_COLOR = GlaciersConstants.INNER_PANEL_BACKGROUND_COLOR;
    private static final Color TITLE_COLOR = GlaciersConstants.INNER_PANEL_TITLE_COLOR;
    private static final Font TITLE_FONT = GlaciersConstants.CONTROL_PANEL_TITLE_FONT;
    private static final Font CONTROL_FONT = GlaciersConstants.CONTROL_PANEL_CONTROL_FONT;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private JComboBox _comboBox;
    
    private ArrayList _listeners; // list of GraphsControlPanelListener
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public GraphsControlPanel() {
        super();
        
        _listeners = new ArrayList();
        
        Border emptyBorder = BorderFactory.createEmptyBorder( 3, 3, 3, 3 );
        TitledBorder titledBorder = new TitledBorder( GlaciersStrings.TITLE_GRAPHS );
        titledBorder.setTitleFont( TITLE_FONT );
        titledBorder.setTitleColor( TITLE_COLOR );
        titledBorder.setBorder( BorderFactory.createLineBorder( TITLE_COLOR, 1 ) );
        Border compoundBorder = BorderFactory.createCompoundBorder( emptyBorder, titledBorder );
        setBorder( compoundBorder );
        
        Object[] items = {
                GlaciersStrings.RADIO_BUTTON_NO_GRAPH,
                GlaciersStrings.RADIO_BUTTON_GLACIER_LENGTH_VERSUS_TIME,
                GlaciersStrings.RADIO_BUTTON_EQUILIBRIUM_LINE_VERSUS_TIME,
                GlaciersStrings.RADIO_BUTTON_ACCUMULATION_VERSUS_ALTITUDE,
                GlaciersStrings.RADIO_BUTTON_ABLATION_VERSUS_ALTITUDE,
                GlaciersStrings.RADIO_BUTTON_MASS_BALANCE_VERSUS_ALTITUDE,
                GlaciersStrings.RADIO_BUTTON_TEMPERATURE_VERSUS_ALTITUDE,
                GlaciersStrings.RADIO_BUTTON_VALLEY_FLOOR_VERSUS_ALTITUDE
        };
        
        _comboBox = new JComboBox( items );
        _comboBox.setFont( CONTROL_FONT );
        _comboBox.setOpaque( false );
        _comboBox.addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
                if ( e.getStateChange() == ItemEvent.SELECTED ) {
                    notifySelectionChanged();
                }
            }
        });
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( _comboBox, row++, column );
        
        Class[] excludedClasses = { JComboBox.class };
        SwingUtils.setBackgroundDeep( this, BACKGROUND_COLOR, excludedClasses, false /* processContentsOfExcludedContainers */ );

        // default state
        _comboBox.setSelectedItem( GlaciersStrings.RADIO_BUTTON_NO_GRAPH );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setNoGraphSelected() {
        _comboBox.setSelectedItem( GlaciersStrings.RADIO_BUTTON_NO_GRAPH );
    }
    
    public boolean isNoGraphSelected() {
        return _comboBox.getSelectedItem().equals( GlaciersStrings.RADIO_BUTTON_NO_GRAPH );
    }
    
    public boolean isGlacierLengthVerusTimeSelected() {
        return _comboBox.getSelectedItem().equals( GlaciersStrings.RADIO_BUTTON_GLACIER_LENGTH_VERSUS_TIME );
    }
    
    public boolean isEquilibriumLineAltitudeVersusTimeSelected() {
        return _comboBox.getSelectedItem().equals( GlaciersStrings.RADIO_BUTTON_EQUILIBRIUM_LINE_VERSUS_TIME );
    }
    
    public boolean isAccumulationVersusAltitudeSelected() {
        return _comboBox.getSelectedItem().equals( GlaciersStrings.RADIO_BUTTON_ACCUMULATION_VERSUS_ALTITUDE );
    }
    
    public boolean isAblationVersusAltitudeSelected() {
        return _comboBox.getSelectedItem().equals( GlaciersStrings.RADIO_BUTTON_ABLATION_VERSUS_ALTITUDE );
    }
    
    public boolean isMassBalanceVersusAltitudeSelected() {
        return _comboBox.getSelectedItem().equals( GlaciersStrings.RADIO_BUTTON_MASS_BALANCE_VERSUS_ALTITUDE );
    }
    
    public boolean isTemperatureVersusAltitudeSelected() {
        return _comboBox.getSelectedItem().equals( GlaciersStrings.RADIO_BUTTON_TEMPERATURE_VERSUS_ALTITUDE );
    }
    
    public boolean isValleyFloorVersusAltitudeSelected() {
        return _comboBox.getSelectedItem().equals( GlaciersStrings.RADIO_BUTTON_VALLEY_FLOOR_VERSUS_ALTITUDE );
    }
    
    //----------------------------------------------------------------------------
    // Listeners
    //----------------------------------------------------------------------------
    
    /**
     * Interface implemented by all listeners who are interested in events related to this control panel.
     */
    public interface GraphsControlPanelListener {
        public void selectionChanged();
    }
    
    public static class GraphsControlPanelAdapter implements GraphsControlPanelListener {
        public void selectionChanged() {}
    }
    
    public void addGraphsControlPanelListener( GraphsControlPanelListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeGraphsControlPanelListener( GraphsControlPanelListener listener ) {
        _listeners.remove( listener );
    }
    
    //----------------------------------------------------------------------------
    // Notification
    //----------------------------------------------------------------------------
    
    private void notifySelectionChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (GraphsControlPanelListener) i.next() ).selectionChanged();
        }
    }
}
