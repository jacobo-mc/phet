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

import edu.colorado.phet.molecularreactions.model.SimpleMolecule;
import edu.colorado.phet.molecularreactions.model.Selectable;

/**
 * SimpleMoleculeGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class EnergySimpleMoleculeGraphic extends AbstractSimpleMoleculeGraphic {

    public EnergySimpleMoleculeGraphic( SimpleMolecule molecule ) {
        super( molecule );
    }

    public void update() {
        super.update();
    }

    public void selectionStatusChanged( SimpleMolecule molecule ) {
        if( molecule.getSelectionStatus() == Selectable.NOT_SELECTED ) {
            setVisible( false );
        }
        super.selectionStatusChanged( molecule );
    }
}
