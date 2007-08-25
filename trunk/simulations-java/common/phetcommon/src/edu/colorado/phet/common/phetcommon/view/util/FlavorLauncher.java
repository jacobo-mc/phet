/* Copyright 2007, University of Colorado */
package edu.colorado.phet.common.phetcommon.view.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * FlavorLauncher provides functionality for running PhET simulations from double-clickable JAR files.
 * Flavors should be listed in a top level properties file called flavors.properties (generated by the build process)
 * The launch is performed like so:
 * 1. If there is a single flavor, that flavor is launched immediately.
 * 2. If there are multiple flavors, and a file called "main-flavor.properies" exists, the flavor identified in that properties file is run.
 * 3. If there are multiple flavors and no "main-flavor" is identified, a GUI is displayed for picking and launching a flavor.
 *
 * This code was adapted from the bound-states flavor launcher: BSLauncher
 *
 * todo: There is currently no support for specifying an ordering of flavors in the GUI  
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @author Sam Reid
 * @version $Revision$
 */
public class FlavorLauncher extends JFrame {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private String[] _args;//todo: support main args
    private SimulationInfo[] info;
    private SimulationInfo selectedSim;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     *
     * @param args command line arguments
     * @param info
     */
    public FlavorLauncher( String[] args, SimulationInfo[] info ) {
        super();
        _args = args;
        this.info = info;
        createUI();
        setResizable( false );
        setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
    }

    //----------------------------------------------------------------------------
    // User interface construction
    //----------------------------------------------------------------------------

    /*
     * Creates the user interface for the dialog.
     *
     * @param parent the parent Frame
     */

    private void createUI() {

        JComponent inputPanel = createInputPanel();
        JPanel actionsPanel = createActionsPanel();

        JPanel bottomPanel = new JPanel( new BorderLayout() );
        bottomPanel.add( new JSeparator(), BorderLayout.NORTH );
        bottomPanel.add( actionsPanel, BorderLayout.CENTER );

        BorderLayout layout = new BorderLayout( 20, 20 );
        JPanel mainPanel = new JPanel( layout );
        mainPanel.setBorder( new EmptyBorder( 10, 10, 10, 10 ) );
        mainPanel.add( inputPanel, BorderLayout.CENTER );
        mainPanel.add( bottomPanel, BorderLayout.SOUTH );

        getContentPane().add( mainPanel );
        pack();

        //Workaround for the case of many simulations
        if (getHeight()>Toolkit.getDefaultToolkit().getScreenSize().height*0.75){
            setSize( getWidth(), (int)( Toolkit.getDefaultToolkit().getScreenSize().height*0.75 ) );
        }
    }

    /*
     * Creates dialog's input panel, which contains user controls.
     *
     * @return the input panel
     */
    private JComponent createInputPanel() {

        JLabel instructions = new JLabel( "<html>" +
                                          "This program contains "+info.length+" simulations.<br>" +
                                          "Select the simulation that you wish to start:<br>" +
                                          "</html>" );


        JPanel inputPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( inputPanel );
        inputPanel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( instructions, row++, column );

        ButtonGroup buttonGroup = new ButtonGroup();
        for( int i = 0; i < this.info.length; i++ ) {
            String title = info[i].getTitle();
            if (title==null||title.trim().length()==0){
                title=info[i].getMainClass().substring( info[i].getMainClass().lastIndexOf( '.')+1);
            }
            JRadioButton radioButton = new JRadioButton( title, i == 0 );
            final int flavorIndex = i;
            radioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    selectedSim=info[flavorIndex];
                }
            } );
            buttonGroup.add( radioButton );
            layout.addComponent( radioButton, row++, column );
        }
        selectedSim = info[0];
        if( info.length > 10 ) {//workaround for case of many sims
            return new JScrollPane( inputPanel );
        }
        return inputPanel;
    }

    /*
     * Creates the dialog's actions panel, consisting of a Close button.
     *
     * @return the actions panel
     */
    protected JPanel createActionsPanel() {

        JButton startButton = new JButton( "Start" );
        startButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleStart();
            }
        } );

        JButton cancelButton = new JButton( "Cancel" );
        cancelButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleCancel();
            }
        } );

        final int rows = 1;
        final int columns = 2; /* same as number of buttons! */
        final int hgap = 5;
        final int vgap = 0;
        JPanel buttonPanel = new JPanel( new GridLayout( rows, columns, hgap, vgap ) );
        buttonPanel.add( startButton );
        buttonPanel.add( cancelButton );

        JPanel actionPanel = new JPanel( new FlowLayout() );
        actionPanel.add( buttonPanel );

        return actionPanel;
    }

    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------

    /*
     * Handles the "Cancel" button.
     * Closes the dialog and exits.
     */

    private void handleCancel() {
        dispose();
        System.exit( 0 );
    }

    /*
     * Handles the "Start" button.
     * Runs the selected simulation.
     */
    private void handleStart() {
        try {
            selectedSim.launch();
            dispose();
        }
        catch( Exception e ) {
            e.printStackTrace();
            JOptionPane.showMessageDialog( this, e.toString(), "Exception", JOptionPane.ERROR_MESSAGE );
        }
    }

    static class SimulationInfo {
        private String flavor;
        private String title;
        private String mainClass;
        private String args;

        public SimulationInfo( String flavor,String title, String mainClass, String args ) {
            this.flavor=flavor;
            this.title = title;
            this.mainClass = mainClass;
            this.args = args;
        }

        public String getTitle() {
            return title;
        }

        public String getMainClass() {
            return mainClass;
        }

        public String getArgs() {
            return args;
        }

        public String[] getArgArray() {
            StringTokenizer stringTokenizer = new StringTokenizer( args );
            ArrayList list = new ArrayList();
            while( stringTokenizer.hasMoreTokens() ) {
                list.add( stringTokenizer.nextToken() );
            }
            return (String[])list.toArray(new String[0]);
        }

        public void launch() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
            Class mainClass = Class.forName( getMainClass() );
            final Method main = mainClass.getMethod( "main", new Class[]{String[].class} );
            Thread thread=new Thread( new Runnable() {
                public void run() {
                    try {
                        main.invoke( null, new Object[]{getArgArray()} );
                    }
                    catch( IllegalAccessException e ) {
                        e.printStackTrace();
                    }
                    catch( InvocationTargetException e ) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }

        public String getFlavor() {
            return flavor;
        }
    }

    public static void main( String args[] ) throws IOException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        //Read flavors:
        Properties prop = new Properties();

        URL resource = Thread.currentThread().getContextClassLoader().getResource( "flavors.properties" );
        if( resource != null ) {//works running from a JAR file
            prop.load( resource.openStream() );
        }
        else {//fallback plan in case not running in a JAR file
            prop.load( new FileInputStream( new File( "flavors.properties" ) ) );
        }

        SimulationInfo[] info = getSimInfo( prop );
        if( info.length == 0 ) {
            throw new RuntimeException( "No flavors found." );
        }

        URL mainURL=Thread.currentThread().getContextClassLoader().getResource( "main-flavor.properties" );
        if (mainURL!=null){
            Properties flavorProperties=new Properties( );
            flavorProperties.load( mainURL.openStream() );
            String mainFlavor=flavorProperties.getProperty( "main.flavor");
            System.out.println( "Launching: "+mainFlavor);
            launchFlavor(info, mainFlavor);
        }

        else if( info.length == 1 ) {
            System.out.println( "Found one flavor: " + info[0].getTitle() );
            System.out.println( "Launching..." );
            info[0].launch();
        }
        else {
            FlavorLauncher launcher = new FlavorLauncher( args, info );
            SwingUtils.centerWindowOnScreen( launcher );
            launcher.show();
        }
    }

    private static void launchFlavor( SimulationInfo[] info, String mainFlavor ) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        for( int i = 0; i < info.length; i++ ) {
            SimulationInfo simulationInfo = info[i];
            if (simulationInfo.getFlavor().equals(mainFlavor)){
                simulationInfo.launch();
            }
        }
    }

    private static String[] listFlavors( Properties prop ) {
        Enumeration names = prop.propertyNames();
        HashSet flavors = new HashSet();

        while( names.hasMoreElements() ) {
            String name = (String)names.nextElement();
            if( name.toLowerCase().startsWith( "project.flavor" ) ) {
                String suffix = name.substring( "project.flavor.".length() );
                int lastDot = suffix.indexOf( '.' );
                if( lastDot >= 0 ) {
                    String flavor = suffix.substring( 0, lastDot );
//                    System.out.println( "flavor = " + flavor );
                    flavors.add( flavor );
                }
            }
        }
        return (String[])flavors.toArray( new String[0] );
    }

    private static SimulationInfo[] getSimInfo( Properties prop ) {
        String[] flavors = listFlavors( prop );
        ArrayList fx = new ArrayList();
        for( int i = 0; i < flavors.length; i++ ) {
            String flavor = flavors[i];
            SimulationInfo f = getFlavor( prop, flavor );
            fx.add( f );
        }
        return (SimulationInfo[])fx.toArray( new SimulationInfo[0] );
    }

    private static SimulationInfo getFlavor( Properties prop, String flavor ) {
        String mainClass = prop.getProperty( "project.flavor." + flavor + ".mainclass" );
        String title = prop.getProperty( "project.flavor." + flavor + ".title" );
        String args = prop.getProperty( "project.flavor." + flavor + ".args" );
        return new SimulationInfo( flavor,title, mainClass, args );
    }
}
