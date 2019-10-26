package players.RLplayer;

import core.GameState;
import players.Player;
import players.heuristics.AdvancedHeuristic;
import players.heuristics.CustomHeuristic;
import players.mcts.SingleTreeNode;
import utils.ElapsedCpuTimer;
import utils.Types;

import java.util.ArrayList;
import java.util.Random;

public class RLLearner {
    private GameState currentState;
    private Random random;
    private Types.ACTIONS[] actions;
    private RLParams params;
    private ArrayList<Double> qValues;
    private ArrayList<GameState> states;


    private RLPolicy policy;

    RLLearner (GameState g){
        currentState = g;
        random = new Random();
        ArrayList<Types.ACTIONS> actionsList = Types.ACTIONS.all();
        actions = new Types.ACTIONS[actionsList.size()];
        policy = new RLPolicy();
         params = new RLParams();
         qValues = new ArrayList<>();
         states = new ArrayList<>();

    }

    public int learn(){
        //todo
        return 0;
    }

    //calculate reward for state here
    private double calculateQ() {
        int index = 0;
        int numIters = 0;
        boolean stop = false;
        int acumTimeTaken = 0;
        int avgTimeTaken = 0;
        long remaining = 0;
        int remainingLimit = 5;
        int fmCallsCount = 0;
        double newQ = Double.MIN_VALUE;
        GameState state = currentState.copy();


        ElapsedCpuTimer ect = new ElapsedCpuTimer();
        ect.setMaxTimeMillis(40);


        while (!stop) {

            int numOfActions = actions.length;
            newQ = policy.getPolicyFromState(currentState);
            ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();
            //TODO

            if (params.stop_type == params.STOP_TIME) {
                numIters++;
                acumTimeTaken += (elapsedTimerIteration.elapsedMillis());
                avgTimeTaken = acumTimeTaken / numIters;
                remaining = ect.remainingTimeMillis();
                stop = remaining <= 2 * avgTimeTaken || remaining <= remainingLimit;
            } else if (params.stop_type == params.STOP_ITERATIONS) {
                numIters++;
                stop = numIters >= params.num_iterations;
            } else if (params.stop_type == params.STOP_FMCALLS) {
                fmCallsCount += params.rollout_depth;
                stop = (fmCallsCount + params.rollout_depth) > params.num_fmcalls;
            }




        }
        return newQ;

    }
}