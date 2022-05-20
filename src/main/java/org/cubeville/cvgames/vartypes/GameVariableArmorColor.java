package org.cubeville.cvgames.vartypes;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.awt.*;

public class GameVariableArmorColor extends GameVariable {

    private Color color;

    @Override
    public void setItem(Player player, String input, String arenaName) throws Error {
        Color newColor = colorFromString(input);
        if (newColor == null) {
            throw new Error("Error setting color!");
        }
        color = newColor;
    }

    private Color colorFromString(String input) {
        if (input == null || input.length() == 0) { return null; }
        if (input.matches("^#([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$")) {
            return Color.decode(input);
        } else {
            return null;
        }
    }

    private String colorString() {
        String buf = Integer.toHexString(color.getRGB());
        return "#" + buf.substring(buf.length()-6);
    }

    @Override
    public void setItem(@Nullable Object string, String arenaName) {
        if (!(string instanceof String)) color = null;
        else color = colorFromString((String) string);
    }

    @Override
    public String typeString() {
        return "Armor Color";
    }

    @Override
    public org.bukkit.Color getItem() {
        return org.bukkit.Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
    }

    @Override
    public String itemString() {
        return color == null ? null : colorString();
    }

    @Override
    public boolean isValid() {
        return color != null;
    }

    @Override
    public TextComponent displayString() {
        if (color == null) return new TextComponent("null");
        TextComponent tc = new TextComponent(colorString());
        tc.setColor(ChatColor.of(color));
        return tc;
    }
}
