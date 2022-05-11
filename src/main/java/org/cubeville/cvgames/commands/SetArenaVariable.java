package org.cubeville.cvgames.commands;

import org.bukkit.entity.Player;
import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvgames.managers.ArenaManager;
import org.cubeville.cvgames.managers.EditingManager;
import org.cubeville.cvgames.models.Game;
import org.cubeville.cvgames.vartypes.GameVariableObject;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SetArenaVariable extends Command {

	public SetArenaVariable() {
		super("setvar");
		addBaseParameter(new CommandParameterString()); // arena name
		addBaseParameter(new CommandParameterString()); // var name
		addOptionalBaseParameter(new CommandParameterString()); // passed in value
		setPermission("cvgames.arenas.setvar");
	}

	@Override
	public CommandResponse execute(Player player, Set<String> set, Map<String, Object> map, List<Object> baseParameters)
		throws CommandExecutionException {
		String arenaName = ArenaManager.filterArenaInput((String) baseParameters.get(0));
		Game arenaGame = ArenaManager.getArena(arenaName).getGame();
		if (arenaGame == null) throw new CommandExecutionException("You need to set the game for the arena " + arenaName);
		String variable = ((String) baseParameters.get(1)).toLowerCase();

		String input = null;
		if (baseParameters.size() > 2) input = (String) baseParameters.get(2);

		GameVariableObject gameVariableObject = EditingManager.getEditObject(ArenaManager.getArena(arenaName), player);
		if (gameVariableObject != null) {
			gameVariableObject.setField(arenaName, variable, player, input);
		} else {
			if (!arenaGame.hasVariable(variable)) throw new CommandExecutionException("That variable does not exist for the game " + arenaGame.getId());
			arenaGame.getGameVariable(variable).setVariable(arenaName, variable, player, input);
		}
		return new CommandResponse("Successfully set variable " + variable + " for " + arenaName);
	}
}
