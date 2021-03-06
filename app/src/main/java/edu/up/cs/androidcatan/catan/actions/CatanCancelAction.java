package edu.up.cs.androidcatan.catan.actions;

import java.io.Serializable;

import edu.up.cs.androidcatan.game.GamePlayer;
import edu.up.cs.androidcatan.game.actionMsg.GameAction;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * https://github.com/alexweininger/android-catan
 **/

public class CatanCancelAction extends GameAction implements Serializable {
    private static final long serialVersionUID = 3385017032374067088L;

    /**
     * CaatanCancelAcation constructor
     *
     * @param player player calling the action
     */
    public CatanCancelAction(GamePlayer player) {
        super(player);
    }
}
