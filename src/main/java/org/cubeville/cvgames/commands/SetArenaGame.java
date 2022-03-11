package org.cubeville.cvgames.commands;

import org.bukkit.command.CommandSender;
import org.cubeville.commons.commands.*;
import org.cubeville.cvgames.ArenaManager;
import org.cubeville.cvgames.CVGames;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SetArenaGame extends BaseCommand {

	public SetArenaGame() {
		super("setgame");
		addBaseParameter(new CommandParameterString()); // arena name
		addBaseParameter(new CommandParameterString()); // game name
		setPermission("cvgames.arenas.setgame");
	}

	@Override
	public CommandResponse execute(CommandSender commandSender, Set<String> set, Map<String, Object> map, List<Object> baseParameters)
		throws CommandExecutionException {
		String arenaName = ArenaManager.filterArenaInput((String) baseParameters.get(0));
		String gameName = CVGames.gameManager().filterGameInput((String) baseParameters.get(1));

		try {
			ArenaManager.setArenaGame(arenaName, gameName);
		} catch (Error e) {
			throw new CommandExecutionException("Could not set arena game properly, please contact a system administrator.");
		}
		return new CommandResponse("&aSet the game to " + gameName + " for arena " + arenaName + "!");
	}
}
