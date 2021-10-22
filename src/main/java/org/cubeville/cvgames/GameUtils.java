package org.cubeville.cvgames;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.*;

public class GameUtils {

	public static Map<String, List<Player>> divideTeams(List<Player> players, List<String> teamNames) {
		List<Float> percentages = new ArrayList<>();
		for (String ignored : teamNames) {
			percentages.add((1.0F / teamNames.size()));
		}
		return divideTeams(players, teamNames, percentages);
	}

	public static Map<String, List<Player>> divideTeams(List<Player> players, List<String> teamNames, List<Float> percentages) {
		if (teamNames.size() != percentages.size()) {
			throw new Error("The size of team names did not match the size of percentages");
		}
		Map<String, List<Player>> divided = new HashMap<>();
		List<Player> currentTeam = new ArrayList<>();
		int teamIndex = 0;
		float maxPercentage = percentages.get(0);
		for (int i = 0; i < players.size(); i++) {
			if ((float) (i + 1) / (float) players.size() > maxPercentage) {
				divided.put(teamNames.get(teamIndex), currentTeam);
				currentTeam.clear();
				teamIndex++;
				maxPercentage += percentages.get(teamIndex);
			}
			currentTeam.add(players.get(i));
		}
		return divided;
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
				String.valueOf(location.getBlockZ()),
				String.valueOf(location.getYaw()),
				String.valueOf(location.getPitch())
			)
		);
		return String.join(",", locParameters);
	}

	private static Location parsePlayerLocation(String s) {
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

	private static Location parseBlockLocation(String s) {
		List<String> params = Arrays.asList(s.split(","));
		return new Location(
			Bukkit.getWorld(params.get(0)), // world
			Integer.parseInt(params.get(1)), // x
			Integer.parseInt(params.get(2)), // y
			Integer.parseInt(params.get(3)), // z
			Float.parseFloat(params.get(4)), // pitch
			Float.parseFloat(params.get(5)) // yaw
		);
	}

}
