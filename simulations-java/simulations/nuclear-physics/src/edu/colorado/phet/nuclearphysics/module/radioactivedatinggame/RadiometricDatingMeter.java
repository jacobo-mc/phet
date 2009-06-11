/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObservable;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.nuclearphysics.common.NucleusType;
import edu.colorado.phet.nuclearphysics.model.HalfLifeInfo;

/**
 * This class encapsulates a meter that supplies information about the amount
 * of a radiometric substance that has decayed in a given sample.
 * 
 * @author John Blanco
 */
public class RadiometricDatingMeter extends SimpleObservable {

	//----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

	private final ProbeModel _probe;
	private DatableObject _itemBeingTouched = null;
	private RadioactiveDatingGameModel _model;
	private NucleusType _nucleusTypeForDating;
	private double _halfLifeOfDatingNucleus;
	
	//----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------
	
	public RadiometricDatingMeter( RadioactiveDatingGameModel model ) {
		_model = model;
		_probe = new ProbeModel(new Point2D.Double(-20, -8), -0.3);
		_probe.addObserver(new SimpleObserver(){
			public void update() {
				updateTouchedItem();
			}
		});
		
		// Set the default nucleus type.
		_nucleusTypeForDating = NucleusType.CARBON_14;
		
		updateTouchedItem();
	}

	//----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------

	public ProbeModel getProbeModel(){
		return _probe;
	}
	
	/**
	 * Get the item that is currently being touched by the meter's probe, if
	 * there is one.
	 * 
	 * @return item being touched if there is one, null if not
	 */
	public DatableObject getItemBeingTouched(){
		return _itemBeingTouched;
	}
	
	public void setNucleusTypeUsedForDating(NucleusType nucleusType){
		_nucleusTypeForDating = nucleusType;
	}
	
	public NucleusType getNucleusTypeUsedForDating(){
		return _nucleusTypeForDating;
	}
	
	/**
	 * Set the half life to use when dating.  This is only applicable when a
	 * custom nuclues is being used, otherwise the half life is determined by
	 * the selected nucleus type.
	 * 
	 * @param halfLife - Half life in milliseconds.
	 */
	public void setHalfLifeForDating(double halfLife){
		
		// This can ONLY be called if a custom nucleus is being used for
		// dating.
		assert _nucleusTypeForDating == NucleusType.CUSTOM;
		
		_halfLifeOfDatingNucleus = halfLife;
	}
	
	/**
	 * Get the half life of the currently selected dating element.
	 * 
	 * @return half life in milliseconds.
	 */
	public double getHalfLifeForDating(){
		if (_nucleusTypeForDating == NucleusType.CUSTOM){
			return _halfLifeOfDatingNucleus;
		}
		else{
			return HalfLifeInfo.getHalfLifeForNucleusType(_nucleusTypeForDating);
		}
	}
	
    /**
     * Update the current touched item based on the input probe location.
     */
    private void updateTouchedItem(){

    	DatableObject newTouchedItem = _model.getDatableItemAtLocation(_probe.getTipLocation());
    	
    	if (_itemBeingTouched != newTouchedItem){
    		_itemBeingTouched = newTouchedItem;
    		notifyObservers();
    	}
    }
	
	/**
	 * This class represents the probe that moves around and comes in contact
	 * with various datable elements in the model.
	 */
    public static class ProbeModel extends SimpleObservable {
        private Point2D.Double tipLocation;
        private double angle;
        private double tipWidth = 0.1 * 0.35;
        private double tipHeight = 0.3 * 1.25 * 0.75;

        public ProbeModel( double angle ) {
            this( new Point2D.Double(), angle );
        }

        public ProbeModel( Point2D.Double tipLocation, double angle ) {
            this.tipLocation = new Point2D.Double( tipLocation.getX(), tipLocation.getY() );
            this.angle = angle;
        }

        public void translate( double dx, double dy ) {
            tipLocation.x += dx;
            tipLocation.y += dy;
            notifyObservers();
        }

        public Point2D getTipLocation() {
            return new Point2D.Double( tipLocation.x, tipLocation.y );
        }

        public Shape getTipShape() {
            Rectangle2D.Double tip = new Rectangle2D.Double( tipLocation.x - tipWidth / 2, tipLocation.y, tipWidth, tipHeight );
            return AffineTransform.getRotateInstance( angle, tipLocation.x, tipLocation.y ).createTransformedShape( tip );
        }

        public double getAngle() {
            return angle;
        }

        static interface Listener {
            void probeModelChanged();
        }
    }
}
