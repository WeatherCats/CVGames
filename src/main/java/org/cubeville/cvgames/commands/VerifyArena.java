package org.cubeville.cvgames.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cubeville.cvgames.managers.ArenaManager;
import org.cubeville.cvgames.models.Game;
import org.cubeville.cvgames.utils.GameUtils;
import org.cubeville.cvgames.vartypes.GameVariable;
import org.cubeville.cvgames.vartypes.GameVariableList;
import org.cubeville.cvgames.vartypes.GameVariableObject;

import java.util.*;

public class VerifyArena extends RunnableCommand {

	@Override
	public TextComponent execute(CommandSender sender, List<Object> parameters)
		throws Error {
		String arenaName = ((String) parameters.get(0)).toLowerCase();
		Game arenaGame = ArenaManager.getArena(arenaName).getGame();
		if (arenaGame == null) throw new Error("You need to set the game for the arena " + arenaName);

		TextComponent out = new TextComponent("Variables for the arena " + arenaName + ":\n");

		// Sort the variables alphabetically
		List<String> varKeys = new ArrayList<>(arenaGame.getVariables());
		Collections.sort(varKeys);

		for (String key : varKeys) {
			GameVariable gv = arenaGame.getGameVariable(key);
			if (gv == null) { continue; }
			out.addExtra(GameUtils.addGameVarString(key + " [" + gv.typeString() + "]: ", gv, arenaName, key));
			out.addExtra(gv.displayString(arenaName));
			out.addExtra("\n");
		}

		return out;
	}
}
