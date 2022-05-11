package org.cubeville.cvgames.commands;

import org.bukkit.command.CommandSender;
import org.cubeville.commons.commands.BaseCommand;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvgames.managers.ArenaManager;
import org.cubeville.cvgames.models.Game;
import org.cubeville.cvgames.vartypes.GameVariable;

import java.util.*;

public class VerifyArena extends BaseCommand {

	public VerifyArena() {
		super("verify");
		addBaseParameter(new CommandParameterString()); // arena name
		setPermission("cvgames.arenas.verify");
	}

	@Override
	public CommandResponse execute(CommandSender commandSender, Set<String> set, Map<String, Object> map, List<Object> baseParameters)
		throws CommandExecutionException {
		String arenaName = ArenaManager.filterArenaInput((String) baseParameters.get(0));
		Game arenaGame = ArenaManager.getArena(arenaName).getGame();
		if (arenaGame == null) throw new CommandExecutionException("You need to set the game for the arena " + arenaName);

		CommandResponse cr = new CommandResponse("Variables for the arena " + arenaName + ":");

		// Sort the variables alphabetically
		List<String> varKeys = new ArrayList<>(arenaGame.getVariables());
		Collections.sort(varKeys);

		for (String key : varKeys) {
			GameVariable gv = arenaGame.getGameVariable(key);
			if (gv == null) { continue; }
			if (gv.itemString() instanceof List) {
				cr.addMessage(addGameVarString(key + " [" + gv.displayString() + "]: ", gv.isValid()));
				for (Object item : (List) gv.itemString()) {
					cr.addMessage(addGameVarString("- " + item, gv.isValid()));
				}
			} else {
				cr.addMessage(addGameVarString(key + " [" + gv.displayString() + "]: " + gv.itemString(), gv.isValid()));
			}
		}

		return cr;
	}

	private String addGameVarString(String message, boolean isValid) {
		String prefix;
		if (isValid) {
			prefix = "&a";
		} else {
			prefix = "&c";
		}

		return prefix + message;
	}


}
