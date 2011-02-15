// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.prisms;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.Property;

/**
 * @author Sam Reid
 */
public class Prism {
    public final Property<Polygon> shape;

    public Prism( Polygon polygon ) {
        this.shape = new Property<Polygon>( polygon );
    }

    public void translate( double dx, double dy ) {
        shape.setValue( shape.getValue().getTranslatedInstance( dx, dy ) );
    }

    public ArrayList<Intersection> getIntersections( Ray incidentRay ) {
        return shape.getValue().getIntersections( incidentRay );
    }
}
