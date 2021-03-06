package edu.up.cs.androidcatan.catan.actions;

import android.util.Log;

import java.io.Serializable;

import edu.up.cs.androidcatan.catan.Player;
import edu.up.cs.androidcatan.game.GamePlayer;
import edu.up.cs.androidcatan.game.actionMsg.GameAction;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * https://github.com/alexweininger/android-catan
 **/

public class CatanBuildRoadAction extends GameAction implements Serializable {
    private static final String TAG = "CatanBuildRoadAction";
    private static final long serialVersionUID = 6154724297447188137L;
    private int intersectionAId, intersectionBid, ownerId;
    private boolean isSetupPhase;

    /**
     * CatanBuildAcation constructor
     *
     * @param player          the player calling the action
     * @param isSetupPhase    true or false for if its the setup phase
     * @param ownerId         the id of the owner
     * @param intersectionAId the first intersection of the road
     * @param intersectionBid the second intersection of the road
     */
    public CatanBuildRoadAction(GamePlayer player, boolean isSetupPhase, int ownerId, int intersectionAId, int intersectionBid) {
        super(player);
        if (player instanceof Player) {
            Log.e(TAG, "CatanBuildRoadAction() Runtime error. ", new Exception("Fatal Error: You're dumb. Need GamePlayer object, received Player object."));
        }
        Log.d(TAG, "CatanBuildRoadAction() called with: player = [" + player + "], isSetupPhase = [" + isSetupPhase + "], ownerId = [" + ownerId + "], intersectionAId = [" + intersectionAId + "], intersectionBid = [" + intersectionBid + "]");
        this.intersectionAId = intersectionAId;
        this.intersectionBid = intersectionBid;
        this.ownerId = ownerId;
        this.isSetupPhase = isSetupPhase;
    }

    // getters and setters

    public int getIntAId() {
        return intersectionAId;
    }

    public void setIntersectionAId(int intersectionAId) {
        this.intersectionAId = intersectionAId;
    }

    public int getIntBid() {
        return intersectionBid;
    }

    public void setIntersectionBid(int intersectionBid) {
        this.intersectionBid = intersectionBid;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public boolean isSetupPhase() {
        return isSetupPhase;
    }

    public void setSetupPhase(boolean setupPhase) {
        isSetupPhase = setupPhase;
    }
}
