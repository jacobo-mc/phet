// Copyright 2002-2011, University of Colorado

var canvas;
var touchInProgress = false;
var context;
var particlesInNucleus = new Array();

var masses = new Array();

var redMassImage = new Image();
redMassImage.src = "resources/red-mass.png";

var greenMassImage = new Image();
greenMassImage.src = "resources/green-mass.png";

var rulerImage = new Image();
rulerImage.src = "resources/ruler.png";

masses.push( new ImageSprite( redMassImage, 50, 400 ) );
masses.push( new ImageSprite( greenMassImage, 100, 400 ) );
masses.push( new ImageSprite( rulerImage, 0, 400 ) );

var dragTarget = null;
var relativeGrabPoint = null;
var resetButton;

// Hook up the initialization function.
$( document ).ready( function () {
    init();
} );

// Hook up event handler for window resize.
$( window ).resize( resizer );

// Handler for window resize events.
function resizer() {
    console.log( "resize received" );
    canvas.width = $( window ).width();
    canvas.height = $( window ).height();
    draw();
}

// Initialize the canvas, context,
function init() {

    // Initialize references to the HTML5 canvas and its context.
    canvas = $( '#canvas' )[0];
    if ( canvas.getContext ) {
        context = canvas.getContext( '2d' );
    }

    // Set up event handlers.
    // TODO: Work with JO to "jquery-ize".
    document.onmousedown = onDocumentMouseDown;
    document.onmouseup = onDocumentMouseUp;
    document.onmousemove = onDocumentMouseMove;

    document.addEventListener( 'touchstart', onDocumentTouchStart, false );
    document.addEventListener( 'touchmove', onDocumentTouchMove, false );
    document.addEventListener( 'touchend', onDocumentTouchEnd, false );

    // Add the reset button.
    resetButton = new ResetButton( new Point2D( 600, 325 ), "orange" );

    // Commenting out, since iPad seems to send these continuously.
//	window.addEventListener( 'deviceorientation', onWindowDeviceOrientation, false );

    // Disable elastic scrolling.  This is specific to iOS.
    document.addEventListener(
            'touchmove',
            function ( e ) {
                e.preventDefault();
            },
            false
    );

    // Do the initial drawing, events will cause subsequent updates.
    resizer();
}

function clearBackground() {
    context.save();
    context.globalCompositeOperation = "source-over";
    context.fillStyle = "rgb(255, 255, 153)";
    context.fillRect( 0, 0, canvas.width, canvas.height );
    context.restore();
}

function drawTitle() {
    context.fillStyle = '#00f';
    context.font = '30px sans-serif';
    context.textBaseline = 'top';
    context.fillText( 'Masses and Springs', 10, 10 );
}

function drawPhetLogo() {
    context.fillStyle = '#f80';
    context.font = 'italic 20px sans-serif';
    context.textBaseline = 'top';
    context.fillText( 'PhET', canvas.width - 70, canvas.height / 2 );
}

//-----------------------------------------------------------------------------
// Point2D class.
//-----------------------------------------------------------------------------

function Point2D( x, y ) {
    // Instance Fields or Data Members
    this.x = x;
    this.y = y;
}

Point2D.prototype.toString = function () {
    return this.x + ", " + this.y;
}

Point2D.prototype.setComponents = function ( x, y ) {
    this.x = x;
    this.y = y;
}

Point2D.prototype.minus = function ( pt ) {
    var pt = new Point2D( this.x - pt.x, this.y - pt.y );
    return pt;
}

Point2D.prototype.set = function ( point2D ) {
    this.setComponents( point2D.x, point2D.y );
}

//-----------------------------------------------------------------------------
// Particle class.
//-----------------------------------------------------------------------------

function Particle( color ) {
    this.location = new Point2D( 0, 0 );
    this.radius = 20;
    this.color = color;
}

function ImageSprite( im, x, y ) {
    this.image = im;
    this.position = new Point2D( x, y );

    //Repaint the screen when this image got loaded
    this.image.onload = function () {
        draw();
    }
}

ImageSprite.prototype.draw = function ( context ) {
//    javascript: console.log( "drawing mass" );
    context.drawImage( this.image, this.position.x, this.position.y );
}

ImageSprite.prototype.containsPoint = function ( point ) {
    javascript: console.log( "point = " + point.x + ", " + point.y + ", location = " + this.position.x + ", " + this.position.y + ", width = " + this.image.width + ", height = " + this.image.height );
    return point.x >= this.position.x && point.y >= this.position.y && point.x <= this.position.x + this.image.width && point.y <= this.position.y + this.image.height;
}

ImageSprite.prototype.setPosition = function ( point ) {
    this.position = new Point2D( point.x, point.y );
}

Particle.prototype.draw = function ( context ) {
    var xPos = this.location.x;
    var yPos = this.location.y;
    var gradient = context.createRadialGradient( xPos - this.radius / 3, yPos - this.radius / 3, 0, xPos, yPos, this.radius );
    gradient.addColorStop( 0, "white" );
    gradient.addColorStop( 1, this.color );
    context.fillStyle = gradient;
    context.beginPath();
    context.arc( xPos, yPos, this.radius, 0, Math.PI * 2, true );
    context.closePath();
    context.fill();
}

Particle.prototype.setLocation = function ( location ) {
    this.setLocationComponents( location.x, location.y );
}

Particle.prototype.setLocationComponents = function ( x, y ) {
    this.location.x = x;
    this.location.y = y;
}

Particle.prototype.containsPoint = function ( point ) {
    return Math.sqrt( Math.pow( point.x - this.location.x, 2 ) + Math.pow( point.y - this.location.y, 2 ) ) < this.radius;
}

//-----------------------------------------------------------------------------
// Reset button class
//-----------------------------------------------------------------------------

function ResetButton( initialLocation, color ) {
    this.location = initialLocation;
    this.width = 90;
    this.height = 40;
    this.color = color;
}

ResetButton.prototype.draw = function ( context ) {
    var xPos = this.location.x;
    var yPos = this.location.y;
    var gradient = context.createLinearGradient( xPos, yPos, xPos, yPos + this.height );
    gradient.addColorStop( 0, "white" );
    gradient.addColorStop( 1, this.color );
    // Draw box that defines button outline.
    context.fillStyle = gradient;
    context.fillRect( xPos, yPos, this.width, this.height );
    // Put text on the box.
    context.fillStyle = '#000';
    context.font = '28px sans-serif';
    context.textBaseline = 'top';
    context.fillText( 'Reset', xPos + 5, yPos + 5 );
}

ResetButton.prototype.setLocationComponents = function ( x, y ) {
    this.location.x = x;
    this.location.y = y;
}

ResetButton.prototype.setLocation = function ( location ) {
    this.setLocationComponents( location.x, location.y );
}

ResetButton.prototype.containsPoint = function ( point ) {
    return point.x > this.location.x && point.x < this.location.x + this.width &&
           point.y > this.location.y && point.y < this.location.y + this.height;
}

//-----------------------------------------------------------------------------

// Main drawing function.
function draw() {

    clearBackground();

    // Draw the text.
    drawTitle();
    drawPhetLogo();

    // Draw the reset button.
    resetButton.draw( context );

    // Draw the particles that are in the nucleus.
    for ( var i = 0; i < particlesInNucleus.length; i++ ) {
        particlesInNucleus[i].draw( context );
    }

    //Draw the masses
    for ( i = 0; i < masses.length; i++ ) {
        masses[i].draw( context );
    }

    // Draw particle that is being dragged if there is one.
    if ( dragTarget != null ) {
        dragTarget.draw( context );
    }

}

//-----------------------------------------------------------------------------
// Utility functions
//-----------------------------------------------------------------------------

function removeAllParticles() {
    particlesInNucleus.length = 0;
}

function removeParticleFromNucleus( particle ) {
    for ( i = 0; i < particlesInNucleus.length; i++ ) {
        if ( particlesInNucleus[i] == particle ) {
            particlesInNucleus.splice( i, 1 );
            break;
        }
    }
    adjustNucleonPositions();
}

//-----------------------------------------------------------------------------
// Event handlers.
//-----------------------------------------------------------------------------

function onDocumentMouseDown( event ) {
    onTouchStart( new Point2D( event.clientX, event.clientY ) );
}

function onDocumentMouseUp( event ) {
    onTouchEnd( new Point2D( event.clientX, event.clientY ) );
}

function onDocumentMouseMove( event ) {
    onDrag( new Point2D( event.clientX, event.clientY ) );
}

function onDocumentTouchStart( event ) {
    if ( event.touches.length == 1 ) {

        //in the  the event handler to prevent the event from being propagated to the browser and causing unwanted scrolling events.
        event.preventDefault();
        onTouchStart( new Point2D( event.touches[ 0 ].pageX, event.touches[ 0 ].pageY ) );
    }
}

function onDocumentTouchMove( event ) {
    if ( event.touches.length == 1 ) {

        //in the  the event handler to prevent the event from being propagated to the browser and causing unwanted scrolling events.
        event.preventDefault();
        onDrag( new Point2D( event.touches[ 0 ].pageX, event.touches[ 0 ].pageY ) );
    }
}

function onDocumentTouchEnd( event ) {
    onTouchEnd();
}

function onWindowDeviceOrientation( event ) {
    console.log( "onWindowDeviceOrientation" );
}

function onTouchStart( location ) {
    touchInProgress = true;
    dragTarget = null;

    //See which sprite wants to handle the touch
    for ( var i = 0; i < masses.length; i++ ) {
        var containsPoint = masses[i].containsPoint( location );
//        javascript: console.log( "checking mass contains: " + containsPoint );
        if ( containsPoint ) {
            dragTarget = masses[i];
            relativeGrabPoint = new Point2D( location.x - dragTarget.position.x, location.y - dragTarget.position.y );
            break;
        }
    }

    draw();
}

function onDrag( location ) {
    if ( touchInProgress && dragTarget != null ) {
        dragTarget.setPosition( location.minus( relativeGrabPoint ) );
        draw();
    }
}

function onTouchEnd() {
    touchInProgress = false;
    draw();
}