// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.membranechannels.model;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.membranechannels.module.MembraneChannelsDefaults;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * A motion strategy for traversing a basic membrane channel, i.e. one that
 * has only one gate, and then doing a random walk.
 * <p/>
 * IMPORTANT: This is a version of TraverseChannelMotionStrategy that allows
 * multiple particles through the channel at a time and handles collisions
 * inside the channels.  Part way through the implementation, it was decided
 * that we don't need it.  It is being kept in case this decision is changed.
 * If this is not in use by the time the sim is published (which should be
 * in September of 2010), this class should be removed.
 *
 * @author John Blanco
 */
public class TraverseChannelMotionStrategy2 extends MotionStrategy {

    private static final double DEFAULT_MAX_VELOCITY = 100; // In nanometers per second of sim time.
    private static final double POST_TRAVERSAL_WALK_TIME =
            MembraneChannelsDefaults.DEFAULT_MEMBRANE_CHANNELS_CLOCK_DT * 50;  // In seconds of sim time.
    private static final Random RAND = new Random();

    private MutableVector2D velocityVector = new MutableVector2D();
    private ArrayList<Point2D> traversalPoints;
    private int currentDestinationIndex = 0;
    private boolean channelHasBeenEntered = false; // Flag that is set when the channel is entered.
    private boolean channelHasBeenTraversed = false; // Flag that is set when particle has exited the channel.
    private double velocityScaler;
    protected final MembraneChannel channel;
    private Rectangle2D preTraversalMotionBounds = new Rectangle2D.Double();
    private Rectangle2D postTraversalMotionBounds = new Rectangle2D.Double();
    private MotionStrategy postTraversalMotionStrategy;
    private double postTraversalCountdownTimer = Double.POSITIVE_INFINITY;


    public TraverseChannelMotionStrategy2( MembraneChannel channel, Point2D startingLocation,
                                           Rectangle2D preTraversalMotionBounds, Rectangle2D postTraversalMotionBounds, double velocity ) {
        this.channel = channel;
        this.velocityScaler = velocity;
        this.preTraversalMotionBounds.setFrame( preTraversalMotionBounds );
        this.postTraversalMotionBounds.setFrame( postTraversalMotionBounds );
        traversalPoints = createTraversalPoints( channel, startingLocation );
        currentDestinationIndex = 0;
        setCourseForCurrentTraversalPoint( startingLocation );
    }

    public TraverseChannelMotionStrategy2( MembraneChannel channel, Point2D startingLocation,
                                           Rectangle2D preTraversalMotionBounds, Rectangle2D postTraversalMotionBounds ) {
        this( channel, startingLocation, preTraversalMotionBounds, postTraversalMotionBounds, DEFAULT_MAX_VELOCITY );
    }

    @Override
    public void move( IMovable movableModelElement, double dt ) {

        Point2D currentPositionRef = movableModelElement.getPositionReference();

        if ( !channelHasBeenEntered ) {
            // Update the flag the tracks whether this particle has made it
            // to the channel and started traversing it.
            channelHasBeenEntered = channel.isPointInChannel( currentPositionRef );
        }

        if ( channelHasBeenTraversed ) {
            // The channel has been traversed, and we are currently executing
            // the post-traversal motion.
            postTraversalCountdownTimer -= dt;
            if ( postTraversalCountdownTimer <= 0 ) {
                // The traversal process is complete, set a new Random Walk strategy.
                notifyStrategyComplete( movableModelElement );
                movableModelElement.setMotionStrategy( new RandomWalkMotionStrategy( postTraversalMotionBounds ) );
            }
            else {
                // Move the particle.
                postTraversalMotionStrategy.move( movableModelElement, dt );
            }
        }
        else if ( channel.isOpen() || channelHasBeenEntered ) {
            // The channel is open, or we are inside it, so keep executing
            // this motion strategy.  But first, make sure we are not bumping
            // into any other particles in this channel.
            if ( checkBumping( movableModelElement ) ) {
                reverseDirection( movableModelElement );
            }
            if ( currentDestinationIndex >= traversalPoints.size() || velocityScaler * dt < currentPositionRef.distance( traversalPoints.get( currentDestinationIndex ) ) ) {
                // Move according to the current velocity.
                movableModelElement.setPosition( currentPositionRef.getX() + velocityVector.getX() * dt,
                                                 currentPositionRef.getY() + velocityVector.getY() * dt );
            }
            else {
                // We are close enough to the destination that we should just
                // position ourself there and update to the next traversal point.
                movableModelElement.setPosition( traversalPoints.get( currentDestinationIndex ) );
                currentDestinationIndex++;
                setCourseForCurrentTraversalPoint( movableModelElement.getPosition() );
                if ( currentDestinationIndex == traversalPoints.size() ) {
                    // We have traversed through all points and are now
                    // presumably on the other side of the membrane, or has
                    // reemerged from the side that it went in (i.e. it
                    // changed direction while in the channel).  Start doing
                    // a random walk, but keep it as part of this motion
                    // strategy for now, since we don't want the particle to
                    // be immediately recaptured by the same channel.
                    channelHasBeenTraversed = true;
                    postTraversalMotionStrategy = new RandomWalkMotionStrategy( postTraversalMotionBounds );
                    postTraversalCountdownTimer = POST_TRAVERSAL_WALK_TIME;
                }
            }
        }
        else {
            // The channel has closed and this element has not yet entered it.
            // Time to replace this motion strategy with a different one.
            movableModelElement.setMotionStrategy( new RandomWalkMotionStrategy( preTraversalMotionBounds ) );
        }
    }

    /**
     * Check whether this particle is bumping up against any of the other
     * particles in this channel.
     *
     * @param movableModelElement
     * @return
     */
    private boolean checkBumping( IMovable movableModelElement ) {

        // Create a "bump check point", which is a point at the front of this
        // element, where the front is based on the current direction of
        // travel.
        Point2D bumpCheckPoint = new Point2D.Double(
                movableModelElement.getPositionReference().getX() + movableModelElement.getRadius() * Math.cos( velocityVector.getAngle() ),
                movableModelElement.getPositionReference().getY() + movableModelElement.getRadius() * Math.sin( velocityVector.getAngle() ) );

        // Get a list of the particles traversing the channel.
        // TODO: Commented out - would need to be implemented if this class is
        // ever actually used.
//        ArrayList<Particle> particlesTraversingChannel = channel.getParticlesTraversingChannel();
        ArrayList<Particle> particlesTraversingChannel = new ArrayList<Particle>();

        boolean bumping = false;
        for ( Particle particle : particlesTraversingChannel ) {
            if ( particle == movableModelElement ) {
                // Skip this one - it's us.
                continue;
            }
            Ellipse2D particleShape = new Ellipse2D.Double(
                    particle.getPositionReference().getX() - particle.getRadius(),
                    particle.getPositionReference().getY() - particle.getRadius(),
                    particle.getDiameter(),
                    particle.getDiameter()
            );
            particleShape.setFrame( particle.getPositionReference(), new PDimension( particle.getDiameter(), particle.getDiameter() ) );
            if ( particleShape.contains( bumpCheckPoint ) ) {
                bumping = true;
                break;
            }
        }
        return bumping;
    }

    @Override
    public MutableVector2D getInstantaneousVelocity() {
        return new MutableVector2D( velocityVector.getX(), velocityVector.getY() );
    }

    /**
     * Abort the traversal.  This was created for the case where a particle is
     * traversing the channel as the user grabs the channel.
     * <p/>
     * IMPORTANT NOTE: Because the motion strategy doesn't maintain a reference
     * to the element that it is moving (it works the other way around), the
     * movable element must be supplied.  If the wrong element is supplied,
     * it would obviously cause weird behavior.  So, like, don't do it.
     * <p/>
     * ANOTHER IMPORTANT NOTE: This does not send out notification of the
     * strategy having completed, since it didn't really complete.
     */
    public void abortTraversal( IMovable movableModelElement ) {
        Point2D currentPos = movableModelElement.getPositionReference();
        if ( preTraversalMotionBounds.contains( currentPos ) ) {
            // Hasn't started traversing yet.
            movableModelElement.setMotionStrategy( new RandomWalkMotionStrategy( preTraversalMotionBounds ) );
        }
        else if ( postTraversalMotionBounds.contains( currentPos ) ) {
            // It is all the way across, just still under the channel's control.
            movableModelElement.setMotionStrategy( new RandomWalkMotionStrategy( postTraversalMotionBounds ) );
        }
        else {
            // The particle is actually inside the channel.  In this case, we
            // just go ahead and put it at the last traversal point and set the
            // post-traversal strategy, and hope it doesn't look too weird.
            movableModelElement.setPosition( traversalPoints.get( traversalPoints.size() - 1 ) );
            movableModelElement.setMotionStrategy( new RandomWalkMotionStrategy( postTraversalMotionBounds ) );
        }
    }

    /**
     * Create the points through which a particle must move when traversing
     * this channel.
     *
     * @param channel
     * @param startingLocation
     * @return
     */
    private ArrayList<Point2D> createTraversalPoints( MembraneChannel channel, Point2D startingLocation ) {

        ArrayList<Point2D> points = new ArrayList<Point2D>();
        Point2D ctr = channel.getCenterLocation();
        double r = channel.getChannelSize().getHeight() * 0.7; // Make the point a little outside the channel.
        Point2D outerOpeningLocation = new Point2D.Double( ctr.getX(), ctr.getY() + r );
        Point2D innerOpeningLocation = new Point2D.Double( ctr.getX(), ctr.getY() - r );

        if ( startingLocation.distance( innerOpeningLocation ) < startingLocation.distance( outerOpeningLocation ) ) {
            points.add( innerOpeningLocation );
            points.add( outerOpeningLocation );
        }
        else {
            points.add( outerOpeningLocation );
            points.add( innerOpeningLocation );
        }

        return points;
    }

    private void setCourseForCurrentTraversalPoint( Point2D currentLocation ) {
        if ( currentDestinationIndex < traversalPoints.size() ) {
            Point2D dest = traversalPoints.get( currentDestinationIndex );
            velocityVector.setComponents( dest.getX() - currentLocation.getX(), dest.getY() - currentLocation.getY() );
            double scaleFactor = velocityScaler / velocityVector.magnitude();
            velocityVector.scale( scaleFactor );
        }
        else {
            // All points have been traversed.  Change the direction a bit in
            // order to make things look a little more "Brownian".
            velocityVector.rotate( ( RAND.nextDouble() - 0.5 ) * Math.PI * 0.9 );
        }
    }

    /**
     * This method is intended to be called when the particle needs to
     * reverse the direction in which it is traveling.  The most likely cause
     * for this is that it has collided with another particle.
     * <p/>
     * This is not as straightforward as simply reversing the velocity vector,
     * since we need to separately handle the cases where the particle has not
     * yet entered the channel, or has passed through the channel, as well as
     * when it is inside the channel.
     */
    private void reverseDirection( IMovable modelElement ) {
        if ( !channelHasBeenEntered ) {
            // The element has not yet entered the channel, so we reverse its
            // current direction and then behave as though it has already
            // passed through.
            channelHasBeenEntered = true;
            channelHasBeenTraversed = true;
            postTraversalMotionStrategy = new BoundedLinearMotionStrategy( velocityVector.rotate( Math.PI ) );
            postTraversalCountdownTimer = POST_TRAVERSAL_WALK_TIME;
            Rectangle2D tempMotionBounds = postTraversalMotionBounds;
            postTraversalMotionBounds = preTraversalMotionBounds;
            preTraversalMotionBounds = tempMotionBounds;
        }
        else if ( !channelHasBeenTraversed ) {
            // The particle is in the channel.  Reverse its direction by
            // reversing the set of traversal points and then setting a
            // course to the current one.  This assumes an even number of
            // traversal points and a symmetrical pattern.
            Collections.reverse( traversalPoints );
            setCourseForCurrentTraversalPoint( modelElement.getPositionReference() );
            Rectangle2D tempMotionBounds = postTraversalMotionBounds;
            postTraversalMotionBounds = preTraversalMotionBounds;
            preTraversalMotionBounds = tempMotionBounds;
        }
        else {
            // The channel has been fully traversed.
            // NOTE: Not sure how to handle this case, and it is not implemented.
        }
    }
}
