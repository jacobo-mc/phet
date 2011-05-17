// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.intro.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.Plus;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.sugarandsaltsolutions.common.SugarAndSaltSolutionsColorScheme;
import edu.colorado.phet.sugarandsaltsolutions.common.view.*;
import edu.colorado.phet.sugarandsaltsolutions.intro.model.IntroModel;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication.WATER_COLOR;

/**
 * Canvas for the "intro" tab of sugar and salt solutions
 *
 * @author Sam Reid
 */
public class IntroCanvas extends SugarAndSaltSolutionsCanvas {
    public IntroCanvas( final IntroModel model, SugarAndSaltSolutionsColorScheme config ) {
        super( model, model.anySolutes, config );


        //Show the full water node at the correct color, then overlay a partially transparent one on top, so that some objects (such as the conductivity tester) will look submerged
        addChild( new WaterNode( transform, model.displacedWaterVolume, model.beaker, WATER_COLOR ) );

        //Node that shows things that get submerged such as the conductivity tester
        addChild( conductivityToolboxLayer );
        addChild( submergedInWaterNode );

        //Overlay node that renders as partially transparent in front of submerged objects, such as the conductivity tester.
        //When changing the transparency here make sure it looks good for precipitate as well as submerged probes
        addChild( new WaterNode( transform, model.displacedWaterVolume, model.beaker, new Color( WATER_COLOR.getRed(), WATER_COLOR.getGreen(), WATER_COLOR.getBlue(), 128 ) ) {{

            //Make it so the mouse events pass through the front water layer so it is still possible to pick and move the conductivity tester probes
            setPickable( false );
            setChildrenPickable( false );
        }} );

        //Readout the volume of the water in Liters, only visible if the user opted to show values (in the concentration bar chart)
        addChild( new VolumeIndicatorNode( transform, model.water, model.showConcentrationValues ) );

        //Add a button that allows the user to remove all solutes
        addChild( new ButtonNode( "Remove salt/sugar" ) {{
            //Button should be inside the beaker
            setOffset( transform.modelToViewX( model.beaker.getMaxX() ) - getFullBounds().getWidth() - INSET,
                       transform.modelToViewY( model.beaker.getY() ) - getFullBounds().getHeight() - INSET );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    model.removeSaltAndSugar();
                }
            } );
            model.anySolutes.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    setVisible( visible );
                }
            } );
        }} );

        //Button that maximizes the bar chart
        PImage maximizeButton = new PImage( PhetCommonResources.getMaximizeButtonImage() ) {{
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mousePressed( PInputEvent event ) {
                    model.showConcentrationBarChart.set( true );
                }
            } );
        }};
        //Layout maximize button next to "concentration" label
        HBox contentPane = new HBox(
                new PText( "Concentration" ) {{
                    setFont( CONTROL_FONT );
                }},
                maximizeButton
        );

        //Panel that says "concentration" and has a "+" button to expand the concentration bar chart
        ControlPanelNode showBarChartPanel = new ControlPanelNode( contentPane, WATER_COLOR, new BasicStroke( 1 ), Color.black ) {{
            setOffset( stageSize.getWidth() - getFullBoundsReference().width - INSET, INSET );
            model.showConcentrationBarChart.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean chartVisible ) {
                    setVisible( !chartVisible );
                }
            } );
        }};
        behindShakerNode.addChild( showBarChartPanel );

        //The bar chart itself (when toggled to be visible)
        ConcentrationBarChart concentrationBarChart = new ConcentrationBarChart( model.salt.concentration, model.sugar.concentration, model.showConcentrationValues, model.showConcentrationBarChart ) {{
            setOffset( stageSize.getWidth() - getFullBoundsReference().width - INSET, INSET );
        }};
        behindShakerNode.addChild( concentrationBarChart );

        //Toolbox from which the conductivity tester can be dragged
        conductivityToolboxLayer.addChild( new ConductivityTesterToolboxNode( model, this ) {{
            //Set the location of the control panel
            setOffset( stageSize.getWidth() - getFullBounds().getWidth(), soluteControlPanelNode.getFullBounds().getMaxY() + INSET );
        }} );

        soluteControlPanelNode.setOffset( concentrationBarChart.getFullBounds().getX() - soluteControlPanelNode.getFullBounds().getWidth() - INSET, concentrationBarChart.getFullBounds().getY() );

        //Add an evaporation rate slider below the beaker
        addChild( new EvaporationSlider( model.evaporationRate ) {{
            Point2D point = IntroCanvas.this.transform.modelToView( 0, -model.beaker.getWallWidth() / 2 );
            setOffset( point.getX() - getFullBounds().getWidth() / 2, point.getY() + INSET );
        }} );

        //Show the precipitate as the sum of salt and sugar
        submergedInWaterNode.addChild( new PrecipitateNode( transform, new Plus( model.salt.molesPrecipitated, model.sugar.molesPrecipitated ), model.beaker ) );
    }
}
