package edu.colorado.phet.statesofmatter;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

import java.awt.geom.Rectangle2D;

public class StatesOfMatterConfig extends PhetApplicationConfig {
    private static final String PROJECT_NAME = "states-of-matter";

    public static final PhetResources RESOURCES = PhetResources.forProject(PROJECT_NAME);
    public static final int WINDOW_WIDTH  = 800;
    public static final int WINDOW_HEIGHT = 600;
    public static final int INITIAL_MAX_PARTICLE_COUNT = 600;
    public static final Rectangle2D.Double CONTAINER_BOUNDS = new Rectangle2D.Double(-3, -4, 6, 8);
    public static final Rectangle2D.Double ICE_CUBE_BOUNDS = new Rectangle2D.Double(-1.5, 1, 3, 3);
    public static final double INITIAL_TOTAL_ENERGY = 60000;
    public static final double PARTICLE_RADIUS = 0.1;
    public static final double PARTICLE_MASS = 1.0;
    public static final double PARTICLE_MAX_KE = 100000;
    public static final double PARTICLE_CREATION_CUSHION = 0.005;

    public static final int COMPUTATIONS_PER_RENDER = 5;

    public static final double GRAVITY = -150;
    public static final double DELTA_T = 0.00001;

    public static final double EPSILON = 1.0;
    public static final double RMIN    = 2.0 * PARTICLE_RADIUS;


    public StatesOfMatterConfig(String[] commandLineArgs, FrameSetup frameSetup) {
        super(commandLineArgs, frameSetup, RESOURCES);
    }
}
