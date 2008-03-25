/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.alpharadiation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashMap;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Constants;
import edu.colorado.phet.nuclearphysics2.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics2.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics2.model.Neutron;
import edu.colorado.phet.nuclearphysics2.model.Proton;
import edu.colorado.phet.nuclearphysics2.view.AlphaParticleNode;
import edu.colorado.phet.nuclearphysics2.view.AlphaRadiationChart;
import edu.colorado.phet.nuclearphysics2.view.AtomicNucleusNode;
import edu.colorado.phet.nuclearphysics2.view.NeutronNode;
import edu.colorado.phet.nuclearphysics2.view.ProtonNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents the canvas upon which the view of the model is
 * displayed for the Alpha Radiation tab of this simulation.
 *
 * @author John Blanco
 */
public class AlphaRadiationCanvas extends PhetPCanvas {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Canvas size in femto meters.  Assumes a 4:3 aspect ratio.
    private final double CANVAS_WIDTH = 100;
    private final double CANVAS_HEIGHT = CANVAS_WIDTH * (3.0d/4.0d);
    
    // Translation factors, used to set origin of canvas area.
    private final double WIDTH_TRANSLATION_FACTOR = 2.0;
    private final double HEIGHT_TRANSLATION_FACTOR = 4.0;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    private AlphaRadiationChart _alphaRadiationChart;
    private HashMap _mapAlphaParticlesToNodes = new HashMap();

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public AlphaRadiationCanvas(AlphaRadiationModel alphaRadiationModel) {
        
        // Set the transform strategy in such a way that the center of the
        // visible canvas will be at 0,0.
        setWorldTransformStrategy( new RenderingSizeStrategy(this, 
                new PDimension(CANVAS_WIDTH, CANVAS_HEIGHT) ){
            protected AffineTransform getPreprocessedTransform(){
                return AffineTransform.getTranslateInstance( getWidth()/WIDTH_TRANSLATION_FACTOR, 
                        getHeight()/HEIGHT_TRANSLATION_FACTOR );
            }
        });
        
        // Get the nucleus from the model and then get the constituents
        // and create a visible node for each.
        
        AtomicNucleus atomicNucleus = alphaRadiationModel.getAtomNucleus();
        ArrayList nucleusConstituents = atomicNucleus.getConstituents();
        
        // Create a parent node where we will display the nucleus.  This is
        // being done so that a label can be placed over the top of it.
        PNode nucleusLayer = new PNode();
        addWorldChild(nucleusLayer);
        
        for (int i = 0; i < nucleusConstituents.size(); i++){
            
            Object constituent = nucleusConstituents.get( i );
            
            if (constituent instanceof AlphaParticle){
                // Add a visible representation of the alpha particle to the canvas.
                AlphaParticleNode alphaNode = new AlphaParticleNode((AlphaParticle)constituent);
                nucleusLayer.addChild( alphaNode );
            }
            else if (constituent instanceof Proton){
                // Add a visible representation of the proton to the canvas.
                ProtonNode protonNode = new ProtonNode((Proton)constituent);
                nucleusLayer.addChild( protonNode );
            }
            else if (constituent instanceof Neutron){
                // Add a visible representation of the neutron to the canvas.
                NeutronNode neutronNode = new NeutronNode((Neutron)constituent);
                nucleusLayer.addChild( neutronNode );
            }
            else {
                // There is some unexpected object in the list of constituents
                // of the nucleus.  This should never happen and should be
                // debugged if it does.
                assert false;
            }
        }

        // Add the nucleus node itself to the canvas, which is actually only
        // the label, since the individual nodes show the individual particles.
        // This must be added last so that it can appear on top of the nucleus.
        PNode labelLayer = new PNode();
        addWorldChild(labelLayer);
        AtomicNucleusNode nucleusNode = new AtomicNucleusNode(atomicNucleus);
        labelLayer.addChild( nucleusNode );

        
        // Register with the model for notifications of alpha particles coming
        // and going.
        // TODO: This may be obsolete, since the canvas now (as of March 19
        // 2008) gets the nucleus and registers with the sub-particles thereof
        // instead of watching the particles come and go directly from the
        // model.  Remove this if it is not needed within, say, a week.
        alphaRadiationModel.addListener( new AlphaRadiationModel.Listener(  ){
            
            /**
             * A new particle has been added in the model, so we need to
             * display it on the canvas.
             */
            public void particleAdded(AlphaParticle alphaParticle){
                
                AlphaParticleNode alphaParticleNode = new AlphaParticleNode(alphaParticle);
                
                // Add the particle to the world.
                addWorldChild( 0, alphaParticleNode );
                
                // Map the particle to the node so that we can remove it later.
                _mapAlphaParticlesToNodes.put( alphaParticle, alphaParticleNode );
            }
            
            /**
             * A particle has been removed from the model, so we need to
             * remove its representation from the canvas (i.e. view).
             */
            public void particleRemoved(AlphaParticle alphaParticle){
                AlphaParticleNode alphaParticleNode = 
                    (AlphaParticleNode)_mapAlphaParticlesToNodes.get( alphaParticle );
                assert alphaParticleNode != null;
                removeWorldChild( alphaParticleNode );
                _mapAlphaParticlesToNodes.remove( alphaParticleNode );
            }
        });
        
        // Set the background color.
        setBackground( NuclearPhysics2Constants.CANVAS_BACKGROUND );
        
        // Add the breakout radius to the canvas.
        double radius = AtomicNucleus.TUNNEL_OUT_RADIUS;
        PPath breakoutCircle = new PPath(new Ellipse2D.Double(-radius, -radius, 2*radius, 2*radius));
        breakoutCircle.setStroke( new BasicStroke(0.1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
                new float[] {0.75f, 0.75f }, 0) );
        breakoutCircle.setStrokePaint( new Color(0x990099) );
        addWorldChild(breakoutCircle);
        
        // Add the chart that depicts the tunneling energy threshold.
        this.
        _alphaRadiationChart = new AlphaRadiationChart(50, this);
        addScreenChild( _alphaRadiationChart );

        // Add the lines that make it clear that the tunneling radius is at
        // the point where the particle energy exceeds the potential energy.
        
        PPath leftBreakoutLine = new PPath(new Line2D.Double(-radius, 
                CANVAS_HEIGHT * HEIGHT_TRANSLATION_FACTOR, -radius, 
                CANVAS_HEIGHT * (1 - HEIGHT_TRANSLATION_FACTOR)));
        leftBreakoutLine.setStroke( new BasicStroke(0.1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
                new float[] {2, 2 }, 0) );
        leftBreakoutLine.setStrokePaint( new Color(0x990099) );
        addWorldChild(leftBreakoutLine);

        PPath rightBreakoutLine = new PPath(new Line2D.Double(radius, 
                CANVAS_HEIGHT * HEIGHT_TRANSLATION_FACTOR, radius, 
                CANVAS_HEIGHT * (1 - HEIGHT_TRANSLATION_FACTOR)));
        rightBreakoutLine.setStroke( new BasicStroke(0.1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
                new float[] {2, 2 }, 0) );
        rightBreakoutLine.setStrokePaint( new Color(0x990099) );
        addWorldChild(rightBreakoutLine);

        // Add a listener for when the canvas is resized.
        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                _alphaRadiationChart.componentResized( getWidth(), getHeight() );
            }
        } );
    }

}
