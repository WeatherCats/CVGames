package org.cubeville.cvgames.vartypes;

import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class GameVariableLong extends GameVariable {

    private Long number;

    @Override
    public void setItem(Player player, String input, String arenaName) throws Error {
        number = Long.valueOf(input);
    }

    @Override
    public void setItem(@Nullable String string, String arenaName) {
        try {
            if (string == null) number = null;
            else number = Long.valueOf(string);
        } catch (NumberFormatException e) {
            throw new Error(string + " is not a decimal number!");
        }
    }

    @Override
    public String displayString() {
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
}
