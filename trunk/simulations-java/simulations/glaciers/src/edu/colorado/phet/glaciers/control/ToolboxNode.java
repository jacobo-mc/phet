/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.control.AbstractToolIconNode.BoreholeDrillIconNode;
import edu.colorado.phet.glaciers.control.AbstractToolIconNode.GlacialBudgetMeterIconNode;
import edu.colorado.phet.glaciers.control.AbstractToolIconNode.IceThicknessToolIconNode;
import edu.colorado.phet.glaciers.control.AbstractToolIconNode.ThermometerIconNode;
import edu.colorado.phet.glaciers.control.AbstractToolIconNode.ToolIconListener;
import edu.colorado.phet.glaciers.control.AbstractToolIconNode.TracerFlagIconNode;
import edu.colorado.phet.glaciers.control.AbstractToolIconNode.TrashCanIconNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * 
 * ToolboxNode is the toolbox. It contains a collection of tools, 
 * positioned on a background, with a title tab in the upper left corner.
 * The origin of this node is at the upper-left corner of the tab.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ToolboxNode extends PNode {
    
    // spacing properties
    private static final int HORIZONTAL_TOOL_SPACING = 15; // horizontal space between tools
    private static final int BACKGROUND_MARGIN = 5; // margin between the background and the tools
    private static final int TAB_MARGIN = 5; // margin between the tab and its title text
    
    // background properties
    private static final Color BACKGROUND_COLOR = Color.LIGHT_GRAY; // toolbox background
    private static final Color BACKGROUND_STROKE_COLOR = new Color( 82, 126, 90 ); // green
    private static final Stroke BACKGROUND_STROKE = new BasicStroke( 2f );
    private static final double BACKGROUND_CORNER_RADIUS = 10;
    
    // tab properties
    private static final Font TAB_LABEL_FONT = new PhetDefaultFont( 12 );
    private static final Color TAB_LABEL_COLOR = Color.BLACK;
    private static final Color TAB_COLOR = BACKGROUND_COLOR;
    private static final Color TAB_STROKE_COLOR = BACKGROUND_STROKE_COLOR;
    private static final Stroke TAB_STROKE = BACKGROUND_STROKE;
    private static final double TAB_CORNER_RADIUS = BACKGROUND_CORNER_RADIUS;
    
    private ArrayList _toolIconNodes;
    
    /**
     * Constructor.
     */
    public ToolboxNode() {
        super();
        
        _toolIconNodes = new ArrayList();
        
        // create tools, under a common parent
        PNode toolsParent = new PNode();
        {
            AbstractToolIconNode thermometerIconNode = new ThermometerIconNode();
            _toolIconNodes.add( thermometerIconNode );
            AbstractToolIconNode glacialBudgetMeterIconNode = new GlacialBudgetMeterIconNode();
            _toolIconNodes.add( glacialBudgetMeterIconNode );
            AbstractToolIconNode tracerFlagIconNode = new TracerFlagIconNode();
            _toolIconNodes.add( tracerFlagIconNode );
            AbstractToolIconNode iceThicknessToolIconNode = new IceThicknessToolIconNode();
            _toolIconNodes.add( iceThicknessToolIconNode );
            AbstractToolIconNode boreholeDrillIconNode = new BoreholeDrillIconNode();
            _toolIconNodes.add( boreholeDrillIconNode );
            AbstractToolIconNode trashCan = new TrashCanIconNode();

            toolsParent.addChild( thermometerIconNode );
            toolsParent.addChild( glacialBudgetMeterIconNode );
            toolsParent.addChild( tracerFlagIconNode );
            toolsParent.addChild( iceThicknessToolIconNode );
            toolsParent.addChild( boreholeDrillIconNode );
            toolsParent.addChild( trashCan );
            final double maxToolHeight = toolsParent.getFullBoundsReference().getHeight();
            
            // arrange tools in the toolbox from left to right, vertically centered
            double x = 0;
            double y = ( maxToolHeight - thermometerIconNode.getFullBoundsReference().getHeight() ) / 2;
            thermometerIconNode.setOffset( x, y );
            
            x = thermometerIconNode.getFullBoundsReference().getMaxX() + HORIZONTAL_TOOL_SPACING;
            y = ( maxToolHeight - glacialBudgetMeterIconNode.getFullBoundsReference().getHeight() ) / 2;
            glacialBudgetMeterIconNode.setOffset( x, y );
            
            x = glacialBudgetMeterIconNode.getFullBoundsReference().getMaxX() + HORIZONTAL_TOOL_SPACING;
            y = ( maxToolHeight - tracerFlagIconNode.getFullBoundsReference().getHeight() ) / 2;
            tracerFlagIconNode.setOffset( x, y );
            
            x = tracerFlagIconNode.getFullBoundsReference().getMaxX() + HORIZONTAL_TOOL_SPACING;
            y = ( maxToolHeight - iceThicknessToolIconNode.getFullBoundsReference().getHeight() ) / 2;
            iceThicknessToolIconNode.setOffset( x, y );
            
            x = iceThicknessToolIconNode.getFullBoundsReference().getMaxX() + HORIZONTAL_TOOL_SPACING;
            y = ( maxToolHeight - boreholeDrillIconNode.getFullBoundsReference().getHeight() ) / 2;
            boreholeDrillIconNode.setOffset( x, y );
            
            x = boreholeDrillIconNode.getFullBoundsReference().getMaxX() + HORIZONTAL_TOOL_SPACING;
            y = ( maxToolHeight - trashCan.getFullBoundsReference().getHeight() ) / 2;
            trashCan.setOffset( x, y );
        }
        
        // create the background
        PPath backgroundNode = new PPath();
        {
            final double backgroundWidth = toolsParent.getFullBoundsReference().getWidth() + ( 2 * BACKGROUND_MARGIN );
            final double backgroundHeight = toolsParent.getFullBoundsReference().getHeight() + ( 2 * BACKGROUND_MARGIN );
            RoundRectangle2D r = new RoundRectangle2D.Double( 0, 0, backgroundWidth, backgroundHeight, BACKGROUND_CORNER_RADIUS, BACKGROUND_CORNER_RADIUS );
            backgroundNode.setPathTo( r );
            backgroundNode.setPaint( BACKGROUND_COLOR );
            backgroundNode.setStroke( BACKGROUND_STROKE );
            backgroundNode.setStrokePaint( BACKGROUND_STROKE_COLOR );
        }
        
        // create the title tab
        PComposite tabNode = new PComposite();
        final double tabOverlap = 100;
        {
            PText titleNode = new PText( GlaciersStrings.TITLE_TOOLBOX );
            titleNode.setFont( TAB_LABEL_FONT );
            titleNode.setTextPaint( TAB_LABEL_COLOR );
            
            final double tabWidth = titleNode.getFullBoundsReference().getWidth() + ( 2 * TAB_MARGIN );
            final double tabHeight = titleNode.getFullBoundsReference().getHeight() + ( 2 * TAB_MARGIN ) + tabOverlap;
            RoundRectangle2D r = new RoundRectangle2D.Double( 0, 0, tabWidth, tabHeight, TAB_CORNER_RADIUS, TAB_CORNER_RADIUS );
            PPath pathNode = new PPath( r );
            pathNode.setPaint( TAB_COLOR );
            pathNode.setStroke( TAB_STROKE );
            pathNode.setStrokePaint( TAB_STROKE_COLOR );
            
            tabNode.addChild( pathNode );
            tabNode.addChild( titleNode );
            
            pathNode.setOffset( 0, 0 );
            titleNode.setOffset( TAB_MARGIN, TAB_MARGIN );
        }
       
        addChild( tabNode );
        addChild( backgroundNode );
        addChild( toolsParent );
        
        // origin at upper left corner of tab
        tabNode.setOffset( 0, 0 );
        backgroundNode.setOffset( tabNode.getFullBounds().getX(), tabNode.getFullBounds().getMaxY() - tabOverlap );
        toolsParent.setOffset( backgroundNode.getFullBounds().getX() + BACKGROUND_MARGIN, backgroundNode.getFullBounds().getY() + BACKGROUND_MARGIN );
        
        // only the tools are interactive
        this.setPickable( false );
        toolsParent.setPickable( false );
        backgroundNode.setPickable( false );
        backgroundNode.setChildrenPickable( false );
        tabNode.setPickable( false );
        tabNode.setChildrenPickable( false );
    }
    
    public void addListener( ToolIconListener listener ) {
        Iterator i = _toolIconNodes.iterator();
        while ( i.hasNext() ) {
            ((AbstractToolIconNode)i.next()).addListener( listener );
        }
    }
    
    public void removeListener( ToolIconListener listener ) {
        Iterator i = _toolIconNodes.iterator();
        while ( i.hasNext() ) {
            ((AbstractToolIconNode)i.next()).addListener( listener );
        }
    }
}
