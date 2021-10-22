package org.cubeville.cvgames.commands;

import org.bukkit.entity.Player;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvgames.ArenaCommand;
import org.cubeville.cvgames.ArenaManager;
import org.cubeville.cvgames.CVGames;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SetArenaGame extends ArenaCommand {

	public SetArenaGame() {
		super("setgame");
		addBaseParameter(new CommandParameterString()); // game name
		setPermission("cvgames.arenas.setgame");
	}

	@Override
	public CommandResponse execute(Player player, Set<String> set, Map<String, Object> map, List<Object> baseParameters)
		throws CommandExecutionException {
		String gameName = ((String) baseParameters.get(0)).toLowerCase();

		if (CVGames.gameManager().hasGame(gameName)) {
			throw new CommandExecutionException("Game with name " + gameName + " does not exist!");
		}

		ArenaManager.setArenaGame(arenaName, gameName);
		return new CommandResponse("&aSet the game to  " + gameName + " for arena " + arenaName + "!");
	}
}
