/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.view;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D.Double;

import edu.colorado.phet.common.phetcommon.patterns.Updatable;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;
import edu.umd.cs.piccolo.PNode;

/**
 * This class is a Piccolo PNode extension that represents a particle in the view.
 *
 * @author John Blanco
 */
public class ParticleNode extends PNode {
    
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    public static final ParticleNode TEST = new ParticleNode(StatesOfMatterParticle.TEST);

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private StatesOfMatterParticle m_particle;
    private final StatesOfMatterParticle.Listener m_particleListener;
    
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public ParticleNode(StatesOfMatterParticle particle) {
        m_particle = particle;
        
        // Set ourself up to listen to this particle.
        m_particleListener = new StatesOfMatterParticle.Listener(){
            public void positionChanged(){
                updatePosition();
            }
            public void particleRemoved(StatesOfMatterParticle particle){
                handleParticleRemoved(particle);
            }
        };
        particle.addListener( m_particleListener );
        
        // Create the image that will represent this particle.
        PhetPPath path = new PhetPPath(Color.blue);
        path.setPathTo( new Ellipse2D.Double(-particle.getRadius(), -particle.getRadius(), particle.getRadius() * 2, 
                particle.getRadius() * 2 ));
        addChild(path);
        
        // Set ourself to be non-pickable so that we don't get mouse events.
        setPickable( false );
        setChildrenPickable( false );

        updatePosition();
    }

    //----------------------------------------------------------------------------
    // Public Methods
    //----------------------------------------------------------------------------
    
    public void updatePosition() {
        if (m_particle != null){
            setOffset(m_particle.getX(), m_particle.getY());
        }
    }

    /**
     * Handle the removal of the particle within the model that is being
     * represented in the view by this particle.  This is done by removing
     * ourself from the canvas and by cleaning up any memory references so
     * that we can be garbage collected.
     * 
     * @param particle
     */
    private void handleParticleRemoved(StatesOfMatterParticle particle){
        
        // Remove ourself from the canvas.
        PNode parent = getParent();
        if (parent != null){
            parent.removeChild( this );
        }
        
        // Remove all children, since they have a reference to this object.
        removeAllChildren();
        
        // Explicitly clear our reference to the particle in the model.
        m_particle = null;
    }
}
