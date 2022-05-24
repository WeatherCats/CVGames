package org.cubeville.cvgames.commands;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cubeville.cvgames.managers.ArenaManager;
import org.cubeville.cvgames.CVGames;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SetArenaGame extends RunnableCommand {

	@Override
	public TextComponent execute(CommandSender sender, List<Object> parameters)
		throws Error {
		String arenaName = (String) parameters.get(0);
		String gameName = CVGames.gameManager().filterGameInput((String) parameters.get(1));
		ArenaManager.setArenaGame(arenaName, gameName);

		return new TextComponent("§aSet the game to " + gameName + " for arena " + arenaName + "!");
	}
}
