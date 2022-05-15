package org.cubeville.cvgames.vartypes;

import org.bukkit.entity.Player;

import javax.annotation.Nullable;

import static org.cubeville.cvgames.CVGames.getInstance;

public class GameVariableLong extends GameVariable {

    private Long number;

    @Override
    public void setItem(Player player, String input, String arenaName) throws Error {
        number = Long.valueOf(input);
    }

    @Override
    public void setItem(@Nullable Object object, String arenaName) {
        if (!(object instanceof Long)) number = null;
        else number = (Long) object;
    }

    @Override
    public String typeString() {
        return "Decimal";
    }

    @Override
    public Long getItem() {
        return number;
    }

    @Override
    public String itemString() {
        return number.toString();
    }

    @Override
    public boolean isValid() {
        return number != null;
    }

    @Override
    public Long getFromPath(String path) {
        return getInstance().getConfig().getLong(path);
    }
}
