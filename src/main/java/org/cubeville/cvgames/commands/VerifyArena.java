package org.cubeville.cvgames.commands;

import org.bukkit.entity.Player;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvgames.ArenaCommand;
import org.cubeville.cvgames.ArenaManager;
import org.cubeville.cvgames.Game;
import org.cubeville.cvgames.types.GameVariable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class VerifyArena extends ArenaCommand {

	public VerifyArena() {
		super("verify");
		setPermission("cvgames.arenas.verify");
	}

	@Override
	public CommandResponse execute(Player player, Set<String> set, Map<String, Object> map, List<Object> baseParameters)
		throws CommandExecutionException {
		Game arenaGame = ArenaManager.getArena(arenaName).getGame();
		if (arenaGame == null) throw new CommandExecutionException("You need to set the game for the arena " + arenaName);

		CommandResponse cr = new CommandResponse("Variables for the arena " + arenaName + ":");

		for (Object key : arenaGame.getVerificationMap().keySet()) {
			GameVariable gv = (GameVariable) arenaGame.getVerificationMap().get(key);
			if (gv.storeFormat() instanceof List) {
				for (Object item : (List) gv.storeFormat()) {
					cr.addMessage(addGameVarString("- " + item, gv.isValid()));
				}
			} else {
				cr.addMessage(addGameVarString((String) gv.storeFormat(), gv.isValid()));
			}
		}

		return cr;
	}

	private String addGameVarString(String message, boolean isValid) {
		String colorCode;
		if (isValid) {
			colorCode = "&a";
		} else {
			colorCode = "&c";
		}

		return colorCode + message;
	}


}
