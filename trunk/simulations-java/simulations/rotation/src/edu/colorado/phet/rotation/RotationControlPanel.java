package edu.colorado.phet.rotation;

import edu.colorado.phet.common.motion.graphs.GraphSelectionControl;
import edu.colorado.phet.common.motion.graphs.GraphSetModel;
import edu.colorado.phet.common.motion.graphs.GraphSuiteSet;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.rotation.controls.ShowVectorsControl;
import edu.colorado.phet.rotation.controls.SymbolKey;
import edu.colorado.phet.rotation.controls.VectorViewModel;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jan 9, 2007
 * Time: 7:51:51 AM
 */

public class RotationControlPanel extends JPanel {
    public RotationControlPanel( RulerNode rulerNode, GraphSuiteSet rotationGraphSet, GraphSetModel graphSetModel, VectorViewModel vectorViewModel, JFrame parentFrame ) {
        super( new GridBagLayout() );
        GraphSelectionControl graphSelectionControl = new GraphSelectionControl( rotationGraphSet, graphSetModel );
        SymbolKeyButton symbolKey = new SymbolKeyButton(parentFrame);
        ShowVectorsControl showVectorsControl = new ShowVectorsControl( vectorViewModel );

        RulerButton rulerButton = new RulerButton( rulerNode );

        add( graphSelectionControl, getConstraints( 0, 0 ) );
        add( symbolKey, getConstraints( 2, 0 ) );
        add( rulerButton, getConstraints( 2, 1 ) );
        add( showVectorsControl, getConstraints( 0, 1 ) );
    }

    private GridBagConstraints getConstraints( int gridX, int gridY ) {
        return new GridBagConstraints( gridX, gridY, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets( 10, 10, 10, 10 ), 0, 0 );
    }
}
