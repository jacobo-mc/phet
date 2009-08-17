/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;

/**
 * Collection of localized strings used by this simulations.
 * We load all strings statically so that we will be warned at startup time of any missing strings.
 * Otherwise we'd have to visit every part of the sim to test properly.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ABSStrings {
    
    /* not intended for instantiation */
    private ABSStrings() {}
    
    public static final String TITLE_SOLUTIONS_MODULE = ABSResources.getString( "title.solutionsModule" );
    public static final String TITLE_COMPARING_MODULE = ABSResources.getString( "title.comparingModule" );
    public static final String TITLE_MATCHING_GAME_MODULE = ABSResources.getString( "title.matchingGameModule" );
    public static final String TITLE_ERROR = PhetCommonResources.getString( "Common.title.error" );
    public static final String TITLE_EQUILIBRIUM_EXPRESSION = ABSResources.getString( "title.equilibriumExpression" );
    public static final String TITLE_SYMBOL_LEGEND = ABSResources.getString( "title.symbolLegend" );
    public static final String TITLE_VIEW = ABSResources.getString( "title.view" );
    
    public static final String MESSAGE_NOT_A_CONFIG = ABSResources.getString( "message.notAConfigFile" );
    
    // labels
    public static final String LABEL_CONCENTRATION = ABSResources.getString( "label.concentration" );
    public static final String LABEL_PH = ABSResources.getString( "label.pH" );
    public static final String LABEL_EQUATION_SCALING = ABSResources.getString( "label.equationScaling" );
    public static final String LABEL_STRENGTH = ABSResources.getString( "label.strength" );
    public static final String LABEL_WEAK = ABSResources.getString( "label.weak" );
    public static final String LABEL_WEAKER = ABSResources.getString( "label.weaker" );
    public static final String LABEL_STRONG = ABSResources.getString( "label.strong" );
    public static final String LABEL_STRONGER = ABSResources.getString( "label.stronger" );
    public static final String LABEL_CONCENTRATION_GRAPH_Y_AXIS = ABSResources.getString("label.concentrationGraph.yAxis" );
    public static final String LABEL_SOLUTE = ABSResources.getString( "label.solute" );
    public static final String LABEL_POINTS = ABSResources.getString( "label.points" );
    public static final String LABEL_SOLUTIONS = ABSResources.getString( "label.solutions" );
    
    // legend text
    public static final String LEGEND_HA = ABSResources.getString( "legend.HA" );
    public static final String LEGEND_B = ABSResources.getString( "legend.B" );
    public static final String LEGEND_MOH = ABSResources.getString( "legend.MOH" );
    
    // game
    public static final String GAME_ACIDBASE_QUESTION = ABSResources.getString( "game.acidbase.question" );
    public static final String GAME_ACIDBASE_CORRECT = ABSResources.getString( "game.acidbase.correct" );
    public static final String GAME_ACIDBASE_WRONG = ABSResources.getString( "game.acidbase.wrong" );
    public static final String GAME_MATCH_QUESTION = ABSResources.getString( "game.match.question" );
    public static final String GAME_MATCH_CORRECT = ABSResources.getString( "game.match.correct" );
    public static final String GAME_MATCH_WRONG = ABSResources.getString( "game.match.wrong" );
    public static final String GAME_CONTINUE = ABSResources.getString( "game.continue" );
    
    // check boxes
    public static final String CHECK_BOX_MOLECULE_COUNTS = ABSResources.getString( "checkBox.moleculeCounts" );
    public static final String CHECK_BOX_BEAKER_LABEL = ABSResources.getString( "checkBox.beakerLabel" );
    public static final String CHECK_BOX_SOLUTE_RATIO_SPECIFIC = ABSResources.getString( "checkBox.soluteRatio-reactant_product" );
    public static final String CHECK_BOX_SOLUTE_RATIO_GENERAL = ABSResources.getString( "checkBox.soluteRatio" );
    public static final String CHECK_BOX_H3O_OH_RATIO = ABSResources.getString( "checkBox.ratio-H3O_OH" );
    public static final String CHECK_BOX_SYMBOL_LEGEND = ABSResources.getString( "checkBox.symbolLegend" );
    
    // buttons
    public static final String BUTTON_NEW_SOLUTION = ABSResources.getString( "button.newSolution" );
    public static final String BUTTON_CHECK_MATCH = ABSResources.getString( "button.checkMatch" );
    public static final String BUTTON_ACID = ABSResources.getString( "button.acid" );
    public static final String BUTTON_BASE = ABSResources.getString( "button.base" );
    public static final String BUTTON_GRAPH = ABSResources.getString( "button.graph" );
    public static final String BUTTON_EQUILIBRIUM_EXPRESSIONS = ABSResources.getString( "button.equilibriumExpressions" );
    public static final String BUTTON_REACTION_EQUATIONS = ABSResources.getString( "button.reactionEquations" );
    
    // radio buttons
    public static final String RADIO_BUTTON_EQUATION_SCALING_OFF = ABSResources.getString( "radioButton.equationScaling.off" );
    public static final String RADIO_BUTTON_EQUATION_SCALING_ON = ABSResources.getString( "radioButton.equationScaling.on" );
    public static final String RADIO_BUTTON_BEAKERS = ABSResources.getString( "radioButton.beakers" );
    public static final String RADIO_BUTTON_GRAPHS = ABSResources.getString( "radioButton.graphs" );
    public static final String RADIO_BUTTON_EQUATIONS = ABSResources.getString( "radioButton.equations" );
    
    // units
    public static final String UNITS_LITERS = ABSResources.getString( "units.liters" );
    public static final String UNITS_MOLES_PER_LITER = ABSResources.getString( "units.molesPerLiter" );
    public static final String UNITS_MOLAR = ABSResources.getString( "units.molar" );
    
    // water
    public static final String WATER = ABSResources.getString( "water" );
    public static final String PURE_WATER = ABSResources.getString( "pureWater" );
    public static final String NO_SOLUTE = ABSResources.getString( "noSolute" );
    
    // acids
    public static final String CUSTOM_ACID = ABSResources.getString( "acid.customAcid" );
    public static final String HYDROCHLORIC_ACID = ABSResources.getString( "acid.hydrochloricAcid" );
    public static final String PERCHLORIC_ACID = ABSResources.getString( "acid.perchloridAcid" );
    public static final String CHLOROUS_ACID = ABSResources.getString( "acid.chlorousAcid" );
    public static final String HYPOCHLOROUS_ACID = ABSResources.getString( "acid.hypochlorousAcid" );
    public static final String HYDROFLUORIC_ACID = ABSResources.getString( "acid.hydrofluoricAcid" );
    public static final String ACETIC_ACID = ABSResources.getString( "acid.aceticAcid" );
    
    // bases
    public static final String CUSTOM_BASE = ABSResources.getString( "base.customBase" );
    public static final String SODIUM_HYDROXIDE = ABSResources.getString( "base.sodiumHydroxide" );
    public static final String AMMONIA = ABSResources.getString( "base.ammonia" );
    public static final String PYRIDINE = ABSResources.getString( "base.pyridine" );
    
    // values
    public static final String VALUE_LARGE = ABSResources.getString( "value.large" );
    public static final String VALUE_NEGLIGIBLE = ABSResources.getString( "value.negligible" );
}
