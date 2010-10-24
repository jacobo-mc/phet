/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view.meters;

import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.capacitorlab.CLImages;
import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit;
import edu.colorado.phet.capacitorlab.model.EFieldDetector;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.view.PlusNode;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.BoundedDragHandler;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.Vector2DNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Electric-field (E-field) detector.
 * This is not a node, but it manages the connections and interactivity of a set of related nodes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EFieldDetectorView {
    
    private static final Font TITLE_FONT = new PhetFont( Font.BOLD, 18 );
    private static final Color TITLE_COLOR = Color.WHITE;
    
    private static final Color BODY_COLOR = Color.BLACK;
    private static final double BODY_CORNER_RADIUS = 15;
    private static final int BODY_X_MARGIN = 5;
    private static final int BODY_Y_MARGIN = 5;
    private static final int BODY_X_SPACING = 2;
    private static final int BODY_Y_SPACING = 4;
    
    private static final Color WIRE_COLOR = Color.BLACK;
    private static final Stroke WIRE_STROKE = new BasicStroke( 3f );
        
    private static final PDimension VECTOR_DISPLAY_SIZE = new PDimension( 200, 175 );
    private static final Color VECTOR_DISPLAY_BACKGROUND = Color.WHITE;
    private static final double VECTOR_REFERENCE_MAGNITUDE = BatteryCapacitorCircuit.getMaxPlatesDielectricEField();
    private static final double VECTOR_REFERENCE_LENGTH = 0.7 * VECTOR_DISPLAY_SIZE.getHeight();
    private static final Dimension VECTOR_ARROW_HEAD_SIZE = new Dimension( 30, 20 );
    private static final int VECTOR_ARROW_TAIL_WIDTH = 10;
    
    private static final Font VALUE_FONT = new PhetFont( 14 );
    private static final NumberFormat VALUE_FORMAT = new DecimalFormat( "0" );
    
    private static final Font CONTROL_FONT = new PhetFont( Font.BOLD, 16 );
    private static final Color CONTROL_COLOR = Color.WHITE;

    private final BodyNode bodyNode;
    private final ProbeNode probeNode;
    private final CubicWireNode wireNode;
    
    public EFieldDetectorView( EFieldDetector detector, ModelViewTransform mvt, PNode dragBoundsNode, boolean dev ) {
        
        bodyNode = new BodyNode( detector );
        bodyNode.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent event ) {
                if ( event.getPropertyName().equals( PNode.PROPERTY_VISIBLE ) ) {
                    probeNode.setVisible( bodyNode.getVisible() );
                    wireNode.setVisible( bodyNode.getVisible() );
                }
                else if ( event.getPropertyName().equals( PNode.PROPERTY_FULL_BOUNDS ) ) {
                    updateWire();
                }
            }
        });
        
        probeNode = new ProbeNode( detector, mvt, dev );
//XXX        probeNode.rotate( -mvt.getYaw() ); // rotated so that it's sticking into the capacitor
        probeNode.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent event ) {
                if ( event.getPropertyName().equals( PNode.PROPERTY_FULL_BOUNDS ) ) {
                    updateWire();
                }
            }
        });
        
        wireNode = new CubicWireNode( WIRE_COLOR, -25, 100 ); //XXX constants
        wireNode.setPickable( false );
        
        // interactivity
        bodyNode.addInputEventListener( new CursorHandler() );
        probeNode.addInputEventListener( new CursorHandler() );
        bodyNode.addInputEventListener( new BoundedDragHandler( bodyNode, dragBoundsNode ) );
        probeNode.addInputEventListener( new BoundedDragHandler( probeNode, dragBoundsNode ) );
        
        updateWire();
    }
    
    private void updateWire() {
        // connect to left center of body
        double x = bodyNode.getFullBoundsReference().getMinX();
        double y = bodyNode.getFullBoundsReference().getCenterY();
        Point2D bodyConnectionPoint = new Point2D.Double( x, y );
        // connect to bottom center of probe
        x = probeNode.getFullBoundsReference().getCenterX();
        y = probeNode.getFullBoundsReference().getMaxY();
        Point2D probeConnectionPoint = new Point2D.Double( x, y );
        wireNode.setEndPoints( bodyConnectionPoint, probeConnectionPoint );
    }
    
    public PNode getBodyNode() {
        return bodyNode;
    }
    
    public PNode getProbeNode() {
        return probeNode;
    }
    
    public PNode getWireNode() {
        return wireNode;
    }
    
    public void setShowVectorsPanelVisible( boolean visible ) {
        bodyNode.setShowVectorsPanelVisible( visible );
    }
    
    /**
     * Body of the meter, origin at upper-left corner of bounding rectangle.
     */
    private static class BodyNode extends PhetPNode {
        
        private final PSwing showVectorsPSwing;
        private final VectorDisplayNode vectorDisplayNode;
        
        public BodyNode( final EFieldDetector detector ) {
            
            // title that appears at the top
            PText titleNode = new PText( CLStrings.ELECTRIC_FIELD );
            titleNode.setTextPaint( TITLE_COLOR );
            titleNode.setFont( TITLE_FONT );
            
            // close button
            PImage closeButtonNode = new PImage( CLImages.CLOSE_BUTTON );
            closeButtonNode.addInputEventListener( new CursorHandler() );
            closeButtonNode.addInputEventListener( new PBasicInputEventHandler() {
                @Override
                public void mouseReleased( PInputEvent event ) {
                    BodyNode.this.setVisible( false );
                }
            });
            
            // display area for vectors and values
            vectorDisplayNode = new VectorDisplayNode( detector, VECTOR_DISPLAY_SIZE );

            // Vector controls
            ShowVectorsPanel showVectorsPanel = new ShowVectorsPanel( detector );
            showVectorsPSwing = new PSwing( showVectorsPanel );
            
            // Show Values check box
            final JCheckBox showValuesCheckBox = new JCheckBox( CLStrings.SHOW_VALUES );
            showValuesCheckBox.setFont( CONTROL_FONT );
            showValuesCheckBox.setForeground( CONTROL_COLOR );
            showValuesCheckBox.setSelected( detector.isValuesVisible() );
            showValuesCheckBox.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    detector.setValuesVisible( showValuesCheckBox.isSelected() );
                }
            });
            detector.addValuesVisibleListener( new SimpleObserver() {
                public void update() {
                    showValuesCheckBox.setSelected( detector.isValuesVisible() );
                }
            });
            PSwing showValuesPSwing = new PSwing( showValuesCheckBox );
            
            // background
            double maxControlWidth = Math.max( showVectorsPSwing.getFullBoundsReference().getWidth(), showValuesPSwing.getFullBoundsReference().getWidth() );
            double width = maxControlWidth + vectorDisplayNode.getFullBoundsReference().getWidth() + ( 2 * BODY_X_MARGIN ) + BODY_X_SPACING;
            double height = titleNode.getFullBoundsReference().getHeight() + BODY_Y_SPACING + Math.max( showVectorsPSwing.getFullBoundsReference().getHeight(), vectorDisplayNode.getFullBoundsReference().getHeight() ) + ( 2 * BODY_Y_MARGIN );
            PPath backgroundNode = new PPath( new RoundRectangle2D.Double( 0, 0, width, height, BODY_CORNER_RADIUS, BODY_CORNER_RADIUS ) );
            backgroundNode.setPaint( BODY_COLOR );
            backgroundNode.setStroke( null );
            
            // rendering order
            addChild( backgroundNode );
            addChild( titleNode );
            addChild( closeButtonNode );
            addChild( showVectorsPSwing );
            addChild( showValuesPSwing );
            addChild( vectorDisplayNode );
            
            // layout
            double x = 0;
            double y = 0;
            backgroundNode.setOffset( x, y );
            // title
            x = backgroundNode.getFullBoundsReference().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 );
            y = BODY_Y_MARGIN;
            titleNode.setOffset( x, y );
            // close button
            x = backgroundNode.getFullBoundsReference().getMaxX() - closeButtonNode.getFullBoundsReference().getWidth() - BODY_X_MARGIN;
            y = backgroundNode.getFullBoundsReference().getMinY() + BODY_Y_MARGIN;
            closeButtonNode.setOffset( x, y );
            // upper-left controls
            x = BODY_X_MARGIN;
            y = titleNode.getFullBoundsReference().getMaxY() + BODY_Y_SPACING;
            showVectorsPSwing.setOffset( x, y );
            // lower-left controls
            x = BODY_X_MARGIN;
            y = backgroundNode.getFullBoundsReference().getMaxY() - showValuesPSwing.getFullBoundsReference().getHeight() - BODY_Y_MARGIN;
            showValuesPSwing.setOffset( x, y );
            // vectors
            x = BODY_X_MARGIN + maxControlWidth + BODY_X_SPACING;
            y = showVectorsPSwing.getYOffset();
            vectorDisplayNode.setOffset( x, y );
        }
        
        public void setShowVectorsPanelVisible( boolean visible ) {
            showVectorsPSwing.setVisible( visible );
        }
    }
    
    /*
     * Panel with check boxes for vectors.
     */
    private static class ShowVectorsPanel extends GridPanel {
        
        private final JCheckBox plateCheckBox, dielectricCheckBox, sumCheckBox;
        
        public ShowVectorsPanel( final EFieldDetector detector ) {
            
            setBackground( BODY_COLOR );
            
            JLabel showVectorsLabel = new JLabel( CLStrings.SHOW_VECTORS );
            showVectorsLabel.setFont( CONTROL_FONT );
            showVectorsLabel.setForeground( CONTROL_COLOR );
            
            plateCheckBox = new JCheckBox( CLStrings.PLATE );
            plateCheckBox.setFont( CONTROL_FONT );
            plateCheckBox.setForeground( CLPaints.PLATE_EFIELD_VECTOR );
            plateCheckBox.setSelected( detector.isPlateVisible() );
            plateCheckBox.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    detector.setPlateVisible( plateCheckBox.isSelected() );
                }
            });
           
            dielectricCheckBox = new JCheckBox( CLStrings.DIELECTRIC );
            dielectricCheckBox.setFont( CONTROL_FONT );
            dielectricCheckBox.setForeground( CLPaints.DIELECTRIC_EFIELD_VECTOR );
            dielectricCheckBox.setSelected( detector.isDielectricVisible() );
            dielectricCheckBox.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    detector.setDielectricVisible( dielectricCheckBox.isSelected() );
                }
            });
            
            sumCheckBox = new JCheckBox( CLStrings.SUM );
            sumCheckBox.setFont( CONTROL_FONT );
            sumCheckBox.setForeground( CLPaints.SUM_EFIELD_VECTOR );
            sumCheckBox.setSelected( detector.isSumVectorVisible() );
            sumCheckBox.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    detector.setSumVisible( sumCheckBox.isSelected() );
                }
            });
            
            // layout
            setAnchor( Anchor.WEST );
            int row = 0;
            int column = 0;
            add( showVectorsLabel, row++, column );
            add( plateCheckBox, row++, column );
            add( dielectricCheckBox, row++, column );
            add( sumCheckBox, row++, column );
            
            // listen to detector properties
            detector.addPlateVisibleListener( new SimpleObserver() {
                public void update() {
                    plateCheckBox.setSelected( detector.isPlateVisible() );
                }
            });
            detector.addDielectricVisibleListener( new SimpleObserver() {
                public void update() {
                    dielectricCheckBox.setSelected( detector.isDielectricVisible() );
                }
            });
            detector.addSumVisibleListener( new SimpleObserver() {
                public void update() {
                    sumCheckBox.setSelected( detector.isSumVectorVisible() );
                }
            });
        }
    }
    
    /*
     * Rectangular area where the vectors are displayed.
     * Vectors are clipped to this area.
     */
    private static final class VectorDisplayNode extends PComposite {
        
        private final PPath backgroundNode;
        private final FieldVectorLabelNode plateLabelNode, dielectricLabelNode, sumLabelNode;
        private final FieldVectorNode plateVectorNode, dielectricVectorNode, sumVectorNode;
        private final FieldValueNode plateValueNode, dielectricValueNode, sumValueNode;
        
        public VectorDisplayNode( final EFieldDetector detector, PDimension size ) {
            
            // background
            backgroundNode = new PPath( new Rectangle2D.Double( 0, 0, size.getWidth(), size.getHeight() ) );
            backgroundNode.setPaint( VECTOR_DISPLAY_BACKGROUND );
            backgroundNode.setStroke( null );
            addChild( backgroundNode );
            
            // labels
            plateLabelNode = new FieldVectorLabelNode( CLStrings.PLATE );
            dielectricLabelNode = new FieldVectorLabelNode( CLStrings.DIELECTRIC );
            sumLabelNode = new FieldVectorLabelNode( CLStrings.SUM );
            
            // vectors
            plateVectorNode = new FieldVectorNode( detector.getPlateVector(), CLPaints.PLATE_EFIELD_VECTOR );
            dielectricVectorNode = new FieldVectorNode( detector.getDielectricVector(), CLPaints.DIELECTRIC_EFIELD_VECTOR );
            sumVectorNode = new FieldVectorNode( detector.getSumVector(), CLPaints.SUM_EFIELD_VECTOR );
            
            // values
            plateValueNode = new FieldValueNode();
            dielectricValueNode = new FieldValueNode();
            sumValueNode = new FieldValueNode();
            
            // rendering order
            addChild( plateLabelNode );
            addChild( dielectricLabelNode );
            addChild( sumLabelNode );
            addChild( plateVectorNode );
            addChild( dielectricVectorNode );
            addChild( sumVectorNode );
            addChild( plateValueNode );
            addChild( dielectricValueNode );
            addChild( sumValueNode );
            
            // static layout
            backgroundNode.setOffset( 0, 0 );
            
            // listen to detector properties
            detector.addPlateVectorListener( new SimpleObserver() {
                public void update() {
                    plateVectorNode.setXY( 0, detector.getPlateVector() );
                    plateValueNode.setValue( detector.getPlateVector() );
                    updateLayout();
                }
            });
            detector.addDielectricVectorListener( new SimpleObserver() {
                public void update() {
                    dielectricVectorNode.setXY( 0, -detector.getDielectricVector() ); //XXX why is this sign change needed?
                    dielectricValueNode.setValue( detector.getDielectricVector() );
                    updateLayout();
                }
            });
            detector.addSumVectorListener( new SimpleObserver() {
                public void update() {
                    sumVectorNode.setXY( 0, detector.getSumVector() ); 
                    sumValueNode.setValue( detector.getSumVector() );
                    updateLayout();
                }
            });
            detector.addPlateVisibleListener( new SimpleObserver() {
                public void update() {
                    plateLabelNode.setVisible( detector.isPlateVisible() );
                    plateVectorNode.setVisible( detector.isPlateVisible() );
                    plateValueNode.setVisible( detector.isPlateVisible() && detector.isValuesVisible() );
                }
            });
            detector.addDielectricVisibleListener( new SimpleObserver() {
                public void update() {
                    dielectricLabelNode.setVisible( detector.isDielectricVisible() );
                    dielectricVectorNode.setVisible( detector.isDielectricVisible() );
                    dielectricValueNode.setVisible( detector.isDielectricVisible() && detector.isValuesVisible() );
                }
            });
            detector.addSumVisibleListener( new SimpleObserver() {
                public void update() {
                    sumLabelNode.setVisible( detector.isSumVectorVisible() );
                    sumVectorNode.setVisible( detector.isSumVectorVisible() );
                    sumValueNode.setVisible( detector.isSumVectorVisible() && detector.isValuesVisible() );
                }
            });
            detector.addValuesVisibleListener( new SimpleObserver() {
                public void update() {
                    plateValueNode.setVisible( detector.isPlateVisible() && detector.isValuesVisible() );
                    dielectricValueNode.setVisible( detector.isDielectricVisible() && detector.isValuesVisible() );
                    sumValueNode.setVisible( detector.isSumVectorVisible() && detector.isValuesVisible() );
                }
            });
            
            updateLayout();
        }
        
        // dynamic layout
        private void updateLayout() {
            
            //XXX replace this mess, there's a lot of duplicate code here
            
            double x, y;
            
            // vectors
            {
                final double xSpacing = 45; //XXX constant, horizontal spacing between plate and dielectric vector centers
                
                // plate vector is vertically centered
                x = backgroundNode.getFullBoundsReference().getCenterX() - xSpacing;
                y = backgroundNode.getFullBoundsReference().getCenterY() - ( plateVectorNode.getFullBoundsReference().getHeight() / 2 ) - PNodeLayoutUtils.getOriginYOffset( plateVectorNode );
                plateVectorNode.setOffset( x, y );
                
                // sum vector is aligned with tail of plate vector
                x = backgroundNode.getFullBoundsReference().getCenterX() + xSpacing;
                y = plateVectorNode.getYOffset();
                sumVectorNode.setOffset( x, y );
                
                // dielectric vector fills in space above or below sum vector
                x = sumVectorNode.getXOffset();
                if ( dielectricVectorNode.getVector().getY() > 0 ) {
                    y = sumVectorNode.getFullBoundsReference().getMinY() - dielectricVectorNode.getFullBoundsReference().getHeight(); // above
                }
                else {
                    y = sumVectorNode.getFullBoundsReference().getMaxY() + dielectricVectorNode.getFullBoundsReference().getHeight(); // below
                }
                dielectricVectorNode.setOffset( x, y );
            }
            
            // labels and values, all placed at vector tails, horizontally centered
            {
                final double ySpacing = 2; // space between vector tail and label
                
                // plate label
                if ( plateVectorNode.getVector().getY() > 0 ) {
                    x = plateVectorNode.getFullBoundsReference().getCenterX() - ( plateValueNode.getFullBoundsReference().getWidth() / 2 );
                    y = plateVectorNode.getFullBoundsReference().getMinY() - plateValueNode.getFullBoundsReference().getHeight() - ySpacing;
                    plateValueNode.setOffset( x, y );
                    x = plateVectorNode.getFullBoundsReference().getCenterX() - ( plateLabelNode.getFullBoundsReference().getWidth() / 2 );
                    y = plateValueNode.getFullBoundsReference().getMinY() - plateLabelNode.getFullBoundsReference().getHeight() - 1;
                    plateLabelNode.setOffset( x, y );
                }
                else {
                    x = plateVectorNode.getFullBoundsReference().getCenterX() - ( plateLabelNode.getFullBoundsReference().getWidth() / 2 );
                    y = plateVectorNode.getFullBoundsReference().getMaxY() + ySpacing;
                    plateLabelNode.setOffset( x, y );
                    x = plateVectorNode.getFullBoundsReference().getCenterX() - ( plateValueNode.getFullBoundsReference().getWidth() / 2 );
                    y = plateLabelNode.getFullBoundsReference().getMaxY() + 1;
                    plateValueNode.setOffset( x, y );
                }
                
                // sum label
                if ( sumVectorNode.getVector().getY() > 0 ) {
                    x = sumVectorNode.getFullBoundsReference().getCenterX() - ( sumValueNode.getFullBoundsReference().getWidth() / 2 );
                    y = sumVectorNode.getFullBoundsReference().getMinY() - sumValueNode.getFullBoundsReference().getHeight() - ySpacing;
                    sumValueNode.setOffset( x, y );
                    x = sumVectorNode.getFullBoundsReference().getCenterX() - ( sumLabelNode.getFullBoundsReference().getWidth() / 2 );
                    y = sumValueNode.getFullBoundsReference().getMinY() - sumLabelNode.getFullBoundsReference().getHeight() - 1;
                    sumLabelNode.setOffset( x, y );
                }
                else {
                    x = sumVectorNode.getFullBoundsReference().getCenterX() - ( sumLabelNode.getFullBoundsReference().getWidth() / 2 );
                    y = sumVectorNode.getFullBoundsReference().getMaxY() + ySpacing;
                    sumLabelNode.setOffset( x, y );
                    x = sumVectorNode.getFullBoundsReference().getCenterX() - ( sumValueNode.getFullBoundsReference().getWidth() / 2 );
                    y = sumLabelNode.getFullBoundsReference().getMaxY() + 1;
                    sumValueNode.setOffset( x, y );
                }
                
                // dielectric label
                if ( dielectricVectorNode.getVector().getY() >= 0 ) {
                    x = dielectricVectorNode.getFullBoundsReference().getCenterX() - ( dielectricValueNode.getFullBoundsReference().getWidth() / 2 );
                    y = dielectricVectorNode.getFullBoundsReference().getMinY() - dielectricValueNode.getFullBoundsReference().getHeight() - ySpacing;
                    dielectricValueNode.setOffset( x, y );
                    x = dielectricVectorNode.getFullBoundsReference().getCenterX() - ( dielectricLabelNode.getFullBoundsReference().getWidth() / 2 );
                    y = dielectricValueNode.getFullBoundsReference().getMinY() - dielectricLabelNode.getFullBoundsReference().getHeight() - 1;
                    dielectricLabelNode.setOffset( x, y );
                }
                else {
                    x = dielectricVectorNode.getFullBoundsReference().getCenterX() - ( dielectricLabelNode.getFullBoundsReference().getWidth() / 2 );
                    y = dielectricVectorNode.getFullBoundsReference().getMaxY() + ySpacing;
                    dielectricLabelNode.setOffset( x, y );
                    x = dielectricVectorNode.getFullBoundsReference().getCenterX() - ( dielectricValueNode.getFullBoundsReference().getWidth() / 2 );
                    y = dielectricLabelNode.getFullBoundsReference().getMaxY() + 1;
                    dielectricValueNode.setOffset( x, y );
                }
            }
        }
    }
    
    //XXX combine FieldVectorLabelNode and FieldVectorNode to simplify layout code in VectorDisplayNode
    /*
     * Field vector label.
     */
    private static class FieldVectorLabelNode extends PText {
        public FieldVectorLabelNode( String text ) {
            super( text );
            setFont( new PhetFont( 15 ) );
        }
    }
    
    /*
     * Field vector.
     */
    private static class FieldVectorNode extends Vector2DNode {
        
        public FieldVectorNode( double value, Color color ) {
            super( 0, value, VECTOR_REFERENCE_MAGNITUDE, VECTOR_REFERENCE_LENGTH );
            setArrowFillPaint( color );
            setHeadSize( VECTOR_ARROW_HEAD_SIZE );
            setTailWidth( VECTOR_ARROW_TAIL_WIDTH );
        }
    }
    
    /*
     * Displays a numeric field value.
     */
    private static class FieldValueNode extends PText {
        
        public FieldValueNode() {
            setFont( VALUE_FONT );
            setValue( 0 );
        }
        
        public void setValue( double value ) {
            String valueString = VALUE_FORMAT.format( Math.abs( value ) );
            setText( MessageFormat.format( CLStrings.PATTERN_VALUE_UNITS, valueString, CLStrings.VOLTS_PER_METER ) );
        }
    }
    
    /*
     * Probe.
     * Origin is in the center of the probe's crosshairs, and the location of the crosshairs 
     * is dependent on the image file.  The only way to align the crosshairs and the origin
     * is via visual inspection. Running with -dev will add an additional node that allows 
     * you to visually check alignment.
     */
    private static class ProbeNode extends PhetPNode {
        
        public ProbeNode( final EFieldDetector detector, final ModelViewTransform mvt, boolean dev ) {
            super();
            
            PImage imageNode = new PImage( CLImages.EFIELD_PROBE );
            addChild( imageNode );
            double x = -imageNode.getFullBoundsReference().getWidth() / 2;
            double y = 0.078 * -imageNode.getFullBoundsReference().getHeight(); // multiplier is dependent on image file
            imageNode.setOffset( x, y );
            
            // Put a '+' at origin to check that probe image is offset properly.
            if ( dev ) {
                PlusNode plusNode = new PlusNode( 12, 2, Color.GRAY );
                addChild( plusNode );
            }
            
            detector.addProbeLocationListener( new SimpleObserver() {
                public void update() {
                    setOffset( mvt.modelToView( detector.getProbeLocationReference() ) );
                }
            });
            
            addInputEventListener( new ProbeDragHandler( this, detector, mvt ) );
        }
    }
    
    private static class ProbeDragHandler extends PDragSequenceEventHandler {
        
        private final ProbeNode probeNode;
        private final EFieldDetector detector;
        private final ModelViewTransform mvt;
        
        private double clickXOffset, clickYOffset;
        
        public ProbeDragHandler( ProbeNode probeNode, EFieldDetector detector, ModelViewTransform mvt ) {
            this.probeNode = probeNode;
            this.detector = detector;
            this.mvt = mvt;
        }
        
        @Override
        protected void startDrag( PInputEvent event ) {
            super.startDrag( event );
            Point2D pMouse = event.getPositionRelativeTo( probeNode.getParent() );
            Point2D pOrigin = mvt.modelToViewDelta( detector.getProbeLocationReference() );
            clickXOffset = pMouse.getX() - pOrigin.getX();
            clickYOffset = pMouse.getY() - pOrigin.getY();
        }
        
        @Override
        protected void drag( final PInputEvent event ) {
            super.drag( event );
            Point2D pMouse = event.getPositionRelativeTo( probeNode.getParent() );
            double xView = pMouse.getX() - clickXOffset;
            double yView = pMouse.getY() - clickYOffset;
            Point3D pModel = new Point3D.Double( mvt.viewToModel( xView, yView ) );
            detector.setProbeLocation( pModel );
        }
    }
    
    /*
     * Wire that connects the probe to the body.
     */
    private static class CubicWireNode extends PPath {

        private final double controlPointDx, controlPointDy;

        public CubicWireNode( Color color, double controlPointDx, double controlPointDy ) {
            this.controlPointDx = controlPointDx;
            this.controlPointDy = controlPointDy;
            setStroke( WIRE_STROKE );
            setStrokePaint( color );
        }

        public void setEndPoints( Point2D startPoint, Point2D endPoint ) {
            Point2D ctrl1 = new Point2D.Double( startPoint.getX() + controlPointDx, startPoint.getY() );
            Point2D ctrl2 = new Point2D.Double( endPoint.getX(), endPoint.getY() + controlPointDy );
            setPathTo( new CubicCurve2D.Double( startPoint.getX(), startPoint.getY(), ctrl1.getX(), ctrl1.getY(), ctrl2.getX(), ctrl2.getY(), endPoint.getX(), endPoint.getY() ) );
        }
    }
}
