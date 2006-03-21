/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.controls;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.FractionalDoubleSlit;
import edu.colorado.phet.qm.model.potentials.HorizontalDoubleSlit;
import edu.colorado.phet.qm.modules.intensity.IntensityModule;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jul 27, 2005
 * Time: 9:37:07 AM
 * Copyright (c) Jul 27, 2005 by Sam Reid
 */

public class DoubleSlitControlPanel extends VerticalLayoutPanel {
    private DiscreteModel discreteModel;
    private HorizontalDoubleSlit horizontalDoubleSlit;
    private JComponent slitSize;
    private JComponent slitSeparation;
    private JComponent verticalPosition;
    private SlitDetectorPanel slitDetectorPanel;
    private SchrodingerModule module;
    private FractionalDoubleSlit fractionalSlit;

    public DoubleSlitControlPanel( final DiscreteModel discreteModel, SchrodingerModule schrodingerModule ) {
        this.discreteModel = discreteModel;
        this.module = schrodingerModule;
        this.horizontalDoubleSlit = discreteModel.getDoubleSlitPotential();
        this.fractionalSlit = discreteModel.getFractionalDoubleSlit();
        verticalPosition = createComponent( "Vertical Position", new Setter() {
            double insetY = 10 / 60.0;

            public void valueChanged( double val ) {
                fractionalSlit.setY( val );
            }

            public double getValue() {
                return fractionalSlit.getY();
            }

            public double getMin() {
                return 0 + insetY;
            }

            public double getMax() {
                return ResolutionControl.DEFAULT_WAVE_SIZE / 60.0 - insetY;
            }
        } );


        slitSize = createComponent( "Slit Width ", new Setter() {
            public void valueChanged( double val ) {
                fractionalSlit.setSlitSize( val );
            }

            public double getValue() {
                return fractionalSlit.getSlitSize();
            }

            public double getMin() {
                return 4 / 60.0;
            }

            public double getMax() {
                return 25 / 60.0;
            }
        } );

        slitSeparation = createComponent( "Slit Separation", new Setter() {
            public void valueChanged( double val ) {
                fractionalSlit.setSlitSeparation( val );
            }

            public double getValue() {
                return fractionalSlit.getSlitSeparation();
            }

            public double getMin() {
                return 0;
            }

            public double getMax() {
                return 30 / 60.0;
            }
        } );

        final JCheckBox absorbtiveSlit = new JCheckBox( "Absorbing Barriers", getDiscreteModel().isBarrierAbsorptive() );
        absorbtiveSlit.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getDiscreteModel().setBarrierAbsorptive( absorbtiveSlit.isSelected() );
            }
        } );
        add( absorbtiveSlit );


        add( slitSize );
        add( slitSeparation );
        add( verticalPosition );

        if( schrodingerModule instanceof IntensityModule ) {//todo use polymorphism here
            slitDetectorPanel = new SlitDetectorPanel( (IntensityModule)schrodingerModule );
            addFullWidth( slitDetectorPanel );
        }
        setControlsEnabled( true );
        addFullWidth( new InverseSlitsCheckbox( schrodingerModule.getSchrodingerPanel() ) );
    }

    public SlitDetectorPanel getSlitDetectorPanel() {
        return slitDetectorPanel;
    }

    private void setControlsEnabled( boolean selected ) {
        slitSize.setEnabled( selected );
        slitSeparation.setEnabled( selected );
        verticalPosition.setEnabled( selected );
    }

    private JComponent createComponent( String title, final Setter setter ) {
        final Function.LinearFunction modelToView = new Function.LinearFunction( setter.getMin(), setter.getMax(), 0, 100 );
        final Function.LinearFunction viewToModel = new Function.LinearFunction( 0, 100, setter.getMin(), setter.getMax() );
        int value = (int)modelToView.evaluate( setter.getValue() );
//        System.out.println( "title: " + title + "+value = " + value );
        final JSlider comp = new JSlider( 0, 100, value );
        comp.setBorder( BorderFactory.createTitledBorder( title ) );
        comp.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setter.valueChanged( viewToModel.evaluate( comp.getValue() ) );
            }
        } );
        module.getClock().addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent event ) {
                comp.setValue( (int)modelToView.evaluate( setter.getValue() ) );
            }
        } );
        return comp;
    }

    private interface Setter {
        void valueChanged( double val );

        double getValue();

        double getMin();

        double getMax();
    }

    private DiscreteModel getDiscreteModel() {
        return discreteModel;
    }
}
