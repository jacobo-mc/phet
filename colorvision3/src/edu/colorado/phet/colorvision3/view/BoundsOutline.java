/* BoundsOutline.java */

package edu.colorado.phet.colorvision3.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Stroke;

import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;

/**
 * BoundsOutline is a class used for debugging the bondaries of graphics components.
 * <p>
 * In the paint method of your component (typically at the end), add a call to 
 * BoundsOutline.paint.  If BoundsOutline.isEnabled is true, the bounds of your 
 * component will be rendered as an outline, in a Color that you specify.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @revision $Id$
 */
public class BoundsOutline
{
	//----------------------------------------------------------------------------
	// Class data
  //----------------------------------------------------------------------------

  // Default stroke
  private static final Stroke DEFAULT_STROKE = new BasicStroke(1f);
  // Default paint
  private static final Paint DEFAULT_PAINT = Color.RED;
  
  // Global control of bounds rendering.
  private static boolean _enabled = false;
  
	//----------------------------------------------------------------------------
	// Constructors
  //----------------------------------------------------------------------------

  /**
   * Not intended for instantiation.
   */
  private BoundsOutline() {}
  
	//----------------------------------------------------------------------------
	// Accessors
  //----------------------------------------------------------------------------

  /**
   * Globally enabled/disables rendering of bounds.
   * 
   * @param enabled true to enable, false to disable
   */
  public static void setEnabled( boolean enabled )
  {
    _enabled = enabled;
  }
  
  /**
   * Determines if rendering of bounds is enabled.
   * 
   * @return true if enabled, false if disabled
   */
  public static boolean isEnabled()
  {
    return _enabled;
  }
  
	//----------------------------------------------------------------------------
	// Rendering
  //----------------------------------------------------------------------------

  /**
   * Draws a graphic component's bounds using a default paint and stroke.
   * If disabled, draws nothing.
   * 
   * @param g2 the 2D graphics context
   * @param component the graphics component
   */
  public static void paint( Graphics2D g2, PhetGraphic component )
  {
    if ( _enabled )
    {
      BoundsOutline.paint( g2, component, DEFAULT_PAINT, DEFAULT_STROKE );
    }
  }
  
  /**
   * Draws a graphic component's bounds using a specified paint and default stroke.
   * If disabled, draws nothing.
   * 
   * @param g2 the 2D graphics context
   * @param component the graphics component
   * @param paint the paint to use for the outline
   */
  public static void paint( Graphics2D g2, PhetGraphic component, Paint paint )
  {
    if ( _enabled )
    {
      BoundsOutline.paint( g2, component, paint, DEFAULT_STROKE );
    }
  }
  
  /**
   * Draws a graphic component's bounds using a specified paint and stroke.
   * If disabled, draws nothing.
   * 
   * @param g2 the 2D graphics context
   * @param component the graphics component
   * @param paint the paint to use for the outline
   * @param troke the stroke to use for the outline
   */
  public static void paint( Graphics2D g2, PhetGraphic component, Paint paint, Stroke stroke )
  {
    if ( _enabled )
    {
      BoundsOutline.paint( g2, component.getBounds(), paint, stroke );
    }
  }
  
  /**
   * Draws the specified bounds using a specified paint and stroke.
   * If disabled, draws nothing.
   * 
   * @param g2 the 2D graphics context
   * @param bounds the bounds
   * @param paint the paint to use for the outline
   * @param stroke the stroke to use for the outline
   */
  public static void paint( Graphics2D g2, Rectangle bounds, Paint paint, Stroke stroke )
  {
    if ( _enabled )
    {
      Paint oldPaint = g2.getPaint();
      Stroke oldStroke = g2.getStroke();
      
      Rectangle r = new Rectangle( bounds.x, bounds.y, bounds.width-1, bounds.height-1 );
      g2.setPaint( paint );
      g2.setStroke( stroke );
      g2.draw( r );
      
      g2.setPaint( oldPaint );
      g2.setStroke( oldStroke );
    }
  }
}


/* end of file */