package org.cubeville.cvgames.commands;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.cubeville.commons.commands.*;
import org.cubeville.cvgames.ArenaCommand;
import org.cubeville.cvgames.ArenaManager;
import org.cubeville.cvgames.SignManager;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class AddArenaSign extends ArenaCommand {

	public AddArenaSign() {
		super("signs add");
		addBaseParameter(new CommandParameterString()); // arena name
		setPermission("cvgames.arenas.signs.add");
	}

	@Override
	public CommandResponse execute(Player player, Set<String> set, Map<String, Object> map, List<Object> baseParameters)
		throws CommandExecutionException {

		Block block = player.getTargetBlock(null, 100);
		if (!(SignManager.signMaterials.contains(block.getType()))) {
			throw new CommandExecutionException("You must be looking at a sign for this command to work.");
		}
		Sign sign = (Sign) block.getState();

		SignManager.addSign(sign, ArenaManager.getArena(arenaName));
		return new CommandResponse("Successfully created a sign for the arena " + arenaName + "!");
	}
}
