﻿// PrivacyDialog.as
//
// Handles creating and displaying the privacy dialog
//
// Author: Jonathan Olson

import org.aswing.*;
import org.aswing.util.*;
import org.aswing.border.*;

class PrivacyDialog {
	
	public var backgroundMC : MovieClip;
	
	// shorthand for debugging function
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	public function PrivacyDialog() {
		debug("PrivacyDialog initializing\n");
		
		// mysterious fix since "this" does not refer to a MovieClip or Component
		ASWingUtils.getRootMovieClip();
		
		// create the background
		backgroundMC = _root.createEmptyMovieClip("backgroundMC", _root.getNextHighestDepth());
		backgroundMC.beginFill(_level0.bgColor);
		// larger dimensions in case people resize afterwards
		backgroundMC.moveTo(-5000, -5000);
		backgroundMC.lineTo(5000, -5000);
		backgroundMC.lineTo(5000, 5000);
		backgroundMC.lineTo(-5000, 5000);
		backgroundMC.endFill();
		
		// create a window
		var window : JFrame = new JFrame(_level0, "Software & Privacy Agreements");
		
		// we don't want this window closable
		window.setClosable(false);
		
		// make sure we can access it from anywhere
		_level0.privacyWindow = window;
		
		// set the background to default
		window.setBackground(_level0.common.backgroundColor);
		
		// layout things vertically
		window.getContentPane().setLayout(new SoftBoxLayout(SoftBoxLayout.Y_AXIS));
		
		// construct the string of text to show
		var str : String = "";
		str += "In all PhET simulations, we collect a minimal amount of information ";
		str += "when the simulation starts. You can disable the sending of this ";
		str += "information at any time via the Preferences button.\n\n";
		str += "By clicking \"Agree and Continue\", you agree to PhET's licensing ";
		str += "and privacy policies. (For details, click here).";
		/*
		str += "<b>Physics Education Technology project</b>\n";
		str += _level0.comStrings.get("Copyright") + " \u00A9 2004-2008 University of Colorado\n";
		str += _level0.comStrings.get("SomeRightsReserved") + ".\n";
		str += _level0.comStrings.get("Visit") + " <a href='http://phet.colorado.edu'>http://phet.colorado.edu</a>\n\n";
		
		str += "<b><font size='16'>" + _level0.simName + "</font></b>\n";
		str += _level0.comStrings.get("Version") + ": " + _level0.versionMajor + "." + _level0.versionMinor;
		if(_level0.dev != "00") {
			str += "." + _level0.dev;
		}
		str += " (" + _level0.revision + ")\n";
		str += _level0.comStrings.get("FlashVersion") + ": " + System.capabilities.version + "\n";
		str += _level0.comStrings.get("OSVersion") + ": " + System.capabilities.os + "\n";
		*/
		
		
		// create CSS to make links blue
		var css : TextField.StyleSheet = new TextField.StyleSheet();
		css.parseCSS("a:link{color:#0000FF;font-weight:bold;}" +
			"a:visited{color:#0000FF;font-weight:bold;}" +
			"a:hover{color:#0000FF;text-decoration:underline;font-weight:bold;}" +
			"a:active{color:#0000FF;font-weight:bold;}"); 
		
		var textArea = new JTextArea(str, 0, 30);
		textArea.setHtml(true);
		textArea.setEditable(false);
		textArea.setCSS(css);
		textArea.setWordWrap(true);
		textArea.setWidth(300);
		textArea.setBorder(new EmptyBorder(null, new Insets(5, 5, 5, 5)));
		textArea.setBackground(_level0.common.backgroundColor);
		
		window.getContentPane().append(textArea);
		
		window.getContentPane().append(new JSpacer(5, 5));
		
		// panel to lay the buttons in
		var panel : JPanel = new JPanel(new BoxLayout());
		
		// button that will allow us to continue
		var continueButton : JButton = new JButton("Accept and Continue");
		continueButton.addEventListener(JButton.ON_PRESS, Delegate.create(this, continueClicked));
		CommonButtons.padButtonAdd(continueButton, panel);
		
		// button will cancel acceptance, and do... something
		var cancelButton : JButton = new JButton("Cancel");
		cancelButton.addEventListener(JButton.ON_PRESS, Delegate.create(this, cancelClicked));
		CommonButtons.padButtonAdd(cancelButton, panel);
		
		window.getContentPane().append(panel);
		
		// fit the window to its contents
		window.setHeight(window.getContentPane().getPreferredSize().height + 50);
		window.setWidth(window.getContentPane().getPreferredSize().width + 50);
		
		// center the window
		window.setLocation((Stage.width - window.getWidth()) / 2, (Stage.height - window.getHeight()) / 2);
		window.show();
	}
	
	public function continueClicked(src : JButton) {
		// set policy as accepted
		_level0.preferences.agreeToPrivacy();
		
		// hide this window
		_level0.privacyWindow.setVisible(false);
		
		backgroundMC.removeMovieClip();
		
		// continue with common code initialization
		_level0.common.postAgreement();
	}
	
	public function cancelClicked(src : JButton) {
		// hide this window
		_level0.privacyWindow.setVisible(false);
	}
}

