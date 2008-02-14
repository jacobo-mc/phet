package edu.colorado.phet.reids.admin;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.*;

import edu.colorado.phet.build.FileUtils;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

/**
 * Created by: Sam
 * Feb 13, 2008 at 6:46:15 PM
 */
public class TimesheetApp extends JFrame {
    private TimesheetData timesheetData;
    private ArrayList recentFiles = new ArrayList();
    private File currentFile;
    private String WINDOW_HEIGHT = "window.h";
    private String WINDOW_WIDTH = "window.w";
    private String WINDOW_Y = "window.y";
    private String WINDOW_X = "window.x";
    private String RECENT_FILES = "recentFiles";
    private String CURRENT_FILE = "currentFile";
    private JMenu fileMenu = new JMenu( "File" );

    public TimesheetApp() throws IOException {
        super( "Timesheet" );
        this.timesheetData = new TimesheetData();
        timesheetData.addEntry( new TimesheetDataEntry( new Date(), new Date(), "cck", "hello" ) );
//        for ( int i = 0; i < 10 * 7 * 4 * 12; i++ ) {
        for ( int i = 0; i < 20; i++ ) {
            timesheetData.addEntry( new TimesheetDataEntry( new Date( System.currentTimeMillis() - 1000 ), new Date(), "cck", "hello2" ) );
        }
        for ( int i = 0; i < 20; i++ ) {
            timesheetData.addEntry( new TimesheetDataEntry( new Date( System.currentTimeMillis() - 1000 ), new Date(), "moving man", "hello2" ) );
        }
        final TimesheetDataEntry dataEntry = new TimesheetDataEntry( new Date(), null, "cck", "hello 3" );
        dataEntry.setRunning( true );
        timesheetData.addEntry( dataEntry );
        final JMenuItem openItem = new JMenuItem( "Open" );
        openItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    load();
                }
                catch( IOException e1 ) {
                    JOptionPane.showMessageDialog( TimesheetApp.this, e1.getMessage() );
                }
            }
        } );
        fileMenu.add( openItem );

        final JMenuItem saveItem = new JMenuItem( "Save" );
        saveItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    save();
                }
                catch( IOException e1 ) {
                    JOptionPane.showMessageDialog( TimesheetApp.this, e1.getMessage() );
                }
            }
        } );
        fileMenu.add( saveItem );

        final JMenuItem saveAsItem = new JMenuItem( "Save As" );
        saveAsItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    saveAs();
                }
                catch( IOException e1 ) {
                    JOptionPane.showMessageDialog( TimesheetApp.this, e1.getMessage() );
                }
            }
        } );
        fileMenu.add( saveAsItem );

        fileMenu.addSeparator();
        final JMenuItem exitItem = new JMenuItem( "Exit" );
        exitItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    exit();
                }
                catch( IOException e1 ) {
                    e1.printStackTrace();
                }
            }
        } );
        fileMenu.add( exitItem );
        fileMenu.addSeparator();

        addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                try {
                    exit();
                }
                catch( IOException e1 ) {
                    e1.printStackTrace();
                }
            }
        } );
        final JMenuBar jMenuBar = new JMenuBar();
        jMenuBar.add( fileMenu );
        setJMenuBar( jMenuBar );

        setContentPane( new ContentPane( timesheetData, this ) );
        new FrameSetup.CenteredWithInsets( 200, 200 ).initialize( this );
        loadPreferences();
    }

    private void loadPreferences() throws IOException {
        File prefFile = getPrefFile();
        Properties p = new Properties();
        p.load( new FileReader( prefFile ) );
        Rectangle r = new Rectangle();
        r.x = Integer.parseInt( p.getProperty( WINDOW_X ) );
        r.y = Integer.parseInt( p.getProperty( WINDOW_Y ) );
        r.width = Integer.parseInt( p.getProperty( WINDOW_WIDTH ) );
        r.height = Integer.parseInt( p.getProperty( WINDOW_HEIGHT ) );
        setSize( r.width, r.height );
        setLocation( r.x, r.y );


        String recentFiles = p.getProperty( RECENT_FILES );
        System.out.println( "Loaded prefs, r=" + r + ", recent=" + recentFiles );
        StringTokenizer stringTokenizer = new StringTokenizer( recentFiles, "," );
        this.recentFiles.clear();
        while ( stringTokenizer.hasMoreTokens() ) {
            final File file = new File( stringTokenizer.nextToken() );
            if ( !this.recentFiles.contains( file ) ) {
                this.recentFiles.add( file );
            }
        }
        updateMenuWithRecent();
        String currentFile = p.getProperty( CURRENT_FILE );
        if ( new File( currentFile ).exists() ) {
            load( new File( currentFile ) );
        }
    }

    public static class RecentFileMenuItem extends JMenuItem {
        public RecentFileMenuItem( String text ) {
            super( text );
        }
    }

    private void updateMenuWithRecent() {
        for ( int i = 0; i < fileMenu.getPopupMenu().getComponentCount(); i++ ) {
            if ( fileMenu.getPopupMenu().getComponent( i ) instanceof RecentFileMenuItem ) {
                fileMenu.getPopupMenu().remove( i );
                i--;
            }
        }
        for ( int i = 0; i < recentFiles.size(); i++ ) {
            File file = (File) recentFiles.get( i );
            RecentFileMenuItem recentFileMenuItem = new RecentFileMenuItem( file.getAbsolutePath() );
            fileMenu.add( recentFileMenuItem, fileMenu.getComponentCount() - 1 );
        }
    }

    private void savePreferences() throws IOException {
        File prefFile = getPrefFile();
//        System.out.println( "prefFile.getAbsolutePath() = " + prefFile.getAbsolutePath() );
        Properties properties = new Properties();
        properties.put( WINDOW_X, getX() + "" );
        properties.put( WINDOW_Y, getY() + "" );
        properties.put( WINDOW_WIDTH, getWidth() + "" );
        properties.put( WINDOW_HEIGHT, getHeight() + "" );

        properties.put( RECENT_FILES, getRecentFileListString() );
        properties.put( CURRENT_FILE, currentFile == null ? "null" : currentFile.getAbsolutePath() );

        properties.store( new FileWriter( prefFile ), "auto-generated on " + new Date() );
        System.out.println( "Stored prefs: " + properties );
    }

    private File getPrefFile() {
        return new File( System.getProperty( "user.home", "." ), "timesheet-app.properties" );
    }

    private String getRecentFileListString() {
        String s = "";
        for ( int i = 0; i < recentFiles.size(); i++ ) {
            File file = (File) recentFiles.get( i );
            s += file.getAbsolutePath();
            if ( i < recentFiles.size() - 1 ) {
                s += ",";
            }
        }
        return s;
    }

    public TimesheetData getTimesheetData() {
        return timesheetData;
    }

    public static String toString( long timeMillis ) {
        long allSeconds = timeMillis / 1000;
        long hours = allSeconds / 3600;
        long remainingSeconds = allSeconds - hours * 3600;
        long minutes = remainingSeconds / 60;
        long seconds = remainingSeconds - minutes * 60;
        assert hours * 3600 + minutes * 60 + seconds == allSeconds;
        DecimalFormat decimalFormat = new DecimalFormat( "00" );
        return decimalFormat.format( hours ) + ":" + decimalFormat.format( minutes ) + ":" + decimalFormat.format( seconds );
    }

    private void exit() throws IOException {
        savePreferences();
        //save dialog
        //save prefs
        System.exit( 0 );
    }


    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                try {
                    new TimesheetApp().start();
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
            }
        } );
    }

    private void start() {
        setVisible( true );
    }

    public void load() throws IOException {
        final JFileChooser jFileChooser = currentFile == null ? new JFileChooser() : new JFileChooser( currentFile.getParentFile() );
        int option = jFileChooser.showOpenDialog( this );
        if ( option == JFileChooser.APPROVE_OPTION ) {
            final File selectedFile = jFileChooser.getSelectedFile();
            load( selectedFile );
        }
        else {
            System.out.println( "TimesheetApp.load, didn't save" );
        }

    }

    private void load( File selectedFile ) throws IOException {
        currentFile = selectedFile;
        String str = FileUtils.loadFileAsString( currentFile );
        timesheetData.loadCSV( str );
        addCurrentToRecent();
        setTitle( "Timesheet: " + selectedFile.getName() + " [" + selectedFile.getAbsolutePath() + "]" );
    }

    public void saveAs() throws IOException {
        File selected = selectSaveFile();
        if ( selected != null ) {
            save( selected );
        }
        else {

            System.out.println( "Didn't save" );
        }
    }

    public void save() throws IOException {
        //TODO: save over last save file, if exists
        File selected = currentFile;
        if ( currentFile == null ) {
            selected = selectSaveFile();
        }
        if ( selected != null ) {
            save( selected );
        }
        else {
            System.out.println( "didn't save" );
        }
    }

    private File selectSaveFile() {
        File selected = null;
        final JFileChooser chooser = currentFile == null ? new JFileChooser() : new JFileChooser( currentFile.getParentFile() );
        int val = chooser.showSaveDialog( this );
        if ( val == JFileChooser.APPROVE_OPTION ) {
            File file = chooser.getSelectedFile();
            if ( file.exists() ) {
                int option = JOptionPane.showConfirmDialog( this, "File exists, overwrite?" );
                if ( option == JOptionPane.YES_OPTION ) {
                    selected = file;
                }
            }
            else {
                selected = file;
            }
        }
        return selected;
    }

    private void save( File selected ) throws IOException {
        this.currentFile = selected;
        addCurrentToRecent();
        currentFile.getParentFile().mkdirs();
        String s = timesheetData.toCSV();
        FileUtils.writeString( currentFile, s );
    }

    private void addCurrentToRecent() {
        if ( !recentFiles.contains( currentFile ) ) {
            recentFiles.add( currentFile );
        }
        updateMenuWithRecent();
    }
}
