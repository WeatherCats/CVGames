package org.cubeville.cvgames.commands;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.cubeville.cvgames.managers.ArenaManager;
import org.cubeville.cvgames.CVGames;
import org.cubeville.cvgames.models.Arena;

import java.util.List;

public class AddArenaGame extends RunnableCommand {

	@Override
	public TextComponent execute(CommandSender sender, List<Object> parameters)
		throws Error {
		Arena arena = (Arena) parameters.get(0);
		String gameName = (String) parameters.get(1);
		ArenaManager.addArenaGame(arena.getName(), gameName);

		return new TextComponent("§aAdd the game " + gameName + " to arena " + arena.getName() + "!");
	}
}
