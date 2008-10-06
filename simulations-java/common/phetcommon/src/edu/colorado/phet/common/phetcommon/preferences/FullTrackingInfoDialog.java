package edu.colorado.phet.common.phetcommon.preferences;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.phetcommon.application.PhetAboutDialog;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

public class FullTrackingInfoDialog extends JDialog {
    private ITrackingInfo iTrackingInfo;

    public FullTrackingInfoDialog( Dialog owner, ITrackingInfo iTrackingInfo ) {
        super( owner );
        init( iTrackingInfo );
    }

    public FullTrackingInfoDialog( Frame owner, ITrackingInfo iTrackingInfo ) {
        super( owner );
        init( iTrackingInfo );
    }

    private void init( ITrackingInfo tracker ) {
        this.iTrackingInfo = tracker;
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        getContentPane().setLayout( new GridBagLayout() );
        getContentPane().add( createLogoPanel(), constraints );
        getContentPane().add( createReportPanel(), constraints );
        pack();
        SwingUtils.centerDialogInParent( this );
    }

    private JComponent createReportPanel() {
        final JTextArea jt = new JTextArea( "" );
        if ( iTrackingInfo.getHumanReadableTrackingInformation() != null ) {
            jt.setText( iTrackingInfo.getHumanReadableTrackingInformation() );
        }
        jt.setBorder( BorderFactory.createTitledBorder( "Report" ) );
        jt.setEditable( false );
        return jt;
    }

    private JPanel createLogoPanel() {

        BufferedImage image = PhetCommonResources.getInstance().getImage( PhetLookAndFeel.PHET_LOGO_120x50 );
        JLabel logoLabel = new JLabel( new ImageIcon( image ) );
        logoLabel.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        logoLabel.setToolTipText( getLocalizedString( "Common.About.WebLink" ) );
        logoLabel.addMouseListener( new MouseInputAdapter() {
            public void mouseReleased( MouseEvent e ) {
                PhetServiceManager.showPhetPage();
            }
        } );

        String html = INFO;
        html = html.replaceAll( "@FONT_SIZE@", new PhetFont().getSize() + "pt" );
        html = html.replaceAll( "@FONT_FAMILY@", new PhetFont().getFamily() );
        PhetAboutDialog.HTMLPane copyrightLabel = new PhetAboutDialog.HTMLPane( html );

        VerticalLayoutPanel logoPanel = new VerticalLayoutPanel();
        logoPanel.add( logoLabel );
        logoPanel.setInsets( new Insets( 10, 10, 10, 10 ) ); // top,left,bottom,right
        logoPanel.add( copyrightLabel );

        return logoPanel;
    }

    private String getLocalizedString( String propertyName ) {
        return PhetCommonResources.getInstance().getLocalizedString( propertyName );
    }

    private static final String INFO =
            "<html><head><style type=\"text/css\">body { font-size: @FONT_SIZE@; font-family: @FONT_FAMILY@ }</style></head>" +
            "<b><a href=http://phet.colorado.edu>PhET</a></b> " +
            "is made possible by grants that require us to track anonymous usage statistics.<br>No personal or private data is sent; you can see the full report sent to PhET below.<br><br>"
            +
            "Please visit the PhET website for more information: <a href=http://phet.colorado.edu>http://phet.colorado.edu</a>" +
            "</html>";
}
