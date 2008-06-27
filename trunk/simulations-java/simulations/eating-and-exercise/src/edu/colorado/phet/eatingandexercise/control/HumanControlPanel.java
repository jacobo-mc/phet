package edu.colorado.phet.eatingandexercise.control;

import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseResources;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseStrings;
import edu.colorado.phet.eatingandexercise.model.EatingAndExerciseUnits;
import edu.colorado.phet.eatingandexercise.model.Human;
import edu.colorado.phet.eatingandexercise.module.eatingandexercise.EatingAndExerciseModel;
import edu.colorado.phet.eatingandexercise.util.FeetInchesFormat;

/**
 * Created by: Sam
 * Apr 3, 2008 at 1:14:21 PM
 */
public class HumanControlPanel extends VerticalLayoutPanel {
    private EatingAndExerciseModel model;
    private Human human;

    private ArrayList listeners = new ArrayList();

    private HumanSlider bodyFatSlider;
    private HumanSlider ageSlider;
    private HumanSlider heightSlider;
    private HumanSlider weightSlider;

    public HumanControlPanel( final EatingAndExerciseModel model, final Human human ) {
        this.model = model;
        this.human = human;
        getGridBagConstraints().insets = new Insets( 4, 4, 4, 4 );
        setFillNone();

        add( new GenderControl( human ) );
        setFillHorizontal();

//        add( new ActivityLevelControlPanel( human ) );
//        add( new ActivityLevelComboBox( human ) );

        ageSlider = new HumanSlider( 0, 100, EatingAndExerciseUnits.secondsToYears( human.getAge() ), EatingAndExerciseResources.getString( "age" ), EatingAndExerciseStrings.AGE_FORMAT.toPattern(), EatingAndExerciseResources.getString( "units.years" ) );
        add( ageSlider );

        ageSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                human.setAge( EatingAndExerciseUnits.yearsToSeconds( ageSlider.getValue() ) );
            }
        } );

        human.addListener( new Human.Adapter() {
            public void ageChanged() {
                ageSlider.setValue( EatingAndExerciseUnits.secondsToYears( human.getAge() ) );
            }
        } );

        //todo: find a more elegant way to decide when to reset the chart regions
        ageSlider.getTextField().addKeyListener( new KeyAdapter() {
            public void keyReleased( KeyEvent e ) {
                notifyAgeManuallyChanged();
            }
        } );
        ageSlider.getTextField().addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifyAgeManuallyChanged();
            }
        } );
        ageSlider.getTextField().addFocusListener( new FocusListener() {
            public void focusGained( FocusEvent e ) {
            }

            public void focusLost( FocusEvent e ) {
                notifyAgeManuallyChanged();
            }
        } );
        ageSlider.getSlider().addMouseListener( new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                notifyAgeManuallyChanged();
            }
        } );
        ageSlider.getSlider().addMouseMotionListener( new MouseMotionAdapter() {
            public void mouseDragged( MouseEvent e ) {
                notifyAgeManuallyChanged();
            }
        } );

        //todo: factor out slider that accommodates units
        final double minHeight = 1;
        final double maxHeight = 2.72;
        heightSlider = new HumanSlider( model.getUnits().modelToViewDistance( minHeight ), model.getUnits().modelToViewDistance( maxHeight ), model.getUnits().modelToViewDistance( human.getHeight() ), EatingAndExerciseResources.getString( "height" ), "0.00", model.getUnits().getDistanceUnit() );
        heightSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                human.setHeight( model.getUnits().viewToModelDistance( heightSlider.getValue() ) );
            }
        } );
        human.addListener( new Human.Adapter() {
            public void heightChanged() {
                heightSlider.setValue( EatingAndExerciseUnits.metersToFeet( human.getHeight() ) );
            }
        } );
        model.addListener( new EatingAndExerciseModel.Adapter() {
            public void unitsChanged() {
                double origHeight = human.getHeight();
                double value = model.getUnits().modelToViewDistance( human.getHeight() );

                //have to change range before changing value
                heightSlider.setRange( model.getUnits().modelToViewDistance( minHeight ), model.getUnits().modelToViewDistance( maxHeight ) );
                heightSlider.setValue( value );
                heightSlider.setUnits( model.getUnits().getDistanceUnit() );

                heightSlider.setPaintLabels( false );
                heightSlider.setPaintTicks( false );

                human.setHeight( origHeight );//restore original value since clamping the range at a different time as the value can lead to incorrect values
            }
        } );
        heightSlider.setTextFieldFormat( new FeetInchesFormat() );
        model.addListener( new EatingAndExerciseModel.Adapter() {
            public void unitsChanged() {
                heightSlider.setTextFieldFormat( model.getUnits() == EatingAndExerciseModel.Units.METRIC ? (NumberFormat) EatingAndExerciseStrings.AGE_FORMAT : new FeetInchesFormat() );
            }
        } );

        add( heightSlider );


        final double minWeight = 0;
        final double maxWeight = EatingAndExerciseUnits.poundsToKg( 300 );
        weightSlider = new HumanSlider( model.getUnits().modelToViewMass( minWeight ), model.getUnits().modelToViewMass( maxWeight ), model.getUnits().modelToViewMass( human.getMass() ), EatingAndExerciseResources.getString( "weight" ), EatingAndExerciseStrings.WEIGHT_FORMAT.toPattern(), model.getUnits().getMassUnit() );
        weightSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                human.setMass( model.getUnits().viewToModelMass( weightSlider.getValue() ) );
            }
        } );
        human.addListener( new Human.Adapter() {
            public void weightChanged() {
                weightSlider.setValue( model.getUnits().modelToViewMass( human.getMass() ) );
            }
        } );
        model.addListener( new EatingAndExerciseModel.Adapter() {
            public void unitsChanged() {
                weightSlider.setValue( model.getUnits().modelToViewMass( human.getMass() ) );
                weightSlider.setUnits( model.getUnits().getMassUnit() );
                weightSlider.setRange( model.getUnits().modelToViewMass( minWeight ), model.getUnits().modelToViewMass( maxWeight ) );
                weightSlider.setPaintLabels( false );
                weightSlider.setPaintTicks( false );
            }
        } );
        add( weightSlider );

        bodyFatSlider = new HumanSlider( 0, 100, human.getFatMassPercent(), EatingAndExerciseResources.getString( "body.fat" ), "0.0", "%" );
        bodyFatSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                human.setFatMassPercent( bodyFatSlider.getValue() );
            }
        } );
        human.addListener( new Human.Adapter() {
            public void fatPercentChanged() {
                bodyFatSlider.setValue( human.getFatMassPercent() );
            }
        } );
        human.addListener( new Human.Adapter() {
            public void genderChanged() {
                updateBodyFatSlider();
            }
        } );
        add( bodyFatSlider );

        updateBodyFatSlider();
        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                updateBodyFatSlider();
            }
        } );
    }

    public double getAgeSliderY() {
        return ageSlider.getY();
    }

    private void updateBodyFatSlider() {
        bodyFatSlider.setRange( 0, human.getGender().getMaxFatMassPercent() );
        Hashtable table = new Hashtable();
        table.put( new Double( 10 ), new JLabel( EatingAndExerciseResources.getString( "muscular" ) ) );
        table.put( new Double( human.getGender().getMaxFatMassPercent() ), new JLabel( EatingAndExerciseResources.getString( "non-muscular" ) ) );
        bodyFatSlider.setTickLabels( table );
    }

    public static interface Listener {
        void ageManuallyChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyAgeManuallyChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).ageManuallyChanged();
        }
    }
}