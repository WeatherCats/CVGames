package org.cubeville.cvgames.commands;

import org.bukkit.entity.Player;
import org.cubeville.cvgames.managers.ArenaManager;
import org.cubeville.cvgames.models.Game;
import org.cubeville.cvgames.vartypes.GameVariable;

import java.util.*;

public class VerifyArena extends RunnableCommand {

	@Override
	public String execute(Player player, List<Object> parameters)
		throws Error {
		String arenaName = (String) parameters.get(0);
		Game arenaGame = ArenaManager.getArena(arenaName).getGame();
		if (arenaGame == null) throw new Error("You need to set the game for the arena " + arenaName);

		StringBuilder response = new StringBuilder("Variables for the arena " + arenaName + ":\n");

		// Sort the variables alphabetically
		List<String> varKeys = new ArrayList<>(arenaGame.getVariables());
		Collections.sort(varKeys);

		for (String key : varKeys) {
			GameVariable gv = arenaGame.getGameVariable(key);
			if (gv == null) { continue; }
			if (gv.itemString() instanceof List) {
				response.append(addGameVarString(key + " [" + gv.typeString() + "]: §f", gv.isValid()));
				for (Object item : (List) gv.itemString()) {
					response.append(addGameVarString("§f- " + item, gv.isValid()));
				}
			} else {
				response.append(addGameVarString(key + " [" + gv.typeString() + "]: §f" + gv.displayString(), gv.isValid()));
			}
		}

		return response.toString();
	}

	private String addGameVarString(String message, boolean isValid) {
		String prefix;
		if (isValid) {
			prefix = "§a";
		} else {
			prefix = "§c";
		}

		return prefix + message + "\n";
	}


}
