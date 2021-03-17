package org.cubeville.cvgames.arenas;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.commons.commands.*;

import java.util.*;

public class AddArenaSpawn extends Command {

	private JavaPlugin plugin;

	public AddArenaSpawn(JavaPlugin plugin) {
		super("arenas addspawn");

		Set<String> teamParams = new HashSet<>(
			Arrays.asList("ffa", "team1", "team2", "team3", "team4", "team5", "team6", "team7", "team8")
		);
		this.plugin = plugin;

		setPermission("cvgames.arenas.addspawn");

		addBaseParameter(new CommandParameterString()); // arena name
		addBaseParameter(new CommandParameterEnumeratedString(teamParams)); // team
	}

	@Override
	public CommandResponse execute(Player player, Set<String> set, Map<String, Object> map, List<Object> baseParameters)
		throws CommandExecutionException {

		FileConfiguration config = plugin.getConfig();

		if (!config.contains("arenas." + baseParameters.get(0))) {
			throw new CommandExecutionException("Arena " + baseParameters.get(0) + " does not exist.");
		}

		String locationsPath = "arenas." + baseParameters.get(0) + "." + baseParameters.get(1);

		if (!config.contains(locationsPath)) {
			config.set(locationsPath, new ArrayList<String>());
		}

		List<String> locations = config.getStringList(locationsPath);
		Location pLoc = player.getLocation();
		List<String> locParameters = new ArrayList<>(
			Arrays.asList(
				pLoc.getWorld().getName(), // world
				String.valueOf(pLoc.getBlockX()),
				String.valueOf(pLoc.getBlockY()),
				String.valueOf(pLoc.getBlockZ()),
				String.valueOf(pLoc.getYaw()),
				String.valueOf(pLoc.getPitch())
			)
		);

		locations.add(String.join(",", locParameters));

		config.set(locationsPath, locations);
		plugin.saveConfig();

		return new CommandResponse("Set player location as spawn for " + baseParameters.get(1) + " on arena " + baseParameters.get(0));
	}
}
