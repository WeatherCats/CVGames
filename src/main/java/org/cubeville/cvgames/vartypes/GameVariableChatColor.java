package org.cubeville.cvgames.vartypes;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class GameVariableChatColor extends GameVariable {

	private ChatColor color;

	public GameVariableChatColor() {}

	public GameVariableChatColor(String description) {
		super(description);
	}

	@Override
	public void setItem(Player player, String input, String arenaName) throws Error {
		ChatColor newColor = colorFromString(input);
		if (newColor == null) {
			throw new Error("Error setting color!");
		}
		color = newColor;
	}

	private ChatColor colorFromString(String input) {
		if (input == null || input.length() == 0) { return null; }
		if (input.length() == 1) {
			return ChatColor.getByChar(input.toLowerCase().charAt(0));
		} else {
			return ChatColor.of(input);
		}
	}

	@Override
	public void setItem(@Nullable Object string, String arenaName) {
		if (!(string instanceof String)) color = null;
		else color = colorFromString((String) string);
	}

	@Override
	public String typeString() {
		return "Chat Color";
	}

	@Override
	public ChatColor getItem() {
		return color;
	}

	@Override
	public String itemString() {
		return color == null ? null : color.getName();
	}

	@Override
	public boolean isValid() {
		return color != null;
	}

	@Override
	public TextComponent displayString(String arenaName) {
		if (color == null) return new TextComponent("null");
		TextComponent tc = new TextComponent(color.getName());
		tc.setColor(color);
		return tc;
	}

}
