package edu.colorado.phet.build.translate;

import java.util.StringTokenizer;

import javax.swing.*;

import org.apache.tools.ant.BuildException;

import edu.colorado.phet.build.AbstractPhetTask;

/**
 * Created by: Sam
 * Jan 13, 2008 at 2:26:00 PM
 */
public class AddTranslationTask extends AbstractPhetTask {
    private String simulationList;
    private String languageCode;
    private String username;
    private String password;
    private boolean deployEnabled = true;

    public void execute() throws BuildException {
        super.execute();
        try {
            final String simulationList = promptIfNecessary( "simulation(s)", this.simulationList );
            final String languageCode = promptIfNecessary( "language", this.languageCode );
            final String username = promptIfNecessary( "username", this.username );
            final String password = promptIfNecessary( "password", this.password );
            StringTokenizer st = new StringTokenizer( simulationList, " " );
            while ( st.hasMoreTokens() ) {
                new AddTranslation( getBaseDir(), deployEnabled ).addTranslation( st.nextToken(),
                                                                                  languageCode,
                                                                                  username,
                                                                                  password );
            }
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    }

    private String promptIfNecessary( String variableName, String variableValue ) {
        return variableValue == null || variableValue.trim().length() == 0 || variableValue.startsWith( "${"  ) ?
               JOptionPane.showInputDialog( "Enter the " + variableName + ":" )
               : variableValue;
    }

    public void setDeployEnabled( boolean deployEnabled ) {
        this.deployEnabled = deployEnabled;
    }
    
    //todo: handle simulation list more elegantly
    public void setSimulation( String simulationList ) {
        this.simulationList = simulationList;
    }

    public void setLanguage( String language ) {
        this.languageCode = language;
    }

    public void setUsername( String username ) {
        this.username = username;
    }

    public void setPassword( String password ) {
        this.password = password;
    }
}
