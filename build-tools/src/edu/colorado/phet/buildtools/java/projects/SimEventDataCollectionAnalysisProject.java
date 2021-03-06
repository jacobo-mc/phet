// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildtools.java.projects;

import java.io.File;
import java.io.IOException;

//Project that builds a library jar for code that can inspect, view and provide diagnostic information on data files recorded by the sim event collection project.
public class SimEventDataCollectionAnalysisProject extends JavaSimulationProject {

    //Flag that indicates whether this jar should be built for use as a dependency
    private boolean libraryProject = false;

    public SimEventDataCollectionAnalysisProject( File file ) throws IOException {
        super( file );
    }

    //Built from util, so has a different path to trunk
    public File getTrunkAbsolute() {
        return getProjectDir().getParentFile().getParentFile(); // ../../trunk/
    }

    //When you want to deploy this as a library project, do not discard any classes
    @Override public boolean isShrink() {
        return !libraryProject;
    }
}