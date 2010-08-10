/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.controls;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.acidbasesolutions.model.MagnifyingGlass.MagnifyingGlassChangeListener;
import edu.colorado.phet.acidbasesolutions.model.Molecule.WaterMolecule;
import edu.colorado.phet.acidbasesolutions.model.SolutionRepresentation.SolutionRepresentationChangeAdapter;
import edu.colorado.phet.acidbasesolutions.util.HTMLCheckBox;
import edu.colorado.phet.acidbasesolutions.view.ABSRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;

/**
 * Control panel that provides access to various "tools".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ToolsControl extends JPanel {
    
    private static final Color SEPARATOR_COLOR = new Color( 150, 150, 150 );

    private final ABSModel model;
    private final JRadioButton pHMeterRadioButton, pHPaperRadioButton, conductivityTesterRadioButton;
    private final JRadioButton magnifyingGlassRadioButton, concentrationGraphRadioButton;
    private final JCheckBox showWaterCheckBox;
    
    public ToolsControl( final ABSModel model ) {
        
        // model
        this.model = model;
        model.getMagnifyingGlass().addMagnifyingGlassListener( new MagnifyingGlassChangeListener() {
            public void waterVisibleChanged() {
                updateControl();
            }
        });
        model.getMagnifyingGlass().addSolutionRepresentationChangeListener( new SolutionRepresentationChangeAdapter() {
            @Override
            public void visibilityChanged() {
                showWaterCheckBox.setEnabled( model.getMagnifyingGlass().isVisible() );
            }
        });
        
        // border
        TitledBorder titledBorder = new TitledBorder( ABSStrings.TOOLS );
        titledBorder.setTitleFont( ABSConstants.TITLED_BORDER_FONT );
        titledBorder.setBorder( new LineBorder( Color.BLACK, 1 ) );
        setBorder( titledBorder );
        
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateModel();
            }
        };
        
        // radio buttons
        ButtonGroup group = new ButtonGroup();
        pHMeterRadioButton = new ABSRadioButton( ABSStrings.PH_METER, group, actionListener );
        pHPaperRadioButton = new ABSRadioButton( ABSStrings.PH_PAPER, group, actionListener );
        conductivityTesterRadioButton = new ABSRadioButton( ABSStrings.CONDUCTIVITY_TESTER, group, actionListener );
        magnifyingGlassRadioButton = new ABSRadioButton( ABSStrings.MAGNIFYING_GLASS, group, actionListener );
        concentrationGraphRadioButton = new ABSRadioButton( ABSStrings.CONCENTRATION_GRAPH, group, actionListener );
        
        // "Show Water" check box
        WaterMolecule waterMolecule = new WaterMolecule();
        String html = HTMLUtils.toHTMLString( MessageFormat.format( ABSStrings.PATTERN_SHOW_WATER_MOLECULES, waterMolecule.getSymbol() ) );
        showWaterCheckBox = new HTMLCheckBox( html );
        showWaterCheckBox.addActionListener( actionListener );
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( pHMeterRadioButton, row++, column );
        layout.addComponent( pHPaperRadioButton, row++, column );
        layout.addComponent( conductivityTesterRadioButton, row++, column );
        layout.addComponent( magnifyingGlassRadioButton, row++, column );
        layout.addComponent( concentrationGraphRadioButton, row++, column );
        JSeparator separator = new JSeparator();
        separator.setForeground( SEPARATOR_COLOR );
        layout.addFilledComponent( separator, row++, column, GridBagConstraints.HORIZONTAL );
        layout.addComponent( showWaterCheckBox, row++, column );
        
        // default state
        updateControl();
    }
    
    protected void setPHPaperControlVisible( boolean visible ) {
        pHPaperRadioButton.setVisible( visible );
    }
    
    protected void setCondutivityTesterControlVisible( boolean visible ) {
        conductivityTesterRadioButton.setVisible( visible );
    }
    
    private void updateControl() {
        pHMeterRadioButton.setSelected( model.getPHMeter().isVisible() );
        pHPaperRadioButton.setSelected( model.getPHPaper().isVisible() );
        conductivityTesterRadioButton.setSelected( model.getConductivityTester().isVisible() );
        magnifyingGlassRadioButton.setSelected( model.getMagnifyingGlass().isVisible() );
        concentrationGraphRadioButton.setSelected( model.getConcentrationGraph().isVisible() );
        showWaterCheckBox.setSelected( model.getMagnifyingGlass().isWaterVisible() );
    }
    
    private void updateModel() {
        model.getPHMeter().setVisible( pHMeterRadioButton.isSelected() );
        model.getPHPaper().setVisible( pHPaperRadioButton.isSelected() );
        model.getConductivityTester().setVisible( conductivityTesterRadioButton.isSelected() );
        model.getMagnifyingGlass().setVisible( magnifyingGlassRadioButton.isSelected() );
        model.getMagnifyingGlass().setWaterVisible( showWaterCheckBox.isSelected() );
        model.getConcentrationGraph().setVisible( concentrationGraphRadioButton.isSelected() );
    }
}
