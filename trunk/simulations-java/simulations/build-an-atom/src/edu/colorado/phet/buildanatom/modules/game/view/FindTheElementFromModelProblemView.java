package edu.colorado.phet.buildanatom.modules.game.view;

import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.Problem;

/**
 * @author Sam Reid
 */
public class FindTheElementFromModelProblemView extends FindTheElementProblemView {
    private GameAtomModelNode gameAtomModelNode;

    public FindTheElementFromModelProblemView( final BuildAnAtomGameModel model, GameCanvas gameCanvas, final Problem problem ) {
        super( model, gameCanvas, problem );
        gameAtomModelNode = new GameAtomModelNode( problem.getAnswer() );
    }

    @Override
    public void init() {
        super.init();
        addChild( gameAtomModelNode );
    }

    @Override
    public void teardown() {
        super.teardown();
        removeChild( gameAtomModelNode );
    }

}
