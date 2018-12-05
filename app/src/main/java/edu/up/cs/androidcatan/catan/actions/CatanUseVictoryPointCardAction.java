package edu.up.cs.androidcatan.catan.actions;

import android.util.Log;

import java.io.Serializable;

import edu.up.cs.androidcatan.game.GamePlayer;
import edu.up.cs.androidcatan.game.actionMsg.GameAction;

public class CatanUseVictoryPointCardAction extends GameAction implements Serializable {
    private static final String TAG = "CatanUseVictoryPointCardAction";
    private static final long serialVersionUID = -1682414277459896713L;

    public CatanUseVictoryPointCardAction(GamePlayer player){
        super(player);
        Log.d(TAG, "CatanUseVictoryPointCardAction called");
    }
}
