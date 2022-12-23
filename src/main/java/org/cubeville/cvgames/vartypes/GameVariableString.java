package org.cubeville.cvgames.vartypes;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.cubeville.cvgames.utils.GameUtils;
import org.w3c.dom.Text;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.regex.Matcher;

import static org.cubeville.cvgames.utils.GameUtils.createColorTextComponent;

public class GameVariableString extends GameVariable {

    String item;

    public GameVariableString() {}

    public GameVariableString(String description) {
        super(description);
    }

    @Override
    public void setItem(Player player, String input, String arenaName) throws Error {
        item = input;
    }

    @Override
    public String typeString() {
        return "String";
    }

    @Override
    public void setItem(@Nullable Object object, String arenaName) {
        if (!(object instanceof String)) {
            item = null;
        } else {
            item = (String) object;
        }
    }

    @Override
    public Object getItem() {
        return GameUtils.createColorString(item);
    }

    @Override
    public String itemString() {
        return item;
    }

    @Override
    public TextComponent displayString(String arenaName) {
        return item == null ? new TextComponent("null") : createColorTextComponent(item);
    }

    @Override
    public boolean isValid() {
        return item != null;
    }

}
