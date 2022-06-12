package org.cubeville.cvgames.commands;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cubeville.cvgames.managers.ArenaManager;
import org.cubeville.cvgames.managers.EditingManager;
import org.cubeville.cvgames.models.BaseGame;
import org.cubeville.cvgames.vartypes.GameVariable;
import org.cubeville.cvgames.vartypes.GameVariableList;
import org.cubeville.cvgames.vartypes.GameVariableObject;

import java.util.List;

public class RemoveArenaVariable extends RunnableCommand {

	@Override
	public TextComponent execute(CommandSender sender, List<Object> baseParameters)
		throws Error {
		if (!(sender instanceof Player)) throw new Error("You cannot run this command from console!");
		Player player = (Player) sender;
		String arenaName = (String) baseParameters.get(0);
		BaseGame arenaGame = ArenaManager.getArena(arenaName).getGame();
		if (arenaGame == null) throw new Error("You need to set the game for the arena " + arenaName);
		String variable = ((String) baseParameters.get(1)).toLowerCase();
		GameVariableObject gameVariableObject = EditingManager.getEditObject(ArenaManager.getArena(arenaName), player);
		GameVariable gameVariable;
		if (gameVariableObject == null) {
			if (!arenaGame.hasVariable(variable))
				throw new Error("That variable does not exist for the game " + arenaGame.getId());
			gameVariable = arenaGame.getGameVariable(variable);
		} else {
			if (gameVariableObject.getVariableAtField(variable) == null) throw new Error("That variable does not exist for your selected object!");
			gameVariable = gameVariableObject.getVariableAtField(variable);
		}
		if (!(gameVariable instanceof GameVariableList)) throw new Error("The variable " + variable +" is not a list");
		int index;
		try {
			index = Integer.parseInt((String) baseParameters.get(2));
		} catch (NumberFormatException e) {
			throw new Error(baseParameters.get(2) + " is not a valid index!");
		}
		GameVariableList<?> list = (GameVariableList<?>) gameVariable;
		if (list.getVariableAtIndex(index - 1) == null) throw new Error("The list " + variable +" does not have an index of " + index);

		list.removeVariable(arenaName, gameVariableObject == null ? variable : EditingManager.getEditPath(arenaName, player) + "." + variable, index - 1);
		return new TextComponent("§bSuccessfully removed index " + index + " from variable " + variable);
	}
}
