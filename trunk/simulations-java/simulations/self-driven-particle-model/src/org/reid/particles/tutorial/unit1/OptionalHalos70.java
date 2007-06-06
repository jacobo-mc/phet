/* Copyright 2004, Sam Reid */
package org.reid.particles.tutorial.unit1;

import edu.colorado.phet.piccolo.pswing.PSwing;
import org.reid.particles.tutorial.BasicTutorialCanvas;
import org.reid.particles.tutorial.Page;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Aug 25, 2005
 * Time: 12:16:16 AM
 * Copyright (c) Aug 25, 2005 by Sam Reid
 */

public class OptionalHalos70 extends Page {
    private PSwing checkBox;

    public OptionalHalos70( final BasicTutorialCanvas basicPage ) {
        super( basicPage );
        setText( "You can optionally disable the visual range indicators.  " +
                 "This can improve performance and make it easier to see visualize certain phenomena." );
        final JCheckBox showHalos = new JCheckBox( "Show Range", basicPage.isHalosVisible() );
        showHalos.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                basicPage.setHalosVisible( showHalos.isSelected() );
                advance();
            }
        } );
        checkBox = new PSwing( basicPage, showHalos );

    }

    public void init() {
        super.init();
        checkBox.setOffset( getBasePage().getPreviousButton().getFullBounds().getX(), getBasePage().getPreviousButton().getFullBounds().getMaxY() + 5 );
        addChild( checkBox );
    }

    public void teardown() {
        super.teardown();
        removeChild( checkBox );
    }
}
