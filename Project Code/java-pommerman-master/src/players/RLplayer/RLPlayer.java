package players.RLplayer;

import core.GameState;
import players.Player;
import players.heuristics.AdvancedHeuristic;
import players.heuristics.CustomHeuristic;
import players.mcts.SingleTreeNode;
import utils.ElapsedCpuTimer;
import utils.Pair;
import utils.Types;
import utils.Vector2d;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Policy;
import java.util.*;
import java.util.stream.Collectors;


public class RLPlayer extends Player {

    private Random random;
    private GameState currentState;
    private Types.ACTIONS[] actions;
    RLParams params;
    RLLearner learner;
    RLPolicy policy;
    File deathFileDir = new File(".");
    File deathFile = new File(deathFileDir, "death.txt");
    boolean loopedThrough = false;
    int forcedMoves = 0;


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
        policy = new RLPolicy();
        //System.out.println(RLLearner.qVals.isEmpty());
        if(RLLearner.qVals.isEmpty() ) {
            RLLearner.initialiseMap();
        }
        int i = 0;
        for (Types.ACTIONS act : actionsList) {
            actions[i++] = act;
        }

    }

    @Override
    public Types.ACTIONS act(GameState gs) {
        //TODO

        ElapsedCpuTimer ect = new ElapsedCpuTimer();
        ect.setMaxTimeMillis(params.num_time);

        // Number of actions available
        int num_actions = actions.length;
        currentState = gs;
        GameState copyState = currentState.copy();

        learner.learn(copyState);

            Double bestQVal = Double.MIN_VALUE;
            Vector2d optimalCord = new Vector2d(0,0);
            for (Map.Entry<Vector2d, Double> entry : RLLearner.qVals.entrySet()) {
                if(entry.getValue() > bestQVal){
                    bestQVal = entry.getValue();
                    optimalCord = entry.getKey();
                }
            }

            copyState = currentState.copy(); //copy current state for game simulation
            Types.ACTIONS pickedAction = actions[0]; //set default picked action
            Vector2d bestCord = new Vector2d(gs.getPosition());
            GameState bestState = copyState; //get best state
            Vector2d bestStatePos = bestState.getPosition(); //get pos for best state
            Vector2d bestPair = new Vector2d(bestStatePos.x,bestStatePos.y); // create vector for best state pos
            int notMovedFor = 0;

            for(Types.ACTIONS a : actions){
                GameState next = policy.roll(copyState, a);
//                System.out.println(" Current pos " + copyState.getPosition());
//                System.out.println("Next pos " + next.getPosition());
                if((next.getPosition().dist(optimalCord) < bestCord.dist(optimalCord))){ //compare distances
                    bestCord = next.getPosition();
                    pickedAction = a;
                }
                Vector2d nextPos = next.getPosition();
                Vector2d newPair = new Vector2d(nextPos.x,nextPos.y);
                if(policy.evaluate(next, RLLearner.qVals.get(newPair), Double.MAX_VALUE) > policy.evaluate(bestState, RLLearner.qVals.get(bestPair),Double.MAX_VALUE )){
                    bestState = next;
                    pickedAction = a;
                    bestStatePos = bestState.getPosition();
                    bestPair = new Vector2d(bestStatePos.x,bestStatePos.y);


                }

                if(next.getPosition() == copyState.getPosition()){
                    notMovedFor ++;
                    if(notMovedFor > 5) {
                        pickedAction = actions[random.nextInt(actions.length)];
                        while(policy.evaluate(policy.roll(copyState,pickedAction), RLLearner.qVals.get(currentState.getPosition()), Double.MAX_VALUE) < RLLearner.qVals.get(currentState.getPosition())){
                            pickedAction = actions[random.nextInt(actions.length)];
                        }
                        //forcedMoves++;
                       // GameState next2 = policy.roll(copyState, pickedAction);

                        //condition to make so player doesn't kill itself
//                        while (pickedAction == Types.ACTIONS.ACTION_BOMB && next2.getPosition() == next.getPosition()) {
//                            pickedAction = actions[random.nextInt(actions.length)];
//                        }
                    }

                }
//                if(pickedAction == Types.ACTIONS.ACTION_BOMB){
//                    System.out.println("RL PLAYER PLANTED BOMB");
//                }


                //Log deaths in file



            }

//        Types.TILETYPE[] aliveAgentIDs = currentState.getAliveAgentIDs();
//        boolean isDead = true;
//        for(Types.TILETYPE agent: aliveAgentIDs){
//            System.out.println(agent.getKey());
//            if(agent.getKey() == this.getPlayerID()){
//                isDead = false;
//            }
//
//        }
//        if(isDead && !loopedThrough) {
//            try (FileWriter fw = new FileWriter(deathFile)) {
//                fw.write("Death at: " + currentState.getPosition());
//                fw.flush();
//                loopedThrough = true;
//            } catch (IOException ex) {
//            }
//
//
//        }

            return pickedAction;






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
