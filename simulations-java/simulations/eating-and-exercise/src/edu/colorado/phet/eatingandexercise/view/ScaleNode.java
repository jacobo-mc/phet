package edu.colorado.phet.eatingandexercise.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.BevelBorder;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseResources;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseStrings;
import edu.colorado.phet.eatingandexercise.model.Human;
import edu.colorado.phet.eatingandexercise.module.eatingandexercise.EatingAndExerciseModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Scale that the human is standing on, used to show his/her weight.
 * The scale has a pseudo-3D look, the weight is displayed on the front face.
 * Radio buttons are provides for switching between English and Metric units.
 * While these radio buttons are part of the scale, they don't affect just the
 * scale, they change units for the entire sim.
 * 
 * Created by: Sam
 * Apr 9, 2008 at 8:35:03 PM
 */
public class ScaleNode extends PNode {
    
    private static final double FACE_WIDTH = 0.9;
    private static final double FACE_HEIGHT = 0.13;
    private static final double FACE_Y = 0.05;
    private static final double DEPTH_DX = 0.06;
    private static final double DEPTH_DY = 0.1;
    private static final float STROKE_WIDTH = 0.02f;
    private static final Color SCALE_COLOR = Color.LIGHT_GRAY;
    private static final String BMI_LABEL = EatingAndExerciseResources.getString( "bmi" );
    private static final String BMI_UNITS = EatingAndExerciseResources.getString( "units.bmi" );
    private static final double TEXT_SCALE = 1.0 / 175.0;
    
    private final EatingAndExerciseModel model;
    private final Human human;
    private final PText weightReadout;

    public ScaleNode( final EatingAndExerciseModel model, Human human ) {
        
        this.model = model;
        this.human = human;
        
        // top face of the scale
        DoubleGeneralPath topPath = new DoubleGeneralPath();
        topPath.moveTo( -FACE_WIDTH / 2, FACE_Y );
        topPath.lineTo( -FACE_WIDTH / 2 + DEPTH_DX, FACE_Y - DEPTH_DY );
        topPath.lineTo( FACE_WIDTH / 2 - DEPTH_DX, FACE_Y - DEPTH_DY );
        topPath.lineTo( FACE_WIDTH / 2, FACE_Y );
        topPath.lineTo( -FACE_WIDTH / 2, FACE_Y );
        addChild( new PhetPPath( topPath.getGeneralPath(), SCALE_COLOR, new BasicStroke( STROKE_WIDTH ), Color.black ) );

        // front face
        DoubleGeneralPath facePath = new DoubleGeneralPath();
        facePath.moveTo( -FACE_WIDTH / 2, FACE_Y );
        facePath.lineTo( -FACE_WIDTH / 2, FACE_Y + FACE_HEIGHT );
        facePath.lineTo( FACE_WIDTH / 2, FACE_Y + FACE_HEIGHT );
        facePath.lineTo( FACE_WIDTH / 2, FACE_Y );
        facePath.lineTo( -FACE_WIDTH / 2, FACE_Y );
        PNode faceNode = new PhetPPath( facePath.getGeneralPath(), SCALE_COLOR, new BasicStroke( STROKE_WIDTH ), Color.black );
        addChild( faceNode );
        
        // monitor changes in the human's weight
        human.addListener( new Human.Adapter() {
            public void weightChanged() {
                updateReadout();
            }

            public void bmiChanged() {
                updateReadout();
            }
        } );
        
        // weight read-out appears on the front of the scale
        weightReadout = new EatingAndExercisePText( "??" );
        weightReadout.scale( TEXT_SCALE );
        addChild( weightReadout );
        updateReadout();

        // radio buttons for switching between English and Metric units
        JPanel units = new VerticalLayoutPanel();
        units.setBorder( BorderFactory.createBevelBorder( BevelBorder.LOWERED ) );
        ButtonGroup buttonGroup = new ButtonGroup();
        for ( int i = 0; i < EatingAndExerciseModel.availableUnits.length; i++ ) {
            final JRadioButton jRadioButton = new JRadioButton( EatingAndExerciseModel.availableUnits[i].getShortName(), EatingAndExerciseModel.availableUnits[i] == model.getUnits() );
            buttonGroup.add( jRadioButton );
            final int i1 = i;
            jRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    model.setUnits( EatingAndExerciseModel.availableUnits[i1] );
                }
            } );
            model.addListener( new EatingAndExerciseModel.Adapter() {
                public void unitsChanged() {
                    jRadioButton.setSelected( model.getUnits() == EatingAndExerciseModel.availableUnits[i1] );
                }
            } );
            units.add( jRadioButton );
        }

        PSwing unitsPSwing = new PSwing( units );
        unitsPSwing.setOffset( FACE_WIDTH / 2 + STROKE_WIDTH / 2, 0 );
        unitsPSwing.scale( TEXT_SCALE * 0.75 );
        addChild( unitsPSwing );
        model.addListener( new EatingAndExerciseModel.Adapter() {
            public void unitsChanged() {
                updateReadout();
            }
        } );
    }

    private void updateReadout() {
        weightReadout.setText( "" + EatingAndExerciseStrings.WEIGHT_FORMAT.format( model.getUnits().modelToViewMass( human.getMass() ) ) + " " + model.getUnits().getMassUnit() + ", " + BMI_LABEL + ": " + EatingAndExerciseStrings.BMI_FORMAT.format( human.getBMI() ) + " " + BMI_UNITS );
        updateTextLayout();
    }

    private void updateTextLayout() {
        weightReadout.setOffset( 0 - weightReadout.getFullBounds().getWidth() / 2, FACE_Y + FACE_HEIGHT - weightReadout.getFullBounds().getHeight() - 0.01 );
    }
}
