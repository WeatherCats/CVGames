package org.cubeville.cvgames.commands;

import org.bukkit.entity.Player;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvgames.ArenaCommand;
import org.cubeville.cvgames.ArenaManager;
import org.cubeville.cvgames.Game;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SetArenaVariable extends ArenaCommand {

	public SetArenaVariable() {
		super("variables set");
		addBaseParameter(new CommandParameterString()); // variable name
		addOptionalBaseParameter(new CommandParameterString()); // passed in value
		setPermission("cvgames.arenas.setvar");
	}

	@Override
	public CommandResponse execute(Player player, Set<String> set, Map<String, Object> map, List<Object> baseParameters)
		throws CommandExecutionException {
		Game arenaGame = ArenaManager.getArena(arenaName).getGame();
		if (arenaGame == null) throw new CommandExecutionException("You need to set the game for the arena " + arenaName);
		String variable = (String) baseParameters.get(0);
		if (!arenaGame.hasVariable(variable)) throw new CommandExecutionException("That variable does not exist for the game " + arenaGame.getId());

		String input = null;
		if (baseParameters.get(1) != null) input = (String) baseParameters.get(1);

		arenaGame.getGamesVariable(variable).setVariable(arenaName, variable, player, input);
		return new CommandResponse("Successfully set variable " + variable + " for " + arenaName);
	}
}
