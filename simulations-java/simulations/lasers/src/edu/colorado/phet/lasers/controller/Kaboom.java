/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.controller;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.view.util.FrameSequence;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel2;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.quantum.model.Tube;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;
import edu.colorado.phet.lasers.model.LaserModel;

/**
 * Kaboom
 * <p/>
 * An animated graphic that declared that the laser has blown up.
 * <p/>
 * Unued, commented code is still in here that makes an image of the apparatus panel, then
 * gractures it nad makes the pieces spin away, off the frame.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Kaboom implements ModelElement {

    private BaseLaserModule module;
    private LaserModel model;
    private boolean kaboomed;
    private List kaboomGraphics = new ArrayList();
    private JLabel labelMessage;
    private PhetShapeGraphic backgroundGraphic;
    private double blackBacgroundLayer = Double.MAX_VALUE - 2;
    private double tileLayer = Double.MAX_VALUE;

    public Kaboom( BaseLaserModule module ) {
        // Unless the module has an apparatus panel at this time things won't work right
        if ( module.getApparatusPanel() == null ) {
            throw new RuntimeException( "Module doesn't have an apparatus panel" );
        }
        this.module = module;
        model = module.getLaserModel();
    }

    public void stepInTime( double dt ) {
        int numPhotons = model.getNumLasingPhotons();
        if ( numPhotons > LaserConfig.KABOOM_THRESHOLD && !kaboomed ) {
            model.reset();
            kaboom();
            kaboomed = true;
        }
    }

    private void kaboom() {
        final ApparatusPanel2 panel = (ApparatusPanel2) module.getApparatusPanel();

        Tube cavity = module.getCavity();
        Rectangle2D bounds = cavity.getBounds();
        bounds = RectangleUtils.expand( bounds, 30, 20 );
        bounds.setRect( bounds.getMinX(), bounds.getMinY(), panel.getWidth() - bounds.getMinX(), bounds.getHeight() );

        // Make a white rectangle to cover the cavity and the area to the right of it
        backgroundGraphic = new PhetShapeGraphic( panel, bounds, Color.white );
        panel.addGraphic( backgroundGraphic,
                          blackBacgroundLayer );
        SwingClock clock = new SwingClock( 1000 / 40, 1 );

        // Add the flames
        clock.start();
        Flames flames = new Flames( panel, clock );
        flames.setLocation( (int) cavity.getBounds().getMinX(), (int) cavity.getBounds().getMaxY() - flames.getHeight() );
        panel.addGraphic( flames, tileLayer - .5 );

        // Add the message to the user
        labelMessage = new JLabel( SimStrings.getInstance().getString( "Kaboom.message" ) );
        labelMessage.setFont( new Font( "Lucida sans", Font.BOLD, 24 ) );
        labelMessage.setForeground( Color.red );
        labelMessage.setLocation( -20, 10 );
        panel.add( labelMessage );
        labelMessage.reshape( panel.getWidth() / 2 - 200, panel.getHeight() / 2 - 70,
                              labelMessage.getPreferredSize().width,
                              labelMessage.getPreferredSize().height );
        panel.revalidate();       
    }

    public void clearGraphics( ApparatusPanel apparatusPanel ) {
        List kaboomGraphics = getKaboomGraphics();
        for ( int i = 0; i < kaboomGraphics.size(); i++ ) {
            PhetGraphic graphic = (PhetGraphic) kaboomGraphics.get( i );
            apparatusPanel.removeGraphic( graphic );
        }
        if ( backgroundGraphic != null ) {
            apparatusPanel.removeGraphic( backgroundGraphic );
        }
        if ( labelMessage != null ) {
            apparatusPanel.remove( labelMessage );
        }
        apparatusPanel.revalidate();
        apparatusPanel.repaint();
    }

    //----------------------------------------------------------------
    // Getters and setters
    //----------------------------------------------------------------

    public List getKaboomGraphics() {
        return kaboomGraphics;
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    private class Flames extends PhetImageGraphic {
        FrameSequence frames;

        protected Flames( final Component component, final IClock clock ) {
            super( component );
            try {
                frames = new FrameSequence( "lasers/images/flames", 15 );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            setImage( frames.getFrame( 0 ) );

            clock.addClockListener( new ClockAdapter() {
                public void clockTicked( ClockEvent event ) {
                    if ( frames.getCurrFrameNum() == ( frames.getNumFrames() - 1 ) ) {
                        clock.removeClockListener( this );
                        ( (ApparatusPanel) component ).removeGraphic( Flames.this );
                    }
                    else {
                        nextFrame();
                    }
                }
            } );
        }

        public void nextFrame() {
            setImage( frames.getNextFrame() );
        }
    }
}
