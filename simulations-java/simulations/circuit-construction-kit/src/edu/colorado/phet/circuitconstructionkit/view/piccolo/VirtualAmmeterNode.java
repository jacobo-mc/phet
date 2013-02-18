// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import java.awt.Component;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.CCKResources;
import edu.colorado.phet.circuitconstructionkit.CCKSimSharing;
import edu.colorado.phet.circuitconstructionkit.model.Circuit;
import edu.colorado.phet.circuitconstructionkit.model.CircuitListenerAdapter;
import edu.colorado.phet.circuitconstructionkit.model.Junction;
import edu.colorado.phet.circuitconstructionkit.model.analysis.CircuitSolutionListener;
import edu.colorado.phet.circuitconstructionkit.model.components.Branch;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ModelComponentTypes;
import edu.colorado.phet.common.phetcommon.simsharing.messages.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * User: Sam Reid
 * Date: Jun 14, 2004
 * Time: 7:56:28 PM
 */
public class VirtualAmmeterNode extends PhetPNode {
    private TargetReadoutToolNode targetReadoutToolNode;
    private Component panel;
    private CCKModule module;
    private Circuit circuit;
    private Branch previousBranch = null; // Used to detect changes in connection state.

    public VirtualAmmeterNode( Circuit circuit, Component panel, CCKModule module ) {
        this( new TargetReadoutToolNode(), panel, circuit, module );
    }

    public VirtualAmmeterNode( TargetReadoutToolNode targetReadoutTool, final Component panel, Circuit circuit, final CCKModule module ) {
        this.targetReadoutToolNode = targetReadoutTool;
        this.panel = panel;
        this.module = module;
        this.circuit = circuit;
        targetReadoutToolNode.scale( 1.0 / 60.0 * 0.75 );
        targetReadoutToolNode.setOffset( 1, 1 );
        addChild( targetReadoutTool );

        addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                PDimension pt = event.getDeltaRelativeTo( VirtualAmmeterNode.this );
                translate( pt.width, pt.height );
                update();
            }
        } );
        addInputEventListener( new CursorHandler() );
        module.getCCKModel().getCircuitSolver().addSolutionListener( new CircuitSolutionListener() {
            public void circuitSolverFinished() {
                update();
            }
        } );
        circuit.addCircuitListener( new CircuitListenerAdapter() {

            public void junctionRemoved( Junction junction ) {
                update();
            }

            public void branchRemoved( Branch branch ) {
                update();
            }

            public void junctionsMoved() {
                update();
            }

            public void branchesMoved( Branch[] branches ) {
                update();
            }

            public void junctionAdded( Junction junction ) {
                update();
            }

            public void junctionsConnected( Junction a, Junction b, Junction newTarget ) {
                update();
            }

            public void junctionsSplit( Junction old, Junction[] j ) {
                update();
            }

            public void branchAdded( Branch branch ) {
                update();
            }

        } );
        update();
    }

    public void update() {
        Point2D target = new Point2D.Double();
        targetReadoutToolNode.localToGlobal( target );
        globalToLocal( target );
        localToParent( target );
        String[] previousText = targetReadoutToolNode.getText();

        //check for intersect with circuit.
        Branch branch = circuit.getBranch( target );
        if ( branch != null ) {
            double current = branch.getCurrent();
            DecimalFormat df = new DecimalFormat( "0.00" );
            String amps = df.format( Math.abs( current ) );
            targetReadoutToolNode.setText( amps + " " + CCKResources.getString( "VirtualAmmeter.Amps" ) );
        }
        else {
            resetText();
        }

        // Send a sim sharing message if the connection state has changed.
        if ( previousBranch != branch ) {
            IModelAction modelAction = previousBranch == null ? CCKSimSharing.ModelActions.connectionFormed : CCKSimSharing.ModelActions.connectionBroken;
            SimSharingManager.sendModelMessage( CCKSimSharing.ModelComponents.nonContactAmmeterModel,
                                                ModelComponentTypes.modelElement,
                                                modelAction
            );
            previousBranch = branch;
        }

        // Send a sim sharing message if the readout has changed.
        String[] currentText = targetReadoutToolNode.getText();
        if ( ( previousText.length != currentText.length ) ||
             ( previousText.length == 1 && currentText.length == 1 && !previousText[0].equals( currentText[0] ) ) ) {

            String currentlyDisplayedText = ( branch == null || currentText.length == 0 ) ? "undefined" : currentText[0];
            SimSharingManager.sendModelMessage( CCKSimSharing.ModelComponents.nonContactAmmeterModel,
                                                ModelComponentTypes.modelElement,
                                                CCKSimSharing.ModelActions.measuredCurrentChanged,
                                                new ParameterSet( new Parameter( new ParameterKey( "current" ), currentlyDisplayedText ) ) );

        }
    }

    private void resetText() {
        String[] text = new String[] {
                CCKResources.getString( "VirtualAmmeter.HelpString1" ),
                CCKResources.getString( "VirtualAmmeter.HelpString2" )
        };
        targetReadoutToolNode.setText( text );
    }

}
