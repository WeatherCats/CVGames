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
	public void setItem(@Nullable String string, String arenaName) {
		if (string == null) color = null;
		else color = ChatColor.getByChar(string);
	}

	@Override
	public String displayString() {
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
