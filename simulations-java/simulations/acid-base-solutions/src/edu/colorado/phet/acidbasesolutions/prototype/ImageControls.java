/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.HorizontalLayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;

/**
 * Controls for the images view of molecules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ImageControls extends JPanel {
    
    private final ImagesNode imagesNode;
    private final LinearValueControl maxMoleculesControl, scaleControl, transparencyControl, maxH2OControl, h2oTransparencyControl;
    private final JCheckBox showH2OCheckBox;
    
    public ImageControls( final ImagesNode imagesNode ) {
        setBorder( new TitledBorder( "Images" ) );
        
        this.imagesNode = imagesNode;
        imagesNode.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateControls();
            }
        });
        
        // max images
        IntegerRange maxImagesRange = MGPConstants.MAX_IMAGES_RANGE;
        maxMoleculesControl = new LinearValueControl( maxImagesRange.getMin(), maxImagesRange.getMax(), "max images:", "####0", "", new HorizontalLayoutStrategy() );
        maxMoleculesControl.setUpDownArrowDelta( 1 );
        maxMoleculesControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                imagesNode.setMaxMolecules( (int)maxMoleculesControl.getValue() );
            }
        });
        
        // scale
        DoubleRange scaleRange = MGPConstants.IMAGE_SCALE_RANGE;
        scaleControl = new LinearValueControl( scaleRange.getMin(), scaleRange.getMax(), "scale:", "0.00", "", new HorizontalLayoutStrategy() );
        scaleControl.setUpDownArrowDelta( 0.01 );
        scaleControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                imagesNode.setImageScale( scaleControl.getValue() );
            }
        });

        // transparency
        DoubleRange transparencyRange = MGPConstants.DOT_TRANSPARENCY_RANGE;
        transparencyControl = new LinearValueControl( transparencyRange.getMin(), transparencyRange.getMax(), "transparency:", "0.00", "", new HorizontalLayoutStrategy() );
        transparencyControl.setUpDownArrowDelta( 0.01 );
        transparencyControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                imagesNode.setMoleculeTransparency( (float)transparencyControl.getValue() );
            }
        });
        Hashtable<Double, JLabel> transparencyLabelTable = new Hashtable<Double, JLabel>();
        transparencyLabelTable.put( new Double( transparencyControl.getMinimum() ), new JLabel( "invisible" ) );
        transparencyLabelTable.put( new Double( transparencyControl.getMaximum() ), new JLabel( "opaque" ) );
        transparencyControl.setTickLabels( transparencyLabelTable );
        
        // H2O images
        IntegerRange h2OImagesRange = MGPConstants.MAX_H2O_IMAGES_RANGE;
        String h2oImagesLabel = HTMLUtils.toHTMLString( MGPConstants.H2O_FRAGMENT + " images:" );
        maxH2OControl = new LinearValueControl( h2OImagesRange.getMin(), h2OImagesRange.getMax(), h2oImagesLabel, "####0", "", new HorizontalLayoutStrategy() );
        maxH2OControl.setUpDownArrowDelta( 1 );
        maxH2OControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                imagesNode.setMaxH2O( (int) maxH2OControl.getValue() );
            }
        });
        
        // H2O transparency
        DoubleRange h2oTransparencyRange = MGPConstants.DOT_TRANSPARENCY_RANGE;
        String h2oTransparencyLabel = HTMLUtils.toHTMLString( MGPConstants.H2O_FRAGMENT + " transparency:" );
        h2oTransparencyControl = new LinearValueControl( h2oTransparencyRange.getMin(), h2oTransparencyRange.getMax(), h2oTransparencyLabel, "0.00", "", new HorizontalLayoutStrategy() );
        h2oTransparencyControl.setUpDownArrowDelta( 0.01 );
        h2oTransparencyControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                imagesNode.setH2OTransparency( (float) h2oTransparencyControl.getValue() );
            }
        });
        Hashtable<Double, JLabel> h2oTransparencyLabelTable = new Hashtable<Double, JLabel>();
        h2oTransparencyLabelTable.put( new Double( h2oTransparencyControl.getMinimum() ), new JLabel( "invisible" ) );
        h2oTransparencyLabelTable.put( new Double( h2oTransparencyControl.getMaximum() ), new JLabel( "opaque" ) );
        h2oTransparencyControl.setTickLabels( h2oTransparencyLabelTable );
        
        // show H2O checkbox
        showH2OCheckBox = new JCheckBox( "show H2O" );
        showH2OCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                imagesNode.setH2OVisible( showH2OCheckBox.isSelected() );
            }
        } );
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int column = 0;
        layout.addComponent( maxMoleculesControl, row, column++ );
        row++;
        column = 0;
        layout.addComponent( scaleControl, row, column++ );
        row++;
        column = 0;
        layout.addComponent( transparencyControl, row, column++ );
        row++;
        column = 0;
        layout.addComponent( maxH2OControl, row, column++ );
        row++;
        column = 0;
        layout.addComponent( h2oTransparencyControl, row, column++ );
        row++;
        column = 0;
        layout.addComponent( showH2OCheckBox, row, column++ );
        
        updateControls();
    }
    
    private void updateControls() {
        maxMoleculesControl.setValue( imagesNode.getMaxMolecules() );
        maxH2OControl.setValue( imagesNode.getMaxH2O() );
        scaleControl.setValue( imagesNode.getImageScale() );
        transparencyControl.setValue( imagesNode.getMoleculeTransparency() ); 
        h2oTransparencyControl.setValue( imagesNode.getH2OTransparency() );
        showH2OCheckBox.setSelected( imagesNode.isH2OVisible() );
    }
}
