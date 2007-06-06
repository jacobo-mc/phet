/* Copyright 2004, Sam Reid */
package org.reid.particles.tutorial;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Aug 22, 2005
 * Time: 11:39:00 PM
 * Copyright (c) Aug 22, 2005 by Sam Reid
 */

public class TutorialFrame extends JFrame {
    private TutorialApplication tutorialApplication;

    public TutorialFrame( TutorialApplication tutorialApplication ) {
        super( "The Self-Driven Particle Model" );
        this.tutorialApplication = tutorialApplication;
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }
}
