/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.colorado.phet.nuclearphysics.model.Nucleon;

/**
 * This class displays a visual representation of the neutron on the canvas.
 *
 * @author John Blanco
 */
public class NeutronNode extends SphericalNode implements NucleonNode {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    private final static double PARTICLE_DIAMETER = 1.6;  // Femto meters.
    private static final Color COLOR = Color.DARK_GRAY;
    private static final Color HILITE_COLOR = new Color(0xeeeeee);
    private static final Paint ROUND_GRADIENT = new RoundGradientPaint( -PARTICLE_DIAMETER / 6, -PARTICLE_DIAMETER / 6,
            HILITE_COLOR, new Point2D.Double( PARTICLE_DIAMETER/2, PARTICLE_DIAMETER/2 ), COLOR );
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    private Nucleon _nucleon;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public NeutronNode(Nucleon nucleon)
    {
        super(PARTICLE_DIAMETER, ROUND_GRADIENT, false);
        
        _nucleon = nucleon;
        
        nucleon.addListener(new Nucleon.Listener(){
            public void positionChanged()
            {
                update();
            }
            
        });
        
        setPickable( false );
        setChildrenPickable( false );
        
        // Call update at the end of construction to assure that the view is
        // synchronized with the model.
        update();
    }
    
    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------
    
    public Nucleon getNucleon(){
        return _nucleon;
    }
    
    //------------------------------------------------------------------------
    // Public Methods
    //------------------------------------------------------------------------

    //------------------------------------------------------------------------
    // Private Methods
    //------------------------------------------------------------------------

    private void update(){
        setOffset( _nucleon.getPositionReference().getX() - PARTICLE_DIAMETER/2,  
                _nucleon.getPositionReference().getY() - PARTICLE_DIAMETER/2);
    }
}
