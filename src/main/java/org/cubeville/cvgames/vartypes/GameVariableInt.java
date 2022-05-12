package org.cubeville.cvgames.vartypes;

import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class GameVariableInt extends GameVariable {

	private Integer number;

	@Override
	public void setItem(Player player, String input, String arenaName) throws Error {
		number = Integer.valueOf(input);
	}

	@Override
	public void setItem(@Nullable String string, String arenaName) {
		if (string == null) number = null;
		else number = Integer.valueOf(string);
	}

	@Override
	public String displayString() {
		return "Integer";
	}

	@Override
	public Integer getItem() {
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
