package org.cubeville.cvgames.types;

import org.bukkit.entity.Player;
import org.cubeville.commons.commands.CommandExecutionException;

public class GameVariableString extends GameVariable {

	String item;

	@Override
	public void setItem(Player player, String input) throws CommandExecutionException {
		item = input;
	}

	@Override
	public Object getItem() {
		return item;
	}

	@Override
	public Object storeFormat() {
		return item;
	}

	@Override
	public boolean isValid() {
		return item != null;
	}
}
