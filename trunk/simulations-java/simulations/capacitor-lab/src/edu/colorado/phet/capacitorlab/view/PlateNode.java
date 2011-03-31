// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view;

import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.Polarity;
import edu.colorado.phet.capacitorlab.view.PlateChargeNode.AirPlateChargeNode;
import edu.colorado.phet.capacitorlab.view.PlateChargeNode.DielectricPlateChargeNode;

/**
 * Visual representation of a capacitor plate.
 * For a partially-inserted dielectric, the portion of the plate that contacts the dielectric
 * is charged differently than the portion of the plate that contacts air.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class PlateNode extends BoxNode {

    private final PlateChargeNode dielectricPlateChargeNode; // shows charge on the portion of the plate that contacts the dielectric
    private final PlateChargeNode airPlateChargeNode; // shows charge on the portion of the plate that contacts air

    public static class TopPlateNode extends PlateNode {
        public TopPlateNode( Capacitor capacitor, CLModelViewTransform3D mvt ) {
            super( capacitor, mvt, Polarity.POSITIVE );
        }
    }

    public static class BottomPlateNode extends PlateNode {
        public BottomPlateNode( Capacitor capacitor, CLModelViewTransform3D mvt ) {
            super( capacitor, mvt, Polarity.NEGATIVE );
        }
    }

    public PlateNode( Capacitor capacitor, CLModelViewTransform3D mvt, Polarity polarity ) {
        super( mvt, CLPaints.PLATE, capacitor.getPlateSize() );

        this.dielectricPlateChargeNode = new DielectricPlateChargeNode( capacitor, mvt, polarity );
        addChild( dielectricPlateChargeNode );

        this.airPlateChargeNode = new AirPlateChargeNode( capacitor, mvt, polarity );
        addChild( airPlateChargeNode );
    }

    public void setChargeVisible( boolean visible ) {
        dielectricPlateChargeNode.setVisible( visible );
        airPlateChargeNode.setVisible( visible );
    }

    public boolean isChargeVisible() {
        return dielectricPlateChargeNode.getVisible();
    }
}
