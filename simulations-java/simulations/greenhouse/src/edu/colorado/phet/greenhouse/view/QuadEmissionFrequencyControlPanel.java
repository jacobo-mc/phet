// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.greenhouse.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SpectrumImageFactory.ExponentialGrowthSpectrumImageFactory;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.greenhouse.GreenhouseConfig;
import edu.colorado.phet.greenhouse.GreenhouseResources;
import edu.colorado.phet.greenhouse.model.PhotonAbsorptionModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * This is a control panel that is intended for use in the play area and
 * that allows the setting of 4 different emission frequencies.
 *
 * @author John Blanco
 */
public class QuadEmissionFrequencyControlPanel extends PNode {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    private static final Color BACKGROUND_COLOR = new Color( 185, 178, 95 );
//    private static final Dimension PANEL_SIZE = new Dimension( 800, 200 );
    // TODO: Temporarily reduced size to accommodate reduced content, see other
    // todo markers in this file.
    private static final Dimension PANEL_SIZE = new Dimension( 850, 150 );
    private static final double EDGE_TO_ARROW_DISTANCE_X = 20;
    private static final double EDGE_TO_ARROW_DISTANCE_Y = 0;

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    public QuadEmissionFrequencyControlPanel( final PhotonAbsorptionModel model ){

        // Create the main background shape.
        final PNode backgroundNode = new PhetPPath( new RoundRectangle2D.Double(0, 0, PANEL_SIZE.getWidth(),
                PANEL_SIZE.getHeight(), 20, 20), BACKGROUND_COLOR );

        // Add the radio buttons that set the emission wavelength.
        final WavelengthSelectButtonNode microwaveSelectorNode =
            new WavelengthSelectButtonNode( GreenhouseResources.getString( "QuadWavelengthSelector.Microwave" ), model, GreenhouseConfig.microWavelength );
        final WavelengthSelectButtonNode infraredSelectorNode =
            new WavelengthSelectButtonNode( GreenhouseResources.getString( "QuadWavelengthSelector.Infrared" ), model, GreenhouseConfig.irWavelength );
        final WavelengthSelectButtonNode visibleLightSelectorNode =
            new WavelengthSelectButtonNode( GreenhouseResources.getString( "QuadWavelengthSelector.Visible" ), model, GreenhouseConfig.visibleWaveLength );
        final WavelengthSelectButtonNode ultravioletSelectorNode =
            new WavelengthSelectButtonNode( GreenhouseResources.getString( "QuadWavelengthSelector.Ultraviolet" ), model, GreenhouseConfig.uvWavelength );

        // Put all the buttons into a button group.  Without this, for some
        // reason, the individual buttons will toggle to the off state if
        // pressed twice in a row.
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( microwaveSelectorNode.getButton() );
        buttonGroup.add( infraredSelectorNode.getButton() );
        buttonGroup.add( visibleLightSelectorNode.getButton() );
        buttonGroup.add( ultravioletSelectorNode.getButton() );

        // Create a "panel" sort of node that contains all the selector
        // buttons, then position it on the main node.
        final PNode wavelengthSelectorPanelNode = new PNode();
        double interSelectorSpacing = ( PANEL_SIZE.getWidth() - microwaveSelectorNode.getFullBoundsReference().width -
                infraredSelectorNode.getFullBoundsReference().width -
                visibleLightSelectorNode.getFullBoundsReference().width -
                ultravioletSelectorNode.getFullBoundsReference().width ) / 5;
        interSelectorSpacing = Math.max( interSelectorSpacing, 0 ); // Don't allow less than 0.
        microwaveSelectorNode.setOffset( interSelectorSpacing, 0 );
        wavelengthSelectorPanelNode.addChild( microwaveSelectorNode );
        infraredSelectorNode.setOffset( microwaveSelectorNode.getFullBoundsReference().getMaxX() + interSelectorSpacing, 0 );
        wavelengthSelectorPanelNode.addChild( infraredSelectorNode );
        visibleLightSelectorNode.setOffset(  infraredSelectorNode.getFullBoundsReference().getMaxX() + interSelectorSpacing, 0 );
        wavelengthSelectorPanelNode.addChild( visibleLightSelectorNode );
        ultravioletSelectorNode.setOffset( visibleLightSelectorNode.getFullBoundsReference().getMaxX() + interSelectorSpacing, 0 );
        wavelengthSelectorPanelNode.addChild( ultravioletSelectorNode );
        wavelengthSelectorPanelNode.setOffset( 0, 10 );

        /*
         * TODO: 1/13/2011 - Kelly L has suggested removing the spectrum due
         * to confusion shown by interviewees - they interpreted it as the
         * emission spectrum of hydrogen.  Remove permanently if and when this
         * decision is finalized.
        // Create a data structure that maps the wavelengths to the x
        // positions of their selectors.  This is needed by the spectrum node
        // in order to create a visual connection between the selection button
        // and the selected range within the spectrum.
        HashMap<Double, Double> mapWavelengthToXPos = new HashMap<Double, Double>(){{
                put( GreenhouseConfig.microWavelength, wavelengthSelectorPanelNode.getXOffset() + microwaveSelectorNode.getFullBoundsReference().getCenterX() );
                put( GreenhouseConfig.irWavelength, wavelengthSelectorPanelNode.getXOffset() + infraredSelectorNode.getFullBoundsReference().getCenterX() );
                put( GreenhouseConfig.visibleWaveLength, wavelengthSelectorPanelNode.getXOffset() + visibleLightSelectorNode.getFullBoundsReference().getCenterX() );
                put( GreenhouseConfig.uvWavelength, wavelengthSelectorPanelNode.getXOffset() + ultravioletSelectorNode.getFullBoundsReference().getCenterX() );
        }};

        // Create the node that represents the spectrum.
        SpectrumNode spectrumNode = new SpectrumNode( (int) ( PANEL_SIZE.getWidth() * 0.9 ),
                (int) ( PANEL_SIZE.getHeight() * 0.45 ), model, mapWavelengthToXPos );
        spectrumNode.setOffset( PANEL_SIZE.getWidth() / 2 - spectrumNode.getFullBoundsReference().width / 2,
                wavelengthSelectorPanelNode.getFullBoundsReference().getMaxY() );

        // Add the caption.
        PText title = new PText( GreenhouseResources.getString( "QuadWavelengthSelector.PhotonEnergy" ) );
        title.setFont( new PhetFont( 28 ) );
        title.setOffset( PANEL_SIZE.getWidth() / 2 - title.getFullBoundsReference().width / 2,
                PANEL_SIZE.getHeight() - title.getFullBoundsReference().height - 15 );
        backgroundNode.addChild( title );
         */

        // Add the energy arrow.
        // TODO: i18n
        EnergyArrow energyArrow = new EnergyArrow( "Increasing frequency and energy", model ){{
            centerFullBoundsOnPoint( backgroundNode.getFullBoundsReference().getCenterX(),
                    PANEL_SIZE.getHeight() - getFullBoundsReference().height / 2 - 10 );
        }};
        backgroundNode.addChild( energyArrow );

        // Add everything in the needed order.
        addChild( backgroundNode );
        backgroundNode.addChild( wavelengthSelectorPanelNode );
//        addChild( spectrumNode );
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------

    /**
     * Convenience class that puts a radio button with a caption into a PNode.
     */
    private static class WavelengthSelectButtonNode extends PNode {

        private static final Font LABEL_FONT  = new PhetFont( 16 );
        JRadioButton button;

        public WavelengthSelectButtonNode( final String text, final PhotonAbsorptionModel photonAbsorptionModel, final double wavelength ){

            // Add the radio button.
            button = new JRadioButton(){{
                setFont( LABEL_FONT );
                setText( text );
                setBackground( BACKGROUND_COLOR );
                setOpaque( false );
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        photonAbsorptionModel.setEmittedPhotonWavelength( wavelength );
                    }
                });
                photonAbsorptionModel.addListener( new PhotonAbsorptionModel.Adapter() {
                    @Override
                    public void emittedPhotonWavelengthChanged() {
                        setSelected( photonAbsorptionModel.getEmittedPhotonWavelength() == wavelength );
                    }
                } );
                // Set initial state.
                setSelected( photonAbsorptionModel.getEmittedPhotonWavelength() == wavelength );
            }};

            // TODO: We received some feedback that the buttons were a little
            // small, so the following scaling operation makes them bigger
            // relative to the font.  Keep or discard once reviewed.  Note
            // that the scaling factor combined with the font size control
            // the relative size of the button.
            PSwing buttonNode = new PSwing( button ){{
                setScale( 1.5 );
            }};
            addChild( buttonNode );

            // Add an image of a photon.
            PhotonNode photonNode = new PhotonNode( wavelength );
            photonNode.addInputEventListener( new PBasicInputEventHandler(){
                @Override
                public void mouseClicked( PInputEvent event ) {
                    photonAbsorptionModel.setEmittedPhotonWavelength( wavelength );
                }
            });
            addChild( photonNode );

            // Do the layout.  The photon is above the radio button, centered.
            photonNode.setOffset(
                    buttonNode.getFullBoundsReference().width / 2,
                    photonNode.getFullBoundsReference().height / 2 );
            buttonNode.setOffset( 0, photonNode.getFullBoundsReference().height * 0.6 );

        }

        public JRadioButton getButton(){
            return button;
        }
    }

    /**
     * Class that defines the spectrum that is shown on this control panel.
     * This consists of the spectrum and of a marker that exists below the
     * actual spectrum that shows the currently selected range.
     *
     * @author John Blanco
     */
    public static class SpectrumNode extends PNode {

        private static final double MIN_WAVELENGTH = 1E-10; // In meters.
        private static final double MAX_WAVELENGTH = 10; // In meters
        private static final double SPECTRUM_HEIGHT_PROPORTION = 0.5;
        private static final Stroke MARKER_STROKE = new BasicStroke( 5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        private static final Color MARKER_COLOR = new Color( 139, 129, 76 );

        // Static data structure that maps the frequency values used in the
        // model to frequency ranges depicted on this spectrum.
        private static final HashMap<Double, DoubleRange> mapFreqToRange = new HashMap<Double, DoubleRange>(){{
            put( GreenhouseConfig.microWavelength, new DoubleRange(1E-3, 1));
            put( GreenhouseConfig.irWavelength, new DoubleRange(780E-9, 1E-3));
            put( GreenhouseConfig.visibleWaveLength, new DoubleRange(380E-9, 780E-9));
            put( GreenhouseConfig.uvWavelength, new DoubleRange(1E-9, 380E-9));
        }};

        private final PhotonAbsorptionModel model;
        private final PPath markerNode = new PhetPPath( MARKER_STROKE, MARKER_COLOR );
        private final PImage spectrumImageNode;
        private final double height;
        HashMap<Double, Double> mapWavelengthToXPos;

        /**
         * Constructor.
         * @param mapWavelengthToXPos
         */
        public SpectrumNode( int width, int height, PhotonAbsorptionModel model, HashMap<Double, Double> mapWavelengthToXPos ){
            this.model = model;
            this.height = height;
            this.mapWavelengthToXPos = mapWavelengthToXPos;

            model.addListener( new PhotonAbsorptionModel.Adapter(){
                @Override
                public void emittedPhotonWavelengthChanged() {
                    updateMarker();
                }
            });

            spectrumImageNode = new PImage( new ExponentialGrowthSpectrumImageFactory().createHorizontalSpectrum( width,
                    (int)( height * SPECTRUM_HEIGHT_PROPORTION ), MIN_WAVELENGTH * 1E9, MAX_WAVELENGTH * 1E9) );

            // The spectrum image factory creates a spectrum by default that
            // is oriented from short to long wavelengths, and we need the
            // opposite, so we flip it here.  Note that this makes the offset
            // go to the lower right corner, so positioning becomes a bit
            // tricky.
            spectrumImageNode.rotateAboutPoint( Math.PI, spectrumImageNode.getFullBoundsReference().getCenter2D() );
            spectrumImageNode.setOffset( spectrumImageNode.getOffset().getX(), height );

            addChild( spectrumImageNode );
            addChild( markerNode );

            updateMarker();
        }

        /**
         * Update the marker, which reflects the currently selected band of
         * the spectrum.  The marker consists of a spectrum range indicator
         * and a line connecting it to the selector.
         */
        private void updateMarker(){
            double wavelengthSetting = model.getEmittedPhotonWavelength();
            DoubleRange wavelengthRange = mapFreqToRange.get( wavelengthSetting );
            double totalMarkerHeight = height - spectrumImageNode.getFullBoundsReference().height;

            // Add the range indicator to the path.
            double rangeIndicatorMinY = totalMarkerHeight * 2 / 3;
            double rangeIndicatorMaxY = totalMarkerHeight;
            double rangeIndicatorMinX = mapWavelengthToNormalizedXPos( wavelengthRange.getMin() ) * spectrumImageNode.getFullBoundsReference().width;
            double rangeIndicatorMaxX = mapWavelengthToNormalizedXPos( wavelengthRange.getMax() ) * spectrumImageNode.getFullBoundsReference().width;
            DoubleGeneralPath markerPath = new DoubleGeneralPath();
            markerPath.moveTo( rangeIndicatorMinX, rangeIndicatorMaxY );
            markerPath.lineTo( rangeIndicatorMinX, rangeIndicatorMinY );
            markerPath.lineTo( rangeIndicatorMaxX, rangeIndicatorMinY );
            markerPath.lineTo( rangeIndicatorMaxX, rangeIndicatorMaxY );

            // Now add the line that connects the range indicator to the selector.
            double rangeIndicatorMiddleX = rangeIndicatorMinX + ( ( rangeIndicatorMaxX - rangeIndicatorMinX  ) / 2 );
            assert mapWavelengthToXPos.containsKey( model.getEmittedPhotonWavelength() ); // If not there, we can't draw the connecting line.
            Point2D connectingLineEndPoint = parentToLocal( new Point2D.Double( mapWavelengthToXPos.get( model.getEmittedPhotonWavelength() ), 0 ) );
            markerPath.moveTo( rangeIndicatorMiddleX, rangeIndicatorMinY );
            markerPath.lineTo( rangeIndicatorMiddleX, rangeIndicatorMinY - totalMarkerHeight / 3 );
            markerPath.lineTo( connectingLineEndPoint.getX(), rangeIndicatorMinY - totalMarkerHeight / 3 );
            markerPath.lineTo( connectingLineEndPoint.getX(), 0 );

            // Set the node to the path that we just calculated.
            markerNode.setPathTo( markerPath.getGeneralPath() );
        }

        private double mapWavelengthToNormalizedXPos( double wavelength ){
            return 1 - Math.log( wavelength / MIN_WAVELENGTH ) / Math.log( MAX_WAVELENGTH / MIN_WAVELENGTH );
        }
    }

    /**
     * Class that defines the "energy arrow", which is an arrow that depicts
     * the direction of increasing energy.
     *
     * @author John Blanco
     */
    private static class EnergyArrow extends PNode {

        private static final double ARROW_LENGTH = 700;
        private static final double ARROW_HEAD_HEIGHT = 60;
        private static final double ARROW_HEAD_WIDTH = 60;
        private static final double ARROW_TAIL_WIDTH = 30;
//        private static final Paint NORMAL_COLOR = new GradientPaint( 0, (float) ( -ARROW_HEAD_HEIGHT / 2 ),
//                new Color( 180, 82, 205 ), 0, (float) ( ARROW_HEAD_HEIGHT / 2 ), Color.WHITE );
        private static final Paint NORMAL_COLOR = new GradientPaint( 0, (float) ( -ARROW_HEAD_HEIGHT / 2 ),
                new Color( 180, 82, 205 ), 0, (float)(ARROW_HEAD_HEIGHT * 0.25), Color.WHITE );

        public EnergyArrow( String captionText, final PhotonAbsorptionModel model ){
            // Create and add the arrow.  The arrow points to the right.
            Point2D headPoint, tailPoint;
            headPoint = new Point2D.Double(ARROW_LENGTH, 0);
            tailPoint = new Point2D.Double(0, 0);
            final ArrowNode arrowNode = new ArrowNode( tailPoint, headPoint, ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH ){{
                setPaint( NORMAL_COLOR );
                setStroke( new BasicStroke( 3 ) );
            }};
            addChild( arrowNode );

            // Create and add the caption.
            HTMLNode caption = new HTMLNode( captionText );
            caption.setFont( new PhetFont( 20, true ) );
            caption.centerFullBoundsOnPoint( arrowNode.getFullBoundsReference().getCenterX(), arrowNode.getFullBoundsReference().getCenterY() );
            addChild( caption );
        }

        /**
         * Increase the current wavelength setting of the model.  Note that
         * the implementation is less than ideal because it requires knowledge
         * of the available wavelengths.  It may be desirable to make this
         * more general some day.
         */
        private void increaseWavelength( PhotonAbsorptionModel model ){
            if (model.getEmittedPhotonWavelength() == GreenhouseConfig.uvWavelength){
                model.setEmittedPhotonWavelength( GreenhouseConfig.visibleWaveLength );
            }
            else if ( model.getEmittedPhotonWavelength() == GreenhouseConfig.visibleWaveLength ){
                model.setEmittedPhotonWavelength( GreenhouseConfig.irWavelength );
            }
            else if ( model.getEmittedPhotonWavelength() == GreenhouseConfig.irWavelength ){
                model.setEmittedPhotonWavelength( GreenhouseConfig.microWavelength );
            }
        }

        /**
         * Decrease the current wavelength setting of the model.  Note that
         * the implementation is less than ideal because it requires knowledge
         * of the available wavelengths.  It may be desirable to make this
         * more general some day.
         */
        private void decreaseWavelength( PhotonAbsorptionModel model ){
            if ( model.getEmittedPhotonWavelength() == GreenhouseConfig.microWavelength ){
                model.setEmittedPhotonWavelength( GreenhouseConfig.irWavelength );
            }
            else if ( model.getEmittedPhotonWavelength() == GreenhouseConfig.irWavelength ){
                model.setEmittedPhotonWavelength( GreenhouseConfig.visibleWaveLength );
            }
            else if (model.getEmittedPhotonWavelength() == GreenhouseConfig.visibleWaveLength ){
                model.setEmittedPhotonWavelength( GreenhouseConfig.uvWavelength );
            }
        }
    }


    /**
     * Class that defines the "energy arrow", which is an arrow on each side
     * of the chart that indicates increasing or decreasing energy amounts.
     *
     * TODO: This is an older version of the energy arrow class that was used
     * initially, when it was specified that there be two energy arrows on the
     * control panel.  Get rid of this once we decide for certain to replace
     * it with a single arrow.
     *
     * @author John Blanco
     */
    private static class EnergyArrow2 extends PNode {

        private static final double ARROW_LENGTH = 60;
        private static final double ARROW_HEAD_HEIGHT = 10;
        private static final double ARROW_HEAD_WIDTH = 12;
        private static final double ARROW_TAIL_WIDTH = 1;
        private static final Color NORMAL_COLOR = Color.BLACK;
        private static final Color HILITE_COLOR = Color.YELLOW;

        public enum Direction { POINTS_LEFT, POINTS_RIGHT };

        public EnergyArrow2( String captionText, final Direction direction, final PhotonAbsorptionModel model ){
            HTMLNode caption = new HTMLNode( captionText );
            caption.setFont( new PhetFont( 18, true ) );
            addChild( caption );

            Point2D headPoint, tailPoint;
            double arrowXPos;
            if ( direction == Direction.POINTS_LEFT ){
                // Arrow points to the left.
                headPoint = new Point2D.Double(0, 0);
                tailPoint = new Point2D.Double(ARROW_LENGTH, 0);
                caption.setOffset( ARROW_LENGTH + 10, ARROW_HEAD_WIDTH / 2 - caption.getFullBoundsReference().height / 2 );
                arrowXPos = 0;
            }
            else{
                // Arrow points to the right.
                headPoint = new Point2D.Double(ARROW_LENGTH, 0);
                tailPoint = new Point2D.Double(0, 0);
                caption.setOffset( 0, ARROW_HEAD_WIDTH / 2 - caption.getFullBoundsReference().height / 2 );
                arrowXPos = caption.getFullBoundsReference().width + 5;
            }

            final ArrowNode arrowNode = new ArrowNode( tailPoint, headPoint, ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH ){{
                setPaint( NORMAL_COLOR );
                setStroke( new BasicStroke( 3 ) );
            }};

            // ArrowNodes, by default, are set up such that the horizontal
            // left and vertical center of the arrow is at (0, 0).  This makes
            // it tricky in this particular application to position the
            // overall node where we want it, so the following line shifts the
            // arrow such that the (0, 0) position for this entire node is its
            // upper left corner (which is fairly typical amongst PNodes
            // anyway).
            arrowNode.setOffset( arrowXPos, arrowNode.getHeight() / 2 );

            // Add the arrow node as a child.
            addChild( arrowNode );
        }


        /**
         * Increase the current wavelength setting of the model.  Note that
         * the implementation is less than ideal because it requires knowledge
         * of the available wavelengths.  It may be desirable to make this
         * more general some day.
         */
        private void increaseWavelength( PhotonAbsorptionModel model ){
            if (model.getEmittedPhotonWavelength() == GreenhouseConfig.uvWavelength){
                model.setEmittedPhotonWavelength( GreenhouseConfig.visibleWaveLength );
            }
            else if ( model.getEmittedPhotonWavelength() == GreenhouseConfig.visibleWaveLength ){
                model.setEmittedPhotonWavelength( GreenhouseConfig.irWavelength );
            }
            else if ( model.getEmittedPhotonWavelength() == GreenhouseConfig.irWavelength ){
                model.setEmittedPhotonWavelength( GreenhouseConfig.microWavelength );
            }
        }

        /**
         * Decrease the current wavelength setting of the model.  Note that
         * the implementation is less than ideal because it requires knowledge
         * of the available wavelengths.  It may be desirable to make this
         * more general some day.
         */
        private void decreaseWavelength( PhotonAbsorptionModel model ){
            if ( model.getEmittedPhotonWavelength() == GreenhouseConfig.microWavelength ){
                model.setEmittedPhotonWavelength( GreenhouseConfig.irWavelength );
            }
            else if ( model.getEmittedPhotonWavelength() == GreenhouseConfig.irWavelength ){
                model.setEmittedPhotonWavelength( GreenhouseConfig.visibleWaveLength );
            }
            else if (model.getEmittedPhotonWavelength() == GreenhouseConfig.visibleWaveLength ){
                model.setEmittedPhotonWavelength( GreenhouseConfig.uvWavelength );
            }
        }
    }
}
