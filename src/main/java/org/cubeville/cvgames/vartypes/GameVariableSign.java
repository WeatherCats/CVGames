package org.cubeville.cvgames.vartypes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.cubeville.cvgames.utils.GameUtils;
import org.cubeville.cvgames.managers.SignManager;

import javax.annotation.Nullable;
import java.util.logging.Level;

public class GameVariableSign extends GameVariable {

	Sign sign;

	@Override
	public void setItem(Player player, String input, String arenaName) throws Error {
		Block block = player.getTargetBlock(null, 20);
		if (block.isEmpty()) throw new Error("You need to be looking at a block to execute this command");
		if (!SignManager.signMaterials.contains(block.getType())) throw new Error("You need to be looking at a sign to execute this command");
		sign = (Sign) block.getState();
	}

	@Override
	public void setItem(@Nullable Object object, String arenaName) {
		if (!(object instanceof String)) {
			sign = null;
		} else {
			Location signLoc = GameUtils.parseBlockLocation((String) object);
			if (!SignManager.signMaterials.contains(signLoc.getBlock().getType())) {
				sign = null;
				Bukkit.getLogger().log(Level.WARNING,
					"Sign variable for arena " + arenaName + " pointing to location " + object
						+ " is not a sign. Please remove this variable from the configuration for " + arenaName + ".");
			} else {
				sign = (Sign) signLoc.getBlock().getState();
			}
		}
	}

	public String typeString() {
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
