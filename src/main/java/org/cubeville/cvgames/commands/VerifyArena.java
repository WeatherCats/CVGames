package org.cubeville.cvgames.commands;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.cubeville.cvgames.managers.ArenaManager;
import org.cubeville.cvgames.models.Game;
import org.cubeville.cvgames.vartypes.GameVariable;

import java.util.*;

public class VerifyArena extends RunnableCommand {

	@Override
	public TextComponent execute(Player player, List<Object> parameters)
		throws Error {
		String arenaName = (String) parameters.get(0);
		Game arenaGame = ArenaManager.getArena(arenaName).getGame();
		if (arenaGame == null) throw new Error("You need to set the game for the arena " + arenaName);

		TextComponent out = new TextComponent("Variables for the arena " + arenaName + ":\n");

		// Sort the variables alphabetically
		List<String> varKeys = new ArrayList<>(arenaGame.getVariables());
		Collections.sort(varKeys);


		for (String key : varKeys) {
			GameVariable gv = arenaGame.getGameVariable(key);
			if (gv == null) { continue; }
			out.addExtra(addGameVarString(key + " [" + gv.typeString() + "]: §f", gv.isValid()));
			out.addExtra(gv.displayString());
			out.addExtra("\n");
		}

		return out;
	}

	private String addGameVarString(String message, boolean isValid) {
		String prefix;
		if (isValid) {
			prefix = "§a";
		} else {
			prefix = "§c";
		}

		return prefix + message;
	}


}
