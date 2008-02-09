/* Copyright 2004, Sam Reid */
package edu.colorado.phet.selfdrivenparticlemodel.view;

import edu.colorado.phet.selfdrivenparticlemodel.model.ParticleModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PClip;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Aug 23, 2005
 * Time: 12:33:23 AM
 * Copyright (c) Aug 23, 2005 by Sam Reid
 */

public class UniverseGraphic extends PNode {
    private PClip particleLayer;
    private PClip interactionLayer;

    public UniverseGraphic( ParticleModel model ) {
        interactionLayer = new PClip();
        interactionLayer.setPathTo( new Rectangle2D.Double( 0, 0, model.getBoxWidth(), model.getBoxHeight() ) );
        addChild( interactionLayer );

        particleLayer = new PClip();
        particleLayer.setPathTo( new Rectangle2D.Double( 0, 0, model.getBoxWidth(), model.getBoxHeight() ) );
        addChild( particleLayer );

        PPath path = new PPath( new Rectangle2D.Double( 0, 0, model.getBoxWidth(), model.getBoxHeight() ) );
        path.setPaint( Color.white );
        path.setStrokePaint( Color.black );
        path.setStroke( new BasicStroke( 2 ) );
        interactionLayer.addChild( path );
    }

    public void addInfluenceGraphic( ParticleInfluenceGraphic particleInfluenceGraphic ) {
        interactionLayer.addChild( particleInfluenceGraphic );
    }

    public void removeInfluenceGraphic( ParticleInfluenceGraphic particleInfluenceGraphic ) {
        interactionLayer.removeChild( particleInfluenceGraphic );
    }

    public void addParticleGraphic( ParticleGraphicWithTail particleGraphicWithTail ) {
        particleLayer.addChild( particleGraphicWithTail );
    }
//    public void fullPaint( PPaintContext paintContext ) {
//        Shape origClip = paintContext.getLocalClip();
//
//        super.fullPaint( paintContext );
//    }

    public void removeParticleGraphic( ParticleGraphicWithTail particleGraphicWithTail ) {
        particleLayer.removeChild( particleGraphicWithTail );
    }
}
