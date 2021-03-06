// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.view;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.selfdrivenparticlemodel.model.Particle;
import edu.colorado.phet.selfdrivenparticlemodel.model.ParticleModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

public class ParticleInfluenceGraphic extends PNode {
    private Particle p;
    private PPath child;
    private ParticleModel particleModel;
    public static int count = 0;

    public ParticleInfluenceGraphic( ParticleModel particleModel, Particle p ) {
        count++;
        this.particleModel = particleModel;
        this.p = p;

        double radius = getRadius();
        Ellipse2D.Double shape = new Ellipse2D.Double( -radius, -radius, radius * 2, radius * 2 );
        child = new PPath( shape );
        child.setStrokePaint( null );
        child.setPaint( Color.yellow );
        addChild( child );

        p.addListener( new Particle.Listener() {
            public void locationChanged() {
                update();
            }
        } );
        particleModel.addListener( new ParticleModel.Adapter() {
            public void radiusChanged() {
                update();
            }
        } );
        update();
    }

    private double getRadius() {
        double radius = particleModel.getRadius();
        return radius;
    }

    private void update() {
        double radius = getRadius();
        Ellipse2D.Double shape = new Ellipse2D.Double( -radius, -radius, radius * 2, radius * 2 );
        child.setPathTo( shape );
        setOffset( p.getX(), p.getY() );
    }

    public Particle getParticle() {
        return p;
    }
}
