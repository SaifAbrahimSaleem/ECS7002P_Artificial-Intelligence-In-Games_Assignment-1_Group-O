package players.RLplayer;

import core.GameState;
import players.Player;
import players.heuristics.AdvancedHeuristic;
import players.heuristics.CustomHeuristic;
import players.mcts.SingleTreeNode;
import utils.ElapsedCpuTimer;
import utils.Pair;
import utils.Types;

import javax.swing.*;
import java.util.*;


public class RLPlayer extends Player {

    private Random random;
    private GameState currentState;
    private Types.ACTIONS[] actions;
    RLParams params;
    RLLearner learner;

    /**
     * Default constructor, to be called in subclasses (initializes player ID and random seed for this agent.
     *     * @param seed - random seed for this player.
     * @param pId  - this player's ID.
     */
    public RLPlayer(long seed, int pId) {
        super(seed, pId);
        random = new Random();
        ArrayList<Types.ACTIONS> actionsList = Types.ACTIONS.all();
        actions = new Types.ACTIONS[actionsList.size()];
        params = new RLParams();
        learner = new RLLearner(this.currentState);
        //System.out.println(RLLearner.qVals.isEmpty());
        if(RLLearner.qVals.isEmpty() ) {
            RLLearner.initialiseMap();
        }
    }

    @Override
    public Types.ACTIONS act(GameState gs) {
        //TODO

        ElapsedCpuTimer ect = new ElapsedCpuTimer();
        ect.setMaxTimeMillis(params.num_time);

        // Number of actions available
        int num_actions = actions.length;

        learner.learn();

        if(Types.DEFAULT_VISION_RANGE ==-1){
            //everything visible
            Set<Map.Entry<Pair, Double>> qvalues = RLLearner.qVals.entrySet();
            System.out.println("set");
            System.out.println(qvalues);
        }else if(Types.DEFAULT_VISION_RANGE > 1) {
            //Types.whatever  = vision range
        }else{
            return null;
        }

        return actions[random.nextInt(actions.length)];
    }

    @Override
    public int[] getMessage() {
        return new int[0];
    }

    @Override
    public Player copy() {

        RLPlayer copy =  new RLPlayer(this.seed, this.playerID);
        copy.setCurrentGameState(this.currentState);
        return copy;
    }



    private void setCurrentGameState(GameState gs)
    {
        currentState = gs;
    }

    private GameState getCurrentState(){
        return  currentState;
    }
}
