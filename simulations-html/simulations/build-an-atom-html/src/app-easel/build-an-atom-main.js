// Copyright 2002-2012, University of Colorado
require( [
             'underscore',
             'easel',
             'view/BuildAnAtomStage',
             'model/BuildAnAtomModel',
             'view/SymbolView',
             'view/MassNumberView',
             'tpl!templates/periodic-table.html'
         ], function ( _, Easel, BuildAnAtomStage, BuildAnAtomModel, SymbolView, MassNumberView, periodicTable ) {

    var buildAnAtomModel = new BuildAnAtomModel();
    window.buildAnAtomStage = new BuildAnAtomStage( document.getElementById( 'atom-construction-canvas' ), buildAnAtomModel );

    $( document ).ready( function () {

        var atom = buildAnAtomModel.atom;
        var symbolWidget = new SymbolView( atom );
        var massNumberWidget = new MassNumberView( atom );

        $( '#periodic-table' ).html( periodicTable() );
    } );
} );
