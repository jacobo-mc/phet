define( ['easel', 'underscore', 'view/easel/EnergySkateParkRootNode'], function ( Easel, _, EnergySkateParkRootNode ) {
    function EnergySkateParkCanvas( $canvas, Strings, analytics, model ) {
        var self = this;
        $canvas[0].onselectstart = function () { return false; }; // IE
        $canvas[0].onmousedown = function () { return false; }; // Mozilla
        console.log( $canvas );

        this.root = EnergySkateParkRootNode( model, analytics );

        this.stage = new Easel.Stage( $canvas[0] );
        this.stage.mouseMoveOutside = true;
        this.stage.addChild( this.root );

        //Paint once after initialization
        this.stage.update();

        //TODO: Rewrite this to use CSS
        function handleResize() {

            //Gets rid of scroll bars
            var width = $( window ).width();
            var height = $( window ).height();

            var scale = Math.min( width / 1024, height / 768 );
            var canvasW = scale * 1024;
            var canvasH = scale * 768;

            //Allow the canvas to fill the screen, but still center the content within the window.
            $canvas.attr( 'width', width );
            $canvas.attr( 'height', height );
            var left = (width - canvasW) / 2;
            var top = (height - canvasH) / 2;

            self.root.scaleX = self.root.scaleY = scale;
            self.root.x = left;
            self.root.y = top;

            self.stage.update();

            //Center the footer under the ground
            var $footer = $( ".footer" );
            $footer.css( "top", canvasH + top - 100 * scale + 'px' ).
                    css( "left", width / 2 - $footer.width() + $( ".play-pause-button" ).width() / 2 );
        }

        $( window ).resize( handleResize );
        handleResize();

        Easel.Ticker.setFPS( 60 );

        //Enable touch and prevent default
        Easel.Touch.enable( this.stage, false, true );

        //Necessary to enable MouseOver events
        this.stage.enableMouseOver();
    }

    EnergySkateParkCanvas.prototype.render = function () {
        this.stage.tick();
    };
    return EnergySkateParkCanvas;
} );
