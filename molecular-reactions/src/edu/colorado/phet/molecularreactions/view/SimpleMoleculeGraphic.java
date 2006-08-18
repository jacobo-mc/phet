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

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.molecularreactions.model.MoleculeA;
import edu.colorado.phet.molecularreactions.model.MoleculeB;
import edu.colorado.phet.molecularreactions.model.SimpleMolecule;
import edu.colorado.phet.molecularreactions.model.Selectable;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;

/**
 * SimpleMoleculeGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimpleMoleculeGraphic extends PNode implements SimpleObserver, SimpleMolecule.Listener {

    private static HashMap moleculeTypeToColor = new HashMap();
    private static Color moleculeAColor = new Color( 0, 200, 0 );
    private static Color moleculeBColor = new Color( 240, 240, 0 );
    private static Color defaultMoleculeColor = new Color( 100, 100, 100 );

    static {
        moleculeTypeToColor.put( MoleculeA.class, moleculeAColor );
        moleculeTypeToColor.put( MoleculeB.class, moleculeBColor );
    }

    private static Color getColor( SimpleMolecule molecule ) {
        Color color = (Color)moleculeTypeToColor.get( molecule.getClass() );
        if( color == null ) {
            color = defaultMoleculeColor;
        }
        return color;
    }

    private static Stroke defaultStroke = new BasicStroke( 1 );
    private static Stroke selectedStroke = new BasicStroke( 2 );
    private static Stroke nearestToSelectedStroke = new BasicStroke( 2 );

    private static Paint defaultStrokePaint = Color.black;
    private static Paint selectedStrokePaint = Color.red;
    private static Paint nearestToSelectedStrokePaint = new Color( 255, 0, 255 );
    private SimpleMolecule molecule;
    private PPath pPath;

    public SimpleMoleculeGraphic( SimpleMolecule molecule ) {
        this.molecule = molecule;
        molecule.addObserver( this );
        molecule.addListener( this );

        Shape s = new Ellipse2D.Double( -molecule.getRadius(),
                                        -molecule.getRadius(),
                                        molecule.getRadius() * 2,
                                        molecule.getRadius() * 2 );
        pPath = new PPath( s, defaultStroke );
        pPath.setPaint( getColor( molecule ) );
        pPath.setStrokePaint( defaultStrokePaint );
        addChild( pPath );
        update();
    }

    public void update() {
        setOffset( molecule.getPosition() );
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of SimpleMolecule.Listener
    //--------------------------------------------------------------------------------------------------

    public void selectionStatusChanged( SimpleMolecule molecule ) {
        if( molecule.getSelectionStatus() == Selectable.SELECTED ) {
            pPath.setStroke( selectedStroke );
            pPath.setStrokePaint( selectedStrokePaint );
        }
        else if( molecule.getSelectionStatus() == Selectable.NEAREST_TO_SELECTED) {
            pPath.setStroke( nearestToSelectedStroke );
            pPath.setStrokePaint( nearestToSelectedStrokePaint );
        }
        else {
            pPath.setStroke( defaultStroke );
            pPath.setStrokePaint( defaultStrokePaint );
        }
    }
}
