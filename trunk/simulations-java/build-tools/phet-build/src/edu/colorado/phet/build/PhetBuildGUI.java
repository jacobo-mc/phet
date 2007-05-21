package edu.colorado.phet.build;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * Author: Sam Reid
 * May 19, 2007, 1:39:19 AM
 */
public class PhetBuildGUI extends AbstractPhetTask {
    private JFrame frame;
    private final Object blocker = new Object();
    private JList flavorList;
    private JList localeList;
    private JList simList;

    // The method executing the task
    public final void execute() throws BuildException {
        buildGUI();
        start();
        //avoid closing ant until we've finished with this application
        synchronized( blocker ) {
            try {
                int hours = 1;
                blocker.wait( 1000 * 60 * 60 * hours );
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }
        }
    }

    private void buildGUI() {

        this.frame = new JFrame( "PhET Build" );
        frame.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                synchronized( blocker ) {
                    blocker.notifyAll();
                }
            }
        } );

        String[] objects = toArray( getProperty( new PhetListSimTask() ) );

        simList = new JList( objects );
//        simList.setPreferredSize( new Dimension( simList.getPreferredSize().width, 400 ) );
        simList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        simList.addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent e ) {
                updateLists();
            }
        } );
        JPanel contentPane = new JPanel();

        flavorList = new JList( new Object[]{} );
        localeList = new JList( new Object[]{} );
        contentPane.setLayout( new GridBagLayout() );
        GridBagConstraints gridBagConstraints = new GridBagConstraints( GridBagConstraints.RELATIVE, 0, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets( 2, 2, 2, 2 ), 0, 0 );
        contentPane.add( new JScrollPane( simList ), gridBagConstraints );
        contentPane.add( new JScrollPane( flavorList ), gridBagConstraints );
        contentPane.add( new JScrollPane( localeList ), gridBagConstraints );

        JPanel commandPanel = new JPanel();
        JButton refresh = new JButton( "Refresh" );
        refresh.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                refresh();
            }
        } );
        JButton run = new JButton( "Run" );
        run.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                run();
            }
        } );
        JButton showLocalizationFile = new JButton( "Show Localization File" );
        showLocalizationFile.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                showLocalizationFile();
            }
        } );

        GridBagConstraints commandConstraints = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 );
        commandPanel.setLayout( new GridBagLayout() );
//        commandPanel.add( refresh, commandConstraints );
        commandPanel.add( showLocalizationFile, commandConstraints );
        commandPanel.add( run, commandConstraints );
        commandPanel.add( Box.createVerticalBox() );

        contentPane.add( commandPanel );

        frame.setContentPane( contentPane );
        frame.setSize( 800, 600 );
        frame.pack();
        frame.setSize( frame.getWidth() + 100, frame.getHeight() + 100 );
    }

    private void showLocalizationFile() {
        PhetProject project = getSelectedProject();
        String locale = getSelectedLocale();
        File localizationFile = project.getLocalizationFile( locale );
        try {
            BufferedReader bufferedReader = new BufferedReader( new FileReader( localizationFile ) );
            String text = "";
            for( String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine() ) {
                text += line + System.getProperty( "line.separator" );
            }
            JTextArea jta = new JTextArea( text );
            JFrame frame = new JFrame( "Localization file for: " + getSelectedProject() + " " + getSelectedLocale() + ". " + localizationFile.getAbsolutePath() );
            frame.setContentPane( new JScrollPane( jta ) );
            frame.setSize( 800, 600 );
            frame.setVisible( true );
        }
        catch( FileNotFoundException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private void refresh() {
        updateLists();
    }

    private void run() {
        final JDialog dialog = new JDialog( frame, "Building Sim" );
        JLabel pane = new JLabel( "Building " + simList.getSelectedValue() + ". Please wait..." );
        pane.setOpaque( true );
        dialog.setContentPane( pane );
        dialog.pack();
        dialog.setLocation( frame.getX() + frame.getWidth() / 2 - dialog.getWidth() / 2, frame.getY() + frame.getHeight() / 2 - dialog.getHeight() / 2 );
        dialog.setVisible( true );
        Thread thread = new Thread( new Runnable() {
            public void run() {
                doRun( dialog );
            }
        } );
        thread.start();
    }

    private void doRun( final JDialog dialog ) {
        String sim = (String)simList.getSelectedValue();
        String flavor = (String)flavorList.getSelectedValue();
        String locale = getSelectedLocale();
        System.out.println( "Building sim: " + sim );
        PhetBuildTask phetBuildTask = new PhetBuildTask();
        phetBuildTask.setProject( sim );
        runTask( phetBuildTask );
        System.out.println( "Build complete" );
        Java java = new Java();

        PhetProject phetProject = getSelectedProject();
        if( phetProject != null ) {
            java.setClassname( phetProject.getFlavor( flavor, locale ).getMainclass() );
            java.setFork( true );
            Path classpath = new Path( getProject() );
            FileSet set = new FileSet();
            set.setFile( phetProject.getDefaultDeployJar() );
            classpath.addFileset( set );
            java.setClasspath( classpath );
            if( !locale.equals( "en" ) ) {
                java.setJvmargs( "-Djavaws.phet.locale=" + locale );
            }
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    dialog.dispose();
                }
            } );
            runTask( java );
        }
    }

    private String getSelectedLocale() {
        return (String)localeList.getSelectedValue();
    }

    private PhetProject getSelectedProject() {
        String sim = (String)simList.getSelectedValue();
        File projectParentDir = PhetBuildUtils.resolveProject( getProject().getBaseDir(), sim );
        try {
            return new PhetProject( projectParentDir, sim );
        }
        catch( IOException e ) {
            e.printStackTrace();
            System.out.println( "no project selected" );
            return null;
        }
    }

    private String[] toArray( String simListString ) {
        ArrayList simNames = new ArrayList();
        StringTokenizer st = new StringTokenizer( simListString, "," );
        while( st.hasMoreTokens() ) {
            simNames.add( st.nextToken() );
        }
        return (String[])simNames.toArray( new String[0] );
    }

    private String getProperty( Task task ) {
        ( (PropertyTask)task ).setProperty( "phet.sim.list" );
        runTask( task );
        return getProject().getProperty( "phet.sim.list" );
    }

    private String getSelectedSim() {
        return (String)simList.getSelectedValue();
    }

    private void updateLists() {
        PhetListFlavorsTask flavorsTask = new PhetListFlavorsTask();
        flavorsTask.setProject( getSelectedSim() );
        flavorList.setListData( toArray( getProperty( flavorsTask ) ) );
        flavorList.setSelectedIndex( 0 );

        PhetListLocalesTask localesTask = new PhetListLocalesTask();
        localesTask.setProject( getSelectedSim() );
        localeList.setListData( setDefaultValueEnglish( toArray( getProperty( localesTask ) ) ) );
        localeList.setSelectedIndex( 0 );
    }

    private String[] setDefaultValueEnglish( String[] strings ) {
        ArrayList list = new ArrayList( Arrays.asList( strings ) );
        list.remove( "en" );
        list.add( 0, "en" );
        return (String[])list.toArray( new String[0] );
    }

    private void start() {
        frame.setVisible( true );
    }
}