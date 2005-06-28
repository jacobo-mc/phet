/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;


/**
 * FlatGraphic flattens all of its children into a static image.
 * Since the graphic is flat, no events are propogated to children.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FlatGraphic extends PhetImageGraphic {

    /* Handles the layering and rendering */
    private GraphicLayerSet _graphicLayerSet;
    
    /* If true, automatically flattens whenever a child graphic is added or removed. */
    private boolean _autoFlattenEnabled;
    
    /**
     * Sole constructor.
     * 
     * @param component
     */
    public FlatGraphic( Component component ) {
        super( component );
        _graphicLayerSet = new GraphicLayerSet( component );
        _autoFlattenEnabled = false;
    }
    
    /**
     * Sets automatic flattening.
     * If enabled, the graphic is automatically flattened whenever 
     * a child is added or removed.  By default, automatic flattening
     * is disabled.  For performance reasons, you may wish to leave it disabled.
     * This will allow you to add/remove a set of children without incurring
     * the rendering expense associated with flattening.
     * 
     * @param autoFlattenEnabled true or false
     */
    public void setAutoFlattenEnabled( boolean autoFlattenEnabled ) {
        if ( autoFlattenEnabled != _autoFlattenEnabled ) {
            _autoFlattenEnabled = autoFlattenEnabled;
            if ( _autoFlattenEnabled ) {
                flatten();
            }
        }
    }
    
    /**
     * Determines whether automatic flattening is enabled.
     * 
     * @return true or false
     */
    public boolean isAutoFlattenEnabled() {
        return _autoFlattenEnabled;
    }
    
    /**
     * Adds a child graphic to a specific layer and re-flattens.
     * 
     * @param graphic
     * @param layer
     */
    public void addGraphic( PhetGraphic graphic, double layer ) {
        _graphicLayerSet.addGraphic( graphic, layer );
        if ( _autoFlattenEnabled ) {
            flatten();
        }
    }
    
    /**
     * Adds a child graphic to the zero-th layer and re-flattens.
     * 
     * @param graphic
     */
    public void addGraphic( PhetGraphic graphic ) {
        _graphicLayerSet.addGraphic( graphic );
        if ( _autoFlattenEnabled ) {
            flatten();
        }
    }
    
    /**
     * Removes a child graphic and re-flattens.
     * 
     * @param graphic
     */
    public void removeGraphic( PhetGraphic graphic ) {
        _graphicLayerSet.removeGraphic( graphic );
        if ( _autoFlattenEnabled ) {
            flatten();
        }
    }
    
    /**
     * Flattens the graphic.
     * <br>
     * If automatic flattening is not enabled, then you need to 
     * call this after adding or removing child graphics.
     * <br>
     * Even if automatic flattening is enabled, clients 
     * will need to call this if they are using references to
     * manipulate child graphics.
     */
    public void flatten() {

        // Determine the offset required to get all graphics drawn into the buffer.
        Rectangle graphicsLayerSetBounds = _graphicLayerSet.getBounds();
        Rectangle thisBounds = getBounds();
        int width = _graphicLayerSet.getWidth();
        int height = _graphicLayerSet.getHeight();
        double xOffset = thisBounds.x - graphicsLayerSetBounds.x;
        double yOffset = thisBounds.y - graphicsLayerSetBounds.y;

        // Draw into the buffer.
        BufferedImage bufferedImage = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2 = bufferedImage.createGraphics();
        _graphicLayerSet.translate( xOffset, yOffset );
        _graphicLayerSet.paint( g2 );

        // Set the image.
        setImage( bufferedImage );

        // Adjust the registration point so that the image appears at the 
        // same screen location as it would if the graphics were not flattened.
        setRegistrationPoint( (int) xOffset, (int) yOffset );
    }
}
