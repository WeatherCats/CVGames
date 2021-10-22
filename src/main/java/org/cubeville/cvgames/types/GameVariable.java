package org.cubeville.cvgames.types;

import org.bukkit.entity.Player;
import org.cubeville.commons.commands.CommandExecutionException;

import static org.cubeville.cvgames.CVGames.getInstance;

public abstract class GameVariable {

	public abstract void setItem(Player player, String input) throws CommandExecutionException;

	public abstract Object getItem();

	public abstract Object storeFormat();

	public abstract boolean isValid();

	public void setVariable(String arenaName, String variableName, Player player, String input) throws CommandExecutionException {
		setItem(player, input);
		storeItem(arenaName, variableName);
	}

	public void storeItem(String arenaName, String variableName) {
		getInstance().getConfig().set("arenas." + arenaName + ".variables." + variableName, storeFormat());
		getInstance().saveConfig();
	}

	public void addItem(Player player, String input) throws CommandExecutionException {
		throw new CommandExecutionException("Cannot add an item to a variable that is not a list. Use set instead.");
	}
}
