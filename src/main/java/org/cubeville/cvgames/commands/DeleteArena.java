package org.cubeville.cvgames.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cubeville.cvgames.managers.ArenaManager;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DeleteArena extends RunnableCommand {

	@Override
	public String execute(Player player, List<Object> parameters) throws Error {

		String arenaName = ArenaManager.filterArenaInput((String) parameters.get(0));
		ArenaManager.deleteArena(arenaName);

		return "&aDeleted the arena " + arenaName + "!";
	}
}
