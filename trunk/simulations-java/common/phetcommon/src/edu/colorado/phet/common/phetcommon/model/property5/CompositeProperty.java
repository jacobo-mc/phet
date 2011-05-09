// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property5;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;

//REVIEW where is oldValue set?
//oldValue gets set in the constructor's call to super

/**
 * The CompositeProperty should be used for ObservableProperties that are combined, such as And, Or, ValueEquals, etc.
 * It makes sure the dependencies are observed for changes and only sends out notifications when the composite value has changed.
 *
 * @param <T>
 */
public class CompositeProperty<T> extends ObservableProperty<T> {
    //Function for computing the new value, usually provided as a closure in the implementing class
    private Function0<T> function;
    private SimpleObserver observer;
    private final ObservableProperty<?>[] properties;

    public CompositeProperty( Function0<T> function, ObservableProperty<?>... properties ) {
        super( function.apply() );
        this.function = function;
        this.properties = properties;

        //Observe all dependencies for changes, and if one of their changes causes this value to change, send out a notification
        observer = new SimpleObserver() {
            public void update() {
                notifyIfChanged();
            }
        };
        for ( ObservableProperty<?> property : properties ) {
            property.addObserver( observer );
        }
    }

    //Remove listeners that were attached to the dependencies to prevent potential memory leaks
    public void cleanup() {
        for ( ObservableProperty<?> property : properties ) {
            property.removeObserver( observer );
        }
    }

    //Get the composite value which is a function of the dependencies
    @Override public T getValue() {
        return function.apply();
    }
}
