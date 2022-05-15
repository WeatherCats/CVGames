package org.cubeville.cvgames.vartypes;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class GameVariableChatColor extends GameVariable {

	private ChatColor color;

	@Override
	public void setItem(Player player, String input, String arenaName) throws Error {
		if (ChatColor.getByChar(input) == null) { throw new Error("That is not a valid color."); }
		color = ChatColor.getByChar(input);
	}

	@Override
	public void setItem(@Nullable Object string, String arenaName) {
		if (!(string instanceof String)) color = null;
		else color = ChatColor.getByChar((String) string);
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
		return color == null ? null : String.valueOf(color.getChar());
	}

	@Override
	public boolean isValid() {
		return color != null;
	}
}
