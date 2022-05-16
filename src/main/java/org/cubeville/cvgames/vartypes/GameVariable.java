package org.cubeville.cvgames.vartypes;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

import static org.cubeville.cvgames.CVGames.getInstance;

public abstract class GameVariable {

	public abstract void setItem(Player player, String input, String arenaName) throws Error;

	public abstract Object getItem();

	public abstract Object itemString();

	public abstract boolean isValid();

	public abstract void setItem(@Nullable Object item, String arenaName);

	public abstract String typeString();

	public TextComponent displayString() {
		if (this.itemString() == null) return new TextComponent("null");
		return new TextComponent(this.itemString().toString());
	}

	public void setVariable(String arenaName, String variableName, Player player, String input) throws Error {
		setItem(player, input, arenaName);
		storeItem(arenaName, variableName);
	}

	public void storeItem(String arenaName, String path) {
		getInstance().getConfig().set("arenas." + arenaName + ".variables." + path, itemString());
		getInstance().saveConfig();
	}

	public void addVariable(String arenaName, String variableName, Player player, String input) throws Error {
		throw new Error("Cannot add an item to a variable that is not a list. Use set instead.");
	}

	public void addItem(Player player, String input, String arenaName) throws Error {
		throw new Error("Cannot add an item to a variable that is not a list. Use set instead.");
	}

	public void clearItem() {
		return;
	}

	public Object getFromPath(String path) {
		return getInstance().getConfig().getString(path);
	}
}
