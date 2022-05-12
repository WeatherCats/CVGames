package org.cubeville.cvgames.commands;

import org.bukkit.entity.Player;
import org.cubeville.cvgames.managers.ArenaManager;
import org.cubeville.cvgames.managers.EditingManager;
import org.cubeville.cvgames.models.Game;
import org.cubeville.cvgames.vartypes.GameVariableObject;

import java.util.List;

public class SetArenaVariable extends RunnableCommand {

	@Override
	public String execute(Player player, List<Object> baseParameters)
		throws Error {
		String arenaName = (String) baseParameters.get(0);
		Game arenaGame = ArenaManager.getArena(arenaName).getGame();
		if (arenaGame == null) throw new Error("You need to set the game for the arena " + arenaName);
		String variable = ((String) baseParameters.get(1)).toLowerCase();

		String input = null;
		if (baseParameters.size() > 2) input = (String) baseParameters.get(2);

		GameVariableObject gameVariableObject = EditingManager.getEditObject(ArenaManager.getArena(arenaName), player);
		if (gameVariableObject != null) {
			gameVariableObject.setField(arenaName, variable, player, input);
		} else {
			if (!arenaGame.hasVariable(variable)) throw new Error("That variable does not exist for the game " + arenaGame.getId());
			arenaGame.getGameVariable(variable).setVariable(arenaName, variable, player, input);
		}
		return "&bSuccessfully set variable " + variable + " for " + arenaName;
	}
}
