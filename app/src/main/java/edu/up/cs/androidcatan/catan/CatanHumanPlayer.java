package edu.up.cs.androidcatan.catan;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.constraint.Group;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.up.cs.androidcatan.R;
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
import edu.up.cs.androidcatan.catan.gamestate.Hexagon;
import edu.up.cs.androidcatan.catan.gamestate.Port;
import edu.up.cs.androidcatan.catan.gamestate.buildings.City;
import edu.up.cs.androidcatan.catan.gamestate.buildings.Road;
import edu.up.cs.androidcatan.catan.graphics.BoardSurfaceView;
import edu.up.cs.androidcatan.catan.graphics.HexagonDrawable;
import edu.up.cs.androidcatan.catan.graphics.HexagonGrid;
import edu.up.cs.androidcatan.game.GameHumanPlayer;
import edu.up.cs.androidcatan.game.GameMainActivity;
import edu.up.cs.androidcatan.game.infoMsg.GameInfo;
import edu.up.cs.androidcatan.game.infoMsg.IllegalMoveInfo;
import edu.up.cs.androidcatan.game.infoMsg.NotYourTurnInfo;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version October 31th, 2018
 * https://github.com/alexweininger/android-catan
 **/
public class CatanHumanPlayer extends GameHumanPlayer implements OnClickListener {
    private final String TAG = "CatanHumanPlayer";

    // instance variables for logic
    private ArrayList<Integer> buildingsBuiltOnThisTurn = new ArrayList<>();
    private float lastTouchDownXY[] = new float[2];
    private boolean debugMode = false;
    private boolean isMenuOpen = false;

    private int selectedHexagonId = -1;
    private ArrayList<Integer> selectedIntersections = new ArrayList<>();

    // resourceCard index values: 0 = Brick, 1 = Lumber, 2 = Grain, 3 = Ore, 4 = Wool
    private int[] robberDiscardedResources = new int[]{0, 0, 0, 0, 0};  //How many resources the player would like to discard
    private /*ArrayList<Integer>*/ int[] resourceIdsToDiscard = new int[]{0, 0, 0, 0, 0};
    private int selectedDevCard = -1;
    private int selectedResourceId = -1;
    private TextView messageTextView = (TextView) null;
    //private Toast popUpMessage = (Toast) null;

    private ArrayList<String> devCards = new ArrayList<>();
    /* ------------------------------ Scoreboard trophy images ------------------------------------ */

    private ImageView roadTrophyImages[] = new ImageView[4];
    private ImageView armyTrophyImages[] = new ImageView[4];

    /* ------------- Building Buttons -------------------- */
    private Button buildCityButton = null;
    private Button buildRoadButton = null;
    private Button buildSettlementButton = null;

    /* ------------- Action Buttons -------------------- */
    private Button sidebarOpenDevCardMenuButton = null;
    private Button robberDiscard = null;
    private Button robberMove = null;
    private Button robberSteal = null;
    private Button tradeButton = null;
    private Button tradeCustomPort = null;
    private Button tradePort = null;
    private Button useDevCard = null;
    private Button buildDevCard = null;
    private Spinner devCardList = null;

    /* ------ Turn Buttons ------- */
    private Button rollButton = null;
    private ImageView diceImageLeft = null;
    private ImageView diceImageRight = null;
    private Button endTurnButton = null;

    /* ------------- Misc Buttons -------------------- */

    private Button sidebarMenuButton = (Button) null;
    private ImageView buildingCosts = null;
    private Button sidebarScoreboardButton = (Button) null;

    /* ------------- resource count text views -------------------- */

    private TextView oreValue = (TextView) null;
    private TextView grainValue = (TextView) null;
    private TextView lumberValue = (TextView) null;
    private TextView woolValue = (TextView) null;
    private TextView brickValue = (TextView) null;

    // scoreboard text views
    private TextView player0Score = (TextView) null;
    private TextView player1Score = (TextView) null;
    private TextView player2Score = (TextView) null;
    private TextView player3Score = (TextView) null;

    // scoreboard player name TextViews
    private TextView player0Name = (TextView) null;
    private TextView player1Name = (TextView) null;
    private TextView player2Name = (TextView) null;
    private TextView player3Name = (TextView) null;

    // misc sidebar TextViews
    private TextView myScore = (TextView) null;
    private TextView currentTurnIdTextView = (TextView) null;
    private TextView playerNameSidebar = (TextView) null;

    //Robber Buttons
    private ImageView robberBrickPlus = (ImageView) null;
    private ImageView robberBrickMinus = (ImageView) null;
    private ImageView robberLumberPlus = (ImageView) null;
    private ImageView robberLumberMinus = (ImageView) null;
    private ImageView robberGrainPlus = (ImageView) null;
    private ImageView robberGrainMinus = (ImageView) null;
    private ImageView robberOrePlus = (ImageView) null;
    private ImageView robberOreMinus = (ImageView) null;
    private ImageView robberWoolPlus = (ImageView) null;
    private ImageView robberWoolMinus = (ImageView) null;
    private TextView robberDiscardMessage = (TextView) null;
    private Button robberConfirmDiscard = (Button) null;

    private TextView robberBrickAmount = (TextView) null;
    private TextView robberLumberAmount = (TextView) null;
    private TextView robberGrainAmount = (TextView) null;
    private TextView robberOreAmount = (TextView) null;
    private TextView robberWoolAmount = (TextView) null;

    private Button robberConfirmHex = (Button) null;
    private TextView robberHexMessage = (TextView) null;

    //Trade Buttons - Receive
    private ImageView brickSelectionBoxReceive = (ImageView) null;
    private ImageView grainSelectionBoxReceive = (ImageView) null;
    private ImageView lumberSelectionBoxReceive = (ImageView) null;
    private ImageView oreSelectionBoxReceive = (ImageView) null;
    private ImageView woolSelectionBoxReceive = (ImageView) null;

    //Trade Buttons - Give
    private ImageView brickSelectionBoxGive = (ImageView) null;
    private ImageView grainSelectionBoxGive = (ImageView) null;
    private ImageView lumberSelectionBoxGive = (ImageView) null;
    private ImageView oreSelectionBoxGive = (ImageView) null;
    private ImageView woolSelectionBoxGive = (ImageView) null;

    //Trade Menu - Receive
    private ImageView image_trade_menu_rec_brick = (ImageView) null;
    private ImageView image_trade_menu_rec_grain = (ImageView) null;
    private ImageView image_trade_menu_rec_ore = (ImageView) null;
    private ImageView image_trade_menu_rec_lumber = (ImageView) null;
    private ImageView image_trade_menu_rec_wool = (ImageView) null;

    //Trade Menu - Give
    private ImageView image_trade_menu_give_brick = (ImageView) null;
    private ImageView image_trade_menu_give_grain = (ImageView) null;
    private ImageView image_trade_menu_give_lumber = (ImageView) null;
    private ImageView image_trade_menu_give_ore = (ImageView) null;
    private ImageView image_trade_menu_give_wool = (ImageView) null;

    //Trade Menu - Confirm and Cancel
    private Button button_trade_menu_confirm = (Button) null;
    private Button button_trade_menu_cancel = (Button) null;
    private int tradeGiveSelection = -1;
    private int tradeReceiveSelection = -1;

    //Monopoly Menu - Resource Icons
    private ImageView monopolyBrickIcon = (ImageView) null;
    private ImageView monopolyGrainIcon = (ImageView) null;
    private ImageView monopolyLumberIcon = (ImageView) null;
    private ImageView monopolyOreIcon = (ImageView) null;
    private ImageView monopolyWoolIcon = (ImageView) null;

    //Monopoly Menu - SelectionBoxes
    private ImageView monopolyBrickSelectionBox = (ImageView) null;
    private ImageView monopolyGrainSelcionBox = (ImageView) null;
    private ImageView monopolyLumberSelectionBox = (ImageView) null;
    private ImageView monopolyOreSelectionBox = (ImageView) null;
    private ImageView monopolyWoolSelectionBox = (ImageView) null;

    //Monopoly Menu - Confrim
    private TextView monopolyConfirm = (TextView) null;
    private int monopolyResourceChoice = -1;

    //Dev Card Menu
    private TextView devcard_text_name = (TextView) null;
    private TextView devcard_text_info = (TextView) null;
    private int devCardId = 0;

    //Other Groups
    private Group scoreBoardGroup = (Group) null;
    private Group developmentGroup = (Group) null;
    private Group tradeGroup = (Group) null;
    private Group robberDiscardGroup = (Group) null;
    private Group robberChooseHexGroup = (Group) null;
    private Group pickResourceGroup = (Group) null;

    //Largest Army Image Views
    private ImageView largestArmyPlayer0 = (ImageView) null;
    private ImageView largestArmyPlayer1 = (ImageView) null;
    private ImageView largestArmyPlayer2 = (ImageView) null;
    private ImageView largestArmyPlayer3 = (ImageView) null;

    //Longest Road Image Views
    private ImageView longestRoadPlayer0 = (ImageView) null;
    private ImageView longestRoadPlayer1 = (ImageView) null;
    private ImageView longestRoadPlayer2 = (ImageView) null;
    private ImageView longestRoadPlayer3 = (ImageView) null;

    private GameMainActivity myActivity;  // the android activity that we are running
    public CatanGameState state = null; // game state
    private BoardSurfaceView boardSurfaceView;

    private int roadCount = 0; // counter variables
    private int settlementCount = 0;

    /*--------------------- Constructors ------------------------*/

    public CatanHumanPlayer (String name) {
        super(name);
    }

    /*---------------------------------- onClick Method -----------------------------------------*/

    /**
     * this method gets called when the user clicks ANY button that has the listener set to 'this'
     *
     * @param button the button that was clicked
     */
    public void onClick (View button) {

        Log.d(TAG, "onClick() called with: button = [" + button + "]");

        if (state == null) {
            Log.e(TAG, "onClick: state is null.");
        } // check if state is null

        /* ---------------------------- Building Sidebar Button OnClick() Handlers --------------------- */
        messageTextView.setTextColor(Color.WHITE);
        // Road button on the sidebar.
        if (button.getId() == R.id.sidebar_button_road) {
            if (selectedIntersections.size() != 2) {
                messageTextView.setText(R.string.need_2_ints_for_road);
                Toast toast = Toast.makeText(myActivity.getApplicationContext(), "Select two intersections to build a road.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();

                return;
            }
            tryBuildRoad(selectedIntersections.get(0), selectedIntersections.get(1));
            return;
        }

        // Settlement button on the sidebar.
        if (button.getId() == R.id.sidebar_button_settlement) {
            Log.d(TAG, "onClick: sidebar_button_settlement listener");
            if (selectedIntersections.size() != 1) {
                messageTextView.setText(R.string.one_int_for_set);
                Toast toast = Toast.makeText(myActivity.getApplicationContext(), "Selecy one intersection to build a settlememt.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();

                shake(messageTextView);
            } else {
                if (tryBuildSettlement(selectedIntersections.get(0))) {
                    messageTextView.setText(R.string.built_settlement);
                    Toast toast = Toast.makeText(myActivity.getApplicationContext(), "Built a settlement.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    //toast.show();

                } else {
                    // tell user location is invalid
                    messageTextView.setText(R.string.invalid_set_loc);
                    Toast toast = Toast.makeText(myActivity.getApplicationContext(), "Invalid settlement location.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }
            }
            return;
        }

        // City button on the sidebar.
        if (button.getId() == R.id.sidebar_button_city) {
            if (selectedIntersections.size() != 1) {
                messageTextView.setText(R.string.select_one_int_for_city);
                Toast toast = Toast.makeText(myActivity.getApplicationContext(), "Select one intersection to build a city.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();

            } else {
                Log.e(TAG, "onClick: build city selected intersection: " + selectedIntersections.get(0));
                if (tryBuildCity(selectedIntersections.get(0))) {
                    messageTextView.setText(R.string.built_city);
                    Toast toast = Toast.makeText(myActivity.getApplicationContext(), "Built a city.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    //toast.show();
                } else {
                    messageTextView.setText(R.string.invalid_city_loc);
                    Toast toast = Toast.makeText(myActivity.getApplicationContext(), "Invalid city location: select a settlement to updgrade into a city.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }
            }
            return;
        }

        /* ----------------------------------- Turn Actions ------------------------------------- */

        // Roll button on the sidebar.
        if (button.getId() == R.id.sidebar_button_roll) {
            Log.d(TAG, "onClick: Roll button clicked.");
            // check if it is the players turn
            if (state.getCurrentPlayerId() != this.playerNum) return;
            // check if it is the action phase
            if (state.isActionPhase()) return;
            // send a CatanRollDiceAction to the game
            game.sendAction(new CatanRollDiceAction(this));
            return;
        }

        // End turn button on the sidebar.
        if (button.getId() == R.id.sidebar_button_endturn) {
            Log.d(TAG, "onClick: End Turn button pressed.");

            if (state.isSetupPhase()) {
                if (!buildingsBuiltOnThisTurn.contains(0) || !buildingsBuiltOnThisTurn.contains(1)) {
                    messageTextView.setText(R.string.build_road_and_set);
                    Toast toast = Toast.makeText(myActivity.getApplicationContext(), R.string.build_road_and_set, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                    shake(messageTextView);
                    return;
                }
            }
            // check if it is the action phase and not the setup phase
            if (!state.isActionPhase() && !state.isSetupPhase()) {
                messageTextView.setText(R.string.cannot_end_turn_before_rolling);
                Toast toast = Toast.makeText(myActivity.getApplicationContext(), "Cannot end turn before rolling!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
                shake(messageTextView);
                return;
            }
            // check if it is the players turn
            if (state.getCurrentPlayerId() != this.playerNum) return;
            game.sendAction(new CatanEndTurnAction(this));
            hideAllMenusAtEndOfTurn();
            this.buildingsBuiltOnThisTurn = new ArrayList<>(); // reset array list
            selectedIntersections.clear();
            selectedHexagonId = -1;
            return;
        }

        /* -------------------------- Scoreboard and Menu Buttons Handlers ---------------------- */

        // Menu button on the sidebar.
        if (button.getId() == R.id.sidebar_button_menu) {
            this.boardSurfaceView.getGrid().toggleDebugMode();
            this.boardSurfaceView.invalidate();
            this.debugMode = !this.debugMode; // toggle debug mode

            toggleViewVisibility(this.buildingCosts); // toggle help image

            //            setAllButtonsToVisible();
            Log.e(TAG, "onClick: toggled debug mode");
            Log.d(TAG, state.toString());
            return;
        }
        // Score button on the sidebar.
        if (button.getId() == R.id.sidebar_button_score) {
            toggleGroupVisibilityAllowTapping(scoreBoardGroup);
        }

        // Trophy images on scoreboard

        showLongestRoadTrophy(state.getCurrentLongestRoadPlayerId());
        showLargestArmyTrophy(state.getCurrentLargestArmyPlayerId());

        /*--------------------------------- Robber onClick --------------------------------*/

        if (button.getId() == R.id.robber_choosehex_confirm) {
            Log.i(TAG, "onClick: Checking if good Hex to place Robber on");
            if (state.getHasMovedRobber()) {
                if (selectedIntersections.size() != 1) {
                    robberHexMessage.setText("Please select only one intersection.");
                    return;
                }

                if (!state.getBoard().hasBuilding(selectedIntersections.get(0))) {
                    robberHexMessage.setText(R.string.select_int_w_bldg_robber);
                    messageTextView.setText(R.string.select_int_w_bldg_robber);
                    Toast toast = Toast.makeText(myActivity.getApplicationContext(), "Please select another player's building", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    //toast.show();
                    return;
                }

                if (state.getBoard().getBuildingAtIntersection(selectedIntersections.get(0)).getOwnerId() == playerNum) {
                    robberHexMessage.setText("Please select an intersection not owned by you.");
                    messageTextView.setText(R.string.select_int_not_owned_by_you);
                    Toast toast = Toast.makeText(myActivity.getApplicationContext(), "Please select an intersection not owned by you.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    //toast.show();
                    return;
                }

                int stealId = state.getBoard().getBuildingAtIntersection(selectedIntersections.get(0)).getOwnerId();
                robberChooseHexGroup.setVisibility(View.GONE);
                game.sendAction(new CatanRobberStealAction(this, playerNum, stealId));
                robberHexMessage.setText("Please select a tile to place the robber on.");
                return;
            }

            if (!tryMoveRobber(selectedHexagonId)) {
                Log.e(TAG, "onClick: Error, Not valid Hexagon chosen");
                robberHexMessage.setText(R.string.invalid_tile);
                shake(robberHexMessage);
                messageTextView.setText(R.string.invalid_tile);
                Toast toast = Toast.makeText(myActivity.getApplicationContext(), "Not a valid title!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                //toast.show();
                shake(messageTextView);
                return;
            }

            Log.i(TAG, "onClick: Successful Hex chosen for Robber, now making group visible");
            robberChooseHexGroup.setVisibility(View.VISIBLE);
            robberHexMessage.setText("Please selected an intersection with a building adjacent to the robber");
            game.sendAction(new CatanRobberMoveAction(this, playerNum, selectedHexagonId));
            return;
        }

        if (button.getId() == R.id.robber_discard_confirm) {
            if (state.validDiscard(this.playerNum, this.robberDiscardedResources)) {
                robberDiscardMessage.setText("Discarding..");
                if (state.getCurrentPlayerId() == playerNum) {
                    robberChooseHexGroup.setVisibility(View.VISIBLE);
                }
                robberDiscardGroup.setVisibility(View.GONE);

                // todo

                robberBrickAmount.setText(R.string.zero);
                robberLumberAmount.setText(R.string.zero);
                robberGrainAmount.setText(R.string.zero);
                robberOreAmount.setText(R.string.zero);
                robberWoolAmount.setText(R.string.zero);

                //                // putting the array into the arraylist todo fix lol this is not good
                //                for (int i = 0; i < robberDiscardedResources.length; i++) {
                //                    for (int j = 0; j < robberDiscardedResources[i]; j++) {
                //
                //                    }
                //                }

                game.sendAction(new CatanRobberDiscardAction(this, playerNum, robberDiscardedResources));
                this.robberDiscardedResources = state.getRobberDiscardedResources();
                robberDiscardedResources = new int[]{0, 0, 0, 0, 0};
                return;
            }

            String message = "" + state.getPlayerList().get(this.playerNum).getTotalResourceCardCount() / 2 + " resources are needed.";
            robberDiscardMessage.setText(message);
            messageTextView.setText(message);
            Toast toast = Toast.makeText(myActivity.getApplicationContext(), message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            shake(messageTextView);
            return;
        }

        int robberDiscardAddButtonIds[] = {R.id.robber_discard_brickAddImg, R.id.robber_discard_grainAddImg, R.id.robber_discard_lumberAddImg, R.id.robber_discard_oreAddImg, R.id.robber_discard_woolAddImg};
        int robberDiscardMinusButtonIds[] = {R.id.robber_discard_brickMinusImg, R.id.robber_discard_grainMinusImg, R.id.robber_discard_lumberMinusImg, R.id.robber_discard_oreMinusImg, R.id.robber_discard_woolMinusImg};
        TextView robberAmounts[] = {robberBrickAmount, robberGrainAmount, robberLumberAmount, robberOreAmount, robberWoolAmount};

        for (int i = 0; i < robberDiscardAddButtonIds.length; i++) {
            if (button.getId() == robberDiscardAddButtonIds[i]) {
                robberDiscardedResources[i]++;
            } else if (button.getId() == robberDiscardMinusButtonIds[i]) {
                robberDiscardedResources[i]--;
            }
        }

        for (int i = 0; i < robberAmounts.length; i++) {
            robberAmounts[i].setText("" + robberDiscardedResources[i]);
        }

        /*-------------------------End of Robber----------------------------------------*/

        /* ---------------- Pick Resource Card Menu ---------------------- */

        int monopolyResourceIds[] = {R.id.pickResMenu_brickIcon, R.id.pickResMenu_grainIcon, R.id.pickResMenu_lumberIcon, R.id.pickResMenu_oreIcon, R.id.pickResMenu_woolIcon};
        ImageView monopolySelectionBox[] = {monopolyBrickSelectionBox, monopolyGrainSelcionBox, monopolyLumberSelectionBox, monopolyOreSelectionBox, monopolyWoolSelectionBox};

        if (selectedDevCard == 2 || selectedDevCard == 3) {
            for (int i = 0; i < monopolyResourceIds.length; i++) {
                if (button.getId() == monopolyResourceIds[i]) selectedResourceId = i;
            }
            for (int i = 0; i < monopolySelectionBox.length; i++) {
                if (i == selectedResourceId) monopolySelectionBox[i].setVisibility(View.VISIBLE);
                else monopolySelectionBox[i].setVisibility(View.INVISIBLE);
            }
        }

        // confirm choose resource button on the pick resource button
        if (button.getId() == R.id.pickResMenu_ConfirmButton) {
            Log.d(TAG, "onClick: Player tried to confirm a monopoly or year of plenty card");
            // make sure they selected a resource
            if (selectedResourceId == -1) {
                messageTextView.setText(R.string.pick_resource);
                Toast toast = Toast.makeText(myActivity.getApplicationContext(), "Select a resource!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                //toast.show();
                shake(messageTextView);
                return;
            }
            // make sure the selected dev card is either a year of plenty or monopoly card
            if (selectedDevCard != 2 && selectedDevCard != 3) {
                Log.e(TAG, "onClick: selected dev card is not 2 or 3");
                toggleGroupVisibilityAllowTapping(pickResourceGroup);
                this.selectedDevCard = -1;
            }
            // send corresponding actions to the game
            if (selectedDevCard == 2)
                game.sendAction(new CatanUseYearOfPlentyCardAction(this, this.selectedResourceId));
            if (selectedDevCard == 3)
                game.sendAction(new CatanUseMonopolyCardAction(this, this.selectedResourceId));
            // hide pick resource menu
            toggleGroupVisibilityAllowTapping(pickResourceGroup);
            this.selectedDevCard = -1;
            this.selectedResourceId = -1;
            return;
        }

        /* -------------------- Development Card Button OnClick() Handlers ---------------------- */

        // Development button located on the sidebar. Should only show/hide dev card menu.
        if (button.getId() == R.id.sidebar_button_devcards) {
            toggleGroupVisibility(developmentGroup); // toggle menu vis.
            return;
        }

        // Use development card button on the dev card menu.
        if (button.getId() == R.id.use_Card) {
            Log.d(TAG, "onClick: Player tapped the use card button.");
            String devCardNames[] = {"Knight Development", "Victory Points Development", "Year of Plenty", "Monopoly", "Road Development"};
            int developmentCardId = -1;
            for (int i = 0; i < devCardNames.length; i++) {
                if (devCardList.getSelectedItem().equals(devCardNames[i])) developmentCardId = i;
            }
            Log.i(TAG, "onClick: Player is using dev card id: " + developmentCardId + " (" + devCardNames[developmentCardId] + ")");

            if (!state.getCurrentPlayer().getDevelopmentCards().contains(developmentCardId)) {
                Log.e(TAG, "onClick: player does not have development card. Cannot use.");
                messageTextView.setText(R.string.dont_have_card);
                Toast toast = Toast.makeText(myActivity.getApplicationContext(), "You don't have that card!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                //toast.show();
                return;
            } else {
                Log.d(TAG, "onClick: Development Card was removed from hand");

                // knight card
                if (developmentCardId == 0) {
                    game.sendAction(new CatanUseKnightCardAction(this));
                    if (devCards.size() == 1) {
                        toggleGroupVisibilityAllowTapping(developmentGroup);
                    }
                    return;
                }

                // victory point card
                if (developmentCardId == 1) {
                    game.sendAction(new CatanUseVictoryPointCardAction(this));
                    if (devCards.size() == 1) {
                        toggleGroupVisibilityAllowTapping(developmentGroup);
                    }
                    return;
                }

                //year of plenty
                if (developmentCardId == 2) {
                    toggleGroupVisibilityAllowTapping(pickResourceGroup);
                    selectedDevCard = 2;
                    return;
                }

                // monopoly
                if (developmentCardId == 3) {
                    toggleGroupVisibility(pickResourceGroup);
                    selectedDevCard = 3;
                }

                // build road card
                if (developmentCardId == 4) {
                    game.sendAction(new CatanUseRoadBuildingCardAction(this));
                    if (devCards.size() == 1) {
                        toggleGroupVisibilityAllowTapping(developmentGroup);
                    }
                    this.rollButton.setAlpha(0.5f);
                    this.rollButton.setClickable(false);
                    this.buildRoadButton.setAlpha(1f);
                    this.buildRoadButton.setClickable(true);
                    this.buildSettlementButton.setAlpha(0.5f);
                    this.buildSettlementButton.setClickable(false);
                    this.buildCityButton.setAlpha(0.5f);
                    this.buildCityButton.setClickable(false);
                    this.sidebarOpenDevCardMenuButton.setAlpha(0.5f);
                    this.sidebarOpenDevCardMenuButton.setClickable(false);
                    this.tradeButton.setAlpha(0.5f);
                    this.tradeButton.setClickable(false);
                    this.endTurnButton.setAlpha(0.5f);
                    this.endTurnButton.setClickable(false);
                    this.sidebarScoreboardButton.setAlpha(0.5f);
                    this.sidebarScoreboardButton.setClickable(false);
                    this.sidebarMenuButton.setAlpha(0.5f);
                    this.sidebarMenuButton.setClickable(false);
                    return;
                }
            }
        }

        // Build development card button in the development card menu.
        if (button.getId() == R.id.build_devCard) {
            // check if player has resources
            if (state.getCurrentPlayer().hasResourceBundle(DevelopmentCard.resourceCost)) {
                // send action to the game
                game.sendAction(new CatanBuyDevCardAction(this));
                messageTextView.setText(R.string.you_built_a_dev);
                Toast toast = Toast.makeText(myActivity.getApplicationContext(), "Development built", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                //toast.show();
            } else {
                messageTextView.setText(R.string.not_enough_for_dev_card);
                Toast toast = Toast.makeText(myActivity.getApplicationContext(), "Not enough resources to build a devlopment.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
                shake(messageTextView);
            }
            return;
        }

        /* ------------------------------------ Trading ---------------------------------- */

        // trade button on sidebar
        if (button.getId() == R.id.sidebar_button_trade) {
            toggleGroupVisibility(tradeGroup);  // toggle menu vis.
            return;
        }

        /* ----------------------- Trade Menu ---------------------------- */

        // arrays of the selection box image views
        ImageView selectionBoxGive[] = {brickSelectionBoxGive, grainSelectionBoxGive, lumberSelectionBoxGive, oreSelectionBoxGive, woolSelectionBoxGive};
        ImageView selectionBoxReceive[] = {brickSelectionBoxReceive, grainSelectionBoxReceive, lumberSelectionBoxReceive, oreSelectionBoxReceive, woolSelectionBoxReceive};

        // set all give selection boxes to transparent
        for (ImageView imageView : selectionBoxGive) {
            imageView.setBackgroundColor(Color.TRANSPARENT);
        }
        // set all receive selection boxes to transparent
        for (ImageView imageView : selectionBoxReceive) {
            imageView.setBackgroundColor(Color.TRANSPARENT);
        }

        // arrays of the buttons
        int giveButtonIds[] = {R.id.image_trade_menu_give_brick, R.id.image_trade_menu_give_grain, R.id.image_trade_menu_give_lumber, R.id.image_trade_menu_give_ore, R.id.image_trade_menu_give_wool};
        int recButtonIds[] = {R.id.image_trade_menu_rec_brick, R.id.image_trade_menu_rec_grain, R.id.image_trade_menu_rec_lumber, R.id.image_trade_menu_rec_ore, R.id.image_trade_menu_rec_wool};

        for (int i = 0; i < 5; i++) {
            if (button.getId() == giveButtonIds[i]) {
                tradeGiveSelection = i;
                break;
            }
            if (button.getId() == recButtonIds[i]) {
                tradeReceiveSelection = i;
                break;
            }
        }

        // if the user selects resource to receive -> highlight the selection
        if (tradeReceiveSelection != -1)
            selectionBoxReceive[tradeReceiveSelection].setBackgroundColor(Color.argb(255, 255, 255, 187));

        // if the user selects resource to give -> highlight the selection
        if (tradeGiveSelection != -1)
            selectionBoxGive[tradeGiveSelection].setBackgroundColor(Color.argb(255, 255, 255, 187));

        // confirm trade logic
        if (button.getId() == R.id.button_trade_menu_confirm) {
            Log.d(TAG, "onClick: Player tried to confirm trade");
            Log.e(TAG, "onClick: selected intersections: " + this.selectedIntersections);
            //checks to see if the user has any intersections selected.
            if (selectedIntersections.size() == 1) {
                if (tryTradeWithPort(tradeGiveSelection, tradeReceiveSelection)) {
                    Log.d(TAG, "onClick: traded with port");
                } else {
                    Log.e(TAG, "onClick: trade with port failed");
                }
            } else if (selectedIntersections.size() == 0) {
                if (tryTradeWithBank(tradeGiveSelection, tradeReceiveSelection)) {
                    Log.d(TAG, "onClick: traded with bank");
                    selectedIntersections.clear();
                } else {
                    Log.e(TAG, "onClick: trade with bank failed");
                }
            } else if (selectedIntersections.size() > 1) {
                Log.e(TAG, "onClick: user has selected too many intersections");
                messageTextView.setText(R.string.less_than_2_res);
                Toast toast = Toast.makeText(myActivity.getApplicationContext(), "Please select less than 2 intersections.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                //toast.show();
            } else {
                Log.e(TAG, "onClick: logic error, because selectedIntersections.size() is negative or null");
            }
        }

        if (button.getId() == R.id.button_trade_menu_cancel) {
            toggleGroupVisibility(tradeGroup);
            tradeReceiveSelection = -1;
            tradeGiveSelection = -1;
        }
    } // onClick END

    /* ----------------------- BoardSurfaceView Touch Listeners --------------------------------- */

    // the purpose of the touch listener is just to store the touch X,Y coordinates
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch (View v, MotionEvent event) {
            if (isMenuOpen) {
                return false;
            }
            // save the X,Y coordinates
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                lastTouchDownXY[0] = event.getX();
                lastTouchDownXY[1] = event.getY();
            }

            // let the touch event pass on to whoever needs it
            return false;
        }
    }; // touchListener END

    // listener that takes the x, y of the touch and converts it into a hex or intersection
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick (View v) {
            if (isMenuOpen) return;
            boolean touchedIntersection = false;
            boolean touchedHexagon = false;
            // retrieve the stored coordinates
            float x = lastTouchDownXY[0];
            float y = lastTouchDownXY[1];
            HexagonGrid grid = boardSurfaceView.getGrid();
            Log.d("TAG", "onLongClick: x = " + x + ", y = " + y); // x, y position

            for (int i = 0; i < grid.getIntersections().length; i++) {
                int xPos = grid.getIntersections()[i].getXPos();
                int yPos = grid.getIntersections()[i].getYPos();

                // if y is greater than y - 25 and less than y + 25
                if (y > yPos - 100 && y < yPos + 100 && x > xPos - 100 && x < xPos + 100) {
                    // if x is greater than point 3 and less than point 0
                    Log.d(TAG, "onClick: Touched intersection id: " + grid.getIntersections()[i].getIntersectionId());
                    touchedIntersection = true;
                    if (grid.getHighlightedIntersections().contains(i)) {
                        boardSurfaceView.getGrid().getHighlightedIntersections().remove((Integer) i);
                        selectedIntersections.remove((Integer) i);
                    } else {
                        boardSurfaceView.getGrid().addHighlightedIntersection(i);
                        if (selectedIntersections.size() > 1) selectedIntersections.remove(0);
                        selectedIntersections.add(i);
                    }
                    boardSurfaceView.getGrid().setHighlightedHexagon(-1);
                    selectedHexagonId = -1;
                    boardSurfaceView.invalidate();
                }
            }
            // if they didn't touch an intersection then check if they touched a hexagon
            if (!touchedIntersection) {
                ArrayList<HexagonDrawable> dHexes = grid.getDrawingHexagons();

                // go through each hexagon and check if the touch matches the bounds of the hex
                int index = 0;
                for (HexagonDrawable hex : dHexes) {
                    int[][] points = hex.getHexagonPoints();

                    // if y is greater than point 0 and less than point 1
                    if (y > points[0][1] && y < points[1][1]) {
                        // if x is greater than point 3 and less than point 0
                        if (x > points[3][0] && x < points[0][0]) {
                            Hexagon dataHexagon = state.getBoard().getHexagonListForDrawing().get(index);
                            Log.w(TAG, "onClick: Touched hexagon id: " + dataHexagon.getHexagonId());
                            touchedHexagon = true;
                            if (dataHexagon.getHexagonId() == boardSurfaceView.getGrid().getHighlightedHexagon()) {
                                // if the hexagon touched is already selected, un-select it
                                boardSurfaceView.getGrid().setHighlightedHexagon(-1);
                                selectedHexagonId = -1;
                            } else {
                                // if touched hexagon is not already selected, select/highlight it
                                boardSurfaceView.getGrid().setHighlightedHexagon(dataHexagon.getHexagonId());
                                selectedHexagonId = dataHexagon.getHexagonId();
                            }
                            boardSurfaceView.getGrid().getHighlightedIntersections().clear();
                            selectedIntersections.clear();
                            boardSurfaceView.invalidate();
                        }
                    }
                    index++;
                }
            }
            // check if no hexagon or intersection was touched (aka. outside the island)
            if (!touchedHexagon && !touchedIntersection) {
                boardSurfaceView.getGrid().setHighlightedHexagon(-1);
                boardSurfaceView.getGrid().getHighlightedIntersections().clear();
                selectedIntersections.clear();
                boardSurfaceView.invalidate();
            }
        }
    }; // clickListener END

    /*--------------------------------------- Validation Methods ---------------------------------*/

    /**
     * @param intersectionA First intersection of the road.
     * @param intersectionB Second intersection of the road. (order does not matter)
     * @return If success.
     */
    private boolean tryBuildRoad (int intersectionA, int intersectionB) {
        Log.d(TAG, "tryBuildRoad() called with: intersectionA = [" + intersectionA + "], intersectionB = [" + intersectionB + "]");
        // check if user given intersections are valid
        if (state.getBoard().validRoadPlacement(state.getCurrentPlayerId(), state.isSetupPhase(), intersectionA, intersectionB)) {
            Log.i(TAG, "tryBuildRoad: Valid road placement received.");
        } else {
            messageTextView.setText(R.string.invalid_road_placement);
            Toast toast = Toast.makeText(myActivity.getApplicationContext(), "Invlid road placement.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            Log.d(TAG, "tryBuildRoad() returned: " + false);
            return false;
        }
        // check if it is the setup phase
        if (state.isSetupPhase()) {
            boardSurfaceView.getGrid().clearHighLightedIntersections();
            selectedIntersections.clear(); // clear the selected intersections
            this.buildingsBuiltOnThisTurn.add(0);
            game.sendAction(new CatanBuildRoadAction(this, state.isSetupPhase(), state.getCurrentPlayerId(), intersectionA, intersectionB));
            messageTextView.setText(R.string.road_built);
            return true;
        }
        // if it is not the setup phase, then check if it is the action phase
        if (!state.isActionPhase()) {
            Log.i(TAG, "tryBuildRoad: Player cannot build road. Not action phase.");
            messageTextView.setText(R.string.roll_the_dice);
            Toast toast = Toast.makeText(myActivity.getApplicationContext(), "Please roll the dice.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            //toast.show();
            shake(messageTextView);
            return false;
        }
        // if it is not the setup phase check if the player has enough resources to build a road
        if (state.getPlayerList().get(state.getCurrentPlayerId()).hasResourceBundle(Road.resourceCost)) {
            // send build settlement action to the game
            Log.d(TAG, "tryBuildRoad: Sending a CatanBuildRoadAction to the game.");
            game.sendAction(new CatanBuildRoadAction(this, state.isSetupPhase(), state.getCurrentPlayerId(), intersectionA, intersectionB));
            // clear selected intersections
            boardSurfaceView.getGrid().clearHighLightedIntersections();
            selectedIntersections.clear(); // clear the selected intersections
            this.buildingsBuiltOnThisTurn.add(0);
            Log.d(TAG, "tryBuildRoad() returned: " + true);
            return true;
        }
        Log.i(TAG, "tryBuildRoad: player does not have enough resources to build a road.");
        messageTextView.setText(R.string.not_enough_for_road);
        Toast toast = Toast.makeText(myActivity.getApplicationContext(), "Not enough resources to build a road.", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
        Log.d(TAG, "tryBuildRoad() returned: " + false);
        return false;
    }

    /**
     * @param intersection1 IntersectionDrawable at which the player is trying to build a settlement upon.
     * @return If the building location chosen is valid, and if the action was carried out.
     */
    private boolean tryBuildSettlement (int intersection1) {
        Log.d(TAG, "tryBuildSettlement() called with: intersection1 = [" + intersection1 + "]");
        // check if valid settlement location
        if (state.getBoard().validBuildingLocation(state.getCurrentPlayerId(), state.isSetupPhase(), intersection1)) {
            Log.d(TAG, "onClick: building location is valid. Sending a BuildSettlementAction to the game.");
            // send build settlement action to the game
            Log.d(TAG, "tryBuildSettlement: Sending a CatanBuildSettlementAction to the game.");
            game.sendAction(new CatanBuildSettlementAction(this, state.isSetupPhase(), state.getCurrentPlayerId(), intersection1));
            this.buildingsBuiltOnThisTurn.add(1);
            this.selectedIntersections.clear();
            Log.d(TAG, "tryBuildSettlement() returned: " + true);
            return true;
        } else {
            messageTextView.setText(R.string.invalid_settlement_loc);
            Toast toast = Toast.makeText(myActivity.getApplicationContext(), "Invalid settlement location.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            shake(messageTextView);
            Log.d(TAG, "tryBuildSettlement: Returning false.");
            return false;
        }
    }

    /**
     * @param intersection Intersection player is attempting to build a city at.
     * @return If a city was built at the intersection.
     */
    private boolean tryBuildCity (int intersection) {
        Log.d(TAG, "tryBuildCity() called with: intersection = [" + intersection + "]");
        // check if it is the setup phase
        if (state.isSetupPhase()) {
            Log.i(TAG, "tryBuildCity: Cannot built city during setup phase. Returning false.");
            return false;
        }
        // check if the player has enough resources
        if (!state.getCurrentPlayer().hasResourceBundle(City.resourceCost)) {
            messageTextView.setText(R.string.not_enough_for_city);
            Toast toast = Toast.makeText(myActivity.getApplicationContext(), "Not enough resources to build a city.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            shake(messageTextView);
            Log.d(TAG, "tryBuildCity() returned: " + false);
            return false;
        }
        // check if valid location
        if (state.getBoard().validCityLocation(state.getCurrentPlayerId(), intersection)) {
            Log.d(TAG, "onClick: building location is valid. Sending a BuildCityAction to the game.");
            // send CatanBuildCityAction to the game
            game.sendAction(new CatanBuildCityAction(this, state.isSetupPhase(), state.getCurrentPlayerId(), intersection));
            this.buildingsBuiltOnThisTurn.add(2);
            this.selectedIntersections.clear();
            return true;
        }
        messageTextView.setText(R.string.invalid_city_loc);
        Toast toast = Toast.makeText(myActivity.getApplicationContext(), "Invaild city location.", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
        shake(messageTextView);
        return false;
    }

    /**
     * @param hexId Hexagon to try to move the robber to.
     * @return Success.
     */
    private boolean tryMoveRobber (int hexId) {
        // make sure they have a hexagon selected
        if (hexId == -1) {
            messageTextView.setText(R.string.hex_for_robber);
            Toast toast = Toast.makeText(myActivity.getApplicationContext(), "Select an intersection to move the robber.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            //toast.show();
            shake(messageTextView);
            return false;
        }
        // make sure they move the robber to a new hexagon
        if (hexId == state.getBoard().getRobber().getHexagonId()) {
            messageTextView.setText(R.string.new_hex);
            Toast toast = Toast.makeText(myActivity.getApplicationContext(), "Robber must be moved to a new hexagon.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            shake(messageTextView);
            return false;
        }
        // get a list of adjacent intersections to the hexagon
        ArrayList<Integer> intersections = state.getBoard().getHexToIntIdMap().get(hexId);
        for (Integer intersection : intersections) {
            if (state.getBoard().getBuildings()[intersection] != null) {
                if (state.getBoard().getBuildings()[intersection].getOwnerId() != playerNum) {
                    messageTextView.setText(R.string.robber_moved);
                    return true;
                }
            }
        }
        messageTextView.setText(R.string.opp_bldg);
        Toast toast = Toast.makeText(myActivity.getApplicationContext(), "Robber must be moved next to an opponents building.", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
        shake(messageTextView);
        return false;
    }

    /**
     * @param resourceGiving Resource the player wants to give in the trade.
     * @param resourceReceiving Resource the player wants to receive in the trade.
     * @return Trade success.
     */
    private boolean tryTradeWithPort (int resourceGiving, int resourceReceiving) {
        if (resourceGiving < 0) {
            messageTextView.setText(R.string.give_res_not_sel);
            Toast toast = Toast.makeText(myActivity.getApplicationContext(), R.string.give_res_not_sel, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            shake(messageTextView);
            return false;
        }
        if (resourceReceiving < 0) {
            messageTextView.setText(R.string.rec_res_not_sel);
            Toast toast = Toast.makeText(myActivity.getApplicationContext(), R.string.rec_res_not_sel, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            shake(messageTextView);
            return false;
        }
        if (resourceGiving == resourceReceiving) {
            messageTextView.setText(R.string.unique_res_trading);
            Toast toast = Toast.makeText(myActivity.getApplicationContext(), R.string.unique_res_trading, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            shake(messageTextView);
            return false;
        }
        ArrayList<Port> ports = state.getBoard().getPortList();
        Port tradingWith = null;
        for (Port port : ports) {
            if (port.getIntersectionB() == selectedIntersections.get(0) || port.getIntersectionA() == selectedIntersections.get(0))
                tradingWith = port;
        }
        // make sure selected intersection has port access
        if (tradingWith == null) {
            this.messageTextView.setText(R.string.no_port_access);
            Toast toast = Toast.makeText(myActivity.getApplicationContext(), R.string.no_port_access, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            Log.d(TAG, "tryTradeWithPort() returned: " + false);
            return false;
        }
        // make sure a building is selected
        if (!state.getBoard().hasBuilding(selectedIntersections.get(0))) {
            return false;
        }
        // check if player owns selected building
        if (state.getBoard().getBuildings()[selectedIntersections.get(0)].getOwnerId() != state.getCurrentPlayerId()) {
            return false;
        }
        // if trading with a normal port
        if (tradingWith.getResourceId() != -1) {
            // check if player has enough resources
            if (state.getPlayerList().get(state.getCurrentPlayerId()).checkResourceCard(tradingWith.getResourceId(), tradingWith.getTradeRatio())) {
                // send action to the game
                game.sendAction(new CatanTradeWithPortAction(this, tradingWith, resourceReceiving));
                messageTextView.setText(R.string.traded_with_port);
                Toast toast = Toast.makeText(myActivity.getApplicationContext(), R.string.traded_with_port, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                //toast.show();
                toggleGroupVisibilityAllowTapping(tradeGroup);
                return true;
            } else {
                messageTextView.setText(R.string.not_enough_for_trade);
                Toast toast = Toast.makeText(myActivity.getApplicationContext(), R.string.not_enough_for_trade, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
                shake(messageTextView);
                return false;
            }

        } else { // if the player is trading with a mystery port
            // check if the player has enough resources
            if (state.getPlayerList().get(state.getCurrentPlayerId()).checkResourceCard(resourceGiving, tradingWith.getTradeRatio())) {
                // send action to the game
                game.sendAction(new CatanTradeWithCustomPortAction(this, resourceGiving, resourceReceiving));
                messageTextView.setText(R.string.traded_with_port);
                Toast toast = Toast.makeText(myActivity.getApplicationContext(), R.string.traded_with_port, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                //toast.show();
                toggleGroupVisibilityAllowTapping(tradeGroup);
                return true;
            } else {
                messageTextView.setText(R.string.not_enough_for_trade);
                Toast toast = Toast.makeText(myActivity.getApplicationContext(), R.string.not_enough_for_trade, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
                shake(messageTextView);
                return false;
            }
        }
    }

    /**
     * @param resourceGiving Resource to give in the trade with the bank.
     * @param resourceReceiving Resource to receive in the trade with the bank.
     * @return Trade success.
     */
    private boolean tryTradeWithBank (int resourceGiving, int resourceReceiving) {
        Log.d(TAG, "tryTradeWithBank() called with: resourceGiving = [" + resourceGiving + "], resourceReceiving = [" + resourceReceiving + "]");
        if (resourceGiving < 0) {
            messageTextView.setText(R.string.give_res_not_sel);
            Toast toast = Toast.makeText(myActivity.getApplicationContext(), R.string.give_res_not_sel, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            shake(messageTextView);
            return false;
        }
        if (resourceReceiving < 0) {
            messageTextView.setText(R.string.rec_res_not_sel);
            Toast toast = Toast.makeText(myActivity.getApplicationContext(), R.string.rec_res_not_sel, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            shake(messageTextView);
            return false;
        }
        if (resourceGiving == resourceReceiving) {
            messageTextView.setText(R.string.unique_res_trading);
            Toast toast = Toast.makeText(myActivity.getApplicationContext(), R.string.unique_res_trading, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            shake(messageTextView);
            return false;
        }
        // Check if player has 4 or more of the resource they have selected to give to the bank.
        if (state.getPlayerList().get(state.getCurrentPlayerId()).getResourceCards()[resourceGiving] - 4 >= 0) {
            Log.d(TAG, "tryTradeWithBank: sending CatanTradeWithBankAction to the game.");
            game.sendAction(new CatanTradeWithBankAction(this, resourceGiving, resourceReceiving));
            return true;
        }
        Log.d(TAG, "tryTradeWithBank: player " + state.getPlayerList().get(state.getCurrentPlayerId()) + " would have have enough " + resourceGiving + " to complete trade");
        return false;
    }

    /* ---------------------------------------- GUI Methods --------------------------------------*/

    private void updateTextViews () {

        // Check if the Game State is null. If it is return void.
        if (this.state == null) {
            Log.e(TAG, "updateTextViews: state is null. Returning void.");
            return;
        }

        String devCardNames[] = {"Knight Development", "Victory Points Development", "Year of Plenty", "Monopoly", "Road Development"};

        if (!devCards.isEmpty()) {
            devCards.clear();
        }
        for (int i = 0; i < state.getPlayerList().get(this.playerNum).getDevelopmentCards().size(); i++) {
            devCards.add(devCardNames[state.getPlayerList().get(this.playerNum).getDevelopmentCards().get(i)]);
        }

        List<String> spinnerList = new ArrayList<>(devCards);
        if (spinnerList.size() == 0) {
            this.useDevCard.setAlpha(0.5f);
            this.useDevCard.setClickable(false);
        } else {
            this.useDevCard.setAlpha(1f);
            this.useDevCard.setClickable(true);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(myActivity, R.layout.support_simple_spinner_dropdown_item, spinnerList);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        devCardList.setAdapter(adapter);

        // Apply the adapter to the spinner
        // array of dice image ids
        int diceImageIds[] = {R.drawable.dice_1, R.drawable.dice_2, R.drawable.dice_3, R.drawable.dice_4, R.drawable.dice_5, R.drawable.dice_6};

        // set the dice ImageViews to the corresponding dice image of the current dice values
        diceImageLeft.setBackgroundResource(diceImageIds[state.getDice().getDiceValues()[0] - 1]);
        diceImageRight.setBackgroundResource(diceImageIds[state.getDice().getDiceValues()[1] - 1]);

        if (this.state.getRobberPhase()) {

            this.messageTextView.setText(R.string.robber_phase);
            Toast toast = Toast.makeText(myActivity.getApplicationContext(), R.string.robber_phase, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            //toast.show();
            // if it is the setup phase, grey out some buttons and make them un clickable
            this.buildRoadButton.setAlpha(0.5f);
            this.buildRoadButton.setClickable(false);
            this.buildSettlementButton.setAlpha(0.5f);
            this.buildSettlementButton.setClickable(false);
            this.buildCityButton.setAlpha(0.5f);
            this.buildCityButton.setClickable(false);
            this.rollButton.setAlpha(0.5f);
            this.rollButton.setClickable(false);
            this.sidebarOpenDevCardMenuButton.setAlpha(0.5f);
            this.sidebarOpenDevCardMenuButton.setClickable(false);
            this.tradeButton.setAlpha(0.5f);
            this.tradeButton.setClickable(false);
            this.endTurnButton.setAlpha(0.5f);
            this.endTurnButton.setClickable(false);

            if (!state.getRobberPlayerListHasDiscarded()[playerNum]) {
                Log.d(TAG, "updateTextViews: Has not discarded cards");
                robberDiscardGroup.setVisibility(View.VISIBLE);
            } else if (state.getCurrentPlayerId() == playerNum && state.allPlayersHaveDiscarded()) {
                Log.d(TAG, "updateTextViews: Now needs to move Robber");
                robberChooseHexGroup.setVisibility(View.VISIBLE);
            }
        }
        if (this.state.isSetupPhase()) { // IF SETUP PHASE
            this.messageTextView.setText(R.string.setup_phase); // set info message
            Toast toast = Toast.makeText(myActivity.getApplicationContext(), R.string.setup_phase, Toast.LENGTH_SHORT);
            //            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            //            toast.show();

            // get settlement and road count for the current turn
            int settlements = Collections.frequency(this.buildingsBuiltOnThisTurn, 1);
            int roads = Collections.frequency(this.buildingsBuiltOnThisTurn, 0);

            // check if they are done with their setup phase turn
            if (settlements == 1 && roads == 1) {
                // they need to end their turn
                this.endTurnButton.setAlpha(1f);
                this.endTurnButton.setClickable(true);
                this.messageTextView.setText(R.string.setup_phase_complete);
                toast = Toast.makeText(myActivity.getApplicationContext(), R.string.setup_phase_complete, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                //toast.show();
            } else {
                this.endTurnButton.setAlpha(0.5f);
                this.endTurnButton.setClickable(false);
            }
            // check if they have built a settlement but not a road
            if (settlements > roads) {
                // they need to build a road
                this.buildRoadButton.setAlpha(1f);
                this.buildRoadButton.setClickable(true);
                this.buildSettlementButton.setAlpha(0.5f);
                this.buildSettlementButton.setClickable(false);
            } else if (settlements == 1) {
                // they need to end their turn
                this.buildRoadButton.setAlpha(0.5f);
                this.buildRoadButton.setClickable(false);
                this.buildSettlementButton.setAlpha(0.5f);
                this.buildSettlementButton.setClickable(false);
            } else {
                // they need to build a settlement
                this.buildRoadButton.setAlpha(0.5f);
                this.buildRoadButton.setClickable(false);
                this.buildSettlementButton.setAlpha(1f);
                this.buildSettlementButton.setClickable(true);
            }
            // if it is the setup phase, grey out some buttons and make them un clickable
            this.buildCityButton.setAlpha(0.5f);
            this.buildCityButton.setClickable(false);
            this.rollButton.setAlpha(0.5f);
            this.rollButton.setClickable(false);
            this.sidebarOpenDevCardMenuButton.setAlpha(0.5f);
            this.sidebarOpenDevCardMenuButton.setClickable(false);
            this.tradeButton.setAlpha(0.5f);
            this.tradeButton.setClickable(false);

        } else if (!state.isActionPhase()) { // IF NOT THE ACTION PHASE AND NOT THE SETUP PHASE

            if (this.playerNum == state.getCurrentPlayerId()) {
                this.messageTextView.setText(R.string.roll_the_dice);
                Toast toast = Toast.makeText(myActivity.getApplicationContext(), R.string.roll_the_dice, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                //toast.show();
            } else {
                messageTextView.setText(String.format("It is %s's turn.", allPlayerNames[state.getCurrentPlayerId()]));
                Toast toast = Toast.makeText(myActivity.getApplicationContext(), String.format("It is %s's turn.", allPlayerNames[state.getCurrentPlayerId()]), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                //toast.show();
            }

            // set the roll button only as available
            this.rollButton.setAlpha(1f);
            this.rollButton.setClickable(true);

            this.buildRoadButton.setAlpha(0.5f);
            this.buildRoadButton.setClickable(false);

            this.buildSettlementButton.setAlpha(0.5f);
            this.buildSettlementButton.setClickable(false);

            this.buildCityButton.setAlpha(0.5f);
            this.buildCityButton.setClickable(false);

            this.sidebarOpenDevCardMenuButton.setAlpha(0.5f);
            this.sidebarOpenDevCardMenuButton.setClickable(false);

            this.tradeButton.setAlpha(0.5f);
            this.tradeButton.setClickable(false);

            this.endTurnButton.setAlpha(0.5f);
            this.endTurnButton.setClickable(false);

        } else { // ACTION PHASE AND NOT SETUP PHASE
            if (this.playerNum == state.getCurrentPlayerId()) {
                this.messageTextView.setText(R.string.action_phase);
                Toast toast = Toast.makeText(myActivity.getApplicationContext(), R.string.action_phase, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                //toast.show();
            }
            setAllButtonsToVisible();
        }

        //Not
        if (this.playerNum != state.getCurrentPlayerId()) {
            this.rollButton.setAlpha(0.5f);
            this.rollButton.setClickable(false);
            this.buildRoadButton.setAlpha(0.5f);
            this.buildRoadButton.setClickable(false);
            this.buildSettlementButton.setAlpha(0.5f);
            this.buildSettlementButton.setClickable(false);
            this.buildCityButton.setAlpha(0.5f);
            this.buildCityButton.setClickable(false);
            this.sidebarOpenDevCardMenuButton.setAlpha(0.5f);
            this.sidebarOpenDevCardMenuButton.setClickable(false);
            this.tradeButton.setAlpha(0.5f);
            this.tradeButton.setClickable(false);
            this.endTurnButton.setAlpha(0.5f);
            this.endTurnButton.setClickable(false);

            this.sidebarScoreboardButton.setAlpha(1f);
            this.sidebarScoreboardButton.setClickable(true);
            this.sidebarMenuButton.setAlpha(1f);
            this.sidebarMenuButton.setClickable(true);
        }

        if (this.debugMode) setAllButtonsToVisible();

        /* ----- update resource value TextViews ----- */

        int[] resourceCards = this.state.getPlayerList().get(this.playerNum).getResourceCards();
        this.brickValue.setText(String.valueOf(resourceCards[0]));
        this.grainValue.setText(String.valueOf(resourceCards[1]));
        this.lumberValue.setText(String.valueOf(resourceCards[2]));
        this.oreValue.setText(String.valueOf(resourceCards[3]));
        this.woolValue.setText(String.valueOf(resourceCards[4]));

        /* ----- update scoreboard ----- */

        TextView scores[] = {player0Score, player1Score, player2Score, player3Score};

        // set the other players score on the scoreboard to their public scores except for the user which shows their private score
        for (int i = 0; i < scores.length; i++) {
            if (i != this.playerNum)
                scores[i].setText(String.valueOf(state.getPlayerList().get(i).getVictoryPoints()));
            else
                scores[this.playerNum].setText(String.valueOf(state.getPlayerList().get(this.playerNum).getVictoryPointsPrivate() + state.getPlayerList().get(this.playerNum).getVictoryPoints()));
        }

        TextView playerNames[] = {player0Name, player1Name, player2Name, player3Name};

        for (int i = 0; i < playerNames.length; i++) {
            playerNames[i].setText(getAllPlayerNames()[i]);
            if (i == state.getCurrentPlayerId()) {
                playerNames[i].setBackgroundColor(Color.argb(120, 255, 255, 255));
                scores[i].setBackgroundColor(Color.argb(120, 255, 255, 255));
            } else {
                playerNames[i].setBackgroundColor(Color.TRANSPARENT);
                scores[i].setBackgroundColor(Color.TRANSPARENT);
            }
        }

        this.player0Score.setTextColor(HexagonGrid.playerColors[0]);
        this.player1Score.setTextColor(HexagonGrid.playerColors[1]);
        this.player2Score.setTextColor(HexagonGrid.playerColors[2]);
        this.player3Score.setTextColor(HexagonGrid.playerColors[3]);

        this.player0Name.setTextColor(HexagonGrid.playerColors[0]);
        this.player1Name.setTextColor(HexagonGrid.playerColors[1]);
        this.player2Name.setTextColor(HexagonGrid.playerColors[2]);
        this.player3Name.setTextColor(HexagonGrid.playerColors[3]);

        /* ----- update misc. sidebar TextViews ----- */
        this.playerNameSidebar.setText(getAllPlayerNames()[0]);

        // human player score (sidebar menu)
        this.myScore.setText(String.format("VPs: %s", String.valueOf(state.getPlayerList().get(this.playerNum).getVictoryPointsPrivate() + state.getPlayerList().get(this.playerNum).getVictoryPoints())));

        // current turn indicator (sidebar menu)
        this.currentTurnIdTextView.setText(String.valueOf(getAllPlayerNames()[state.getCurrentPlayerId()]));
        this.currentTurnIdTextView.setTextColor(HexagonGrid.playerColors[state.getCurrentPlayerId()]);

        /* -------- animations ----------- */
        this.playerNameSidebar.setTextColor(HexagonGrid.playerColors[this.playerNum]);

        if (this.state.getCurrentPlayerId() == this.playerNum && !this.state.isActionPhase())
            this.playerNameSidebar = (TextView) blinkAnimation(this.playerNameSidebar);

    } // updateTextViews END

    /**
     * callback method when we get a message (e.g., from the game)
     *
     * @param info the message
     */
    @Override
    public void receiveInfo (GameInfo info) {
        if (debugMode)
            Log.d(TAG, "receiveInfo() called with: info: \n" + info.toString() + "----------------------------");

        if (this.boardSurfaceView == null) {
            Log.e(TAG, "receiveInfo: boardSurfaceView is null.");
            return;
        }

        if (info instanceof CatanGameState) {
            // set resource count TextViews to the players resource inventory amounts
            Log.i(TAG, "receiveInfo: player list: " + ((CatanGameState) info).getPlayerList());

            this.state = (CatanGameState) info;

            if (state.isRobberPhase()) {
                messageTextView.setText(R.string.robber_phase);
                Toast toast = Toast.makeText(myActivity.getApplicationContext(), R.string.robber_phase, Toast.LENGTH_SHORT);

                if (!state.checkPlayerResources(playerNum) && !state.getRobberPlayerListHasDiscarded()[playerNum])
                    game.sendAction(new CatanRobberDiscardAction(this, playerNum, new int[]{0, 0, 0, 0, 0}));
            }

            updateTextViews();
            drawGraphics();


        } else if (info instanceof NotYourTurnInfo) {
            Log.i(TAG, "receiveInfo: Player tried to make action but it is not their turn.");
        } else if (info instanceof IllegalMoveInfo) {
            Log.i(TAG, "receiveInfo: Illegal move info received.");
        } else {
            Log.e(TAG, "receiveInfo: Received instanceof not anything we know. Returning void.");
        }
    }//receiveInfo

    /**
     * callback method--our game has been chosen/re-chosen to be the GUI,
     * called from the GUI thread
     *
     * @param activity the activity under which we are running
     */
    @SuppressLint("ClickableViewAccessibility")
    public void setAsGui (GameMainActivity activity) {
        Log.d(TAG, "setAsGui() called with: activity = [" + activity + "]");

        myActivity = activity; // remember the activity
        activity.setContentView(R.layout.catan_main_activity); // Load the layout resource for our GUI
        messageTextView = activity.findViewById(R.id.textview_game_message);


        /* ---------- Surface View for drawing the graphics ----------- */

        this.boardSurfaceView = activity.findViewById(R.id.board); // boardSurfaceView board is the custom SurfaceView
        this.boardSurfaceView.setOnClickListener(clickListener);
        this.boardSurfaceView.setOnTouchListener(touchListener);

        /* ----------------------------------- SIDEBAR ------------------------------------------ *

        /* ------------------------ Building Buttons -----------------------------------------*/
        buildRoadButton = activity.findViewById(R.id.sidebar_button_road);
        buildRoadButton.setOnClickListener(this);

        buildSettlementButton = activity.findViewById(R.id.sidebar_button_settlement);
        buildSettlementButton.setOnClickListener(this);

        buildCityButton = activity.findViewById(R.id.sidebar_button_city);
        buildCityButton.setOnClickListener(this);

        // action buttons
        sidebarOpenDevCardMenuButton = activity.findViewById(R.id.sidebar_button_devcards); // buy dev card
        sidebarOpenDevCardMenuButton.setOnClickListener(this);

        tradeButton = activity.findViewById(R.id.sidebar_button_trade); // trade
        tradeButton.setOnClickListener(this);

        /*--------------------Robber Buttons and Groups------------------------*/

        robberBrickPlus = activity.findViewById(R.id.robber_discard_brickAddImg);
        robberBrickMinus = activity.findViewById(R.id.robber_discard_brickMinusImg);
        robberLumberPlus = activity.findViewById(R.id.robber_discard_lumberAddImg);
        robberLumberMinus = activity.findViewById(R.id.robber_discard_lumberMinusImg);
        robberGrainPlus = activity.findViewById(R.id.robber_discard_grainAddImg);
        robberGrainMinus = activity.findViewById(R.id.robber_discard_grainMinusImg);
        robberOrePlus = activity.findViewById(R.id.robber_discard_oreAddImg);
        robberOreMinus = activity.findViewById(R.id.robber_discard_oreMinusImg);
        robberWoolPlus = activity.findViewById(R.id.robber_discard_woolAddImg);
        robberWoolMinus = activity.findViewById(R.id.robber_discard_woolMinusImg);
        robberDiscardMessage = activity.findViewById(R.id.robber_discard_selectMoreResources);
        robberDiscardGroup = activity.findViewById(R.id.robber_discard_group);
        robberConfirmDiscard = activity.findViewById(R.id.robber_discard_confirm);

        robberBrickAmount = activity.findViewById(R.id.robber_discard_brickAmount);
        robberLumberAmount = activity.findViewById(R.id.robber_discard_lumberAmount);
        robberGrainAmount = activity.findViewById(R.id.robber_discard_grainAmount);
        robberOreAmount = activity.findViewById(R.id.robber_discard_oreAmount);
        robberWoolAmount = activity.findViewById(R.id.robber_discard_woolAmount);

        robberConfirmHex = activity.findViewById(R.id.robber_choosehex_confirm);
        robberHexMessage = activity.findViewById(R.id.robber_choosehex_message);
        robberHexMessage.setText(R.string.choose_robber_tile);
        robberChooseHexGroup = activity.findViewById(R.id.robber_choosehex_menu);

        robberBrickPlus.setOnClickListener(this);
        robberBrickMinus.setOnClickListener(this);
        robberLumberPlus.setOnClickListener(this);
        robberLumberMinus.setOnClickListener(this);
        robberGrainPlus.setOnClickListener(this);
        robberGrainMinus.setOnClickListener(this);
        robberOrePlus.setOnClickListener(this);
        robberOreMinus.setOnClickListener(this);
        robberWoolPlus.setOnClickListener(this);
        robberWoolMinus.setOnClickListener(this);

        robberConfirmDiscard.setOnClickListener(this);
        robberConfirmHex.setOnClickListener(this);

        rollButton = activity.findViewById(R.id.sidebar_button_roll); // Roll button
        rollButton.setOnClickListener(this);
        endTurnButton = activity.findViewById(R.id.sidebar_button_endturn); // End Turn button
        endTurnButton.setOnClickListener(this);
        diceImageLeft = activity.findViewById(R.id.diceImageLeft);  //dice roll images
        diceImageRight = activity.findViewById(R.id.diceImageRight);
        /* ---------- Sidebar resource values ---------- */

        this.oreValue = activity.findViewById(R.id.sidebar_value_ore);
        this.grainValue = activity.findViewById(R.id.sidebar_value_grain);
        this.lumberValue = activity.findViewById(R.id.sidebar_value_lumber);
        this.woolValue = activity.findViewById(R.id.sidebar_value_wool);
        this.brickValue = activity.findViewById(R.id.sidebar_value_brick);

        /* ---------- misc sidebar buttons and text views ---------- */

        this.sidebarMenuButton = activity.findViewById(R.id.sidebar_button_menu);
        this.sidebarMenuButton.setOnClickListener(this);
        this.buildingCosts = activity.findViewById(R.id.building_costs);

        this.sidebarScoreboardButton = activity.findViewById(R.id.sidebar_button_score);
        this.sidebarScoreboardButton.setOnClickListener(this);

        this.myScore = activity.findViewById(R.id.sidebar_heading_vp);
        this.currentTurnIdTextView = activity.findViewById(R.id.sidebar_heading_current_turn);
        this.playerNameSidebar = activity.findViewById(R.id.sidebar_heading_playername);

        /* ------------ DEV CARD SPINNER ----------------- */

        devcard_text_info = activity.findViewById(R.id.development_Card_Info);
        devCardList = activity.findViewById(R.id.development_Card_Spinner); // DEV CARD SPINNER

        devCardList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected (AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String devCardNames[] = {"Knight Development", "Victory Points Development", "Year of Plenty", "Monopoly", "Road Development"};

                int devCardId = -1;
                for (int i = 0; i < devCardNames.length; i++) {
                    if (devCardNames[i].equals(devCards.get(position))) devCardId = i;
                }
                switch (devCardId) {
                    case 0:
                        devcard_text_info.setText(R.string.knight_info);
                        break;
                    case 1:
                        devcard_text_info.setText(R.string.victory_point_info);
                        break;
                    case 2:
                        devcard_text_info.setText(R.string.year_of_plenty_name);
                        break;
                    case 3:
                        devcard_text_info.setText(R.string.monopoly_info);
                        break;
                    case 4:
                        devcard_text_info.setText(R.string.road_building_info);
                        break;
                }
            }

            @Override
            public void onNothingSelected (AdapterView<?> parentView) {
                devcard_text_name.setText(R.string.knight_name);
                devcard_text_info.setText(R.string.knight_info);
            }
        });

        scoreBoardGroup = activity.findViewById(R.id.group_scoreboard);

        this.player0Score = activity.findViewById(R.id.Player1_Score); // scores
        this.player1Score = activity.findViewById(R.id.Player2_Score);
        this.player2Score = activity.findViewById(R.id.Player3_Score);
        this.player3Score = activity.findViewById(R.id.Player4_Score);
        this.player0Name = activity.findViewById(R.id.Player1_Name); // names
        this.player1Name = activity.findViewById(R.id.Player2_Name);
        this.player2Name = activity.findViewById(R.id.Player3_Name);
        this.player3Name = activity.findViewById(R.id.Player4_Name);

        this.roadTrophyImages[0] = activity.findViewById(R.id.longest_road_player0);
        this.roadTrophyImages[0] = activity.findViewById(R.id.longest_road_player1);
        this.roadTrophyImages[0] = activity.findViewById(R.id.longest_road_player2);
        this.roadTrophyImages[0] = activity.findViewById(R.id.longest_road_player3);



        /* -------------------------------------- MENUS ---------------------------------------- */

        /* ------------ Development Card Menu ------------- */

        developmentGroup = activity.findViewById(R.id.group_development_card_menu); // dev card menu GROUP

        useDevCard = activity.findViewById(R.id.use_Card); // use dev card
        useDevCard.setOnClickListener(this);
        buildDevCard = activity.findViewById(R.id.build_devCard); // build dev card
        buildDevCard.setOnClickListener(this);

        /* ---------------- Trade Menu -------------------- */

        tradeGroup = activity.findViewById(R.id.group_trade_menu); // trade menu GROUP
        // confirm and cancel trade buttons
        button_trade_menu_confirm = activity.findViewById(R.id.button_trade_menu_confirm);
        button_trade_menu_confirm.setOnClickListener(this);
        button_trade_menu_cancel = activity.findViewById(R.id.button_trade_menu_cancel);
        button_trade_menu_cancel.setOnClickListener(this);
        //Trade Menu Background - Receive
        brickSelectionBoxReceive = activity.findViewById(R.id.brickSelectionBoxReceive);
        grainSelectionBoxReceive = activity.findViewById(R.id.grainSelectionBoxReceive);
        lumberSelectionBoxReceive = activity.findViewById(R.id.lumberSelectionBoxReceive);
        oreSelectionBoxReceive = activity.findViewById(R.id.oreSelectionBoxReceive);
        woolSelectionBoxReceive = activity.findViewById(R.id.woolSelectionBoxReceive);
        //Trade Menu Background - Give
        brickSelectionBoxGive = activity.findViewById(R.id.brickSelectionBoxGive);
        grainSelectionBoxGive = activity.findViewById(R.id.grainSelectiomBoxGive);
        lumberSelectionBoxGive = activity.findViewById(R.id.lumberSelectionBoxGive);
        oreSelectionBoxGive = activity.findViewById(R.id.oreSelectionBoxGive);
        woolSelectionBoxGive = activity.findViewById(R.id.woolSelectionBoxGive);
        //Trade Menu - Receive
        image_trade_menu_give_brick = activity.findViewById(R.id.image_trade_menu_give_brick);
        image_trade_menu_give_brick.setOnClickListener(this);
        image_trade_menu_give_grain = activity.findViewById(R.id.image_trade_menu_give_grain);
        image_trade_menu_give_grain.setOnClickListener(this);
        image_trade_menu_give_lumber = activity.findViewById(R.id.image_trade_menu_give_lumber);
        image_trade_menu_give_lumber.setOnClickListener(this);
        image_trade_menu_give_ore = activity.findViewById(R.id.image_trade_menu_give_ore);
        image_trade_menu_give_ore.setOnClickListener(this);
        image_trade_menu_give_wool = activity.findViewById(R.id.image_trade_menu_give_wool);
        image_trade_menu_give_wool.setOnClickListener(this);
        //Trade Menu - Give
        image_trade_menu_rec_brick = activity.findViewById(R.id.image_trade_menu_rec_brick);
        image_trade_menu_rec_brick.setOnClickListener(this);
        image_trade_menu_rec_grain = activity.findViewById(R.id.image_trade_menu_rec_grain);
        image_trade_menu_rec_grain.setOnClickListener(this);
        image_trade_menu_rec_lumber = activity.findViewById(R.id.image_trade_menu_rec_lumber);
        image_trade_menu_rec_lumber.setOnClickListener(this);
        image_trade_menu_rec_ore = activity.findViewById(R.id.image_trade_menu_rec_ore);
        image_trade_menu_rec_ore.setOnClickListener(this);
        image_trade_menu_rec_wool = activity.findViewById(R.id.image_trade_menu_rec_wool);
        image_trade_menu_rec_wool.setOnClickListener(this);

        /*--------------------Robber Buttons and Groups------------------------*/

        robberDiscardGroup = activity.findViewById(R.id.robber_discard_group);

        robberBrickPlus = activity.findViewById(R.id.robber_discard_brickAddImg);
        robberBrickMinus = activity.findViewById(R.id.robber_discard_brickMinusImg);
        robberLumberPlus = activity.findViewById(R.id.robber_discard_lumberAddImg);
        robberLumberMinus = activity.findViewById(R.id.robber_discard_lumberMinusImg);
        robberGrainPlus = activity.findViewById(R.id.robber_discard_grainAddImg);
        robberGrainMinus = activity.findViewById(R.id.robber_discard_grainMinusImg);
        robberOrePlus = activity.findViewById(R.id.robber_discard_oreAddImg);
        robberOreMinus = activity.findViewById(R.id.robber_discard_oreMinusImg);
        robberWoolPlus = activity.findViewById(R.id.robber_discard_woolAddImg);
        robberWoolMinus = activity.findViewById(R.id.robber_discard_woolMinusImg);

        robberBrickPlus.setOnClickListener(this);
        robberBrickMinus.setOnClickListener(this);
        robberLumberPlus.setOnClickListener(this);
        robberLumberMinus.setOnClickListener(this);
        robberGrainPlus.setOnClickListener(this);
        robberGrainMinus.setOnClickListener(this);
        robberOrePlus.setOnClickListener(this);
        robberOreMinus.setOnClickListener(this);
        robberWoolPlus.setOnClickListener(this);
        robberWoolMinus.setOnClickListener(this);

        List<String> spinnerList = new ArrayList<>(devCards);
        devCardList.setAdapter(new ArrayAdapter<>(activity, R.layout.support_simple_spinner_dropdown_item, spinnerList));
        messageTextView.setTextColor(Color.WHITE);

        /*--------------------------Monopoly---------------------------------*/

        pickResourceGroup = activity.findViewById(R.id.group_pickResourceMenu);
        pickResourceGroup.setOnClickListener(this);

        monopolyBrickIcon = activity.findViewById(R.id.pickResMenu_brickIcon);
        monopolyBrickIcon.setOnClickListener(this);
        monopolyGrainIcon = activity.findViewById(R.id.pickResMenu_grainIcon);
        monopolyGrainIcon.setOnClickListener(this);
        monopolyLumberIcon = activity.findViewById(R.id.pickResMenu_lumberIcon);
        monopolyLumberIcon.setOnClickListener(this);
        monopolyOreIcon = activity.findViewById(R.id.pickResMenu_oreIcon);
        monopolyOreIcon.setOnClickListener(this);
        monopolyWoolIcon = activity.findViewById(R.id.pickResMenu_woolIcon);
        monopolyWoolIcon.setOnClickListener(this);
        monopolyBrickSelectionBox = activity.findViewById(R.id.pickResMenu_brickSelectionBox);
        monopolyGrainSelcionBox = activity.findViewById(R.id.pickResMenu_grainSelectionBox);
        monopolyLumberSelectionBox = activity.findViewById(R.id.pickResMenu_lumberSelectionBox);
        monopolyOreSelectionBox = activity.findViewById(R.id.pickResMenu_oreSelectionBox);
        monopolyWoolSelectionBox = activity.findViewById(R.id.pickResMenu_woolSelectionBox);

        monopolyConfirm = activity.findViewById(R.id.pickResMenu_ConfirmButton);
        monopolyConfirm.setOnClickListener(this);

        /*-----------------------Torphies---------------------------------------*/
        largestArmyPlayer0 = activity.findViewById(R.id.largest_army_player0);
        largestArmyPlayer1 = activity.findViewById(R.id.largest_army_player1);
        largestArmyPlayer2 = activity.findViewById(R.id.largest_army_player2);
        largestArmyPlayer3 = activity.findViewById(R.id.largest_army_player3);

        longestRoadPlayer0 = activity.findViewById(R.id.longest_road_player0);
        longestRoadPlayer1 = activity.findViewById(R.id.longest_road_player1);
        longestRoadPlayer2 = activity.findViewById(R.id.longest_road_player2);
        longestRoadPlayer3 = activity.findViewById(R.id.longest_road_player3);

        // if we have state update the GUI based on the state
        if (this.state != null) receiveInfo(state);
    }// setAsGui() END

    /**
     *
     */
    private void drawGraphics () {
        Log.d(TAG, "drawGraphics() called");

        if (state == null) {
            Log.d(TAG, "drawGraphics: state is null");
            return;
        }

        Canvas canvas = new Canvas();
        // boardSurfaceView.createHexagons(this.state.getBoard());

        int height = boardSurfaceView.getHeight();
        int width = boardSurfaceView.getWidth();

        Log.i(TAG, "drawGraphics: boardSurfaceView height: " + height + " width: " + width);

        this.boardSurfaceView.setGrid(new HexagonGrid(myActivity.getApplicationContext(), state.getBoard(), 80, 185, 175, 20, this.debugMode));
        this.boardSurfaceView.draw(canvas);

        boardSurfaceView.invalidate();
    } // drawGraphics END

    /**
     * Reset the image to gone incase the trophy switched players
     * Sets the visibilty of the image view to visible when the player on the trophy
     *
     * @param playerNum - player who hold trophu
     */
    public void showLargestArmyTrophy (int playerNum) {
        Log.d(TAG, "showLargestArmyTrophy() called with: playerNum = [" + playerNum + "]");

        if (playerNum < 0) {
            Log.w(TAG, "showLongestRoadTrophy: no player has the largest army trophy");
            return;
        }

        ImageView[] largestArmyTrophies = {largestArmyPlayer0, largestArmyPlayer1, largestArmyPlayer2, largestArmyPlayer3};

        for (int i = 0; i < 4; i++) {
            largestArmyTrophies[i].setVisibility(View.GONE);

        }
        largestArmyTrophies[playerNum].setVisibility(View.VISIBLE);
    }

    /**
     * Reset the image to gone incase the trophy switched players
     * Sets the visibilty of the image view to visible when the player on the trophy
     *
     * @param playerNum - player who hold trophu
     */
    public void showLongestRoadTrophy (int playerNum) {
        Log.d(TAG, "showLongestRoadTrophy() called with: playerNum = [" + playerNum + "]");

        if (playerNum < 0) {
            Log.w(TAG, "showLongestRoadTrophy: no player has the longest road trophy");
            return;
        }

        ImageView[] longestRoadTrophies = {longestRoadPlayer0, longestRoadPlayer1, longestRoadPlayer2, longestRoadPlayer3};

        for (int i = 0; i < 4; i++) {
            longestRoadTrophies[i].setVisibility(View.GONE);
        }

        longestRoadTrophies[playerNum].setVisibility(View.VISIBLE);
    }


    /**
     * @param message Game over message.
     */
    protected void gameIsOver (String message) {
        for (int i = 0; i < state.getPlayerList().size(); i++) {
            int add = (i == state.getCurrentLongestRoadPlayerId())? 2:0;
            if (this.state.getPlayerList().get(i).getVictoryPointsPrivate() + add > 9) {
                super.gameIsOver(getAllPlayerNames()[i] + " wins!");
            }
        }
    } // gameIsOver END

    /**
     *
     */
    protected void initAfterReady () {
        Log.e(TAG, "initAfterReady() called");
    }

    /**
     * Returns the GUI's top view object
     *
     * @return the top object in the GUI's view hierarchy
     */
    public View getTopView () {
        return myActivity.findViewById(R.id.top_gui_layout);
    }

    /**
     * Toggles the visibility of a group.
     *
     * @param group Group to toggle visibility.
     */
    private void toggleGroupVisibilityAllowTapping (Group group) {
        if (group.getVisibility() == View.GONE) group.setVisibility(View.VISIBLE);
        else group.setVisibility(View.GONE);
    }

    /**
     * Toggles the visibility of a group.
     *
     * @param group Group to toggle visibility.
     */
    private void toggleGroupVisibility (Group group) {
        if (group.getVisibility() == View.GONE) {
            this.isMenuOpen = true;
            group.setVisibility(View.VISIBLE);
        } else {
            this.isMenuOpen = false;
            group.setVisibility(View.GONE);
        }
    }

    /**
     * @param view View to toggle.
     */
    private void toggleViewVisibility (View view) {
        if (view.getVisibility() == View.GONE) view.setVisibility(View.VISIBLE);
        else view.setVisibility(View.GONE);
    }

    /**
     * Sets all buttons to visible and clickable.
     */
    private void setAllButtonsToVisible () {
        this.buildRoadButton.setAlpha(1f);
        this.buildRoadButton.setClickable(true);
        this.buildSettlementButton.setAlpha(1f);
        this.buildSettlementButton.setClickable(true);
        this.buildCityButton.setAlpha(1f);
        this.buildCityButton.setClickable(true);
        this.endTurnButton.setAlpha(1f);
        this.endTurnButton.setClickable(true);
        this.sidebarOpenDevCardMenuButton.setAlpha(1f);
        this.sidebarOpenDevCardMenuButton.setClickable(true);
        this.tradeButton.setAlpha(1f);
        this.tradeButton.setClickable(true);
        this.buildSettlementButton.setAlpha(1f);
        this.buildSettlementButton.setClickable(true);
        this.buildRoadButton.setAlpha(1f);
        this.buildRoadButton.setClickable(true);
        this.endTurnButton.setAlpha(1f);
        this.endTurnButton.setClickable(true);
        this.rollButton.setAlpha(1f);
        this.rollButton.setClickable(true);
    }

    private void hideAllMenusAtEndOfTurn () {
        developmentGroup.setVisibility(View.GONE);
        tradeGroup.setVisibility(View.GONE);
    }

    /**
     * Make a View Blink for a desired duration
     *
     * @param view View to be animated.
     * @return returns The View with animation properties on it.
     */
    private static View blinkAnimation (View view) {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(300);
        anim.setStartOffset(100);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(3);
        view.startAnimation(anim);
        return view;
    }

    /**
     * @param v View to make shake.
     */
    private void shake (TextView v) {
        Animation shake = AnimationUtils.loadAnimation(myActivity.getApplicationContext(), R.anim.shake_anim);
        v.setTextColor(Color.RED);
        v.startAnimation(shake);
        v.startAnimation(shake);
    }

    /**
     * @return names of all the players in the game
     */
    private String[] getAllPlayerNames () {
        return super.allPlayerNames;
    }

}// class CatanHumanPlayer END

