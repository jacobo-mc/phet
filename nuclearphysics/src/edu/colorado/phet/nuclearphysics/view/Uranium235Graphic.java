/**
 * Class: Uranium235Graphic
 * Package: edu.colorado.phet.nuclearphysics.view
 * Author: Another Guy
 * Date: Mar 19, 2004
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.nuclearphysics.model.Nucleus;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Uranium235Graphic extends NucleusGraphic {
    private static Font font = new Font( "Serif", Font.BOLD, 15 );
    private static Color color = Color.yellow;
    private static AffineTransform nucleusTx = new AffineTransform();

    public Uranium235Graphic( Nucleus nucleus ) {
        super( nucleus );
        this.nucleus = nucleus;
    }

    public void paint( Graphics2D g ) {
        nucleusTx.setToTranslation( nucleus.getLocation().getX(), nucleus.getLocation().getY() );
        super.paint( g );

        AffineTransform orgTx = g.getTransform();
        g.transform( nucleusTx );
        g.setFont( font );
        g.setColor( color );
        g.drawString( "U235", 0, 0 );
        g.setTransform( orgTx );
    }
}
