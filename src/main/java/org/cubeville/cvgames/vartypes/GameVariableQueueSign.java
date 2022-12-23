package org.cubeville.cvgames.vartypes;

import org.bukkit.entity.Player;
import org.cubeville.cvgames.managers.ArenaManager;
import org.cubeville.cvgames.managers.SignManager;
import org.cubeville.cvgames.utils.GameUtils;

import javax.annotation.Nullable;

public class GameVariableQueueSign extends GameVariableSign {

    String gameName;

    public GameVariableQueueSign() {}

    public GameVariableQueueSign(String description) {
        super(description);
    }

    @Override
    public void setItem(Player player, String input, String arenaName)
        throws Error {
        super.setItem(player, input, arenaName);
        if (ArenaManager.getArena(arenaName).getGame(input) == null) {
            throw new Error("Arena " + arenaName + " does not contain game " + input);
        }
        gameName = input;
        SignManager.addSign(sign, arenaName, input);
    }

    @Override
    public void setItem(@Nullable Object object, String arenaName) {
        assert object instanceof String;
        String[] queueSignSplit = ((String) object).split(" ~ ");
        super.setItem(queueSignSplit[0], arenaName);
        if (queueSignSplit.length == 1) {
            // just throw a game in there and fix the config ig
            gameName = ArenaManager.getArena(arenaName).getFirstGame().getId();
        } else {
            gameName = queueSignSplit[1];
        }
        SignManager.addSign(sign, arenaName, gameName);
    }

    @Override
    public String itemString() {
        if (sign == null) { return "<INVALID SIGN>"; }
        return GameUtils.blockLocToString(sign.getLocation()) + " ~ " + gameName;
    }

    @Override
    public void clearItem () {
        if (sign == null) return;
        SignManager.deleteSign(sign.getLocation());
    }
}
