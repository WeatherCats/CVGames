package org.cubeville.cvgames.commands;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.cubeville.cvgames.managers.ArenaManager;
import org.cubeville.cvgames.models.Arena;

import java.util.List;

public class DeleteArena extends RunnableCommand {

	@Override
	public TextComponent execute(CommandSender sender, List<Object> parameters) throws Error {

		Arena arena = ArenaManager.getArena((String) parameters.get(0));
		if (arena == null) {
			throw new Error("Arena with name " + arena.getName() + " does not exist!");
		}
		ArenaManager.deleteArena(arena.getName());

		return new TextComponent("§aDeleted the arena " + arena.getName() + "!");
	}
}
