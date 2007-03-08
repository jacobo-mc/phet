/* Copyright 2007, University of Colorado */
package edu.colorado.phet.molecularreactions.view.energy;

import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.PNode;
import edu.colorado.phet.molecularreactions.view.SeparationIndicatorArrow;
import edu.colorado.phet.molecularreactions.view.EnergyMoleculeGraphic;
import edu.colorado.phet.molecularreactions.model.SimpleMolecule;
import edu.colorado.phet.molecularreactions.model.MoleculeA;
import edu.colorado.phet.molecularreactions.model.MoleculeC;
import edu.colorado.phet.molecularreactions.model.EnergyProfile;
import edu.colorado.phet.molecularreactions.model.reactions.A_BC_AB_C_Reaction;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.molecularreactions.modules.MRModule;
import edu.colorado.phet.common.view.util.SimStrings;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;

public class MoleculeSeparationPane extends PPath {
    private final MoleculeSelectionState selectionState;

    private EnergyMoleculeGraphic selectedMoleculeGraphic;
    private EnergyMoleculeGraphic nearestToSelectedMoleculeGraphic;

    private Insets paneInsets = new Insets( 20, 30, 40, 10 );

    private PNode moleculePaneAxisNode;
    private SeparationIndicatorArrow separationIndicatorArrow;
    private CurvePane curvePane;

    private PNode moleculeLayer;
    private MoleculeSeparationPane.MoleculeGraphicController moleculeGraphicController;

    public MoleculeSeparationPane( final MRModule module, Dimension upperPaneSize, CurvePane curvePane ) {
        super( new Rectangle2D.Double( 0, 0,
                                       upperPaneSize.getWidth(),
                                       upperPaneSize.getHeight() ) );

        this.curvePane = curvePane;

        this.setPaint( MRConfig.MOLECULE_PANE_BACKGROUND );

        this.moleculeLayer = new PNode();
        this.moleculeLayer.setOffset( this.paneInsets.left, 0 );

        addChild( this.moleculeLayer );

        // Axis: An arrow that shows separation of molecules and text label
        // They are grouped in a single node so that they can be made visible or
        // invisible as necessary
        this.moleculePaneAxisNode = new PNode();
        this.separationIndicatorArrow = new SeparationIndicatorArrow( Color.black );
        this.moleculePaneAxisNode.addChild( this.separationIndicatorArrow );
        PText siaLabel = new PText( SimStrings.get( "EnergyView.separation" ) );
        siaLabel.setFont( MRConfig.LABEL_FONT );
        siaLabel.rotate( -Math.PI / 2 );
        siaLabel.setOffset( this.paneInsets.left / 2 - siaLabel.getFullBounds().getWidth() + 2,
                            this.getFullBounds().getHeight() / 2 + siaLabel.getFullBounds().getHeight() / 2 );
        this.moleculePaneAxisNode.addChild( siaLabel );
        this.moleculePaneAxisNode.setVisible( false );

        this.addChild( this.moleculePaneAxisNode );

        selectionState = new MoleculeSelectionState( module );

        moleculeGraphicController = new MoleculeGraphicController( module );

        selectionState.addSelectionStateListener( moleculeGraphicController );
    }

    public MoleculeSelectionState getSelectionState() {
        return selectionState;
    }

    public void reset() {
        super.reset();

        selectionState.reset();

        if( selectedMoleculeGraphic != null ) {
            moleculeLayer.removeChild( selectedMoleculeGraphic );
        }

        if( nearestToSelectedMoleculeGraphic != null ) {
            moleculeLayer.removeChild( nearestToSelectedMoleculeGraphic );
        }

        selectedMoleculeGraphic = null;
        nearestToSelectedMoleculeGraphic = null;
    }

    /*
     * Updates the positions of the molecule graphics in the upper pane.
     */
    public void update( CurvePane curvePane ) {
        if( selectionState.isMoleculeSelected() ) {
            moleculeGraphicController.update();
        }
        else if( selectedMoleculeGraphic != null ) {
            selectedMoleculeGraphic.setOffset( 20, 20 );
        }
        else if( nearestToSelectedMoleculeGraphic != null ) {
            nearestToSelectedMoleculeGraphic.setOffset( 20, 50 );
        }
    }

    private class MoleculeGraphicController implements MoleculeSelectionState.MoleculeSelectionStateListener {
        private final MRModule module;
        private int direction;
        private double yMin, yMax;
        private Point2D.Double midPoint = new Point2D.Double(0, 0);

        public MoleculeGraphicController( MRModule module ) {
            this.module = module;
        }

        public void notifyMoleculeSelected( SimpleMolecule selectedMolecule ) {
            if( selectedMolecule != null ) {
                selectedMoleculeGraphic = new EnergyMoleculeGraphic( selectedMolecule.getFullMolecule(),
                                                                     module.getMRModel().getEnergyProfile() );
                moleculeLayer.addChild( selectedMoleculeGraphic );
                moleculePaneAxisNode.setVisible( true );
            }
            else {
                moleculePaneAxisNode.setVisible( false );
            }

            updateDirection();
        }

        public void notifyNearestToSelectedChanged( SimpleMolecule oldNearest, SimpleMolecule newNearest ) {
            if( nearestToSelectedMoleculeGraphic != null ) {
                moleculeLayer.removeChild( nearestToSelectedMoleculeGraphic );
            }

            nearestToSelectedMoleculeGraphic = new EnergyMoleculeGraphic( newNearest,
                                                                          module.getMRModel().getEnergyProfile() );

            moleculeLayer.addChild( nearestToSelectedMoleculeGraphic );
        }

        public void notifyMoleculeUnselected( SimpleMolecule selectedMolecule ) {
            if( selectedMoleculeGraphic != null && moleculeLayer.getChildrenReference().contains( selectedMoleculeGraphic ) ) {
                moleculeLayer.removeChild( selectedMoleculeGraphic );
            }
        }

        public void notifyFreeMoleculeChanged( SimpleMolecule oldFreeMolecule, SimpleMolecule newFreeMolecule ) {
            updateDirection();

            if (selectedMoleculeGraphic != null && nearestToSelectedMoleculeGraphic != null) {
                // Set locatation of molecules. Use the *direction* variable we set above
                // to determine which graphic should be on top
                if( newFreeMolecule instanceof MoleculeC && newFreeMolecule == selectionState.getSelectedMolecule() ) {
                    selectedMoleculeGraphic.setOffset( midPoint.getX(), yMax );
                    nearestToSelectedMoleculeGraphic.setOffset( midPoint.getX(), yMin );
                }
                else if( newFreeMolecule instanceof MoleculeC && newFreeMolecule == selectionState.getNearestToSelectedMolecule() ) {
                    selectedMoleculeGraphic.setOffset( midPoint.getX(), yMax );
                    nearestToSelectedMoleculeGraphic.setOffset( midPoint.getX(), yMin );
                }
                else if( newFreeMolecule instanceof MoleculeA && newFreeMolecule == selectionState.getSelectedMolecule() ) {
                    selectedMoleculeGraphic.setOffset( midPoint.getX(), yMin );
                    nearestToSelectedMoleculeGraphic.setOffset( midPoint.getX(), yMax );
                }
                else if( newFreeMolecule instanceof MoleculeA && newFreeMolecule == selectionState.getNearestToSelectedMolecule() ) {
                    selectedMoleculeGraphic.setOffset( midPoint.getX(), yMin );
                    nearestToSelectedMoleculeGraphic.setOffset( midPoint.getX(), yMax );
                }
            }
        }

        public void notifyBoundMoleculeChanged( SimpleMolecule oldBoundMolecule, SimpleMolecule newBoundMolecule ) {
            updateDirection();
        }

        public void notifyEnergyProfileChanged( EnergyProfile newProfile ) {
            if( selectedMoleculeGraphic != null ) {
                moleculeLayer.removeChild( selectedMoleculeGraphic );
                selectedMoleculeGraphic = new EnergyMoleculeGraphic( selectionState.getSelectedMolecule().getFullMolecule(),
                                                                     newProfile );
                moleculeLayer.addChild( selectedMoleculeGraphic );
            }
            if( nearestToSelectedMoleculeGraphic != null ) {
                moleculeLayer.removeChild( nearestToSelectedMoleculeGraphic );
                nearestToSelectedMoleculeGraphic = new EnergyMoleculeGraphic( selectionState.getNearestToSelectedMolecule().getFullMolecule(),
                                                                              newProfile );
                moleculeLayer.addChild( nearestToSelectedMoleculeGraphic );
            }
        }

        public void update() {
            updateDirection();
            updatePositions();
            updateSeparationArrow();
            updateEnergyCursor();
        }

        private void updateEnergyCursor() {
            // set location of cursor
            curvePane.setEnergyCursorOffset( midPoint.getX() );
        }

        private void updateSeparationArrow() {
            // Set the size of the separation indicator arrow
            separationIndicatorArrow.setEndpoints( paneInsets.left / 2 + 10, yMin,
                                                   paneInsets.left / 2 + 10, yMax );
        }

        private void updateDirection() {
            if (selectionState.isMoleculeSelected()) {
                SimpleMolecule freeMolecule  = selectionState.getFreeMolecule(),
                               boundMolecule = selectionState.getBoundMolecule();

                if( freeMolecule != null && boundMolecule != null ) {
                    // Figure out on which side of the centerline the molecules should appear
                    // If the selected molecule is an A molecule and it's free, we're on the left
                    if( selectionState.getSelectedMolecule() instanceof MoleculeA && selectionState.getSelectedMolecule() == freeMolecule ) {
                        direction = -1;
                    }
                    // If the selected molecule is an A molecule and it's bound, we're on the right
                    else if( selectionState.getSelectedMolecule() instanceof MoleculeA && selectionState.getSelectedMolecule() == boundMolecule ) {
                        direction = 1;
                    }
                    // If the selected molecule is a C molecule and it's free, we're on the right
                    else if( selectionState.getSelectedMolecule() instanceof MoleculeC && selectionState.getSelectedMolecule() == freeMolecule ) {
                        direction = 1;
                    }
                    // If the selected molecule is a C molecule and it's bound, we're on the left
                    else if( selectionState.getSelectedMolecule() instanceof MoleculeC && selectionState.getSelectedMolecule() == boundMolecule ) {
                        direction = -1;
                    }
                    else {
                        throw new RuntimeException( "internal error" );
                    }
                }
            }
        }

        private void updatePositions() {
            SimpleMolecule freeMolecule  = selectionState.getFreeMolecule(),
                           boundMolecule = selectionState.getBoundMolecule();

            if( freeMolecule != null && boundMolecule != null ) {
                // Position the molecule graphics
                double cmDist = selectionState.getSelectedMolecule().getPosition().distance( selectionState.getNearestToSelectedMolecule().getPosition() );
                A_BC_AB_C_Reaction reaction = (A_BC_AB_C_Reaction)module.getMRModel().getReaction();
                double edgeDist = reaction.getDistanceToCollision( freeMolecule, boundMolecule.getParentComposite() );

                // In the middle of the reaction, the collision distance is underfined
                if( Double.isNaN( edgeDist ) ) {
                    edgeDist = 0;
                }

                double maxSeparation = 80;
                double yOffset = 35;
                double xOffset = 20;

                // The distance between the molecule's CMs when they first come into contact
                double separationAtFootOfHill = Math.min( selectionState.getSelectedMolecule().getRadius(), selectionState.getNearestToSelectedMolecule().getRadius() );

                // Scale the actual inter-molecular distance to the scale of the energy profile
                double r = ( reaction.getEnergyProfile().getThresholdWidth() / 2 ) / separationAtFootOfHill;
                double separationAtReaction = A_BC_AB_C_Reaction.getReactionOffset( freeMolecule, boundMolecule );
                double currentSeparation = freeMolecule.getPosition().distance( boundMolecule.getPosition() );
                double currentOverlap = separationAtFootOfHill - currentSeparation;
                double reactionOverlap = separationAtFootOfHill - separationAtReaction;
                double dr = currentOverlap / reactionOverlap * r;

                double dx = Math.max( ( edgeDist + separationAtFootOfHill ) * r, dr );
                double xOffsetFromCenter = Math.min( curvePane.getCurveAreaSize().getWidth() / 2 - xOffset, dx );
                double x = curvePane.getCurveAreaSize().getWidth() / 2 + ( xOffsetFromCenter * direction );
                double y = yOffset + maxSeparation / 2;

                // Do not allow the energy cursor to move beyond where it's
                // energetically allowed.
                // Note: This is a hack implemented because the physics of the
                //       simulation are fudged.
                double maxX = curvePane.getIntersectionWithHorizontal( x );

                x = Math.min( x, maxX );

                midPoint = new Point2D.Double( x, y );

                yMin = midPoint.getY() - Math.min( cmDist, maxSeparation ) / 2;
                yMax = midPoint.getY() + Math.min( cmDist, maxSeparation ) / 2;
            }
        }
    }
}
