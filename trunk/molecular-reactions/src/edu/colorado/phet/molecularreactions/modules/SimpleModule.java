/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.modules;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.molecularreactions.controller.ManualControlAction;
import edu.colorado.phet.molecularreactions.controller.RunAction;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.model.reactions.A_BC_AB_C_Reaction;
import edu.colorado.phet.molecularreactions.view.AbstractSimpleMoleculeGraphic;
import edu.colorado.phet.molecularreactions.view.LauncherGraphic;
import edu.colorado.phet.molecularreactions.util.ModelElementGraphicManager;
import edu.colorado.phet.piccolo.nodes.RegisterablePNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolo.PNode;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

/**
 * MRModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimpleModule extends MRModule {
    private Launcher launcher;

    public SimpleModule() {
        super( "Simple" );

        // Set up the model
        MRModel model = (MRModel)getModel();

        // Disable marking of the selected molecule and its nearest neighbor
        AbstractSimpleMoleculeGraphic.setMARK_SELECTED_MOLECULE( false );

        getSpatialView().addGraphicFactory( new ModelElementGraphicManager.GraphicFactory( Launcher.class,
                                                                                           getSpatialView().getTopLayer() ) {
            public PNode createGraphic( ModelElement modelElement ) {
                return new LauncherGraphic( (Launcher)modelElement );
            }
        } );

        
        // Set up the molecules
        setInitialConditions( model );

        // create the control panel
        getControlPanel().addControl( new SimpleMRControlPanel( this ) );

        // Add Manual and Run Control buttons
//        createManualRunButtons();

//        // Set up the molecules
//        setInitialConditions( model );
    }

    public Launcher getLauncher() {
        return launcher;
    }

    private void createManualRunButtons() {
        final JButton manualCtrlBtn = new JButton( SimStrings.get( "Control.manualControl" ) );
        manualCtrlBtn.addActionListener( new ManualControlAction( this ) );
        RegisterablePNode ctrlBtnNode = new RegisterablePNode( new PSwing( getPCanvas(), manualCtrlBtn ) );
        double btnX = ( getMRModel().getBox().getMaxX() + getSpatialView().getFullBounds().getWidth() ) / 2;
        ctrlBtnNode.setOffset( btnX, 50 );
        ctrlBtnNode.setRegistrationPoint( ctrlBtnNode.getFullBounds().getWidth() / 2, 0 );
        getSpatialView().addChild( ctrlBtnNode );

        final JButton runBtn = new JButton( SimStrings.get( "Control.run" ) );
        runBtn.addActionListener( new RunAction( this ) );
        RegisterablePNode runBtnNode = new RegisterablePNode( new PSwing( getPCanvas(), runBtn ) );
        runBtnNode.setOffset( btnX, 120 );
        runBtnNode.setRegistrationPoint( runBtnNode.getFullBounds().getWidth() / 2, 0 );
        getSpatialView().addChild( runBtnNode );

        // Add listeners that will enable/disable the buttons appropriately
        manualCtrlBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                runBtn.setEnabled( true );
                manualCtrlBtn.setEnabled( false );
            }
        } );

        runBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                runBtn.setEnabled( false );
                manualCtrlBtn.setEnabled( true );
            }
        } );
        runBtn.setEnabled( false );
        manualCtrlBtn.setEnabled( true );
    }

    /**
     * @param model
     */
    protected void setInitialConditions( MRModel model ) {

        model.setReaction( new A_BC_AB_C_Reaction( model ) );

        // Place the heat source to the right of center
        TemperatureControl tempCtrl = model.getTemperatureControl();
        tempCtrl.setPosition( model.getBox().getMaxX() - 50, tempCtrl.getPosition().getY() );


        // Add the launcher and its graphic
        Point2D launcherTipLocation = new Point2D.Double( (model.getBox().getMinX() + model.getBox().getMaxX()) / 2,
                                                          model.getBox().getMaxY() );
//        Point2D launcherTipLocation = new Point2D.Double( model.getBox().getMinX() + 100, model.getBox().getMaxY() );
        launcher = new Launcher( launcherTipLocation );
        launcher.setTipLocation( launcherTipLocation );
        model.addModelElement( launcher );

        SimpleMolecule m2 = new MoleculeC();
        m2.setPosition( launcher.getTipLocation().getX(), launcher.getTipLocation().getY() - m2.getRadius() );
//                m2.setVelocity( 1, 0 );
//                m2.setVelocity( 1.5, 0 );
        model.addModelElement( m2 );
        launcher.setBodyToLaunch( m2 );
        launcher.setMovementType( Launcher.ONE_DIMENSIONAL );

        SimpleMolecule m1 = new MoleculeB();
        double yLoc = model.getBox().getMinY() + model.getBox().getHeight() / 2;
        m1.setPosition( m2.getPosition().getX(), yLoc );
//                m1.setPosition( 280, yLoc );
        m1.setVelocity( 0, 0 );
        model.addModelElement( m1 );
        SimpleMolecule m1a = new MoleculeA();
        m1a.setPosition( m1.getPosition().getX(), yLoc - m1.getRadius() - m1a.getRadius() );
//                m1a.setPosition( m1.getPosition().getX() + m1.getRadius() + m1a.getRadius(), yLoc );
        m1a.setVelocity( 0, 0 );
        model.addModelElement( m1a );

        CompositeMolecule cm = new MoleculeAB( new SimpleMolecule[]{m1, m1a} );
        cm.setOmega( 0 );
        cm.setVelocity( 0, 0 );
        model.addModelElement( cm );


        m2.setSelectionStatus( Selectable.SELECTED );
    }

    public void reset() {
        super.reset();
        getModel().removeAllModelElements();
        ( (MRModel)getModel() ).setInitialConditions();
        setInitialConditions( (MRModel)getModel() );
    }
}
