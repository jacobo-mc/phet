//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.DensityAndBuoyancyConstants;

import mx.containers.Box;
import mx.containers.VBox;

/**
 * Provides static methods for providing styling (e.g., color, padding) for Box instances
 */
public class DensityVBox extends VBox {
    public function DensityVBox() {
        super();
        init( this );
    }

    //REVIEW putting this in DensityVBox and then calling it for non-DensityVBoxes (eg, DensityHBox constructor) is confusing.
    public static function init( box: Box ): void {
        box.setStyle( "backgroundColor", DensityAndBuoyancyConstants.CONTROL_PANEL_COLOR );
        box.setStyle( "borderStyle", "solid" );

        box.setStyle( "cornerRadius", 5 );

        //Padding so the buttons don't touch the edge
        box.setStyle( "paddingTop", 5 );
        box.setStyle( "paddingBottom", 5 );
        box.setStyle( "paddingLeft", 5 );
        box.setStyle( "paddingRight", 5 );
    }
}
}