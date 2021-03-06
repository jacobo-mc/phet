
/**
 * Created by IntelliJ IDEA.
 * User: Dubson
 * Date: 6/1/11
 * Time: 3:06 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.triglab.view {
import edu.colorado.phet.flashcommon.controls.Tab;
import edu.colorado.phet.flashcommon.controls.TabBar;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.triglab.*;
import edu.colorado.phet.triglab.TrigLabApplication;
import edu.colorado.phet.triglab.control.ControlPanel;
import edu.colorado.phet.triglab.model.TrigModel;

import flash.display.StageQuality;

import org.aswing.event.ModelEvent;


import edu.colorado.phet.flashcommon.view.PhetIcon;
import edu.colorado.phet.flexcommon.util.SpriteUIComponent;

import flash.display.Sprite;

import mx.containers.Canvas;
import mx.controls.sliderClasses.Slider;

//main view and communications hub for Trig Lab sim

public class MainView extends Canvas {

    private var tabBar: TabBar;       //tabBar at top of screen. 2 Tabs: Intro and Game
    private var intro_str: String;    //labels for tabs
    private var game_str: String;
    public var introMode: Boolean;       //true if on intro tab, false if on game tab

    public var myTrigModel:TrigModel;
    public var myUnitCircleView:UnitCircleView;
    public var myReadoutView: ReadoutView;
    public var myGraphView:GraphView;
    public var myControlPanel:ControlPanel;
    public var topCanvas:TrigLabCanvas;

    public var phetLogo: PhetIcon;
    public var stageH: Number;
    public var stageW: Number;



    public function MainView( topCanvas:TrigLabCanvas, stageW: Number, stageH: Number ) {
        //this.topCanvas = topCanvas;   //this line is unnecessary (isn't it?)

        percentWidth = 100;
        percentHeight = 100;
        this.stageH = stageH;
        this.stageW = stageW;
        this.myTrigModel = new TrigModel(this);
        this.myUnitCircleView = new UnitCircleView( this, myTrigModel ) ;
        this.myReadoutView = new ReadoutView( this, myTrigModel );
        this.myGraphView = new GraphView(this, myTrigModel );
        this.myControlPanel = new ControlPanel( this, this.myTrigModel );

        this.addChild( new SpriteUIComponent( this.myUnitCircleView ));
        this.myUnitCircleView.x = 0.3*stageW;
        this.myUnitCircleView.y = 0.27*stageW;

        this.addChild( myReadoutView );
        this.myReadoutView.x = 0.52*stageW;
        this.myReadoutView.y = 0.05*stageH;

        this.addChild( new SpriteUIComponent( this.myGraphView ));
        this.myGraphView.x = 0.6*stageW;
        this.myGraphView.y = 0.8*stageH;

        this.addChild( myControlPanel );
        this.myControlPanel.x = 0.85*stageW;
        this.myControlPanel.y = 0.05*stageH;

        this.phetLogo = new PhetIcon();
        this.phetLogo.setColor( 0x0000ff );
        this.phetLogo.x = stageW - 2.0 * this.phetLogo.width;
        this.phetLogo.y = stageH - 1.5 * this.phetLogo.height;
        this.addChild( new SpriteUIComponent( phetLogo ) );
        this.initializeAll();
    }//end of constructor

    private function initializeStrings(): void {
        intro_str = FlexSimStrings.get( "intro", "Intro   " );
        game_str = FlexSimStrings.get( "game", "Game   " );
    }

    public function setTabView( tabView:int ):void{
        if ( tabView == 1 ){

        }else if ( tabView == 2 ){

        }
    }

    public function initializeAll(): void {
        myTrigModel.smallAngle = 0;

    }//end of initializeAll()



}//end of class
} //end of package
