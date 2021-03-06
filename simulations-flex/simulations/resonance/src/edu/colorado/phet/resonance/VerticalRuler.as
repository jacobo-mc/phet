/**
 * Created by ${PRODUCT_NAME}.
 * User: General User
 * Date: 3/26/11
 * Time: 3:13 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.resonance {

import flash.display.CapsStyle;
import flash.display.Graphics;
import flash.display.Sprite;
import flash.events.MouseEvent;
import flash.geom.Point;
import flash.text.TextField;

import flash.text.TextFieldAutoSize;
import flash.text.TextFormat;

import mx.core.UIComponent;

public class VerticalRuler extends Sprite{
    //private var myMainView: MainView;
    private var myShakerView: ShakerView;
    public var ruler:Sprite;
    private var horizLine1: HorizontalReferenceLine;
    private var horizLine2: HorizontalReferenceLine;
    private var pixPerMeter:Number;
    private var cm_txt:TextField;
    private var tFieldArray:Array;    //array of textFields for numbers on ruler
    private var tFormat:TextFormat;

    public function VerticalRuler( myShakerView: ShakerView ) {
        //this.myMainView = myMainView;
        this.myShakerView = myShakerView;
        this.ruler = new Sprite();
        this.horizLine1 = new HorizontalReferenceLine();
        this.horizLine2 = new HorizontalReferenceLine();
        this.pixPerMeter = myShakerView.pixPerMeter;
        //trace("VerticalRuler.pixPerMeter = "+this.pixPerMeter);
        this.drawRuler();
        this.makeNumbers();
        this.makeSpriteGrabbable( this.ruler );
        this.addChild( this.ruler );
        this.addChild( this.horizLine1 );
        this.addChild( this.horizLine2 );
        this.initializePositions();
        this.makeVisible( false );  //default is that ruler is hidden
    }

    public function initializePositions():void{
        this.horizLine1.x = 0.7*this.ruler.width;
        this.horizLine1.y = 0.15*this.pixPerMeter;
        this.horizLine2.x = 0.7*this.ruler.width;
        this.horizLine2.y = 0.29*this.pixPerMeter;
        //HorizontalReferenceLine is a sprite, which contains a sprite called backGround; following lines reposition backGround sprite within 
        this.horizLine1.initializePosition();
        this.horizLine2.initializePosition();
        this.ruler.x = 0.1*this.pixPerMeter;
        this.ruler.y = 0;
    }

    private function drawRuler(){
        var w:Number = 50;
        var h:Number = 0.5*pixPerMeter;
        var g:Graphics = this.ruler.graphics;
        g.lineStyle( 1, 0x000000, 1, true, "normal", CapsStyle.NONE  );
        g.beginFill( 0xffcc32, 0.2 );   //light tan color for wooden ruler
        g.drawRect(0,0,w,h);
        g.endFill();
        var pixPerMM = this.pixPerMeter/1000;
        var pixPerCM = this.pixPerMeter/100;
        var nbrMarks:int = h/pixPerCM;
        //g.lineStyle(0.5, 0x000000, 1);
        for (var i:int = 0; i < nbrMarks; i++) {
            g.lineStyle(0.5, 0x000000, 1);
            g.moveTo(w, i*pixPerCM);
            g.lineTo(w - 8, i*pixPerCM);
            g.moveTo(0, i*pixPerCM);
            g.lineTo(8, i*pixPerCM);
            if(i%5 == 0 && i != 0) {
               g.lineStyle(2, 0x000000, 1, true, "normal", CapsStyle.NONE );
               g.moveTo(w, i*pixPerCM);
               g.lineTo(w - 12, i*pixPerCM);
               g.moveTo(0, i*pixPerCM);
               g.lineTo(12, i*pixPerCM);
            }
            if(i%10 == 0 && i != 0) {
               g.lineStyle(2, 0x000000, 1, true, "normal", CapsStyle.NONE );
               g.moveTo(w, i*pixPerCM);
               g.lineTo(w - 16, i*pixPerCM);
               g.moveTo(0, i*pixPerCM);
               g.lineTo(16, i*pixPerCM);
            }
        }
    }

    //make numbers on the ruler
    private function makeNumbers():void{
        this.tFieldArray = new Array( 9 );
        this.tFormat = new TextFormat();
        for (var i:int = 0; i < this.tFieldArray.length; i++){
            var nbr:String = String( i*5 + 5 );
            this.tFieldArray[i] = new TextField();
            var label_txt:TextField = this.tFieldArray[i];
            label_txt.selectable = false;
            label_txt.autoSize = TextFieldAutoSize.CENTER;
            label_txt.text = "Label";
            this.tFormat = new TextFormat();	//format of label
            this.tFormat.font = "Arial";
            this.tFormat.color = 0x000000;
            this.tFormat.size = 14;
            label_txt.text = nbr;
            label_txt.setTextFormat( this.tFormat );
            this.ruler.addChild(label_txt);
            label_txt.x = 0.5*this.ruler.width - 0.75*label_txt.width;
            label_txt.y = this.ruler.height - 0.05*( 1 + i )*this.pixPerMeter - 0.5*label_txt.height;
        }
    }


    private function makeSpriteGrabbable( mySprite:Sprite ):void{
        var target:Sprite = mySprite;
        var thisObject: Object = this;
        target.buttonMode = true;
        target.mouseChildren = false;
        target.addEventListener( MouseEvent.MOUSE_DOWN, startTargetDrag );
        var clickOffset: Point;

        function startTargetDrag( evt: MouseEvent ): void {
            clickOffset = new Point( evt.localX, evt.localY );
            stage.addEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
            stage.addEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
        }

        function stopTargetDrag( evt: MouseEvent ): void {
            //trace("stop dragging");
            clickOffset = null;
            stage.removeEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
            stage.removeEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
        }

        function dragTarget( evt: MouseEvent ): void {
            target.x = mouseX - clickOffset.x;
            target.y = mouseY - clickOffset.y;
            evt.updateAfterEvent();
        }//end of dragTarget()
    }//end makeSpriteGrabbable();

    public function makeVisible( tOrF: Boolean ):void{
        this.ruler.visible = tOrF;
        this.horizLine1.visible = tOrF;
        this.horizLine2.visible = tOrF;
    }
} //end of class
} //end of package
