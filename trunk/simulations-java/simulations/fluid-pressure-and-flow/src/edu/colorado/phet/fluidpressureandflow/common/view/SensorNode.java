// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.fluidpressureandflow.common.FPAFStrings;
import edu.colorado.phet.fluidpressureandflow.common.model.Sensor;
import edu.colorado.phet.fluidpressureandflow.common.model.units.Unit;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public abstract class SensorNode<T> extends PNode {

    protected final Property<String> text;

    public SensorNode( final ModelViewTransform transform, final Sensor<T> sensor, final Property<Unit> units ) {
        final Function0<String> getString = new Function0<String>() {
            public String apply() {
                String pattern = FPAFStrings.SENSOR_PATTERN; //TODO i18n
                String value = FPAFStrings.QUESTION_MARK; //TODO i18n
                if ( !Double.isNaN( sensor.getScalarValue() ) ) {
                    value = units.get().getDecimalFormat().format( units.get().siToUnit( sensor.getScalarValue() ) );
                }
                return MessageFormat.format( pattern, value, units.get().getAbbreviation() );
            }
        };
        text = new Property<String>( getString.apply() );
        final SimpleObserver observer = new SimpleObserver() {
            public void update() {
                text.set( getString.apply() );
            }
        };
        sensor.addValueObserver( observer );
        units.addObserver( observer );

        addInputEventListener( new CursorHandler() );

        sensor.location.addObserver( new SimpleObserver() {
            public void update() {
                setOffset( transform.modelToView( sensor.getLocation().toPoint2D() ) );
            }
        } );
    }
}
