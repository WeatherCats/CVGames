package org.cubeville.cvgames.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;
import org.cubeville.cvgames.managers.ArenaManager;
import org.cubeville.cvgames.models.Arena;
import org.cubeville.cvgames.models.BaseGame;
import org.cubeville.cvgames.utils.GameUtils;
import org.cubeville.cvgames.vartypes.GameVariable;
import org.cubeville.cvgames.vartypes.GameVariableList;

import java.util.*;

public class VerifyArena extends RunnableCommand {

	@Override
	public TextComponent execute(CommandSender sender, List<Object> parameters)
		throws Error {
		Arena arena = (Arena) parameters.get(0);

		TextComponent out = new TextComponent();

		TextComponent title = new TextComponent("[Variables for the arena " + arena.getName() + "]\n");
		title.setBold(true);
		title.setColor(ChatColor.AQUA);
		title.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cvgames arena " + arena.getName() + " verify"));
		title.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to run the verify command again")));

		out.addExtra(title);

		TextComponent clearEdit = new TextComponent("[Edit Main Object]\n");
		clearEdit.setBold(true);
		clearEdit.setColor(ChatColor.AQUA);
		clearEdit.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cvgames arena " + arena.getName() + " clearedit"));
		clearEdit.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to set your editing object to the main object")));
		out.addExtra(clearEdit);

		ArrayList<String> varKeys = new ArrayList<>();
		String path = null;
		if (parameters.size() == 1) {
			varKeys.addAll(arena.getVariables());
		} else {
			path = (String) parameters.get(1);
			varKeys.add(path);
		}
		// Sort the variables alphabetically
		Collections.sort(varKeys);

		for (String key : varKeys) {
			String[] splitKey = key.split("\\.");
			String varName = splitKey[splitKey.length - 1];
			GameVariable gv = arena.getGameVariable(key);
			if (gv == null) {
				TextComponent tc = new TextComponent("Could not find game variable \"" + key + "\"");
				tc.setColor(ChatColor.RED);
				out.addExtra(tc);
				continue;
			}
			out.addExtra(GameUtils.addGameVarString(varName + " [" + gv.typeString() + "]: ", gv, arena.getName(), varName));
			if (gv instanceof GameVariableList && path == null) {
				TextComponent tc = new TextComponent("[Show Contents]");
				tc.setBold(true);
				tc.setColor(ChatColor.AQUA);
				tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cvgames arena " + arena.getName() + " verify " + key));
				tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to view the contents of this list")));
				out.addExtra(tc);
			} else {
				out.addExtra(gv.displayString(arena.getName()));
			}
			out.addExtra("\n");
		}
		return out;
	}
}
