// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionsintro;

import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelComponentType;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ISystemAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ISystemComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.fractions.common.util.RichVoidFunction1;

import static edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager.sendUserMessage;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet.parameterSet;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions.pressed;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes.button;
import static edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.ParameterKeys.autoSpin;

/**
 * Sim sharing components for the Fractions intro sim.
 *
 * @author Sam Reid
 */
public class FractionsIntroSimSharing {
    public enum Components implements IUserComponent {
        sameRepresentationRadioButton,
        numberLineRepresentationRadioButton,
        numbersRadioButton,
        picturesRadioButton,
        matchingGameTab, equalityLabTab, introTab,
        maxSpinnerUpButton,
        maxSpinnerDownButton,
        numeratorSpinnerUpButton,
        numeratorSpinnerDownButton,
        denominatorSpinnerUpButton,
        denominatorSpinnerDownButton,

        sliceComponent,
        pieRadioButton,
        horizontalBarRadioButton,
        verticalBarRadioButton,
        waterGlassesRadioButton,
        numberLineRadioButton,
        cakeRadioButton,
        scaledUpFractionSpinnerRightButton,
        scaledUpFractionSpinnerLeftButton,
        lock,
        numberLineArrow, numberLineKnob,
        okButton,
        matchingGameFraction,

        tryAgainButton, showAnswerButton, checkAnswerButton,
        buildAFractionTab, menuButton,

        levelButton,
        soundButton, stopwatchButton,
        backButton, forwardButton,
        mixedNumbersTab,

        soundRadioButton,
        timerRadioButton,
        resetButton,
        refreshButton,
        increaseContainersButton,
        decreaseContainersButton,
        incrementDivisionsButton,
        decrementDivisionsButton,
        playAreaUndoButton,
        piece,
        container,
        nextButton, collectionBoxUndoButton,
        fraction, numberCard, fractionCard, carouselRadioButton, carouselRadioButtonLabel, levelSelectionScreenButton, fractionLabTab,
        pieShapeRadioButton,
        barShapeRadioButton

    }

    public static final String on = "on";
    public static final String off = "off";
    //For chaining with component types
    public static final String rightSide = "rightSide";
    public static final String leftSide = "leftSide";

    public enum ModelComponents implements IModelComponent {
        containerSetComponent, leftScaleValue,
        rightScaleValue, answer, game, event,
        time
    }

    public enum ModelComponentTypes implements IModelComponentType {
        containerSetComponentType,
        scale, answer, game, property, event
    }

    public enum ModelActions implements IModelAction {
        changed, checked, finished, occurred, buildAFractionLevelStarted, shapeContainerDropped, matchingGameLevelStarted, matchingGameLevelRefreshed, matchingGameLevelResumed
    }

    public enum ParameterKeys implements IParameterKey {
        max,
        numerator,
        denominator,
        containerSetKey,
        scale,
        autoSpin,
        soundEnabled, timerEnabled, isCorrect, points, shapeType, targets, correct, leftScaleNumerator, leftScaleDenominator, rightScaleNumerator, rightScaleDenominator,
        leftScaleRepresentation, rightScaleRepresentation,
        hit, source, levelID, target, targetIndex, fractions, constituents, divisions
    }

    public enum SystemComponents implements ISystemComponent {
        buildAFraction
    }

    public enum SystemActions implements ISystemAction {
        allChallengesComplete,
        oneChallengeComplete
    }

    private static RichVoidFunction1<Boolean> sendMessage( final IUserComponent component, final IUserComponentType type, final IUserAction action, final Function1<Boolean, ParameterSet> parameters ) {
        return new RichVoidFunction1<Boolean>() {
            public void apply( final Boolean autoSpin ) {
                sendUserMessage( component, type, action, parameters.apply( autoSpin ) );
            }
        };
    }

    public static VoidFunction1 sendMessageAndApply( Components component, ParameterKeys key, final IntegerProperty value, int delta ) {
        return sendMessage( component, button, pressed, newValue( key, value, delta ) ).andThen( value.add_( delta ) );
    }

    private static Function1<Boolean, ParameterSet> newValue( final ParameterKeys key, final IntegerProperty value, final int delta ) {
        return new Function1<Boolean, ParameterSet>() {
            public ParameterSet apply( Boolean doAutoSpin ) {
                return parameterSet( key, value.get() + delta ).with( autoSpin, doAutoSpin );
            }
        };
    }
}