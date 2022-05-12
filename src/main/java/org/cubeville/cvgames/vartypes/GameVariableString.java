package org.cubeville.cvgames.vartypes;

import org.bukkit.entity.Player;
import javax.annotation.Nullable;

public class GameVariableString extends GameVariable {

	String item;

	@Override
	public void setItem(Player player, String input, String arenaName) throws Error {
		item = input.replaceAll("&", "§");
	}

	@Override
	public String displayString() {
		return "String";
	}

	@Override
	public void setItem(@Nullable String string, String arenaName) {
		item = string;
	}

	@Override
	public Object getItem() {
		return item;
	}

	@Override
	public Object itemString() {
		return item;
	}

	@Override
	public boolean isValid() {
		return item != null;
	}
}
