package org.cubeville.cvgames.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.cubeville.cvgames.models.Arena;
import org.cubeville.cvgames.vartypes.GameVariable;
import org.cubeville.cvgames.vartypes.GameVariableList;
import org.cubeville.cvgames.vartypes.GameVariableObject;

import javax.annotation.Nullable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	public static Scoreboard createScoreboard(Arena arena, String title, List<String> items) {
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard scoreboard = manager.getNewScoreboard();
		String objName = "cvgames-" + arena.getName();
		objName = objName.substring(0, Math.min(objName.length(), 16));
		Objective pbObjective = scoreboard.registerNewObjective(objName, "dummy", title);
		pbObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
		for (int i = 0; i < items.size(); i++) {
			pbObjective.getScore(items.get(i)).setScore(items.size() - i);
		}
		return scoreboard;
	}

	public static final Pattern HEX_PATTERN = Pattern.compile("&#([a-fA-F0-9]{6}|[a-fA-F0-9]{3})");

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

	public static TextComponent addGameVarString(String message, GameVariable gv, String arenaName, String key) {
		String prefix;
		if (gv.isValid()) {
			prefix = "§a";
		} else {
			prefix = "§c";
		}
		TextComponent tc = new TextComponent(prefix + message);
		String suggestedCommand;
		String hover;
		if (gv instanceof GameVariableObject) {
			return tc;
		} else if (gv instanceof GameVariableList) {
			suggestedCommand = "/cvgames arena " + arenaName + " addvar " + key + " ";
			hover = "Click to add an item to this list";
		} else {
			suggestedCommand = "/cvgames arena " + arenaName + " setvar " + key + " ";
			hover = "Click to set this variable";

		}
		tc.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggestedCommand));
		tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hover)));
		return tc;
	}

	public static void clearItemsFromInventory(PlayerInventory inv, List<ItemStack> items) {
		ItemStack[] invContents = inv.getContents();
		for (int i = 0; i < invContents.length; i++) {
			ItemStack checkingItem = invContents[i];
			if (checkingItem == null) continue;
			for (ItemStack item : items) {
				if (checkingItem.isSimilar(item)) {
					inv.setItem(i, null);
				}
			}
		}
	}

}
