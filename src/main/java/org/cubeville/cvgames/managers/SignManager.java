package org.cubeville.cvgames.managers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.cubeville.cvgames.models.QueueSign;
import org.cubeville.cvgames.enums.ArenaStatus;

import java.util.*;

public class SignManager {

	private static HashMap<String, QueueSign> signs = new HashMap<>();

	public static final Set<Material> signMaterials = new HashSet<>(
		Arrays.asList(
			Material.SPRUCE_WALL_SIGN,
			Material.ACACIA_WALL_SIGN,
			Material.BIRCH_WALL_SIGN,
			Material.DARK_OAK_WALL_SIGN,
			Material.JUNGLE_WALL_SIGN,
			Material.OAK_WALL_SIGN,
			Material.CRIMSON_WALL_SIGN,
			Material.WARPED_WALL_SIGN,
			Material.SPRUCE_SIGN,
			Material.ACACIA_SIGN,
			Material.BIRCH_SIGN,
			Material.DARK_OAK_SIGN,
			Material.JUNGLE_SIGN,
			Material.OAK_SIGN,
			Material.CRIMSON_SIGN,
			Material.WARPED_SIGN
		)
	);

	public static QueueSign addSign(Sign sign, String arenaName) {
		QueueSign queueSign = new QueueSign(sign, ArenaManager.getArena(arenaName));
		signs.put(createKey(sign.getLocation()), queueSign);
		return queueSign;
	}

	public static QueueSign getSign(Location location) {
		return signs.get(createKey(location));
	}

	public static QueueSign deleteSign(Location location) {
		return signs.remove(createKey(location));
	}

	public static String createKey(Location l) {
		String coordinateString = l.getX() + "," + l.getY() + "," + l.getZ();
		return "#" + l.getWorld().getName() + "~" + coordinateString;
	}

	public static void updateArenaSignsFill(String arenaName) {
		for (QueueSign sign : signs.values()) {
			if (sign.getArenaName().equals(arenaName)) sign.displayFill();
		}
	}

	public static void updateArenaSignsStatus(String arenaName, ArenaStatus status) {
		for (QueueSign sign : signs.values()) {
			if (sign.getArenaName().equals(arenaName)) sign.displayStatus(status);
		}
	}

	public static void updateSigns() {
		for (QueueSign sign : signs.values()) {
			sign.displayFill();
			sign.displayStatus( ArenaManager.getArena(sign.getArenaName()).getStatus() );
		}
	}
}
