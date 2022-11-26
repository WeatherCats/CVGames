package org.cubeville.cvgames.vartypes;

import org.bukkit.entity.Player;

import javax.annotation.Nullable;

import static org.cubeville.cvgames.CVGames.getInstance;

public class GameVariableFlag extends GameVariable {

    Boolean bool;

    public GameVariableFlag() {}

    public GameVariableFlag(String description) {
        super(description);
    }

    @Override
    public void setItem(Player player, String input, String arenaName) throws Error {
        if (input.equalsIgnoreCase("true")) {
            bool = true;
        } else if (input.equalsIgnoreCase("false")) {
            bool = false;
        } else {
            throw new Error("The value of a flag must either be true or false");
        }
    }

    @Override
    public Boolean getItem() {
        return bool;
    }

    @Override
    public Object itemString() {
        return bool.toString();
    }

    @Override
    public boolean isValid() {
        return bool != null;
    }

    @Override
    public void setItem(@Nullable Object item, String arenaName) {
        if (!(item instanceof Boolean)) bool = null;
        else bool = (Boolean) item;
    }

    @Override
    public Boolean getFromPath(String path) {
        return getInstance().getConfig().getBoolean(path);
    }

    @Override
    public String typeString() {
        return "Flag";
    }
}
