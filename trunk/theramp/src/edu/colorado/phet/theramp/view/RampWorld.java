/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.theramp.RampModule;
import edu.colorado.phet.theramp.model.RampModel;
import edu.colorado.phet.theramp.model.Surface;
import edu.colorado.phet.theramp.view.arrows.*;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 1, 2005
 * Time: 2:56:14 PM
 * Copyright (c) Jun 1, 2005 by Sam Reid
 */

public class RampWorld extends PNode {
    private ArrayList arrowSets = new ArrayList();
    private SurfaceGraphic rampGraphic;
    private BlockGraphic blockGraphic;
    private AbstractArrowSet cartesian;
    private AbstractArrowSet perp;
    private AbstractArrowSet parallel;
    private XArrowSet xArrowSet;
    private YArrowSet yArrowSet;
    private PotentialEnergyZeroGraphic potentialEnergyZeroGraphic;
    private LeanerGraphic leanerGraphic;
    private EarthGraphic earthGraphic;
    private SkyGraphic skyGraphic;
    private SurfaceGraphic groundGraphic;
    //todo piccolo
//    private MeasuringTape measuringTape;
    private RightBarrierGraphic rightBarrierGraphic;
    private LeftBarrierGraphic leftBarrierGraphic;

    public RampWorld( RampModule module, RampPanel rampPanel ) {
        super();
        RampModel rampModel = module.getRampModel();
        Surface ramp = rampModel.getRamp();
        rampGraphic = new RampGraphic( rampPanel, ramp );
        earthGraphic = new EarthGraphic( rampPanel, this );
        skyGraphic = new SkyGraphic( rampPanel, this );
        groundGraphic = new FloorGraphic( rampPanel, rampModel.getGround() );
        blockGraphic = new BlockGraphic( module, rampPanel, rampGraphic, groundGraphic, rampModel.getBlock(), module.getRampObjects()[0] );
        rightBarrierGraphic = new RightBarrierGraphic( rampPanel, rampPanel, rampGraphic );
        leftBarrierGraphic = new LeftBarrierGraphic( rampPanel, rampPanel, groundGraphic );


        addChild( skyGraphic );
        addChild( earthGraphic );
        addChild( rampGraphic );
        addChild( groundGraphic );

        addChild( leftBarrierGraphic );
        addChild( rightBarrierGraphic );

        addChild( blockGraphic );

        cartesian = new CartesianArrowSet( rampPanel, getBlockGraphic() );
        perp = new PerpendicularArrowSet( rampPanel, getBlockGraphic() );
        parallel = new ParallelArrowSet( rampPanel, getBlockGraphic() );
        xArrowSet = new XArrowSet( rampPanel, getBlockGraphic() );
        yArrowSet = new YArrowSet( rampPanel, getBlockGraphic() );
        addArrowSet( cartesian );
        addArrowSet( perp );
        addArrowSet( parallel );
        addArrowSet( xArrowSet );
        addArrowSet( yArrowSet );

        perp.setVisible( false );
        parallel.setVisible( false );
        xArrowSet.setVisible( false );
        yArrowSet.setVisible( false );

        potentialEnergyZeroGraphic = new PotentialEnergyZeroGraphic( rampPanel, rampModel, this );
        addChild( potentialEnergyZeroGraphic );

        try {
            leanerGraphic = new LeanerGraphic( rampPanel, blockGraphic.getObjectGraphic(), this );
            addChild( leanerGraphic );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        //todo piccolo
//        measuringTape = new MeasuringTape( rampPanel, rampGraphic.getScreenTransform(),
//                                           RectangleUtils.getCenter2D( rampGraphic.getScreenTransform().getModelBounds() ) );
//        measuringTape.setVisible( false );
//        addChild( measuringTape );

    }

    void updateArrowSetGraphics() {
        for( int i = 0; i < arrowSets.size(); i++ ) {
            AbstractArrowSet arrowSet = (AbstractArrowSet)arrowSets.get( i );
            arrowSet.updateGraphics();
        }
    }

    public BlockGraphic getBlockGraphic() {
        return blockGraphic;
    }

    public void setCartesianArrowsVisible( boolean selected ) {
        cartesian.setVisible( selected );
    }

    public void setParallelArrowsVisible( boolean selected ) {
        parallel.setVisible( selected );
    }

    public void setPerpendicularArrowsVisible( boolean selected ) {
        perp.setVisible( selected );
    }

    public void setXArrowsVisible( boolean selected ) {
        xArrowSet.setVisible( selected );
    }

    public void setYArrowsVisible( boolean selected ) {
        yArrowSet.setVisible( selected );
    }

    public boolean isCartesianVisible() {
        return cartesian.getVisible();
    }

    public boolean isParallelVisible() {
        return parallel.getVisible();
    }

    public boolean isPerpendicularVisible() {
        return perp.getVisible();
    }

    public boolean isXVisible() {
        return xArrowSet.getVisible();
    }

    public boolean isYVisible() {
        return yArrowSet.getVisible();
    }

    public SurfaceGraphic getRampGraphic() {
        return rampGraphic;
    }

    public double getBlockWidthModel() {
        int widthView = blockGraphic.getObjectWidthView();
        double widthModel = rampGraphic.getScreenTransform().viewToModelDifferentialX( widthView );
        return widthModel;
    }

    public double getModelWidth( int viewWidth ) {
        return rampGraphic.getScreenTransform().viewToModelDifferentialX( viewWidth );
    }

    private void addArrowSet( AbstractArrowSet arrowSet ) {
        addChild( arrowSet );
        arrowSets.add( arrowSet );
    }

    public void setForceVisible( String force, boolean selected ) {
        for( int i = 0; i < arrowSets.size(); i++ ) {
            AbstractArrowSet arrowSet = (AbstractArrowSet)arrowSets.get( i );
            arrowSet.setForceVisible( force, selected );
        }
    }

    public int getRampBaseY() {
        Point v = getRampGraphic().getViewLocation( getRampGraphic().getSurface().getLocation( 0 ) );
        return v.y;
    }

    public Point convertToWorld( Point2D screenPt ) {
        AffineTransform affineTransform = getTransform();
        Point2D out = null;
        try {
            out = affineTransform.inverseTransform( screenPt, null );//todo ignores registration point.
        }
        catch( NoninvertibleTransformException e ) {
            e.printStackTrace();
        }
        return new Point( (int)out.getX(), (int)out.getY() );
    }

    public void setMeasureTapeVisible( boolean visible ) {
        //todo piccolo
//        measuringTape.setVisible( visible );
    }
}
