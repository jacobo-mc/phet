// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.draghandles;

import edu.colorado.phet.boundstates.model.BSAsymmetricPotential;
import edu.colorado.phet.boundstates.module.BSAbstractModuleSpec;
import edu.colorado.phet.boundstates.module.BSPotentialSpec;
import edu.colorado.phet.boundstates.view.BSCombinedChartNode;

/**
 * BSAsymmetricDragManager manages drag handles for 
 * a potential composed of Asymmetric wells.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSAsymmetricDragManager extends BSAbstractDragManager {
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param moduleSpec
     * @param chartNode the chart that the drag handles and markers pertain to
     */
    public BSAsymmetricDragManager( BSAbstractModuleSpec moduleSpec, BSCombinedChartNode chartNode ) {
        super( moduleSpec, chartNode );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Attaches drag handles to the specified potential.
     * Any existing handles are deleted.
     * 
     * @param potential
     */
    public void setPotential( BSAsymmetricPotential potential ) {
        
        removeAllHandlesAndMarkers();
        if ( potential != null ) {
            
            BSAbstractModuleSpec moduleSpec = getModuleSpec();
            BSPotentialSpec potentialSpec = moduleSpec.getAsymmetricSpec();
            BSCombinedChartNode chartNode = getChartNode();
            
            if ( moduleSpec.isOffsetControlSupported() ) {
                BSAbstractHandle offsetHandle = new BSAsymmetricOffsetHandle( potential, potentialSpec, chartNode );
                addHandle( offsetHandle );
            }

            if ( !potentialSpec.getHeightRange().isZero() ) {
                BSAbstractHandle heightHandle = new BSAsymmetricHeightHandle( potential, potentialSpec, chartNode );
                addHandle( heightHandle );
            }

            if ( !potentialSpec.getWidthRange().isZero() ) {
                BSAbstractHandle widthHandle = new BSAsymmetricWidthHandle( potential, potentialSpec, chartNode );
                addHandle( widthHandle );
            }
        }
    }
}
