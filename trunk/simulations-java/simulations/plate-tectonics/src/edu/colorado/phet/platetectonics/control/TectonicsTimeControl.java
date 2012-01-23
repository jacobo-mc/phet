// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import java.awt.Graphics;
import java.text.DecimalFormat;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.AbstractValueControl;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.ILayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;
import edu.colorado.phet.platetectonics.model.TectonicsClock;

public class TectonicsTimeControl extends PiccoloClockControlPanel {
    public TectonicsTimeControl( final TectonicsClock clock, final Property<Boolean> isAutoMode ) {
        super( clock );

        setRewindButtonVisible( false );
        setTimeDisplayVisible( true );
        setUnits( Strings.MILLION_YEARS );
        setTimeFormat( new DecimalFormat( "0" ) );
        setTimeColumns( 4 );

        // TODO: LinearValueControl seems severely broken in Mac visual display
        final LinearValueControl frameRateControl;
        // Frame Rate control
        {
            double min = 0.1;
            double max = 10;
            String label = "";
            String textFieldPattern = "";
            String units = "";
            frameRateControl = new LinearValueControl( min, max, label, textFieldPattern, units, new ILayoutStrategy() {
                public void doLayout( AbstractValueControl valueControl ) {
                    valueControl.add( valueControl.getSlider() );
                }
            } ) {
                @Override public void paint( Graphics g ) {
                    if ( isAutoMode.get() ) {
                        super.paint( g );
                    }
                }
            };
            frameRateControl.setValue( clock.getTimeMultiplier() ); // TODO: improve here
            frameRateControl.setMinorTicksVisible( false );

            // Tick labels
            Hashtable labelTable = new Hashtable();
            labelTable.put( new Double( min ), new JLabel( Strings.TIME_SLOW ) );
            labelTable.put( new Double( max ), new JLabel( Strings.TIME_FAST ) );
            frameRateControl.setTickLabels( labelTable );

            // Change font on tick labels
            Dictionary d = frameRateControl.getSlider().getLabelTable();
            Enumeration e = d.elements();
            while ( e.hasMoreElements() ) {
                Object o = e.nextElement();
                if ( o instanceof JComponent ) { ( (JComponent) o ).setFont( new PhetFont( 10 ) ); }
            }

            // Slider width
            frameRateControl.setSliderWidth( 125 );

            // TODO: handle resetting properly
            frameRateControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent changeEvent ) {
                    clock.setTimeMultiplier( frameRateControl.getValue() );
                }
            } );

            frameRateControl.setPreferredSize( frameRateControl.getPreferredSize() );
        }
        addBetweenTimeDisplayAndButtons( frameRateControl );
        //addBetweenTimeDisplayAndButtons( new JLabel( "Speed control" ) );

        isAutoMode.addObserver( new SimpleObserver() {
            public void update() {
                final boolean isAuto = isAutoMode.get();
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        getPlayPauseButton().setTransparency( isAuto ? 1 : 0 );
                        getPlayPauseButton().setPickable( isAuto );
                        getStepButton().setTransparency( isAuto ? 1 : 0 );
                        getStepButton().setPickable( isAuto );
                        frameRateControl.setEnabled( isAuto );
//                        frameRateControl.setVisible( isAuto );
                        repaint();
                    }
                } );
            }
        } );
    }

}
