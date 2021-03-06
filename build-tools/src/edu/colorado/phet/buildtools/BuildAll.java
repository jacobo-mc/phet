package edu.colorado.phet.buildtools;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import edu.colorado.phet.buildtools.java.projects.JavaSimulationProject;

/**
 * Ant task so the the build-all can be run from Ant
 * See #3326
 */
public class BuildAll extends Task {
    public void execute() throws BuildException {
        super.execute();

        try {
            System.setProperty( "java.awt.headless", "true" );
        }
        catch ( RuntimeException e ) {
            System.err.println( "Failed to set headless property." );
        }

        //Read the property for trunk.  Trunk is passed with a system property to make it compatible with
        //other clients such as jenkins, see #3326
        //See http://ant.1045680.n5.nabble.com/How-to-pass-Java-system-properties-to-a-task-defined-in-a-taskdef-td1354342.html
        String trunk = this.getOwningTarget().getProject().getProperty( "trunk" );
        if ( trunk == null ) {
            throw new BuildException( "Trunk must be passed as a system property, like -Dtrunk=/path/to/trunk" );
        }
        final File trunkFile = new File( trunk );
        if ( !trunkFile.exists() ) {
            throw new BuildException( "No such trunk: " + trunkFile.getAbsolutePath() );
        }

        //Have to init BuildLocalProperties
        BuildLocalProperties.initRelativeToTrunk( trunkFile );
        PhetProject[] projects = PhetProject.getAllSimulationProjects( trunkFile );
        for ( PhetProject phetProject : projects ) {
            System.out.print( "Building " + phetProject.getName() + "..." );
            if ( phetProject instanceof JavaSimulationProject ) {
                try {
                    boolean success = phetProject.build();
                    if ( !success ) {
                        System.out.println();
                        throw new BuildException( "Build failure: " + phetProject.getName() );
                    }
                }
                catch ( Exception e ) {
                    System.out.println();
                    throw new BuildException( "build exception: " + phetProject.getName(), e );
                }
            }
            System.out.println( "success!" );
        }
    }
}