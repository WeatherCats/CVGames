package org.cubeville.cvgames.utils;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import javax.annotation.Nullable;
import java.util.*;

public class GameUtils {

	public static Map<String, List<Player>> divideTeams(List<Player> players, List<String> teamNames) {
		List<Float> percentages = new ArrayList<>();
		for (String ignored : teamNames) {
			percentages.add((1.0F / (float) teamNames.size()));
		}
		return divideTeams(players, teamNames, percentages);
	}

	public static Map<String, List<Player>> divideTeams(List<Player> players, List<String> teamNames, List<Float> percentages) {
		if (teamNames.size() != percentages.size()) {
			throw new Error("The size of team names did not match the size of percentages");
		}
		Collections.shuffle(players);
		int teamIndex = -1;
		float pivotPct = 0;
		Map<String, List<Player>> resultMap = new HashMap<>();

		for (int i = 0; i < players.size(); i++) {
			if (pivotPct <= ((float) i / (float) players.size()) || i == 0) {
				teamIndex++;
				pivotPct += percentages.get(teamIndex);
				resultMap.put(teamNames.get(teamIndex), new ArrayList<>());
			}
			List<Player> ps = resultMap.get(teamNames.get(teamIndex));
			ps.add(players.get(i));
			resultMap.put(teamNames.get(teamIndex), ps);
		}

		return resultMap;
	}


	public static void messagePlayerList(List<Player> players, String message) {
		messagePlayerList(players, message, null);
	}

	public static void messagePlayerList(List<Player> players, String message, @Nullable Sound sound) {
		for (Player player : players) {
			player.sendMessage(message);
			if (sound != null) {
				player.playSound(player.getLocation(), sound, 3.0F, 0.7F);
			}
		}
	}

	public static String playerLocToString(Location location) {
		List<String> locParameters = new ArrayList<>(
			Arrays.asList(
				location.getWorld().getName(), // world
				String.valueOf(location.getX()),
				String.valueOf(location.getY()),
				String.valueOf(location.getZ()),
				String.valueOf(location.getYaw()),
				String.valueOf(location.getPitch())
			)
		);
		return String.join(",", locParameters);
	}

	public static String blockLocToString(Location location) {
		List<String> locParameters = new ArrayList<>(
			Arrays.asList(
				location.getWorld().getName(), // world
				String.valueOf(location.getBlockX()),
				String.valueOf(location.getBlockY()),
				String.valueOf(location.getBlockZ())
			)
		);
		return String.join(",", locParameters);
	}

	public static Location parsePlayerLocation(String s) {
		List<String> params = Arrays.asList(s.split(","));
		return new Location(
			Bukkit.getWorld(params.get(0)), // world
			Float.parseFloat(params.get(1)), // x
			Float.parseFloat(params.get(2)), // y
			Float.parseFloat(params.get(3)), // z
			Float.parseFloat(params.get(4)), // pitch
			Float.parseFloat(params.get(5)) // yaw
		);
	}

	public static Location parseBlockLocation(String s) {
		List<String> params = Arrays.asList(s.split(","));
		return new Location(
			Bukkit.getWorld(params.get(0)), // world
			Integer.parseInt(params.get(1)), // x
			Integer.parseInt(params.get(2)), // y
			Integer.parseInt(params.get(3)) // z
		);
	}

	public static ItemStack createColoredLeatherArmor(Material armorType, Color color) {
		ItemStack armorItem = new ItemStack(armorType);
		if (armorItem.getItemMeta() instanceof LeatherArmorMeta) {
			LeatherArmorMeta meta = (LeatherArmorMeta) armorItem.getItemMeta();
			meta.setColor(color);
			armorItem.setItemMeta(meta);
		}
		return armorItem;
	}

	public static Color hex2Color(String colorStr) {
		return Color.fromRGB(
				Integer.valueOf(colorStr.substring(1, 3), 16),
				Integer.valueOf(colorStr.substring(3, 5), 16),
				Integer.valueOf(colorStr.substring(5, 7), 16)
		);
	}

	public static ItemStack customItem(Material material, String name) {
		return customItem(material, name, null);
	}

	public static ItemStack customItem(Material material, String name, @Nullable Integer amount) {
		if (amount == null) { amount = 1; }
		ItemStack item = new ItemStack(material, amount);
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(name);
		item.setItemMeta(im);
		return item;
	}

}
