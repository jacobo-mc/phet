/**
 * Created by ${PRODUCT_NAME}.
 * User: Sam
 * Date: 12/6/10
 * Time: 2:40 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.view.units.Unit;
import edu.colorado.phet.flexcommon.FlexSimStrings;

import flash.display.Sprite;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFormat;

public class DensitySliderDataTip extends Sprite {
    private var textField: TextField;
    private var waterHeight: Number;

    public function DensitySliderDataTip() {
        super();
        textField = new TextField();
        textField.autoSize = TextFieldAutoSize.RIGHT;
        textField.text = "";
        addChild( textField );

        textField.selectable = false;
        update();
    }

    protected function update(): void {
        graphics.clear();
        var indicatedVolume: Number = waterHeight;

        var readout: Number = DensityConstants.metersToLitersCubed( indicatedVolume );//Convert SI to sim units

        var textFormat: TextFormat = new TextFormat();
        textFormat.size = 24;
        textFormat.bold = true;
        textField.setTextFormat( textFormat );

        textField.x = + 10;
        textField.y = -textField.height / 2;

        graphics.lineStyle( 1, 0x000000 );
        graphics.beginFill( 0xFFFFFF );
        graphics.drawRoundRect( textField.x, textField.y, textField.width, textField.height, 6, 6 );
        graphics.endFill();
    }

    public function setDensity( density: Number, units: Unit ): void {
        textField.text = FlexSimStrings.get( "properties.densityValue", "{0} kg/L", [String( DensityConstants.format( units.fromSI( density ) ) )] );
        update();
    }
}
}