package org.cubeville.cvgames.commands;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cubeville.cvgames.managers.ArenaManager;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DeleteArena extends RunnableCommand {

	@Override
	public TextComponent execute(CommandSender sender, List<Object> parameters) throws Error {

		String arenaName = ArenaManager.filterArenaInput((String) parameters.get(0));
		ArenaManager.deleteArena(arenaName);

		return new TextComponent("§aDeleted the arena " + arenaName + "!");
	}
}
