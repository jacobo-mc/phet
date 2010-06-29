/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.testsolution;

import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.PureWaterSolution;
import edu.colorado.phet.acidbasesolutions.model.ABSModel.DefaultSolutionFactory;
import edu.colorado.phet.acidbasesolutions.module.ABSModule;

/**
 * "Test Solution" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestSolutionModule extends ABSModule {
    
    private final ABSModel model;
    private final TestSolutionCanvas canvas;
    private final TestSolutionControlPanel controlPanel;
    
    public TestSolutionModule() {
        super( ABSStrings.TEST_SOLUTION );
        
        model = new ABSModel( new DefaultSolutionFactory() {
            public AqueousSolution getDefaultSolution() {
                return new PureWaterSolution();
            }
        } );
        
        canvas = new TestSolutionCanvas( model );
        setSimulationPanel( canvas );
        
        controlPanel = new TestSolutionControlPanel( this, model );
        setControlPanel( controlPanel );
        
        reset();
    }
    
    @Override
    public void reset() {
        model.reset();
    }
}
