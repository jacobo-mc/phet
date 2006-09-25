/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.DebugFlags;
import edu.colorado.phet.piccolo.nodes.RegisterablePNode;

import java.util.HashMap;
import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * AbstractSimpleMoleculeGraphic
 * <p/>
 * Base class used in the spatial and energy views for the graphics for simple molecules
 * <p/>
 * The radius of the molecule is not used for the radius of the disk used in the graphic. Rather,
 * the disk is smaller, so that the bonds between molecules can be shown as a line. This may need
 * to change so that collisions look more natural on the screen.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
abstract public class AbstractSimpleMoleculeGraphic extends PNode implements SimpleObserver, SimpleMolecule.Listener {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    private static double BOND_OFFSET = 0;
    private static HashMap moleculeTypeToColor = new HashMap();
    private static HashMap moleculeTypeToAnnotation = new HashMap();
    private static Color moleculeAColor = new Color( 240, 240, 0 );
    private static Color moleculeBColor = new Color( 0, 200, 0 );
    private static Color moleculeCColor = new Color( 100, 100, 240 );
    private static Color defaultMoleculeColor = new Color( 100, 100, 100 );
    private static Stroke defaultStroke = new BasicStroke( 1 );
    private static Stroke selectedStroke = new BasicStroke( 2 );
    private static Stroke nearestToSelectedStroke = new BasicStroke( 2 );
    private static Paint defaultStrokePaint = Color.black;
    private static Paint selectedStrokePaint = Color.magenta;
    private static Paint cmPaint = Color.magenta;
    private static Paint nearestToSelectedStrokePaint = new Color( 255, 0, 255 );
    private static boolean MARK_SELECTED_MOLECULE = false;

    static {
        AbstractSimpleMoleculeGraphic.moleculeTypeToColor.put( MoleculeA.class, AbstractSimpleMoleculeGraphic.moleculeAColor );
        AbstractSimpleMoleculeGraphic.moleculeTypeToColor.put( MoleculeB.class, AbstractSimpleMoleculeGraphic.moleculeBColor );
        AbstractSimpleMoleculeGraphic.moleculeTypeToColor.put( MoleculeC.class, AbstractSimpleMoleculeGraphic.moleculeCColor );

        AbstractSimpleMoleculeGraphic.moleculeTypeToAnnotation.put( MoleculeA.class, "A" );
        AbstractSimpleMoleculeGraphic.moleculeTypeToAnnotation.put( MoleculeB.class, "B" );
        AbstractSimpleMoleculeGraphic.moleculeTypeToAnnotation.put( MoleculeC.class, "C" );
    }

    private static Color getColor( SimpleMolecule molecule ) {
        Color color = (Color)moleculeTypeToColor.get( molecule.getClass() );
        if( color == null ) {
            color = defaultMoleculeColor;
        }
        return color;
    }

    private static String getAnnotation( SimpleMolecule molecule ) {
        String annotation = (String)moleculeTypeToAnnotation.get( molecule.getClass() );
        if( annotation == null ) {
            annotation = "";
        }
        return annotation;
    }

    public static void setMARK_SELECTED_MOLECULE( boolean mark ) {
        AbstractSimpleMoleculeGraphic.MARK_SELECTED_MOLECULE = mark;
    }

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    private SimpleMolecule molecule;
    private PPath pPath;
    private PPath cmNode;
    private double cmRad = 3;

    /**
     * @param molecule
     */
    public AbstractSimpleMoleculeGraphic( SimpleMolecule molecule ) {
        this( molecule, false );
    }

    /**
     * @param molecule
     * @param annotate
     */
    public AbstractSimpleMoleculeGraphic( SimpleMolecule molecule, boolean annotate ) {
        this.molecule = molecule;
        molecule.addObserver( this );
        molecule.addListener( this );

        double radius = molecule.getRadius() - BOND_OFFSET;
        Shape s = new Ellipse2D.Double( -radius,
                                        -radius,
                                        radius * 2,
                                        radius * 2 );
        pPath = new PPath( s, AbstractSimpleMoleculeGraphic.defaultStroke );
        pPath.setPaint( AbstractSimpleMoleculeGraphic.getColor( molecule ) );
        pPath.setStrokePaint( AbstractSimpleMoleculeGraphic.defaultStrokePaint );
        addChild( pPath );

        // The CM marker
        cmNode = new PPath( new Ellipse2D.Double( -cmRad, -cmRad, cmRad * 2, cmRad * 2 ) );
        cmNode.setPaint( cmPaint );
        cmNode.setVisible( false );
        addChild( cmNode );

        // Add annotation, if required
        if( annotate ) {
            PText annotation = new PText( getAnnotation( molecule ) );
            RegisterablePNode rNode = new RegisterablePNode( annotation );
            rNode.setRegistrationPoint( annotation.getWidth() / 2,
                                        annotation.getHeight() / 2 );
            addChild( rNode );
        }

        // Catch mouse clicks that select this graphic's molecule
        if( molecule instanceof MoleculeA || molecule instanceof MoleculeC ) {
            this.addInputEventListener( new PBasicInputEventHandler() {
                public void mouseClicked( PInputEvent event ) {
                    super.mouseClicked( event );
                    getMolecule().setSelectionStatus( Selectable.SELECTED );
                }
            } );
        }

        update();
    }

    public SimpleMolecule getMolecule() {
        return molecule;
    }

    public void update() {
        // noop
    }
    
    //--------------------------------------------------------------------------------------------------
    // Implementation of SimpleMolecule.Listener
    //--------------------------------------------------------------------------------------------------

    public void selectionStatusChanged( SimpleMolecule molecule ) {
        if( MARK_SELECTED_MOLECULE ) {
            if( molecule.getSelectionStatus() == Selectable.SELECTED ) {
                pPath.setStroke( AbstractSimpleMoleculeGraphic.selectedStroke );
                pPath.setStrokePaint( AbstractSimpleMoleculeGraphic.selectedStrokePaint );
            }
            else if( molecule.getSelectionStatus() == Selectable.NEAREST_TO_SELECTED ) {
                cmNode.setVisible( true );
//            pPath.setStroke( AbstractSimpleMoleculeGraphic.nearestToSelectedStroke );
//            pPath.setStrokePaint( AbstractSimpleMoleculeGraphic.nearestToSelectedStrokePaint );
            }
            else {
                cmNode.setVisible( false );
                pPath.setStroke( AbstractSimpleMoleculeGraphic.defaultStroke );
                pPath.setStrokePaint( AbstractSimpleMoleculeGraphic.defaultStrokePaint );
            }
        }

        // for debugging
        cmNode.setVisible( DebugFlags.SHOW_CM );

    }
}
