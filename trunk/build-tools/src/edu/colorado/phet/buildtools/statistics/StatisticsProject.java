package edu.colorado.phet.buildtools.statistics;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import edu.colorado.phet.buildtools.BuildToolsPaths;
import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.Simulation;

/**
 * Statistics database code project. Used for dependencies and new build gui
 */
public class StatisticsProject extends PhetProject {

    public StatisticsProject( File trunk ) throws IOException {
        super( new File( trunk, BuildToolsPaths.STATISTICS ) );
    }

    @Override
    public String getName() {
        return "Statistics Database server code";
    }

    public Simulation getSimulation( String simulationName, Locale locale ) {
        return null;
    }

    public Locale[] getLocales() {
        return new Locale[0];
    }

    public File getTranslationFile( Locale locale ) {
        return null;
    }

    protected File getTrunkAbsolute() {
        return null;
    }

    public String getAlternateMainClass() {
        return null;
    }

    public String getProdServerDeployPath() {
        return null;
    }

    public String getLaunchFileSuffix() {
        return null;
    }

    public boolean build() throws Exception {
        return false;
    }

    public String getListDisplayName() {
        return getName();
    }

    public void runSim( Locale locale, String simulationName ) {

    }

    public PhetProject[] getAllDependencies() {
        return new PhetProject[]{this};
    }
}
