package org.cubeville.cvgames.vartypes;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.cvgames.GameUtils;
import org.cubeville.cvgames.SignManager;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GameVariableSign extends GameVariable {

	Sign sign;

	@Override
	public void setItem(Player player, String input, String arenaName) throws CommandExecutionException {
		Block block = player.getTargetBlock(null, 20);
		if (block.isEmpty()) throw new CommandExecutionException("You need to be looking at a block to execute this command");
		if (!SignManager.signMaterials.contains(block.getType())) throw new CommandExecutionException("You need to be looking at a sign to execute this command");
		sign = (Sign) block.getState();
	}

	@Override
	public void setItem(@Nullable String string, String arenaName) {
		if (string == null) {
			sign = null;
		} else {
			Location signLoc = GameUtils.parseBlockLocation(string);
			if (!SignManager.signMaterials.contains(signLoc.getBlock().getType())) {
				sign = null;
				throw new Error(
					"Error with game variable sign -- Sign variable pointing to location " + string
						+ " is not a sign.");
			}
			sign = (Sign) signLoc.getBlock().getState();
		}
	}

	public String displayString() {
		return "Sign";
	}

	@Override
	public Sign getItem() {
		return sign;
	}

	@Override
	public String itemString() {
		if (sign == null) {
			return null;
		}
		return GameUtils.blockLocToString(sign.getLocation());
	}

	@Override
	public boolean isValid() {
		return sign != null;
	}

}
