package org.cubeville.cvgames.commands;

import org.bukkit.entity.Player;
import org.cubeville.cvgames.managers.ArenaManager;

import java.util.List;

public class CreateArena extends RunnableCommand {

	@Override
	public String execute(Player player, List<Object> parameters) throws Error {
		String arenaName = ((String) parameters.get(0)).toLowerCase();

		if (ArenaManager.hasArena(arenaName)) {
			throw new Error("Arena with name " + arenaName + " already exists!");
		}

		ArenaManager.createArena(arenaName);
		return "&aCreated the arena " + arenaName + "!";
	}
}
