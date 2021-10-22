package org.cubeville.cvgames.types;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.cubeville.cvgames.GameUtils;

public class GameVariablePlayerLocation extends GameVariable {

	private Location tpLoc;

	@Override
	public void setItem(Player player, String input) {
		tpLoc = player.getLocation();
	}

	@Override
	public Location getItem() {
		return tpLoc;
	}

	@Override
	public String storeFormat() {
		if (tpLoc == null) {
			return null;
		}
		return GameUtils.playerLocToString(tpLoc);
	}

	@Override
	public boolean isValid() {
		return tpLoc != null;
	}

}
