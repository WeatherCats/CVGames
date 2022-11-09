package org.cubeville.cvgames.commands;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.cubeville.cvgames.managers.ArenaManager;
import org.cubeville.cvgames.models.Arena;

import java.util.List;

public class DeleteArena extends RunnableCommand {

	@Override
	public TextComponent execute(CommandSender sender, List<Object> parameters) throws Error {

		String arenaName = (String) parameters.get(0);
		if (!ArenaManager.hasArena(arenaName)) {
			throw new Error("Arena with name " + arenaName + " does not exist!");
		}
		ArenaManager.deleteArena(arenaName);

		return new TextComponent("§aDeleted the arena " + arenaName + "!");
	}
}
