package edu.colorado.phet.circuitconstructionkit.view.piccolo.lifelike;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.components.Switch;
import edu.colorado.phet.circuitconstructionkit.view.CCKImageSuite;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.ComponentImageNode;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Sep 19, 2006
 * Time: 2:33:35 PM
 */

public class SwitchBodyImageNode extends ComponentImageNode {
    public SwitchBodyImageNode(CCKModel model, Switch s, JComponent component, CCKModule module) {
        super(model, s, CCKImageSuite.getInstance().getKnifeBoardImage(), component, module);
    }
}
