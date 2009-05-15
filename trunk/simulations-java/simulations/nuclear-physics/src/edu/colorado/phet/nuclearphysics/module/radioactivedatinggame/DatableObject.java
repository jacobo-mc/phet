/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;
import edu.colorado.phet.nuclearphysics.model.Carbon14Nucleus;
import edu.colorado.phet.nuclearphysics.model.Uranium238Nucleus;

/**
 * This class represents a physical object that can be dated using radiometric
 * measurements, such as a skull or a fossil or a tree.
 * 
 */
public class DatableObject {

	private final Point2D center;
	private final double width;
	private final double height;
	private final double age;
	private final String name;
	private final String resourceImageName;
	private final double rotationAngle; // In radians.
	private BufferedImage image;
	
	public DatableObject(String name, String resourceImageName, Point2D center, double width, 
			double rotationAngle, double age) {
		super();
		this.name = name;
		this.center = new Point2D.Double(center.getX(), center.getY());
		this.width = width;
		this.age = age;
		this.resourceImageName = resourceImageName;
		this.rotationAngle = rotationAngle;
		
		image = NuclearPhysicsResources.getImage(resourceImageName);
		if (rotationAngle != 0){
			image = BufferedImageUtils.getRotatedImage(image, rotationAngle);
		}
		
		// The height is defined by a combination of the width of the artifact
		// and the aspect ratio of the image.
		this.height = (double)image.getHeight() / (double)image.getWidth() * width;
	}

	public Point2D getCenter() {
		return new Point2D.Double(center.getX(), center.getY());
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}
	
	/**
	 * Get the age of the item in milliseconds.
	 */
	public double getAge() {
		return age;
	}
	
	/**
	 * Get the amount of a substance that would be left based on the age of an
	 * item and the half life of the nucleus of the radiometric material being
	 * tested.
	 * 
	 * @param item
	 * @param customNucleusHalfLife
	 * @return
	 */
	public static double getPercentageCustomNucleusRemaining( DatableObject item, double customNucleusHalfLife ){
		return calculatePercentageRemaining(item.getAge(), customNucleusHalfLife);
	}
	
	public static double getPercentageCarbon14Remaining( DatableObject item ){
		return calculatePercentageRemaining(item.getAge(), Carbon14Nucleus.HALF_LIFE);
	}
	
	public static double getPercentageUranium238Remaining( DatableObject item ){
		return calculatePercentageRemaining(item.getAge(), Uranium238Nucleus.HALF_LIFE);
	}
	
	private static double calculatePercentageRemaining( double age, double halfLife ){
		if ( age <= 0 ){
			return 100;
		}
		else{
			return 100 * Math.exp( -0.693 * age / halfLife );
		}
	}
	
	public String getResourceImageName() {
		return resourceImageName;
	}

	public BufferedImage getImage() {
		return image;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return super.toString() + ": " + name;
	}
	
	public boolean contains(Point2D pt){
		
		return getBoundingRect().contains(pt);
		
	}
	
	public Rectangle2D getBoundingRect(){
		return new Rectangle2D.Double( center.getX() - width /2, center.getY() - height/2, width, height );
	}

	public double getRotationAngle() {
		return rotationAngle;
	}
}
