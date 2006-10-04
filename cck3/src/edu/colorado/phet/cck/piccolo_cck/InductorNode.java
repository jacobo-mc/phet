package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.CCKImageSuite;
import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.Inductor;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 20, 2006
 * Time: 10:49:26 AM
 * Copyright (c) Sep 20, 2006 by Sam Reid
 */

public class InductorNode extends ComponentImageNode {
    private ICCKModule module;
    private Inductor inductor;

    public InductorNode( CCKModel model, Inductor inductor, Component component, ICCKModule module ) {
        super( model, inductor, CCKImageSuite.getInstance().getInductorImage(), component );
        this.module = module;
        this.inductor = inductor;
    }

    protected JPopupMenu createPopupMenu() {
        return new ComponentMenu.InductorMenu( inductor, module ).getMenuComponent();
    }
}
