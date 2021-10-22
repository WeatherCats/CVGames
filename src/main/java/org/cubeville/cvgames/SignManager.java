package org.cubeville.cvgames;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SignManager {

	public static final Set<Material> signMaterials;

	static {
		signMaterials = new HashSet<>();
		signMaterials.add(Material.SPRUCE_WALL_SIGN);
		signMaterials.add(Material.ACACIA_WALL_SIGN);
		signMaterials.add(Material.BIRCH_WALL_SIGN);
		signMaterials.add(Material.DARK_OAK_WALL_SIGN);
		signMaterials.add(Material.JUNGLE_WALL_SIGN);
		signMaterials.add(Material.OAK_WALL_SIGN);
		signMaterials.add(Material.CRIMSON_WALL_SIGN);
		signMaterials.add(Material.WARPED_WALL_SIGN);
		signMaterials.add(Material.SPRUCE_SIGN);
		signMaterials.add(Material.ACACIA_SIGN);
		signMaterials.add(Material.BIRCH_SIGN);
		signMaterials.add(Material.DARK_OAK_SIGN);
		signMaterials.add(Material.JUNGLE_SIGN);
		signMaterials.add(Material.OAK_SIGN);
		signMaterials.add(Material.CRIMSON_SIGN);
		signMaterials.add(Material.WARPED_SIGN);
	}

	private static HashMap<String, QueueSign> signs = new HashMap<>();

	public static QueueSign addSign(Sign sign, Arena arena) {
		QueueSign queueSign = new QueueSign(sign, arena);
		signs.put(createKey(sign.getLocation()), queueSign);

		CVGames gameInstance = CVGames.getInstance();
		String signsPath = "arenas." + arena.getName() + ".signs";
		List<String> signLocations = gameInstance.getConfig().getStringList(signsPath);
		signLocations.add(GameUtils.blockLocToString(sign.getLocation()));
		gameInstance.getConfig().set(signsPath, signLocations);
		gameInstance.saveConfig();

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
}
