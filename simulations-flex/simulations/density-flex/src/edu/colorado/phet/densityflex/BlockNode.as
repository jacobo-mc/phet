package edu.colorado.phet.densityflex {

import Box2D.Dynamics.b2Body;

import away3d.materials.*;
import away3d.primitives.*;

import flash.display.Bitmap;
import flash.display.BitmapData;
import flash.display.BitmapDataChannel;
import flash.display.Sprite;
import flash.geom.ColorTransform;
import flash.geom.Point;
import flash.geom.Rectangle;
import flash.text.TextField;
import flash.text.TextFormat;

import mx.core.BitmapAsset;

public class BlockNode extends Cube implements Pickable, Listener{

    private var frontSprite : Sprite;
    private var block:Block;

    [Embed(source="../../../../../data/density-flex/images/wall.jpg")]
    private var wallClass : Class;

    public function BlockNode( block:Block ) : void {
        this.block = block;
        this.width = block.getWidth() * DensityModel.DISPLAY_SCALE;
        this.height = block.getHeight() * DensityModel.DISPLAY_SCALE;
        this.depth = block.getDepth() * DensityModel.DISPLAY_SCALE;
        this.segmentsH = 2;
        this.segmentsW = 2;
        this.x = block.getX() * DensityModel.DISPLAY_SCALE;
        this.y = block.getY() * DensityModel.DISPLAY_SCALE;
        this.z = block.getZ() * DensityModel.DISPLAY_SCALE;
        this.useHandCursor = true;
        block.addListener(this);

        frontSprite = new Sprite();

        var wallData : BitmapData = (new wallClass() as BitmapAsset).bitmapData;
        var imageRect : Rectangle = new Rectangle(0, 0, wallData.width, wallData.height);
        wallData.colorTransform(imageRect, new ColorTransform(1.0, 0.5, 0.5));
        var coloredData : BitmapData = (new wallClass() as BitmapAsset).bitmapData;
        if ( block.getColor().redMultiplier < 0.5 ) {
            coloredData.copyChannel(wallData, imageRect, new Point(0, 0), BitmapDataChannel.GREEN, BitmapDataChannel.RED);
        }
        else {
            coloredData.copyChannel(wallData, imageRect, new Point(0, 0), BitmapDataChannel.RED, BitmapDataChannel.RED);
        }
        if ( block.getColor().greenMultiplier < 0.5 ) {
            coloredData.copyChannel(wallData, imageRect, new Point(0, 0), BitmapDataChannel.GREEN, BitmapDataChannel.GREEN);
        }
        else {
            coloredData.copyChannel(wallData, imageRect, new Point(0, 0), BitmapDataChannel.RED, BitmapDataChannel.GREEN);
        }
        if ( block.getColor().blueMultiplier < 0.5 ) {
            coloredData.copyChannel(wallData, imageRect, new Point(0, 0), BitmapDataChannel.GREEN, BitmapDataChannel.BLUE);
        }
        else {
            coloredData.copyChannel(wallData, imageRect, new Point(0, 0), BitmapDataChannel.RED, BitmapDataChannel.BLUE);
        }

        frontSprite.addChild(new Bitmap(coloredData));

        var tf : TextField = new TextField();
        tf.text = String(block.getMass()) + " kg";
        tf.height = wallData.height;
        tf.width = wallData.width;
        var format : TextFormat = new TextFormat();
        format.size = int(45 * (200 / this.width));
        format.bold = true;
        format.font = "Arial";
        tf.multiline = true;
        tf.setTextFormat(format);
        frontSprite.addChild(tf);


        var frontMaterial : MovieMaterial = new MovieMaterial(frontSprite);
        frontMaterial.smooth = true; //makes the font smooth instead of jagged, see http://www.mail-archive.com/away3d-dev@googlegroups.com/msg06699.html
        var redWallMaterial : BitmapMaterial = new BitmapMaterial(coloredData);

        this.cubeMaterials.left = this.cubeMaterials.right = this.cubeMaterials.top = this.cubeMaterials.bottom = this.cubeMaterials.front = redWallMaterial;

        this.cubeMaterials.back = frontMaterial;
    }

    public function setPosition( x:Number, y:Number ): void {
        block.setPosition(x / DensityModel.DISPLAY_SCALE, y / DensityModel.DISPLAY_SCALE);
    }

    public function update():void {
        this.x = block.getX() * DensityModel.DISPLAY_SCALE;
        this.y = block.getY() * DensityModel.DISPLAY_SCALE;
    }

    public function getBlock():Block {
        return block;
    }

    public function getBody():b2Body {
        return block.getBody();
    }
}
}