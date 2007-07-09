/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.dialog;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;

import javax.swing.JDialog;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.charts.PositionHistogramPanel;
import edu.colorado.phet.opticaltweezers.model.Bead;
import edu.colorado.phet.opticaltweezers.model.Laser;

/**
 * PositionHistogramDialog is a nonmodal dialog that display the position histogram.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PositionHistogramDialog extends JDialog {
    
    private PositionHistogramPanel _panel;
    
    /**
     * Constructor.
     * 
     * @param owner parent of this dialog
     * @param font font to used for all controls
     * @param bead
     * @param laser
     * @param binWidth
     */
    public PositionHistogramDialog( Frame owner, Font font, IClock clock, Bead bead, Laser laser ) {
        super( owner );
        assert( owner != null );
        
        setResizable( false );
        setModal( false );
        setTitle( OTResources.getString( "title.positionHistogram" ) );
        
        _panel = new PositionHistogramPanel( this, font, clock, bead, laser );
        getContentPane().add( _panel );
        pack();
        
        // Place the dialog at the upper-left corner of the parent frame
        setLocation( owner.getLocation() );
    }
    
    /**
     * Performs cleanup before disposing of the dialog.
     */
    public void dispose() {
        _panel.cleanup();
        super.dispose();
    }
    
    public int getZoomIndex() {
        return _panel.getZoomIndex();
    }
}
