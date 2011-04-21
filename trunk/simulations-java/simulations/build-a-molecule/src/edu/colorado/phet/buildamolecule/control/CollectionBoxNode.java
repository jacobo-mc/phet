package edu.colorado.phet.buildamolecule.control;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import edu.colorado.phet.buildamolecule.BuildAMoleculeConstants;
import edu.colorado.phet.buildamolecule.model.CollectionBox;
import edu.colorado.phet.buildamolecule.model.MoleculeStructure;
import edu.colorado.phet.buildamolecule.view.BuildAMoleculeCanvas;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.swing.SwingLayoutNode;

/**
 * Represents a generic collection box node which is decorated by additional header nodes (probably text describing what can be put in, what is in it,
 * etc.)
 */
public class CollectionBoxNode extends SwingLayoutNode {

    private final CollectionBox box;
    private final PNode boxNode = new PNode();
    private final PhetPPath blackBox;
    private final PNode moleculeLayer = new PNode();
    private final List<PNode> moleculeNodes = new LinkedList<PNode>();

    private static final double MOLECULE_PADDING = 5;
    private Timer blinkTimer = null;

    public CollectionBoxNode( final BuildAMoleculeCanvas canvas, final CollectionBox box, PNode... headerNodes ) {
        super( new GridBagLayout() );
        this.box = box;

        // grid bag layout and SwingLayoutNode for easier horizontal and vertical layout
        GridBagConstraints c = new GridBagConstraints() {{
            gridx = 0;
            gridy = 0;
        }};

        // add in our header nodes
        for ( PNode headerNode : headerNodes ) {
            addChild( headerNode, c );
            c.gridy += 1;
        }

        c.insets = new Insets( 3, 0, 0, 0 ); // some padding between the black box

        blackBox = new PhetPPath( new Rectangle2D.Double( 0, 0, 160, 50 ), BuildAMoleculeConstants.MOLECULE_COLLECTION_BOX_BACKGROUND ) {{
            canvas.addFullyLayedOutObserver( new SimpleObserver() {
                public void update() {
                    // we need to pass the collection box model coordinates, but here we have relative piccolo coordinates
                    // this requires getting local => global => view => model coordinates

                    // our bounds relative to the root Piccolo canvas
                    Rectangle2D globalBounds = getParent().localToGlobal( getFullBounds() );

                    // pull out the upper-left corner and dimension so we can transform them
                    Point2D upperLeftCorner = new Point2D.Double( globalBounds.getX(), globalBounds.getY() );
                    PDimension dimensions = new PDimension( globalBounds.getWidth(), globalBounds.getHeight() );

                    // transform the point and dimensions to world coordinates
                    canvas.getPhetRootNode().globalToWorld( upperLeftCorner );
                    canvas.getPhetRootNode().globalToWorld( dimensions );

                    // our bounds relative to our simulation (BAM) canvas. Will be filled in
                    Rectangle2D viewBounds = new Rectangle2D.Double( upperLeftCorner.getX(), upperLeftCorner.getY(), dimensions.getWidth(), dimensions.getHeight() );

                    // pass it the model bounds
                    box.setDropBounds( canvas.getModelViewTransform().viewToModel( viewBounds ).getBounds2D() );
                }
            } );
        }};
        boxNode.addChild( blackBox );
        boxNode.addChild( moleculeLayer );
        addChild( boxNode, c );

        updateBoxGraphics();

        box.addListener( new CollectionBox.Listener() {
            public void onAddedMolecule( MoleculeStructure moleculeStructure ) {
                addMolecule();
            }

            public void onRemovedMolecule( MoleculeStructure moleculeStructure ) {
                removeMolecule();
            }

            public void onAcceptedMoleculeCreation( MoleculeStructure moleculeStructure ) {
                blink();
            }
        } );
    }

    private void addMolecule() {
        cancelBlinksInProgress();
        updateBoxGraphics();

        PNode pseudo3DNode = box.getMoleculeType().createPseudo3DNode();
        pseudo3DNode.setOffset( moleculeNodes.size() * ( pseudo3DNode.getFullBounds().getWidth() + MOLECULE_PADDING ) - pseudo3DNode.getFullBounds().getX(), 0 ); // add it to the right
        moleculeLayer.addChild( pseudo3DNode );
        moleculeNodes.add( pseudo3DNode );

        centerMoleculesInBlackBox();
    }

    private void removeMolecule() {
        cancelBlinksInProgress();
        updateBoxGraphics();

        PNode lastMoleculeNode = moleculeNodes.get( moleculeNodes.size() - 1 );
        moleculeLayer.removeChild( lastMoleculeNode );
        moleculeNodes.remove( lastMoleculeNode );

        if ( box.quantity.getValue() > 0 ) {
            centerMoleculesInBlackBox();
        }
    }

    /**
     * Sets up a blinking box to register that a molecule was created that can go into a box
     */
    private void blink() {
        double blinkLengthInSeconds = 1.3;

        // our delay between states
        int blinkDelayInMs = 100;

        // properties that we will use over time in our blinker
        final Property<Boolean> on = new Property<Boolean>( false ); // on/off state
        final Property<Integer> counts = new Property<Integer>( (int) ( blinkLengthInSeconds * 1000 / blinkDelayInMs ) ); // decrements to zero to stop the blinker

        cancelBlinksInProgress();

        blinkTimer = new Timer();
        blinkTimer.schedule( new TimerTask() {
            @Override
            public void run() {
                // decrement and check
                counts.setValue( counts.getValue() - 1 );
                assert ( counts.getValue() >= 0 );

                if ( counts.getValue() == 0 ) {
                    // set up our normal graphics (border/background)
                    updateBoxGraphics();

                    // make sure we don't get called again
                    blinkTimer.cancel();
                    blinkTimer = null;
                }
                else {
                    // toggle state
                    on.setValue( !on.getValue() );

                    // draw graphics
                    if ( on.getValue() ) {
                        blackBox.setPaint( BuildAMoleculeConstants.MOLECULE_COLLECTION_BOX_BACKGROUND_BLINK );
                        blackBox.setStrokePaint( BuildAMoleculeConstants.MOLECULE_COLLECTION_BOX_BORDER_BLINK );
                    }
                    else {
                        blackBox.setPaint( BuildAMoleculeConstants.MOLECULE_COLLECTION_BOX_BACKGROUND );
                        blackBox.setStrokePaint( BuildAMoleculeConstants.MOLECULE_COLLECTION_BACKGROUND );
                    }

                    // make sure this paint happens immediately
                    blackBox.repaint();
                }
            }
        }, 0, blinkDelayInMs );
    }

    private void cancelBlinksInProgress() {
        // stop any previous blinking from happening. don't want double-blinking
        if ( blinkTimer != null ) {
            blinkTimer.cancel();
            blinkTimer = null;
        }
    }

    private void centerMoleculesInBlackBox() {
        PBounds blackBoxFullBounds = blackBox.getFullBounds();
        moleculeLayer.centerFullBoundsOnPoint(
                blackBoxFullBounds.getCenterX() - blackBoxFullBounds.getX(),
                blackBoxFullBounds.getCenterY() - blackBoxFullBounds.getY() );
    }

    private void updateBoxGraphics() {
        blackBox.setStroke( new BasicStroke( 4 ) );
        if ( box.isFull() ) {
            blackBox.setStrokePaint( BuildAMoleculeConstants.MOLECULE_COLLECTION_BOX_HIGHLIGHT );
        }
        else {
            blackBox.setStrokePaint( BuildAMoleculeConstants.MOLECULE_COLLECTION_BACKGROUND );
        }
    }
}
