package org.cubeville.cvgames.vartypes;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.cubeville.cvgames.utils.GameUtils;

import javax.annotation.Nullable;

public class GameVariableLocation extends GameVariable {

	private Location tpLoc;

	@Override
	public void setItem(Player player, String input, String arenaName) {
		tpLoc = player.getLocation();
	}

	@Override
	public Location getItem() {
		return tpLoc;
	}

	@Override
	public String typeString() {
		return "Location";
	}

	@Override
	public String itemString() {
		if (tpLoc == null) {
			return null;
		}
		return GameUtils.playerLocToString(tpLoc);
	}

	@Override
	public void setItem(@Nullable Object string, String arenaName) {
		if (!(string instanceof String)) {
			tpLoc = null;
		} else {
			tpLoc = GameUtils.parsePlayerLocation((String) string);
		}
	}

	@Override
	public boolean isValid() {
		return tpLoc != null;
	}

}
