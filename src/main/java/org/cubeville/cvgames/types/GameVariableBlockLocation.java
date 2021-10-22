package org.cubeville.cvgames.types;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.cvgames.GameUtils;

public class GameVariableBlockLocation extends GameVariable {

	private Location blockLoc;

	@Override
	public void setItem(Player player, String input) throws CommandExecutionException {
		Block block = player.getTargetBlock(null, 20);
		if (block.isEmpty()) throw new CommandExecutionException("You need to be looking at a block to execute this command");
		blockLoc = block.getLocation();
	}

	@Override
	public Location getItem() {
		return blockLoc;
	}

	@Override
	public String storeFormat() {
		if (blockLoc == null) {
			return null;
		}
		return GameUtils.blockLocToString(blockLoc);
	}

	@Override
	public boolean isValid() {
		return blockLoc != null;
	}

}
