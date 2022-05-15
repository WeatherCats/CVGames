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
