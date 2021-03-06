package edu.up.cs.androidcatan.catan;


import android.util.Log;

import java.util.ArrayList;

import edu.up.cs.androidcatan.catan.actions.CatanBuildCityAction;
import edu.up.cs.androidcatan.catan.actions.CatanBuildRoadAction;
import edu.up.cs.androidcatan.catan.actions.CatanBuildSettlementAction;
import edu.up.cs.androidcatan.catan.actions.CatanBuyDevCardAction;
import edu.up.cs.androidcatan.catan.actions.CatanEndTurnAction;
import edu.up.cs.androidcatan.catan.actions.CatanRobberDiscardAction;
import edu.up.cs.androidcatan.catan.actions.CatanRobberMoveAction;
import edu.up.cs.androidcatan.catan.actions.CatanRobberStealAction;
import edu.up.cs.androidcatan.catan.actions.CatanRollDiceAction;
import edu.up.cs.androidcatan.catan.actions.CatanTradeWithBankAction;
import edu.up.cs.androidcatan.catan.actions.CatanTradeWithCustomPortAction;
import edu.up.cs.androidcatan.catan.actions.CatanTradeWithPortAction;
import edu.up.cs.androidcatan.catan.actions.CatanUseKnightCardAction;
import edu.up.cs.androidcatan.catan.actions.CatanUseMonopolyCardAction;
import edu.up.cs.androidcatan.catan.actions.CatanUseRoadBuildingCardAction;
import edu.up.cs.androidcatan.catan.actions.CatanUseVictoryPointCardAction;
import edu.up.cs.androidcatan.catan.actions.CatanUseYearOfPlentyCardAction;
import edu.up.cs.androidcatan.catan.gamestate.DevelopmentCard;
import edu.up.cs.androidcatan.catan.gamestate.Graph;
import edu.up.cs.androidcatan.catan.gamestate.buildings.City;
import edu.up.cs.androidcatan.catan.gamestate.buildings.Road;
import edu.up.cs.androidcatan.catan.gamestate.buildings.Settlement;
import edu.up.cs.androidcatan.game.GamePlayer;
import edu.up.cs.androidcatan.game.LocalGame;
import edu.up.cs.androidcatan.game.actionMsg.GameAction;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * https://github.com/alexweininger/android-catan
 **/

public class CatanLocalGame extends LocalGame {

    private final static String TAG = "CatanLocalGame";

    private CatanGameState state;

    /**
     * constructor for CatanLocalGame
     */
    public CatanLocalGame() {
        super();
        state = new CatanGameState();
    }

    /*--------------------------------------- Action Methods -------------------------------------------*/

    /**
     * Tell whether the given player is allowed to make a move at the
     * present point in the game.
     *
     * @param playerIdx the player's player-number (ID)
     * @return true iff the player is allowed to move
     */
    @Override
    public boolean canMove(int playerIdx) {
        Log.d(TAG, "canMove() called with: playerIdx = [" + playerIdx + "] currentPlayerId(): " + this.state.getCurrentPlayerId());

        // if it is the robber phase
        if (state.isRobberPhase()) {
            // if the player has discarded already
            if (state.getRobberPlayerListHasDiscarded()[playerIdx]) {
                if (state.getCurrentPlayerId() == playerIdx) {
                    // if the player has discarded, and it is their turn, return true
                    Log.d(TAG, "canMove() the player has discarded, and it is their turn, returned " + true);
                    return true;
                } else {
                    // return false if it is the robber phase and they have discarded, and it is not their turn
                    Log.d(TAG, "canMove() it is the robber phase and they have discarded, and it is not their turn returned " + false);
                    return false;
                }
            } else {
                // return true if it is the robber phase and they have not discarded
                Log.d(TAG, "canMove() it is the robber phase and they have not discarded returned " + true);
                return true;
            }
        }

        if (playerIdx < 0 || playerIdx > 3) Log.e(TAG, "canMove: Invalid playerIds: " + playerIdx);

        Log.d(TAG, "canMove() returned: " + (playerIdx == this.state.getCurrentPlayerId()));
        return playerIdx == this.state.getCurrentPlayerId();
    }

    /**
     * Initiates action based on what kind of GameAction object received.
     *
     * @param action The move that the player has sent to the game
     * @return Tells whether the move was a legal one.
     */
    @Override
    protected synchronized boolean makeMove(GameAction action) {
        Log.d(TAG, "makeMove() called with: action = [" + action + "]");
        Log.d(TAG, "makeMove: state: " + state.toString());
        /* --------------------------- Turn Actions --------------------------------------- */

        //Player is rolling the dice
        if (action instanceof CatanRollDiceAction) {
            Log.d(TAG, "makeMove() called with: action = [" + action + "]");

            //Set the gamestates dice value to what the player rolled
            this.state.setCurrentDiceSum(this.state.getDice().roll());
            Log.i(TAG, "rollDice: Player " + this.state.getCurrentPlayerId() + " rolled a " + this.state.getCurrentDiceSum());

            //If Robber is rolled, activate robber phase
            if (state.getCurrentDiceSum() == 7) { // if the robber is rolled
                Log.i(TAG, "rollDice: The robber has been activated.");
                state.setRobberPhase(true);
            } else { //Produce Resources for any other roll
                // produce resources for the roll
                Log.d(TAG, "makeMove: calling produce resources");
                state.produceResources(state.getCurrentDiceSum());
            }

            //Action Phase: Player can now do any action other than roll
            state.setActionPhase(true); // set the action phase to true
            return true;
        }

        //Player is ending their turn
        if (action instanceof CatanEndTurnAction) {
            Log.d(TAG, "makeMove() Player " + state.getCurrentPlayerId() + " is ending their turn.");

            // if it is still the setup phase
            if (this.state.isSetupPhase()) {
                // increment setup phase turn counter
                if (this.state.getSetupPhaseTurnCounter() < 7) {
                    this.state.setSetupPhaseTurnCounter(this.state.getSetupPhaseTurnCounter() + 1);
                    this.state.setCurrentPlayerId(CatanGameState.setupPhaseTurnOrder[state.getSetupPhaseTurnCounter()]);
                } else {
                    // if it is the last turn of the setup phase
                    this.state.setCurrentPlayerId(this.state.getCurrentPlayerId());
                }
            } else { // if it is not the setup phase
                // increment the current turn
                if (this.state.getCurrentPlayerId() == 3) this.state.setCurrentPlayerId(0);
                else this.state.setCurrentPlayerId(this.state.getCurrentPlayerId() + 1);
            }
            // update the setup phase
            if (state.isSetupPhase()) this.state.setSetupPhase(this.state.updateSetupPhase());

            state.setActionPhase(false); // set action phase to false
            state.getCurrentPlayer().setDevCardsBuiltThisTurn(new ArrayList<Integer>());
            Log.i(TAG, "makeMove: It is now " + state.getCurrentPlayerId() + "'s turn.");
            return true;
        }

        /* --------------------------- Build Actions --------------------------------------- */

        //Player is building a road
        if (action instanceof CatanBuildRoadAction) {
            Log.d(TAG, "makeMove() receiving a CatanBuildRoadAction: " + action.toString());

            // if it is the setup phase, do not remove resources
            if (((CatanBuildRoadAction) action).isSetupPhase()) {
                Log.i(TAG, "makeMove: Setup phase. Not checking for resources.");
                // add the road to the board
                state.getBoard().addRoad(((CatanBuildRoadAction) action).getOwnerId(), ((CatanBuildRoadAction) action).getIntAId(), ((CatanBuildRoadAction) action).getIntBid());
                return true;
            }
            // if not setup phase, remove the resource cost of a road from the players resource cards
            if (state.getCurrentPlayer().removeResourceBundle(Road.resourceCost)) {
                // add the road to the board
                state.getBoard().addRoad(((CatanBuildRoadAction) action).getOwnerId(), ((CatanBuildRoadAction) action).getIntAId(), ((CatanBuildRoadAction) action).getIntBid());

                //New graph to add roads to board
                Graph rg = new Graph(54);
                rg.setAllRoads(state.getBoard().getRoads());

                //Thread to update the player with the longest road
                Thread t = new Thread(rg);
                t.start();
                try {
                    Log.i(TAG, "makeMove: thread joined");
                    t.join();
                } catch (Exception e) {
                    Log.e(TAG, "makeMove: t.join()", e);
                }
                state.setCurrentLongestRoadPlayerId(rg.updatePlayerWithLongestRoad());
                state.setCurrentLongestRoadPlayerId(rg.getPlayerIdWithLongestRoad());
                return true;
            }

            //removeResourceBundle failed
            Log.e(TAG, "makeMove: Player sent a CatanBuildRoadAction but removeResourceBundle returned false.");
            return false;
        }

        //Player is building a settlement
        if (action instanceof CatanBuildSettlementAction) {
            Log.i(TAG, "makeMove: received an CatanBuildSettlementAction.");

            // if it is the setup phase do not remove resources
            if (((CatanBuildSettlementAction) action).isSetupPhase()) {
                Log.i(TAG, "makeMove: Setup phase. Not checking for resources.");
                // add settlement to the board
                state.getBoard().addBuilding(((CatanBuildSettlementAction) action).getIntersectionId(), new Settlement(((CatanBuildSettlementAction) action).getOwnerId()));
                if (state.getSetupPhaseTurnCounter() > 3) {
                    ArrayList<Integer> adjacentHexagons = this.state.getBoard().getIntToHexIdMap().get(((CatanBuildSettlementAction) action).getIntersectionId());
                    for (Integer hexagon : adjacentHexagons) {
                        this.state.getCurrentPlayer().addResourceCard(state.getBoard().getHexagonFromId(hexagon).getResourceId(), 1);
                    }
                }
                state.getCurrentPlayer().addVictoryPoints(1);
                Log.d(TAG, "makeMove() returned: " + true);
                return true;
            } else {
                // remove resources from players inventory (also does checks)
                if (state.getCurrentPlayer().removeResourceBundle(Settlement.resourceCost)) {
                    // add settlement to the board
                    state.getBoard().addBuilding(((CatanBuildSettlementAction) action).getIntersectionId(), new Settlement(((CatanBuildSettlementAction) action).getOwnerId()));
                    state.getCurrentPlayer().addVictoryPoints(1);
                    Log.d(TAG, "makeMove() returned: " + true);
                    return true;
                }
                // if the player does not have enough resources at this point in execution something is WRONG
                Log.e(TAG, "buildSettlement: Player " + state.getCurrentPlayerId() + " resources: " + state.getCurrentPlayer().printResourceCards() + " makeMove() returned: " + false);
                return false;
            }
        }

        //Player is building a City
        if (action instanceof CatanBuildCityAction) {
            Log.d(TAG, "makeMove() called with: action = [" + action + "]");

            //Remove resources from player who called the action
            if (state.getCurrentPlayer().removeResourceBundle(City.resourceCost)) {
                // add building to the board
                state.getBoard().addBuilding(((CatanBuildCityAction) action).getIntersectionId(), new City(((CatanBuildCityAction) action).getOwnerId()));
                state.getCurrentPlayer().addVictoryPoints(1);
                Log.d(TAG, "makeMove() returned: " + true);
                return true;
            }
            // if the player does not have enough resources at this point in execution something is WRONG
            Log.e(TAG, "buildCity: Player " + state.getCurrentPlayerId() + " resources: " + state.getCurrentPlayer().printResourceCards() + " makeMove() returned: " + false);
            return false;
        }

        /*------------------------------- Development Card Actions -------------------------------*/

        //Player would like to buy a development card
        if (action instanceof CatanBuyDevCardAction) {
            Log.d(TAG, "makeMove() called with: action = [" + action + "]");

            //Get the current player who wants the dev card
            Player player = state.getCurrentPlayer();

            // remove resources from players inventory (also does checks)
            if (!player.removeResourceBundle(DevelopmentCard.resourceCost)) return false;

            // add random dev card to players inventory
            int devCard = state.getRandomDevCard();
            player.getDevelopmentCards().add(devCard);
            player.addDevCardsBuiltThisTurn(devCard);

            return true;
        }

        //Player would like to player a Knight Dev Card
        if (action instanceof CatanUseKnightCardAction) {
            Log.d(TAG, "makeMove() called with: action = [" + action + "]");

            //Remove the dev card from their hand
            state.getCurrentPlayer().removeDevCard(0);

            //Check and update the army size to see if they can claim the "Largest Army" trophy
            state.checkArmySize(state.getCurrentPlayerId());

            //Activate Robber phase
            state.setRobberPhase(true);

            //Skip the discarding phase and move to the move robber phase
            for (int i = 0; i < state.getPlayerList().size(); i++) {
                state.setRobberPlayerListHasDiscarded(new boolean[]{true, true, true, true});
            }

            //Return
            return true;
        }

        //Player would like to use the Victory Points Dev Card
        if (action instanceof CatanUseVictoryPointCardAction) {
            Log.d(TAG, "makeMove() called with: action = [" + action + "]");

            //Remove the dev card and add victory points to the player's private victory points
            state.getCurrentPlayer().removeDevCard(1);
            state.getCurrentPlayer().addPrivateVictoryPoints(1);
            return true;
        }

        //Player would like to use Catan Year of Plenty Dev Card
        if (action instanceof CatanUseYearOfPlentyCardAction) {
            Log.d(TAG, "makeMove() called with: action = [" + action + "]");

            //Add 2 of the specified resource to the player's hand then remove the dev card
            state.getCurrentPlayer().addResourceCard(((CatanUseYearOfPlentyCardAction) action).getChosenResource(), 2);
            state.getCurrentPlayer().removeDevCard(2);
            return true;
        }

        //Player would like to use the Monopoly Dev Card
        if (action instanceof CatanUseMonopolyCardAction) {
            Log.d(TAG, "makeMove() called with: action = [" + action + "]");

            //Set total resources and get the resource the player wants
            int totalResources = 0;
            int resourceId = ((CatanUseMonopolyCardAction) action).getChosenResource();

            //Iterate through all players and remove the specified resource, adding the the totalResources
            for (Player player : state.getPlayerList()) {
                int resCount = player.getResourceCards()[resourceId];
                player.removeResourceCard(resourceId, resCount);
                totalResources += resCount;
            }

            //Add the totalResources of the specified resource then remove the dev card
            state.getCurrentPlayer().addResourceCard(resourceId, totalResources);
            state.getCurrentPlayer().removeDevCard(3);
            return true;
        }

        //Player would like to use the Road Building Dev Card
        if (action instanceof CatanUseRoadBuildingCardAction) {
            Log.d(TAG, "makeMove() called with: action = [" + action + "]");

            //Add the resources to the player's hand to build two roads then remove the dev card
            state.getCurrentPlayer().addResourceCard(0, 2);
            state.getCurrentPlayer().addResourceCard(2, 2);
            state.getCurrentPlayer().removeDevCard(4);
            return true;
        }

        /*---------------------------------- Robber Actions --------------------------------------*/

        //Player is Discarding Cards during Robber Discard Phase
        if (action instanceof CatanRobberDiscardAction) {
            Log.d(TAG, "makeMove() called with: action = [" + action + "], playerId=" + ((CatanRobberDiscardAction) action).getPlayerId());

            //Discard specified resources
            return state.discardResources(((CatanRobberDiscardAction) action).getPlayerId(), ((CatanRobberDiscardAction) action).getRobberDiscardedResources());
        }

        //Player is Moving robber during Move Robber Phase
        if (action instanceof CatanRobberMoveAction) {
            Log.d(TAG, "makeMove() called with: action = [" + action + "]. playerId=" + ((CatanRobberMoveAction) action).getPlayerId());

            //Don't move if they've already moved the robber
            if (state.getHasMovedRobber()) {
                Log.d(TAG, "makeMove: the robber has already been moved");
                return false;
            }

            //Move the robber if valid; then set hasMovedRobber to true
            if (state.getBoard().moveRobber(((CatanRobberMoveAction) action).getHexagonId())) {
                Log.e(TAG, "makeMove() move robber: Player " + ((CatanRobberMoveAction) action).getPlayerId() + " moved the Robber to Hexagon " + ((CatanRobberMoveAction) action).getHexagonId());
                this.state.setHasMovedRobber(true);
                return true;
            }
            Log.e(TAG, "makeMove: moving the robber failed returning false.");
            return false;
        }

        //Player is Stealing from another player during Steal Phase
        if (action instanceof CatanRobberStealAction) {
            Log.d(TAG, "makeMove() called with: action = [" + action + "]");

            //Steal a random resource from a specified player
            return state.robberSteal(((CatanRobberStealAction) action).getPlayerId(), ((CatanRobberStealAction) action).getStealingFromPlayerId());
        }

        /*---------------------------------- Trade Actions ---------------------------------------*/

        //Player is trading with the bank
        if (action instanceof CatanTradeWithBankAction) {
            Log.d(TAG, "makeMove() called with: action = [" + action + "]");

            // Player.removeResources returns false if the player does not have enough, if they do it removes them.
            if (!state.getCurrentPlayer().removeResourceCard(((CatanTradeWithBankAction) action).getResourceIdGiving(), 4)) {
                Log.e(TAG, "makeMove: trade with bank action: not enough resources, player id: " + state.getCurrentPlayerId());
                return false;
            }

            //Trade 4 of player's own resource for 1 specified resource
            state.getCurrentPlayer().addResourceCard(((CatanTradeWithBankAction) action).getResourceIdRec(), 1); // add resource card to players inventory
            Log.w(TAG, "tradeWithBank - player " + state.getCurrentPlayerId() + " traded " + 4 + " " + ((CatanTradeWithBankAction) action).getResourceIdGiving() + " for a " + ((CatanTradeWithBankAction) action).getResourceIdRec() + " with bank.\n");
            return true;
        }

        //Player is traiding with a port
        if (action instanceof CatanTradeWithPortAction) {
            Log.d(TAG, "makeMove() called with: action = [" + action + "]");
            // remove resources from the player
            if (state.getCurrentPlayer().removeResourceCard(((CatanTradeWithPortAction) action).getPort().getResourceId(), ((CatanTradeWithPortAction) action).getPort().getTradeRatio())) {
                // add requested resource to player
                state.getCurrentPlayer().addResourceCard(((CatanTradeWithPortAction) action).getResourceRecId(), 1);
                return true;
            } else { //Invalid trade
                Log.e(TAG, "makeMove: trade with port: Could not remove resources from player. Returning false");
                return false;
            }
        }

        //Player is trading with port that gives a specified resource
        if (action instanceof CatanTradeWithCustomPortAction) {
            // remove resources from players inventory
            if (state.getCurrentPlayer().removeResourceCard(((CatanTradeWithCustomPortAction) action).getResourceGiveId(), 3)) {
                // add requested resource to player
                state.getCurrentPlayer().addResourceCard(((CatanTradeWithCustomPortAction) action).getResourceRecId(), 1);
            } else {
                Log.e(TAG, "makeMove: custom port trade: Could not remove resources from player. Returning false");
                return false;
            }
        }

        // if we reach here, the GameAction object we received is not one that we recognize
        Log.e(TAG, "makeMove: FATAL ERROR: GameAction action was not and instance of an action class that we recognize.");
        return false;
    }

    /*---------------------- Methods for checking the Game State and updating it ------------------------------------*/

    /**
     * Notify the given player that its state has changed. This should involve sending
     * a GameInfo object to the player. If the game is not a perfect-information game
     * this method should remove any information from the game that the player is not
     * allowed to know.
     *
     * @param p the player to notify
     */
    @Override
    protected void sendUpdatedStateTo(GamePlayer p) {
        Log.d(TAG, "sendUpdatedStateTo() called with: p = [" + p + "]");
        Log.i(TAG, "sendUpdatedStateTo: state.toSting():" + this.state.toString());
        Log.i(TAG, "sendUpdatedStateTo: board.toString(): " + this.state.getBoard().toString());
        CatanGameState copy = new CatanGameState(state);
        Log.i(TAG, "sendUpdatedStateTo: board.toString(): " + copy.getBoard().toString());

        p.sendInfo(copy);
    }

    /**
     * Check if the game is over. It is over, return a string that tells
     * who the winner(s), if any, are. If the game is not over, return null;
     *
     * @return a message that tells who has won the game, or null if the
     * game is not over
     */
    @Override
    public String checkIfGameOver() {
        Log.d(TAG, "checkIfGameOver() called");

        //NULL check
        if (playerNames == null) {
            Log.e(TAG, "checkIfGameOver: player names is null");
            return null;
        }

        //Iterate through players and see if anyone has 10 or more victory points
        for (int i = 0; i < this.state.getPlayerList().size(); i++) {

            int lr = (this.state.getCurrentLongestRoadPlayerId() == i) ? 2 : 0;
            int la = (this.state.getCurrentLargestArmyPlayerId() == i) ? 2 : 0;

            //Player has won the game
            if (this.state.getPlayerList().get(i).getVictoryPointsPrivate() + lr + la + this.state.getPlayerList().get(i).getVictoryPoints() > 9) {
                return playerNames[i] + " wins!";
            }
        }
        return null; // return null if no winner, but the game is not over
    }

    /**
     * Starts the game. Creates initial game state.
     *
     * @param players The list of players in the game.
     */
    @Override
    public void start(GamePlayer[] players) {
        super.start(players);
        state = new CatanGameState(state);
    }

    public void setState(CatanGameState state) {
        this.state = state;
    }

}
