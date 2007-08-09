package edu.colorado.phet.rotation.torque;

import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.rotation.AngleUnitModel;
import edu.colorado.phet.rotation.controls.VectorViewModel;
import edu.colorado.phet.rotation.view.RotationPlayAreaNode;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * Author: Sam Reid
 * Aug 8, 2007, 7:28:37 PM
 */
public class TorqueSimPlayAreaNode extends RotationPlayAreaNode {
    TorqueModel torqueModel;
    private PhetPPath appliedForceVector;
    private PhetPPath tangentialComponentVector;

    public TorqueSimPlayAreaNode( final TorqueModel torqueModel, VectorViewModel vectorViewModel, AngleUnitModel angleUnitModel ) {
        super( torqueModel, vectorViewModel, angleUnitModel );
        this.torqueModel = torqueModel;
        getPlatformNode().addInputEventListener( new RotationPlatformTorqueHandler( getPlatformNode(), torqueModel, torqueModel.getRotationPlatform() ) );
        getPlatformNode().addInputEventListener( new CursorHandler() );

        appliedForceVector = new PhetPPath( null, Color.blue, new BasicStroke( 0.01f ), Color.black );
        tangentialComponentVector = new PhetPPath( null, Color.green, new BasicStroke( 0.01f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[]{0.05f, 0.05f}, 0 ), Color.darkGray );
        torqueModel.addListener( new TorqueModel.Adapter() {
            public void appliedForceChanged() {
                updateArrows();
            }
        } );
        addChild( tangentialComponentVector );
        addChild( appliedForceVector );

        torqueModel.addListener( new TorqueModel.Adapter() {
            public void showComponentsChanged() {
                tangentialComponentVector.setVisible( torqueModel.isShowComponents() );
            }
        } );
        updateArrows();
    }

    private void updateArrows() {
        appliedForceVector.setPathTo( getForceShape( torqueModel.getAppliedForce() ) );
        tangentialComponentVector.setPathTo( getForceShape( this.torqueModel.getTangentialAppliedForce() ) );
    }

    private Shape getForceShape( Line2D.Double vector ) {
        return new Arrow( vector.getP1(), vector.getP2(), 0.25, 0.25, 0.1, 1.0, true ).getShape();
    }

}
