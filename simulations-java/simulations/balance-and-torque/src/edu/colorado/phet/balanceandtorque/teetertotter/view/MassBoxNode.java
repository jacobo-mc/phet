// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import edu.colorado.phet.balanceandtorque.teetertotter.model.TeeterTotterTorqueModel;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.swing.SwingLayoutNode;

/**
 * This class defines the box from which the user can drag masses that can
 * then be set on the balance.
 *
 * @author John Blanco
 */
public class MassBoxNode extends PNode {

    private static final PDimension SIZE = new PDimension( 220, 300 );
    private static final PhetFont BUTTON_FONT = new PhetFont( 16 );
    private static final Color BUTTON_COLOR = new Color( 50, 205, 50 );

    private ArrayList<PNode> massSets = new ArrayList<PNode>();
    private Property<Integer> activeMassSet = new Property<Integer>( 0 );

    /**
     * Constructor.
     */
    public MassBoxNode( final TeeterTotterTorqueModel model, final ModelViewTransform mvt, final PhetPCanvas canvas ) {

        // Create a node that contains the set of bricks.  This is a "mass set".
        massSets.add( new SwingLayoutNode( new GridLayout( 2, 2, 20, 20 ) ) {{
            addChild( new BrickStackInMassBoxNode( 1, model, mvt, canvas ) );
            addChild( new BrickStackInMassBoxNode( 2, model, mvt, canvas ) );
            addChild( new BrickStackInMassBoxNode( 3, model, mvt, canvas ) );
            addChild( new BrickStackInMassBoxNode( 4, model, mvt, canvas ) );
        }} );

        // Create a node that contains people.  This is also a "mass set".
//        massesets.add( new SwingLayoutNode( new GridLayout( 1, 2, 20, 20 ) ) {{
//            AdultMaleHumanInMassBoxNode adultMaleHumanInMassBoxNode = new AdultMaleHumanInMassBoxNode( model, mvt, canvas );
//            addChild( adultMaleHumanInMassBoxNode );
//            AdolescentHumanInMassBoxNode adolescentHumanInMassBoxNode = new AdolescentHumanInMassBoxNode( model, mvt, canvas );
//            addChild( adolescentHumanInMassBoxNode );
//        }} );
        massSets.add( new SwingLayoutNode( new FlowLayout() ) {{
            AdolescentHumanInMassBoxNode adolescentHumanInMassBoxNode = new AdolescentHumanInMassBoxNode( model, mvt, canvas );
            addChild( adolescentHumanInMassBoxNode );
            AdultMaleHumanInMassBoxNode adultMaleHumanInMassBoxNode = new AdultMaleHumanInMassBoxNode( model, mvt, canvas );
            addChild( adultMaleHumanInMassBoxNode );
        }} );

        // Create a node that contains mystery objects.  This is also a "mass set".
        massSets.add( new SwingLayoutNode( new GridLayout( 2, 2, 20, 20 ) ) {{
            addChild( new MysteryObjectInMassBoxNode( 0, model, mvt, canvas ) );
            addChild( new MysteryObjectInMassBoxNode( 1, model, mvt, canvas ) );
            addChild( new MysteryObjectInMassBoxNode( 2, model, mvt, canvas ) );
            addChild( new MysteryObjectInMassBoxNode( 3, model, mvt, canvas ) );
        }} );

        // TODO: i18n
        TextButtonNode nextButton = new TextButtonNode( "Next", BUTTON_FONT, BUTTON_COLOR ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    activeMassSet.set( ( activeMassSet.get() + 1 ) % massSets.size() );
                }
            } );
            // Set up a listener that disables the button if there are no more mass sets.
            activeMassSet.valueEquals( massSets.size() - 1 ).addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean atLastMass ) {
                    setEnabled( !atLastMass );
                }
            } );
        }};
        // TODO: i18n
        TextButtonNode previousButton = new TextButtonNode( "Previous", BUTTON_FONT, BUTTON_COLOR ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    activeMassSet.set( Math.abs( ( activeMassSet.get() - 1 ) % massSets.size() ) );
                }
            } );
            // Set up a listener that disables the button if they are looking at the first mass set.
            activeMassSet.valueEquals( 0 ).addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean atFirstMass ) {
                    setEnabled( !atFirstMass );
                }
            } );
        }};

        // Create the content node that will be placed on to a control panel.
        // This contains a title, buttons for moving between mass sets, and
        // the mass sets.
        final PNode contentNode = new VBox(
                // Title.
                // TODO: i18n
                new PText( "Masses" ) {{
                    setFont( new PhetFont( 20 ) );
                }},
                // Buttons.
                new HBox( previousButton, nextButton ),
                // Mass set.
                massSets.get( activeMassSet.get() )
        );

        // Hook up a listener that will switch the mass sets when necessary.
        activeMassSet.addObserver( new ChangeObserver<Integer>() {
            public void update( Integer newValue, Integer oldValue ) {
                contentNode.removeChild( massSets.get( oldValue ) );
                contentNode.addChild( massSets.get( newValue ) );
            }
        } );

        // Last step: Create and add the control panel.
        addChild( new ControlPanelNode( contentNode ) );
    }
}