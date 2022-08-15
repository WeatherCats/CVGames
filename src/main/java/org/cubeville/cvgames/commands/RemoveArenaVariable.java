package org.cubeville.cvgames.commands;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cubeville.cvgames.managers.ArenaManager;
import org.cubeville.cvgames.managers.EditingManager;
import org.cubeville.cvgames.models.Arena;
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
		Arena arena = (Arena) baseParameters.get(0);
		String variable = ((String) baseParameters.get(1)).toLowerCase();
		GameVariableObject gameVariableObject = EditingManager.getEditObject(arena, player);
		GameVariable gameVariable;
		if (gameVariableObject == null) {
			if (!arena.hasVariable(variable))
				throw new Error("That variable does not exist for the arena " + arena);
			gameVariable = arena.getGameVariable(variable);
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

		list.removeVariable(arena.getName(), gameVariableObject == null ? variable : EditingManager.getEditPath(arena.getName(), player) + "." + variable, index - 1);
		return new TextComponent("§bSuccessfully removed index " + index + " from variable " + variable);
	}
}
