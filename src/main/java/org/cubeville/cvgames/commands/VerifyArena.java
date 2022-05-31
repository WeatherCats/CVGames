package org.cubeville.cvgames.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.A;
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

		ArrayList<String> varKeys = new ArrayList<>();
		String path = null;
		if (parameters.size() == 1) {
			varKeys.addAll(arenaGame.getVariables());
		} else {
			path = (String) parameters.get(1);
			varKeys.add(path);
		}
		// Sort the variables alphabetically
		Collections.sort(varKeys);

		for (String key : varKeys) {
			String[] splitKey = key.split("\\.");
			String varName = splitKey[splitKey.length - 1];
			GameVariable gv = arenaGame.getGameVariable(key);
			if (gv == null) {
				TextComponent tc = new TextComponent("Could not find game variable \"" + key + "\"");
				tc.setColor(ChatColor.RED);
				out.addExtra("Could not find ");
				continue;
			}
			out.addExtra(GameUtils.addGameVarString(varName + " [" + gv.typeString() + "]: ", gv, arenaName, varName));
			if (gv instanceof GameVariableList && path == null) {
				TextComponent tc = new TextComponent("[Show Contents]");
				tc.setBold(true);
				tc.setColor(ChatColor.AQUA);
				tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cvgames arena " + arenaName + " verify " + key));
				tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to view the contents of this list")));
				out.addExtra(tc);
			} else {
				out.addExtra(gv.displayString(arenaName));
			}
			out.addExtra("\n");
		}

		return out;
	}
}
