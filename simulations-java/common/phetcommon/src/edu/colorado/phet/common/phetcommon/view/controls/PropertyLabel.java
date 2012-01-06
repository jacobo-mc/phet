// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.view.controls;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JLabel;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingConstants;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;

/**
 * Pressing this label sets a property value.
 * This is useful for icons that are associated with Swing controls.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PropertyLabel<T> extends JLabel {

    public PropertyLabel( final SimSharingConstants.User.UserComponent simSharingObject, Icon icon, final Property<T> property, final T value ) {
        super( icon );
        addMouseListener( new MouseAdapter() {
            @Override public void mousePressed( MouseEvent e ) {
                SimSharingManager.sendUserEvent( simSharingObject, SimSharingConstants.User.UserActions.pressed );
                property.set( value );
            }
        } );
    }
}
