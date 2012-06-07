package edu.colorado.phet.fractionsintro.buildafraction.view.numbers;

import fj.F;
import fj.Ord;
import fj.data.List;
import fj.data.Option;
import lombok.Data;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.FaceNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractionsintro.buildafraction.model.NumberLevel;
import edu.colorado.phet.fractionsintro.buildafraction.model.NumberTarget;
import edu.colorado.phet.fractionsintro.buildafraction.view.BuildAFractionCanvas;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.PatternNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.fractions.FractionsResources.Strings.MY_FRACTIONS;

/**
 * Node for the scene when the user is constructing fractions with numbers.
 *
 * @author Sam Reid
 */
public class NumberSceneNode extends PNode implements NumberDragContext {
    private final ArrayList<FractionGraphic> fractionGraphics = new ArrayList<FractionGraphic>();
    private final PNode rootNode;
    private final BuildAFractionModel model;
    private final PDimension STAGE_SIZE;
    private final NumberSceneContext context;
    private final List<Pair> pairList;
    private final RichPNode toolboxNode;

    @Data class Pair {
        public final ScoreBoxNode targetCell;
        public final PNode patternNode;
    }

    public NumberSceneNode( int level, final PNode rootNode, final BuildAFractionModel model, PDimension STAGE_SIZE, NumberSceneContext context ) {
        this.rootNode = rootNode;
        this.model = model;
        this.STAGE_SIZE = STAGE_SIZE;
        this.context = context;
        final PhetPText title = new PhetPText( MY_FRACTIONS, AbstractFractionsCanvas.CONTROL_FONT );

        //Create the scoring cells with target patterns
        ArrayList<Pair> pairs = new ArrayList<Pair>();
        for ( int i = 0; i < 3; i++ ) {
            NumberTarget target = model.getNumberLevel( level ).getTarget( i );

            ArrayList<PatternNode> nodes = new ArrayList<PatternNode>();
            for ( int k = 0; k < target.filledPattern.length(); k++ ) {
                nodes.add( new PatternNode( target.filledPattern.index( k ), target.color ) );
            }
            HBox patternNode = new HBox( nodes.toArray( new PNode[nodes.size()] ) );
            pairs.add( new Pair( new ScoreBoxNode( target.fraction.numerator, target.fraction.denominator, model.getCreatedFractions( level ) ), new ZeroOffsetNode( patternNode ) ) );
        }
        pairList = List.iterableList( pairs );
        List<PNode> patterns = pairList.map( new F<Pair, PNode>() {
            @Override public PNode f( final Pair pair ) {
                return pair.patternNode;
            }
        } );
        double maxWidth = patterns.map( new F<PNode, Double>() {
            @Override public Double f( final PNode pNode ) {
                return pNode.getFullBounds().getWidth();
            }
        } ).maximum( Ord.doubleOrd );
        double maxHeight = patterns.map( new F<PNode, Double>() {
            @Override public Double f( final PNode pNode ) {
                return pNode.getFullBounds().getHeight();
            }
        } ).maximum( Ord.doubleOrd );

        //Layout for the scoring cells and target patterns
        double separation = 5;
        double rightInset = 10;
        final PBounds targetCellBounds = pairs.get( 0 ).getTargetCell().getFullBounds();
        double offsetX = AbstractFractionsCanvas.STAGE_SIZE.width - maxWidth - separation - targetCellBounds.getWidth() - rightInset;
        double offsetY = title.getFullHeight() + 5;
        double insetY = 5;
        addChild( title );
        for ( Pair pair : pairs ) {

            pair.targetCell.setOffset( offsetX, offsetY );
            pair.patternNode.setOffset( offsetX + targetCellBounds.getWidth() + separation, offsetY + targetCellBounds.getHeight() / 2 - maxHeight / 2 );
            addChild( pair.targetCell );
            addChild( pair.patternNode );

            offsetY += Math.max( maxHeight, targetCellBounds.getHeight() ) + insetY;
        }

        //Center title above the "my fractions" scoring cell boxes
        title.setOffset( pairs.get( 0 ).getTargetCell().getFullBounds().getCenterX() - title.getFullWidth() / 2, pairs.get( 0 ).getTargetCell().getFullBounds().getY() - title.getFullHeight() );

        //Add a piece container toolbox the user can use to get containers
        toolboxNode = new RichPNode() {{
            final PhetPPath border = new PhetPPath( new RoundRectangle2D.Double( 0, 0, 700, 160, 30, 30 ), BuildAFractionCanvas.CONTROL_PANEL_BACKGROUND, BuildAFractionCanvas.controlPanelStroke, Color.darkGray );
            addChild( border );
            setOffset( ( AbstractFractionsCanvas.STAGE_SIZE.width - 150 ) / 2 - this.getFullWidth() / 2, AbstractFractionsCanvas.STAGE_SIZE.height - AbstractFractionsCanvas.INSET - this.getFullHeight() );
        }};
        addChild( toolboxNode );

        final FractionGraphic fractionGraphic = createDefaultFractionGraphic();
        addChild( fractionGraphic );
        fractionGraphics.add( fractionGraphic );

        NumberLevel myLevel = model.getNumberLevel( level );
        for ( Integer number : myLevel.numbers ) {
            NumberNode numberNode = new NumberNode( number, this );
            numberNode.setInitialPosition( toolboxNode.getFullBounds().getX() + toolboxNode.getFullWidth() * ( number + 1 ) / 11.0 - numberNode.getFullBounds().getWidth() / 2, toolboxNode.getCenterY() - numberNode.getFullBounds().getHeight() / 2 );
            addChild( numberNode );
        }
//        int numCopies = 2;
//        for ( int i = 0; i < 10; i++ ) {
//            for ( int k = 0; k < numCopies; k++ ) {
//
//            }
//        }
    }

    private FractionGraphic createDefaultFractionGraphic() {
        final FractionGraphic fractionGraphic = new FractionGraphic() {{
            setOffset( toolboxNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, 300 );
        }};
        return fractionGraphic;
    }

    public void endDrag( final NumberNode numberNode, final PInputEvent event ) {
        boolean hitFraction = false;
        for ( FractionGraphic fractionGraphic : fractionGraphics ) {
            final PhetPPath topBox = fractionGraphic.topBox;
            final PhetPPath bottomBox = fractionGraphic.bottomBox;
            if ( numberNode.getGlobalFullBounds().intersects( topBox.getGlobalFullBounds() ) && topBox.getVisible() ) {
                numberDroppedOnFraction( fractionGraphic, numberNode, topBox );
                hitFraction = true;
                break;
            }
            if ( numberNode.getGlobalFullBounds().intersects( bottomBox.getGlobalFullBounds() ) && bottomBox.getVisible() ) {
                numberDroppedOnFraction( fractionGraphic, numberNode, bottomBox );
                hitFraction = true;
                break;
            }
        }
        //If it didn't hit a fraction, send back to its starting place--the user is not allowed to have free floating numbers in the play area
        if ( !hitFraction ) {
            numberNode.animateHome();
        }
    }

    private void numberDroppedOnFraction( final FractionGraphic fractionGraphic, final NumberNode numberNode, final PhetPPath box ) {
        centerOnBox( numberNode, box );
        box.setVisible( false );
        numberNode.setPickable( false );
        numberNode.setChildrenPickable( false );
        fractionGraphic.splitButton.setVisible( true );
        fractionGraphic.setTarget( box, numberNode );
        if ( fractionGraphic.isComplete() ) {
            model.addCreatedValue( fractionGraphic.getValue() );
            //create an invisible overlay that allows dragging all parts together
            PBounds topBounds = fractionGraphic.getTopNumber().getFullBounds();
            PBounds bottomBounds = fractionGraphic.getBottomNumber().getFullBounds();
            Rectangle2D divisorBounds = fractionGraphic.localToParent( fractionGraphic.divisorLine.getFullBounds() );
            Rectangle2D union = topBounds.createUnion( bottomBounds ).createUnion( divisorBounds );

            //For debugging, show a yellow border
//            final PhetPPath path = new PhetPPath( RectangleUtils.expand( union, 2, 2 ), BuildAFractionCanvas.TRANSPARENT, new BasicStroke( 1 ), Color.yellow );
            final PhetPPath path = new PhetPPath( RectangleUtils.expand( union, 2, 2 ), BuildAFractionCanvas.TRANSPARENT );
            path.addInputEventListener( new CursorHandler() );
            path.addInputEventListener( new SimSharingDragHandler( null, true ) {
                @Override protected void drag( final PInputEvent event ) {
                    super.drag( event );
                    final PDimension delta = event.getDeltaRelativeTo( rootNode );
                    fractionGraphic.translateAll( delta );
                    path.translate( delta.getWidth(), delta.getHeight() );
                }

                @Override protected void endDrag( final PInputEvent event ) {
                    super.endDrag( event );

                    //Snap to a scoring cell or go back to the play area.
                    List<ScoreBoxNode> scoreCells = pairList.map( new F<Pair, ScoreBoxNode>() {
                        @Override public ScoreBoxNode f( final Pair pair ) {
                            return pair.targetCell;
                        }
                    } );
                    for ( ScoreBoxNode scoreCell : scoreCells ) {
                        if ( path.getFullBounds().intersects( scoreCell.getFullBounds() ) && scoreCell.fraction.approxEquals( fractionGraphic.getValue() ) ) {
                            //Lock in target cell
                            Point2D center = path.getFullBounds().getCenter2D();
                            Point2D targetCenter = scoreCell.getFullBounds().getCenter2D();
                            Vector2D delta = new Vector2D( targetCenter, center );
                            fractionGraphic.translateAll( delta.toDimension() );
                            path.translate( delta.x, delta.y );

                            fractionGraphic.splitButton.setVisible( false );
                            removeChild( path );
                            fractionGraphic.setAllPickable( false );

                            scoreCell.completed();

                            //Add a new fraction skeleton when the previous one is completed
                            if ( !allTargetsComplete() ) {
                                final FractionGraphic fractionGraphic = createDefaultFractionGraphic();
                                addChild( fractionGraphic );
                                fractionGraphic.setTransparency( 0.0f );
                                fractionGraphic.animateToTransparency( 1.0f, 1000 );
                                fractionGraphics.add( fractionGraphic );
                            }

                            //but if all filled up, then add a "next" button
                            else {
                                addChild( new VBox( new FaceNode( 300 ), new HTMLImageButtonNode( "Next", Color.orange ) {{
                                    addActionListener( new ActionListener() {
                                        public void actionPerformed( final ActionEvent e ) {
                                            context.goToNext();
                                        }
                                    } );
                                }}
                                ) {{setOffset( STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2 - 100, STAGE_SIZE.getHeight() / 2 - getFullBounds().getHeight() / 2 - 100 );}} );
                            }
                        }
                    }
                }
            } );
            addChild( path );
            fractionGraphic.addSplitListener( new VoidFunction1<Option<Fraction>>() {
                public void apply( final Option<Fraction> fractions ) {
                    removeChild( path );
                    if ( fractions.isSome() ) {
                        model.removeCreatedValueFromNumberLevel( fractions.some() );
                    }
                }
            } );
        }
    }

    private boolean allTargetsComplete() {
        return pairList.map( new F<Pair, Boolean>() {
            @Override public Boolean f( final Pair pair ) {
                return pair.targetCell.isCompleted();
            }
        } ).filter( new F<Boolean, Boolean>() {
            @Override public Boolean f( final Boolean b ) {
                return b;
            }
        } ).length() == pairList.length();
    }

    private void centerOnBox( final NumberNode numberNode, final PhetPPath box ) {
        Rectangle2D bounds = box.getGlobalFullBounds();
        bounds = rootNode.globalToLocal( bounds );
        numberNode.centerFullBoundsOnPoint( bounds.getCenterX(), bounds.getCenterY() );
    }
}