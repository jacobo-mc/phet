/**
 * Class: ApparatusPanelContainer
 * Package: edu.colorado.phet.common.view.tabs
 * Author: Another Guy
 * Date: Jun 11, 2003
 */
package edu.colorado.phet.common_greenhouse.view.apparatuspanelcontainment;

import edu.colorado.phet.common_greenhouse.application.ModuleObserver;

import javax.swing.*;

public interface ApparatusPanelContainer extends ModuleObserver {
    JComponent getComponent();
}
