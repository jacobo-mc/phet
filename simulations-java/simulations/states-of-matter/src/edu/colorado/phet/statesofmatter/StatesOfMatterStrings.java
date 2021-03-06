// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.statesofmatter;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;


/**
 * StatesOfMatterStrings is the collection of localized strings used by this simulations.
 * We load all strings as statics so that we will be warned at startup time of any missing strings.
 *
 * @author John Blanco
 */
public class StatesOfMatterStrings {
    /* Not intended for instantiation. */
    private StatesOfMatterStrings() {
    }

    public static final String STOVE_CONTROL_PANEL_TITLE = StatesOfMatterResources.getString( "Stove.Title" );
    public static final String STOVE_CONTROL_PANEL_HEAT_LABEL = StatesOfMatterResources.getString( "Stove.Heat" );
    public static final String STOVE_CONTROL_PANEL_COOL_LABEL = StatesOfMatterResources.getString( "Stove.Cool" );

    public static final String PRESSURE_GAUGE_TITLE = StatesOfMatterResources.getString( "PressureGauge.Title" );
    public static final String PRESSURE_GAUGE_UNITS = StatesOfMatterResources.getString( "PressureGauge.Units" );
    public static final String PRESSURE_GAUGE_OVERLOAD = StatesOfMatterResources.getString( "PressureGauge.Overload" );

    public static final String TITLE_SOLID_LIQUID_GAS_MODULE = StatesOfMatterResources.getString( "ModuleTitle.SolidLiquidGasModule" );
    public static final String TITLE_PHASE_CHANGES_MODULE = StatesOfMatterResources.getString( "ModuleTitle.PhaseChangesModule" );
    public static final String TITLE_INTERACTION_POTENTIAL_MODULE = StatesOfMatterResources.getString( "ModuleTitle.InteractionPotentialModule" );

    public static final String MOLECULE_TYPE_SELECT_LABEL = StatesOfMatterResources.getString( "SolidLiquidGasControl.MoleculeSelection" );
    public static final String OXYGEN_SELECTION_LABEL = StatesOfMatterResources.getString( "SolidLiquidGasControl.Oxygen" );
    public static final String NEON_SELECTION_LABEL = StatesOfMatterResources.getString( "SolidLiquidGasControl.Neon" );
    public static final String ARGON_SELECTION_LABEL = StatesOfMatterResources.getString( "SolidLiquidGasControl.Argon" );
    public static final String WATER_SELECTION_LABEL = StatesOfMatterResources.getString( "SolidLiquidGasControl.Water" );
    public static final String ADJUSTABLE_ATTRACTION_SELECTION_LABEL = StatesOfMatterResources.getString( "SolidLiquidGasControl.AdjustableAttraction" );

    public static final String FORCE_STATE_CHANGE = StatesOfMatterResources.getString( "SolidLiquidGasControl.StateSelection" );
    public static final String PHASE_STATE_SOLID = StatesOfMatterResources.getString( "SolidLiquidGasControl.Solid" );
    public static final String PHASE_STATE_LIQUID = StatesOfMatterResources.getString( "SolidLiquidGasControl.Liquid" );
    public static final String PHASE_STATE_GAS = StatesOfMatterResources.getString( "SolidLiquidGasControl.Gas" );

    public static final String PHASE_DIAGRAM_X_AXIS_LABEL = StatesOfMatterResources.getString( "PhaseChanges.PhaseDiagram.xAxisLabel" );
    public static final String PHASE_DIAGRAM_Y_AXIS_LABEL = StatesOfMatterResources.getString( "PhaseChanges.PhaseDiagram.yAxisLabel" );
    public static final String PHASE_DIAGRAM_SOLID = StatesOfMatterResources.getString( "PhaseChanges.PhaseDiagram.Solid" );
    public static final String PHASE_DIAGRAM_LIQUID = StatesOfMatterResources.getString( "PhaseChanges.PhaseDiagram.Liquid" );
    public static final String PHASE_DIAGRAM_GAS = StatesOfMatterResources.getString( "PhaseChanges.PhaseDiagram.Gas" );
    public static final String PHASE_DIAGRAM_TRIPLE_POINT = StatesOfMatterResources.getString( "PhaseChanges.PhaseDiagram.TriplePoint" );
    public static final String PHASE_DIAGRAM_CRITICAL_POINT = StatesOfMatterResources.getString( "PhaseChanges.PhaseDiagram.CriticalPoint" );
    public static final String PHASE_DIAGRAM_BUTTON_LABEL = StatesOfMatterResources.getString( "PhaseChanges.PhaseDiagram.ButtonLabel" );

    public static final String INTERACTION_POTENTIAL_ATOM_SELECT_LABEL = StatesOfMatterResources.getString( "InteractionPotential.Atoms" );
    public static final String FIXED_ATOM_LABEL = StatesOfMatterResources.getString( "InteractionPotential.FixedAtom" );
    public static final String MOVABLE_ATOM_LABEL = StatesOfMatterResources.getString( "InteractionPotential.MovableAtom" );
    public static final String INTERACTION_POTENTIAL_SHOW_FORCES = StatesOfMatterResources.getString( "InteractionPotential.ShowForces" );
    public static final String INTERACTION_POTENTIAL_HIDE_FORCES = StatesOfMatterResources.getString( "InteractionPotential.HideForces" );
    public static final String INTERACTION_POTENTIAL_TOTAL_FORCES = StatesOfMatterResources.getString( "InteractionPotential.TotalForces" );
    public static final String INTERACTION_POTENTIAL_COMPONENT_FORCES = StatesOfMatterResources.getString( "InteractionPotential.ComponentForces" );
    public static final String ATTRACTIVE_FORCE_KEY = StatesOfMatterResources.getString( "InteractionPotential.AttractiveForce" );
    public static final String REPULSIVE_FORCE_KEY = StatesOfMatterResources.getString( "InteractionPotential.RepulsiveForce" );
    public static final String STOP_ATOM = StatesOfMatterResources.getString( "InteractionPotential.StopAtom" );
    public static final String RETRIEVE_ATOM = StatesOfMatterResources.getString( "InteractionPotential.RetrieveAtom" );

    public static final String INTERACTION_POTENTIAL_GRAPH_X_AXIS_LABEL_ATOMS = StatesOfMatterResources.getString( "PhaseChanges.InteractionPotentialGraph.xAxisLabelAtoms" );
    public static final String INTERACTION_POTENTIAL_GRAPH_X_AXIS_LABEL_MOLECULES = StatesOfMatterResources.getString( "PhaseChanges.InteractionPotentialGraph.xAxisLabelMolecules" );
    public static final String INTERACTION_POTENTIAL_GRAPH_Y_AXIS_LABEL = StatesOfMatterResources.getString( "PhaseChanges.InteractionPotentialGraph.yAxisLabel" );
    public static final String INTERACTION_POTENTIAL_BUTTON_LABEL = StatesOfMatterResources.getString( "PhaseChanges.InteractionPotentialGraph.ButtonLabel" );

    public static final String GRAVITY_CONTROL_TITLE = StatesOfMatterResources.getString( "GravityControl.Title" );
    public static final String GRAVITY_CONTROL_NONE = StatesOfMatterResources.getString( "GravityControl.None" );
    public static final String GRAVITY_CONTROL_LOTS = StatesOfMatterResources.getString( "GravityControl.Lots" );

    public static final String ATOM_DIAMETER_CONTROL_TITLE = StatesOfMatterResources.getString( "AtomDiameterControl.Title" );
    public static final String ATOM_DIAMETER_SMALL = StatesOfMatterResources.getString( "AtomDiameterControl.Small" );
    public static final String ATOM_DIAMETER_LARGE = StatesOfMatterResources.getString( "AtomDiameterControl.Large" );
    public static final String INTERACTION_STRENGTH_CONTROL_TITLE = StatesOfMatterResources.getString( "InteractionStrengthControl.Title" );
    public static final String INTERACTION_STRENGTH_WEAK = StatesOfMatterResources.getString( "InteractionStrengthControl.Weak" );
    public static final String INTERACTION_STRENGTH_STRONG = StatesOfMatterResources.getString( "InteractionStrengthControl.Strong" );

    public static final String CLOCK_SPEED_CONTROL_CAPTION = PhetCommonResources.getString( "Common.sim.speed" );

    public static final String RESET = StatesOfMatterResources.getString( "Reset" );

    public static final String WIGGLE_ME_CAPTION = StatesOfMatterResources.getString( "WiggleMeCaption" );
    public static final String RETURN_LID = StatesOfMatterResources.getString( "ReturnLid" );

    public static final String KELVIN = StatesOfMatterResources.getString( "Kelvin" );
    public static final String CELSIUS = StatesOfMatterResources.getString( "Celsius" );
    public static final String UNITS_K = StatesOfMatterResources.getString( "units.K" );
    public static final String UNITS_C = StatesOfMatterResources.getString( "units.C" );
}
