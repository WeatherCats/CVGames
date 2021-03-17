package org.cubeville.cvgames.arenas;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ArenaManager {

	String game;
	JavaPlugin plugin;

	public ArenaManager(JavaPlugin plugin, String game) {
		this.game = game;
		this.plugin = plugin;
		if (!plugin.getConfig().contains("games." + game)) {
			plugin.getConfig().set("games." + game, new ArrayList<String>());
			plugin.saveConfig();
		}
	}

	private Location parseLocation(String s) {
		List<String> params = Arrays.asList(s.split(","));
		System.out.println(params);
		return new Location(
			Bukkit.getWorld(params.get(0)), // world
			Integer.parseInt(params.get(1)), // x
			Integer.parseInt(params.get(2)), // y
			Integer.parseInt(params.get(3)), // z
			Float.parseFloat(params.get(4)), // pitch
			Float.parseFloat(params.get(5)) // yaw
		);
	}

	public void startTeleport(Map<String, List<Player>> teamMap) {
		FileConfiguration config = plugin.getConfig();
		List<String> arenaNames = config.getStringList("games." + game);
		Random rand = new Random();
		String selectedArena = arenaNames.get(rand.nextInt(arenaNames.size()));

		for (String team : teamMap.keySet()) {
			List<String> startPoints = config.getStringList("arenas." + selectedArena + "." + team);
			Collections.shuffle(startPoints);

			List<Player> teamPlayers = teamMap.get(team);
			for (int i = 0; i < teamPlayers.size(); i++) {
				teamPlayers.get(i).teleport(parseLocation(startPoints.get(i % startPoints.size())));
			}
		}
	}

}
