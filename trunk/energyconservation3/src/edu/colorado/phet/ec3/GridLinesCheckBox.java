/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.common.view.VerticalLayoutPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jun 7, 2006
 * Time: 11:10:03 PM
 * Copyright (c) Jun 7, 2006 by Sam Reid
 */

public class GridLinesCheckBox extends VerticalLayoutPanel {
    private EC3Module module;

    public GridLinesCheckBox( final EC3Module module ) {
        this.module = module;
        final JCheckBox gridlines = new JCheckBox( "Show Grid", getRoot().isGridVisible() );
        gridlines.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getRoot().setGridVisible( gridlines.isSelected() );
            }
        } );
        add( gridlines );
    }

    public EC3RootNode getRoot() {
        return module.getEnergyConservationCanvas().getRootNode();
    }
}
