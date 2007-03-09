/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.EventChannel;

import java.util.EventListener;
import java.util.List;

/**
 * SelectedMoleculeTracker
 * <p/>
 * The simulation has a *selected molecule*. This is a molecule chosen by the user.
 * Instances of the SelectedMoleculeTracker find the coleset molecule to the
 * *selected molecule* that could react with it. These two molecules are then
 * represented on the EnergyView.
 * <p/>
 * Instances of the class attach themselves as listeners to all instances of SimpleMolecule,
 * and listener for changes in their selection status.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SelectedMoleculeTracker extends PublishingModel.ModelListenerAdapter implements ModelElement,
                                                                                             SimpleMolecule.ChangeListener {
    private MRModel model;
    private SimpleMolecule moleculeTracked;
    private SimpleMolecule closestMolecule;

    public SelectedMoleculeTracker( MRModel model ) {
        this.model = model;
        model.addListener( this );
    }

    private void setMoleculeTracked( SimpleMolecule newMoleculeTracked ) {
        SimpleMolecule prevMolecule = moleculeTracked;

        if (prevMolecule != newMoleculeTracked) {
            // If the previously tracked molecule is different than the one we are to
            // track now, tell the previously tracked molecule that it is no longer
            // selected
            if( prevMolecule != null &&
                prevMolecule.getSelectionStatus() != Selectable.NOT_SELECTED ) {
                prevMolecule.setSelectionStatus( Selectable.NOT_SELECTED );
            }
            moleculeTracked = newMoleculeTracked;
            listenerProxy.moleculeBeingTrackedChanged( newMoleculeTracked, prevMolecule );
        }
    }

    private void setClosestMolecule( SimpleMolecule newClosestMolecule ) {
        SimpleMolecule prevClosest = closestMolecule;

        if (prevClosest != newClosestMolecule) {
            // If the previously tracked molecule is different than the one we are to
            // track now, tell the previously tracked molecule that it is no longer
            // selected
            if( prevClosest != null &&
                prevClosest.getSelectionStatus() != Selectable.NOT_SELECTED ) {
                prevClosest.setSelectionStatus( Selectable.NOT_SELECTED );
            }
            closestMolecule = newClosestMolecule;
            listenerProxy.closestMoleculeChanged( closestMolecule, prevClosest );
        }
    }

    public SimpleMolecule getMoleculeTracked() {
        return moleculeTracked;
    }

    public SimpleMolecule getClosestMolecule() {
        return closestMolecule;
    }

    public void stepInTime( double dt ) {
        List modelElements = model.getModelElements();

        // Look for the closest molecule to the one being tracked that isn't of the
        // same type
        if( moleculeTracked != null ) {

            // Determine which type of molecules are eligible to be "closest".
            Class nearestMoleculeType = getNearestMoleculeType();

            // Find the closest eligible molecule to the selected molecule
            SimpleMolecule newClosestMolecule = null;

            double closestDistSq = Double.POSITIVE_INFINITY;

            for( int i = 0; i < modelElements.size(); i++ ) {
                Object o = modelElements.get( i );
                if( nearestMoleculeType.isInstance( o ) ) {
                    SimpleMolecule testMolecule = (SimpleMolecule)o;
                    if( moleculeTracked.isPartOfComposite() && !testMolecule.isPartOfComposite()
                        || !moleculeTracked.isPartOfComposite() && testMolecule.isPartOfComposite() ) {

                        // Make sure that the non-B molecule in the composite is not the same type as the
                        // non-composite
                        SimpleMolecule sm1 = moleculeTracked.isPartOfComposite() ? (SimpleMolecule)testMolecule : moleculeTracked;
                        CompositeMolecule cm = moleculeTracked.isPartOfComposite() ? (CompositeMolecule)moleculeTracked.getFullMolecule() : (CompositeMolecule)testMolecule.getFullMolecule();
                        SimpleMolecule sm2 = cm.getComponentMolecules()[0] instanceof MoleculeB ? cm.getComponentMolecules()[1] : cm.getComponentMolecules()[0];
                        if( sm1.getClass() == sm2.getClass() ) {
                            System.out.println( "SelectedMoleculeTracker.stepInTime" );
                            break;
                        }

                        double distSq = moleculeTracked.getPosition().distanceSq( testMolecule.getPosition() );
                        if( distSq < closestDistSq ) {
                            closestDistSq = distSq;
                            newClosestMolecule = testMolecule;
                        }
                    }
                }
            }

            setClosestMolecule( newClosestMolecule );
        }
        else {
            setClosestMolecule( null );
        }
    }

    private Class getNearestMoleculeType() {
        Class nearestMoleculeType = null;

        if( moleculeTracked instanceof MoleculeA ) {
            if( moleculeTracked.isPartOfComposite() ) {
                nearestMoleculeType = MoleculeC.class;
            }
            else {
                nearestMoleculeType = MoleculeB.class;
            }
        }
        if( moleculeTracked instanceof MoleculeC ) {
            if( moleculeTracked.isPartOfComposite() ) {
                nearestMoleculeType = MoleculeA.class;
            }
            else {
                nearestMoleculeType = MoleculeB.class;
            }
        }
        if( moleculeTracked instanceof MoleculeB ) {
            CompositeMolecule cm = (CompositeMolecule)moleculeTracked.getParentComposite();
            AbstractMolecule[] components = cm.getComponentMolecules();
            if( components[0] == moleculeTracked ) {
                nearestMoleculeType = components[1].getClass();
            }
            else {
                nearestMoleculeType = components[0].getClass();
            }
        }
        return nearestMoleculeType;
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of MRModel.Listener
    //--------------------------------------------------------------------------------------------------

    public void modelElementAdded( ModelElement element ) {
        if( element instanceof SimpleMolecule ) {
            ( (SimpleMolecule)element ).addListener( this );
        }
    }

    public void modelElementRemoved( ModelElement element ) {
        if( element instanceof SimpleMolecule ) {
            ( (SimpleMolecule)element ).removeListener( this );
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of SimpleMolecule.Listener
    //--------------------------------------------------------------------------------------------------

    public void selectionStatusChanged( SimpleMolecule molecule ) {
        if( molecule.getSelectionStatus() == Selectable.SELECTED ) {
            setMoleculeTracked( molecule );
        }

        if( getMoleculeTracked() == molecule && molecule.getSelectionStatus() != Selectable.SELECTED ) {
            setMoleculeTracked( null );
            setClosestMolecule( null );
        }
    }

    public void compositeStateChanged( AbstractMolecule molecule ) {

    }

    //--------------------------------------------------------------------------------------------------
    // Events and listeners
    //--------------------------------------------------------------------------------------------------
    public interface Listener extends EventListener {
        void moleculeBeingTrackedChanged( SimpleMolecule newTrackedMolecule, SimpleMolecule prevTrackedMolecule );

        void closestMoleculeChanged( SimpleMolecule newClosestMolecule, SimpleMolecule prevClosestMolecule );
    }

    private EventChannel listenerEventChannel = new EventChannel( Listener.class );
    private Listener listenerProxy = (Listener)listenerEventChannel.getListenerProxy();

    public void addListener( Listener listener ) {
        listenerEventChannel.addListener( listener );
    }

    public void removeListener( Listener listener ) {
        listenerEventChannel.removeListener( listener );
    }
}
