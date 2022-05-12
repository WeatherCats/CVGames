package org.cubeville.cvgames.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cubeville.cvgames.managers.ArenaManager;
import org.cubeville.cvgames.CVGames;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SetArenaGame extends RunnableCommand {

	@Override
	public String execute(Player player, List<Object> parameters)
		throws Error {
		String arenaName = (String) parameters.get(0);
		String gameName = CVGames.gameManager().filterGameInput((String) parameters.get(1));
		ArenaManager.setArenaGame(arenaName, gameName);

		return "&aSet the game to " + gameName + " for arena " + arenaName + "!";
	}
}
