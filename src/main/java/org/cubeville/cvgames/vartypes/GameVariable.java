package org.cubeville.cvgames.vartypes;

import org.bukkit.entity.Player;
import org.cubeville.commons.commands.CommandExecutionException;

import javax.annotation.Nullable;

import static org.cubeville.cvgames.CVGames.getInstance;

public abstract class GameVariable {

	public abstract void setItem(Player player, String input, String arenaName) throws CommandExecutionException;

	public abstract Object getItem();

	public abstract Object itemString();

	public abstract boolean isValid();

	public abstract void setItem(@Nullable String string, String arenaName);

	public abstract String displayString();

	public void setVariable(String arenaName, String variableName, Player player, String input) throws CommandExecutionException {
		setItem(player, input, arenaName);
		storeItem(arenaName, variableName);
	}

	public void storeItem(String arenaName, String path) {
		getInstance().getConfig().set("arenas." + arenaName + ".variables." + path, itemString());
		getInstance().saveConfig();
	}

	public void addVariable(String arenaName, String variableName, Player player, String input) throws CommandExecutionException {
		throw new CommandExecutionException("Cannot add an item to a variable that is not a list. Use set instead.");
	}

	public void addItem(Player player, String input, String arenaName) throws CommandExecutionException {
		throw new CommandExecutionException("Cannot add an item to a variable that is not a list. Use set instead.");
	}
}
