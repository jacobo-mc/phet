package edu.colorado.phet.common.phetcommon.model;

import edu.colorado.phet.common.phetcommon.util.SimpleObservable;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * This can be used to represent a value in a MVC style pattern.  It remembers its default value and can be reset.
 * The wrapped type T should be immutable, or at least protected from external modification.
 * Notifications are sent to observers when they register with addObserver, and when the value changes.
 *
 * @author Sam Reid
 * @author Chris Malley
 */
public class Property<T> extends SimpleObservable {
    private T value;
    private final T defaultValue;

    public Property( T value ) {
        this.defaultValue = value;
        this.value = value;
    }

    /**
     * Adds a SimpleObserver to observe the value of this instance, also immediately updates the SimpleObserver,
     * so that the client code is not responsible for doing so.
     * This helps the SimpleObserver to always be synchronized with this instance.
     *
     * @param simpleObserver
     */
    @Override
    public void addObserver( SimpleObserver simpleObserver ) {
        super.addObserver( simpleObserver );
        simpleObserver.update();
    }

    public void reset() {
        setValue( defaultValue );
    }

    public T getValue() {
        return value;
    }

    public void setValue( T value ) {
        if ( !this.value.equals(value) ) {
            this.value = value;
            notifyObservers();
        }
    }
}
