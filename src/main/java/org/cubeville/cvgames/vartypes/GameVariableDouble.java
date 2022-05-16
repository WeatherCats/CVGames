package org.cubeville.cvgames.vartypes;

import org.bukkit.entity.Player;

import javax.annotation.Nullable;

import static org.cubeville.cvgames.CVGames.getInstance;

public class GameVariableDouble extends GameVariable {

    private Double number;

    @Override
    public void setItem(Player player, String input, String arenaName) throws Error {
        number = Double.parseDouble(input);
    }

    @Override
    public void setItem(@Nullable Object object, String arenaName) {
        if (!(object instanceof Double)) number = null;
        else number = (Double) object;
    }

    @Override
    public String typeString() {
        return "Decimal";
    }

    @Override
    public Double getItem() {
        return number;
    }

    @Override
    public Double itemString() {
        return number == null ? null : number;
    }

    @Override
    public boolean isValid() {
        return number != null;
    }

    @Override
    public Double getFromPath(String path) {
        return getInstance().getConfig().getDouble(path);
    }
}
