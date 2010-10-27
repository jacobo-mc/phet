package edu.colorado.phet.buildanatom.modules.game.view;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * This node is a combination of a spinner and a piece of text, and is
 * settable to display either one of the other.
 */
public class ValueNode extends PNode {
    private final PText text = new PText( "0" );
    private final JSpinner spinner;
    private NumberFormat numberFormat=new DecimalFormat( "0" );
    private SimpleObserver updateReadouts;

    public ValueNode( final Property<Integer> numericProperty, int minimum, int maximum, int stepSize, final Font textFont, final Property<Boolean> showEditable, final InteractiveSymbolNode.Function0<Color> colorFunction ) {
        spinner = new JSpinner( new SpinnerNumberModel( numericProperty.getValue().intValue(), minimum, maximum, stepSize ) ) {
            {
                setFont( textFont );
                addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        numericProperty.setValue( (Integer) getValue() );
                    }
                } );
            }
        };

        // If the numericProperty changes external to the spinner (such as when we show the answer to a problem), the
        // text and the spinner will both need to be updated.
        updateReadouts = new SimpleObserver() {
            public void update() {
                spinner.setValue( numericProperty.getValue() );
                try {
                    //Try to set the text color to red for protons, but be prepared to fail due to type unsafety
                    ( (JSpinner.DefaultEditor) spinner.getEditor() ).getTextField().setForeground( colorFunction.apply() );
                }
                catch ( Exception e ) {
                    System.out.println( "ignoring = " + e );
                }
                text.setTextPaint( colorFunction.apply() );
                text.setText( numberFormat.format( numericProperty.getValue() ) );
            }
        };
        numericProperty.addObserver( updateReadouts );

        final PSwing spinnerPSwing = new PSwing( spinner );
        text.setFont( textFont );
        text.setOffset( spinnerPSwing.getFullBoundsReference().getCenterX() - text.getFullBoundsReference().width / 2,
                spinnerPSwing.getFullBoundsReference().getCenterY() - text.getFullBoundsReference().height / 2 );

        // Listen to the numericProperty that controls whether or not the
        // editable version is shown or the fixed text is shown.
        showEditable.addObserver( new SimpleObserver() {
            public void update() {
                removeAllChildren();
                if ( showEditable.getValue() ) {
                    addChild( spinnerPSwing );
                }
                else {
                    addChild( text );
                }
            }
        } );
    }

    public void setNumberFormat( NumberFormat format ) {
        this.numberFormat = format;
        JSpinner.DefaultEditor numberEditor = (JSpinner.DefaultEditor) getSpinnerEditor();
        final NumberFormatter formatter = new NumberFormatter( format );
        formatter.setValueClass( Integer.class );
        numberEditor.getTextField().setFormatterFactory( new DefaultFormatterFactory( formatter ) );
        spinner.setEditor( numberEditor );
        updateReadouts.update();
    }

    public JComponent getSpinnerEditor(){
        return spinner.getEditor();
    }
}
