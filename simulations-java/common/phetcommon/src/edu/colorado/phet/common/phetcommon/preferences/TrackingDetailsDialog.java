package edu.colorado.phet.common.phetcommon.preferences;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.tracking.ITrackingInfo;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.phetcommon.application.GrayRectWorkaroundDialog;

/**
 * Dialog that appears when you press the "Details" button in the Tracking preferences panel.
 */
public class TrackingDetailsDialog extends AbstractPrivacyPopupDialog {
    
    private static final String TITLE = PhetCommonResources.getString( "Common.tracking.details.title" );
    private static final String DESCRIPTION = PhetCommonResources.getString( "Common.tracking.details.description" );

    public TrackingDetailsDialog( Dialog owner, ITrackingInfo trackingInfo ) {
        super( TITLE,owner );
        init( trackingInfo );
    }

    public TrackingDetailsDialog( Frame owner, ITrackingInfo trackingInfo ) {
        super( TITLE,owner );
        init( trackingInfo );
    }

    protected JComponent createDescription() {
        return new JLabel( DESCRIPTION );
    }
    
    protected JComponent createReport( ITrackingInfo trackingInfo ) {
        
        final JTextArea textArea = new JTextArea( "" );
        final String text = trackingInfo.getHumanReadableTrackingInformation();
        if ( text != null ) {
            textArea.setText( text );
        }
        textArea.setEditable( false );
        
        JScrollPane scrollPane = new JScrollPane( textArea );
        scrollPane.setPreferredSize( new Dimension( scrollPane.getPreferredSize().width + 30, 200 ) );
        
        // this ensures that the first line of text is at the top of the scrollpane
        textArea.setCaretPosition( 0 );
        
        return scrollPane;
    }

}
