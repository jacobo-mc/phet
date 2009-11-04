/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * This is a base class for model elements that exist inside a cell or in
 * extracellular space, and that are not composed of any other model elements.
 * They are, in a sense, the atomic elements of this model.
 * 
 * @author John Blanco
 */
public abstract class SimpleModelElement implements IModelElement{
	
	// Range within with bonding can occur.
	protected static final double BONDING_RANGE = 1000;  // In nanometers.
	
	// Range at which a bond forms when two binding partners are moving
	// towards each other.
	protected static final double BOND_FORMING_DISTANCE = 1; // In nanometers.
	
	private Shape shape;
	private Point2D position;
	private Paint paint;  // The paint to use when representing this element in the view.
	private Vector2D.Double velocity = new Vector2D.Double();
    protected ArrayList<Listener> listeners = new ArrayList<Listener>();
    private ArrayList<BindingPoint> bindingPoints = new ArrayList<BindingPoint>();
    private AbstractMotionStrategy motionStrategy = null;
    
	public SimpleModelElement(Shape initialShape, Point2D initialPosition, Paint paint){
		this.shape = initialShape;
		this.position = initialPosition;
		this.paint = paint;
	}
	
	public abstract ModelElementType getType();
	
	public Shape getShape(){
		return shape;
	}
	
	protected void setShape(Shape shape){
		this.shape = shape;  
	}
	
	public Point2D getPositionRef(){
		return position;
	}
	
	public void setPosition(double xPos, double yPos ){
		if (xPos != position.getX() || yPos != position.getY()){
			position.setLocation(xPos, yPos);
			notifyPositionChanged();
		}
	}
	
	public void setPosition(Point2D newPosition){
		setPosition(newPosition.getX(), newPosition.getY());
	}
	
	public Paint getPaint(){
		return paint;
	}
	
	public String getLabel(){
		return null;
	}
	
	public void setVelocity(double xVel, double yVel){
		velocity.setComponents(xVel, yVel);
	}
	
	public void setVelocity(Vector2D newVelocity){
		setVelocity(newVelocity.getX(), newVelocity.getY());
	}
	
	public Vector2D getVelocityRef(){
		return velocity;
	}
	
	/**
	 * Update the position of this model element based on its current position
	 * and its velocity.  Note that this assumes that this must be called at
	 * an appropriate frequency in order of the motion of the model element to
	 * be correct.
	 */
	public void updatePosition(){
		if (velocity.getX() != 0 || velocity.getY() != 0){
			position.setLocation(position.getX() + velocity.getX(), position.getY() + velocity.getY());
			notifyPositionChanged();
		}
	}
	
	public BindingPoint getBindingPointForElement(ModelElementType elementType){
		BindingPoint matchingBindingPoint = null;
		for (BindingPoint bindingPoint : bindingPoints){
			if (bindingPoint.getElementType() == elementType){
				// We have a match.
				matchingBindingPoint = bindingPoint;
				break;
			}
		}
		if (matchingBindingPoint == null){
			System.err.println(getClass().getName() + " - Warning: " + this.getType() + " has no binding point found for " + elementType);
		}
		return matchingBindingPoint;
	}
	
	/**
	 * Add a binding point to the list that is being maintained for this model
	 * element.
	 */
	protected void addBindingPoint(BindingPoint bindingPoint){
		bindingPoints.add(bindingPoint);
	}
	
	protected void notifyPositionChanged(){
		// Notify all listeners of the position change.
		for (Listener listener : listeners)
		{
			listener.positionChanged(); 
		}        
	}
	
	public void addListener(Listener listener) {
		if (listeners.contains( listener ))
		{
			// Don't bother re-adding.
			System.err.println(getClass().getName() + "- Warning: Attempting to re-add a listener that is already listening.");
			assert false;
			return;
		}
		
		listeners.add( listener );
	}
	
	public void removeListener(Listener listener){
		listeners.remove(listener);
	}
	
	public boolean availableForBonding(ModelElementType elementType) {
		// Always says no in the base class.
		return false;
	}

	public boolean considerProposalFrom(IModelElement modelElement) {
		// Always refuses proposal in the base class.
		return false;
	}

	public boolean releaseBondWith(IModelElement modelElement) {
		// Always refuses to release in the base class.
		return false;
	}

	public void updatePositionAndMotion() {
		if (motionStrategy != null){
			motionStrategy.updatePositionAndMotion();
		}
	}

	public void updatePotentialBondingPartners( ArrayList<IModelElement> modelElements ) {
		// Does nothing in the base class, which essentially means that it
		// if not overridden it will not initiate any bonds.
	}
	
	protected void setMotionStrategy(AbstractMotionStrategy motionStrategy){
		this.motionStrategy = motionStrategy;
	}
	
	protected AbstractMotionStrategy getMotionStrategyRef(){
		return motionStrategy;
	}
	
    //------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------
	
    public interface Listener {
        void positionChanged();
    }
}
