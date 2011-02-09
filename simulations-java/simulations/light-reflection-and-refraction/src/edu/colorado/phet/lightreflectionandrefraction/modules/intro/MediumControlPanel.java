// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.intro;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PComboBox;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * @author Sam Reid
 */
public class MediumControlPanel extends PNode {

    public MediumControlPanel( final PhetPCanvas phetPCanvas, final Property<Medium> medium, final Property<Function1<Double, Color>> colorMappingFunction ) {
        final double inset = 12;
        final PNode content = new PNode() {{
            final PhetFont labelFont = new PhetFont( 16 );
            final PText materialLabel = new PText( "Material:" ) {{
                setFont( labelFont );
            }};
            addChild( materialLabel );
            final PComboBox[] x = new PComboBox[1];
            final PSwing comboBoxPSwing = new PSwing( x[0] = new PComboBox( new Object[] { "Air", "Water", "Glass", "Mystery A", "Mystery B" } ) {{
                setFont( labelFont );
            }} ) {{
                x[0].setEnvironment( this, phetPCanvas );
                setOffset( materialLabel.getFullBounds().getMaxX() + 10, materialLabel.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 + 1 );
            }};
            addChild( comboBoxPSwing );

            final PSwing slider = new PSwing( new IndexOfRefractionSlider( medium, colorMappingFunction, "" ) {{
                SwingUtils.setBackgroundDeep( this, new Color( 0, 0, 0, 0 ) );
                getTextField().setBackground( Color.white );
                getTextField().setFont( labelFont );
            }} ) {{
                setOffset( 0, materialLabel.getFullBounds().getMaxY() );
            }};
            addChild( slider );

            final PText indexOfRefractionLabel = new PText( "Index of Refraction (n)" ) {{
                setFont( labelFont );
                setOffset( 0, slider.getFullBounds().getMaxY() );
            }};
            addChild( indexOfRefractionLabel );
            setOffset( inset, inset );
        }};

        final PhetPPath background = new PhetPPath( Color.lightGray, new BasicStroke( 2 ), Color.darkGray ) {{
            final PropertyChangeListener updateSize = new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    final PBounds b = content.getFullBounds();
                    setPathTo( new RoundRectangle2D.Double( 0, 0, b.getWidth() + inset * 2, b.getHeight() + inset * 2, 20, 20 ) );
                }
            };
            content.addPropertyChangeListener( PROPERTY_FULL_BOUNDS, updateSize );
            updateSize.propertyChange( null );
        }};
        addChild( background );
        addChild( content );
    }

    public static void main( String[] args ) throws ClassNotFoundException, UnsupportedLookAndFeelException, IllegalAccessException, InstantiationException, InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait( new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
                }
                catch ( ClassNotFoundException e ) {
                    e.printStackTrace();
                }
                catch ( InstantiationException e ) {
                    e.printStackTrace();
                }
                catch ( IllegalAccessException e ) {
                    e.printStackTrace();
                }
                catch ( UnsupportedLookAndFeelException e ) {
                    e.printStackTrace();
                }
                new JFrame() {{
                    setContentPane( new PhetPCanvas() {{
                        getLayer().addChild( new MediumControlPanel( this, null, null ) {{
                            System.out.println( "getFullBounds() = " + getFullBounds() );
//                            setOffset( 100, 100 );
//                            scale( 2 );
                        }} );
                    }} );
                    setSize( 800, 600 );
                    setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
                }}.setVisible( true );
            }
        } );
    }
}
