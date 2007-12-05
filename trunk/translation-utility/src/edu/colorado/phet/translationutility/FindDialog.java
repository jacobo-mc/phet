/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.EventListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.EventListenerList;

/**
 * FindDialog is a dialog that lets you specify a string to find.
 * When the Next or Previous buttons are pressed, all FindListeners are notified.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FindDialog extends JDialog {
    
    private static final String FIND_LABEL = TUResources.getString( "label.find" );
    private static final String NEXT_LABEL = TUResources.getString( "button.next" );
    private static final String PREVIOUS_LABEL = TUResources.getString( "button.previous" );
    private static final String CLOSE_LABEL = TUResources.getString( "button.close" );
    
    private static final Icon NEXT_ICON = TUResources.getIcon( "nextArrow.png" );
    private static final Icon PREVIOUS_ICON = TUResources.getIcon( "previousArrow.png" );
    
    private JTextField _textField;
    private JButton _nextButton;
    private JButton _previousButton;
    private EventListenerList _listenerList;
    
    /**
     * FindListener is the interface implemented by all listeners who 
     * want to be notified when the Next or Previous buttons are pushed.
     */
    public static interface FindListener extends EventListener {
        public void findNext( String text );
        public void findPrevious( String text );
    }

    /**
     * Constructor.
     * 
     * @param owner
     * @param defaultText
     * @param textFieldFont
     */
    public FindDialog( Frame owner, String defaultText, Font textFieldFont ) {
        super( owner );
        
        setTitle( TUResources.getString( "title.findDialog" ) );
        setModal( false );
        setResizable( false );
        
        _listenerList = new EventListenerList();
        
        // create the panel where the user inputs information
        JPanel inputPanel = new JPanel();
        {
            JLabel findLabel = new JLabel( FIND_LABEL );

            _textField = new JTextField( defaultText );
            _textField.setFont( textFieldFont );
            _textField.setColumns( 30 );
            _textField.setEditable( true );
            _textField.addKeyListener( new KeyAdapter() {
                public void keyReleased( KeyEvent e ) {
                    updateButtons();
                    // pressing enter in the textfield is the same as pressing the Next button
                    if ( e.getKeyCode() == KeyEvent.VK_ENTER ) {
                        fireNext();
                    }
                }
            } );
            
            inputPanel.setLayout( new FlowLayout( FlowLayout.LEFT ) );
            inputPanel.add( findLabel );
            inputPanel.add( _textField );
        }
        
        // create the panel that contains action buttons
        JPanel buttonPanel = new JPanel();
        {
            _nextButton = new JButton( NEXT_LABEL, NEXT_ICON );
            _nextButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    fireNext();
                }
            } );

            _previousButton = new JButton( PREVIOUS_LABEL, PREVIOUS_ICON );
            _previousButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    firePrevious();
                }
            } );
            
            JButton closeButton = new JButton( CLOSE_LABEL );
            closeButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    dispose();
                }
            } );
            
            JPanel innerPanel = new JPanel( new GridLayout( 1, 7 ) );
            innerPanel.add( _nextButton );
            innerPanel.add( _previousButton );
            innerPanel.add( Box.createHorizontalStrut( 20 ) );
            innerPanel.add( closeButton );
            buttonPanel.add( innerPanel );
        }
        
        // layout
        JPanel bottomPanel = new JPanel( new BorderLayout() );
        bottomPanel.add( new JSeparator(), BorderLayout.NORTH );
        bottomPanel.add( buttonPanel, BorderLayout.CENTER );
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout( new BorderLayout() );
        mainPanel.setBorder( new EmptyBorder( 10, 10, 0, 10 ) );
        mainPanel.add( inputPanel, BorderLayout.CENTER );
        mainPanel.add( bottomPanel, BorderLayout.SOUTH );
        setContentPane( mainPanel );
        pack();
        
        // set the initial state of the buttons
        updateButtons();
    }
    
    /**
     * Gets the text that has been entered in the text field.
     * @return String
     */
    public String getText() {
        return _textField.getText();
    }
    
    /*
     * Updates the state of the Next and Previous buttons.
     * These buttons are disabled if the text field is empty.
     */
    private void updateButtons() {
        String s = _textField.getText();
        boolean enabled = ( s != null && s.length() != 0 );
        _nextButton.setEnabled( enabled );
        _previousButton.setEnabled( enabled );
    }
    
    /**
     * Adds a FindListener.
     * @param listener
     */
    public void addFindListener( FindListener listener ) {
        _listenerList.add( FindListener.class, listener );
    }
    
    /**
     * Removes a FindListener.
     * @param listener
     */
    public void removeFindListener( FindListener listener ) {
        _listenerList.remove( FindListener.class, listener );
    }
    
    /*
     * Notifies all FindListeners that the Next button has been pressed.
     */
    private void fireNext() {
        String text = getText();
        if ( text != null && text.length() > 0 ) {
            Object[] listeners = _listenerList.getListenerList();
            for ( int i = 0; i < listeners.length; i += 2 ) {
                if ( listeners[i] == FindListener.class ) {
                    ( (FindListener) listeners[i + 1] ).findNext( text );
                }
            }
        }
    }
    
    /*
     * Notifies all FindListeners that the Previous button has been pressed.
     */
    private void firePrevious() {
        String text = getText();
        if ( text != null && text.length() > 0 ) {
            Object[] listeners = _listenerList.getListenerList();
            for ( int i = 0; i < listeners.length; i += 2 ) {
                if ( listeners[i] == FindListener.class ) {
                    ( (FindListener) listeners[i + 1] ).findPrevious( text );
                }
            }
        }
    }
}
