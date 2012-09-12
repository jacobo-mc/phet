// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;

/**
 * Base class for energy sources, converters, and users.
 *
 * @author John Blanco
 */
public abstract class EnergySystemElement extends PositionableFadableModelElement {

    // List of images, if any, that are used in the view to represent this element.
    // TODO: I think that this can go once each element has its own node, and was basically done for prototyping.
    private final List<ModelElementImage> imageList = new ArrayList<ModelElementImage>();

    // List of energy chunks contained and managed by this energy system element.
    public final ObservableList<EnergyChunk> energyChunkList = new ObservableList<EnergyChunk>();

    // Image that is used as an icon in the view to represent this element.
    private final Image iconImage;

    // State variable that tracks whether this element is currently active.
    protected boolean active = false;

    protected EnergySystemElement( Image iconImage, List<ModelElementImage> images ) {
        this.iconImage = iconImage;
        this.imageList.addAll( images );
    }

    public List<ModelElementImage> getImageList() {
        return imageList;
    }

    public Image getIconImage() {
        return iconImage;
    }

    public abstract IUserComponent getUserComponent();

    public void activate() {
        active = true;
    }

    public void deactivate() {
        active = false;
    }
}
