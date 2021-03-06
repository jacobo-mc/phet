// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dischargelamps.view;

import java.awt.image.BufferedImage;

import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.dischargelamps.DischargeLampsResources;
import edu.colorado.phet.lasers.view.AbstractLegend;

/**
 * Created by: Sam
 * May 25, 2008 at 11:23:40 PM
 */
public class DischargeLampsLegend extends AbstractLegend {
    public DischargeLampsLegend() {
        super( DischargeLampsResources.getString( "Legend.title" ) );
        addLegendItem( getAtomImage(), DischargeLampsResources.getString( "Legend.atom" ) );
        addLegendItem( getElectronImage(), DischargeLampsResources.getString( "Legend.electron" ) );
        addLegendItem( createPhotonLegendImage(), DischargeLampsResources.getString( "Legend.photon" ) );
    }

    protected BufferedImage getElectronImage() {
        return DischargeLampsResources.getImage( DischargeLampsConfig.ELECTRON_IMAGE_FILE_NAME );
    }
}
