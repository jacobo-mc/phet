/**
 * Class: NuclearPhysicsModel
 * Package: edu.colorado.phet.nuclearphysics.model
 * Author: Another Guy
 * Date: Oct 14, 2004
 */
package edu.colorado.phet.nuclearphysics.model;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;

import java.util.LinkedList;
import java.util.List;

public class NuclearPhysicsModel extends BaseModel {
    private LinkedList nuclearModelElements = new LinkedList();

    public void removeModelElement( ModelElement m ) {
        super.removeModelElement( m );
        if( m instanceof NuclearModelElement ) {
            NuclearModelElement nuclearModelElement = (NuclearModelElement)m;
            nuclearModelElements.remove( nuclearModelElement );
            nuclearModelElement.leaveSystem();
        }
    }

    public void addModelElement( ModelElement modelElement ) {
        super.addModelElement( modelElement );
        if( modelElement instanceof NuclearModelElement ) {
            nuclearModelElements.add( modelElement );
        }
    }

    public void removeNuclearPartilces() {
        while( nuclearModelElements.size() > 0 ) {
            NuclearModelElement nuclearModelElement = (NuclearModelElement)nuclearModelElements.get( 0 );
            this.removeModelElement( nuclearModelElement );
            nuclearModelElements.remove( nuclearModelElement );
        }
    }

    public List getNuclearModelElements() {
        return nuclearModelElements;
    }
}
