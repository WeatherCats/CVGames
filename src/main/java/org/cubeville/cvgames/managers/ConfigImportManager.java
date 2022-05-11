package org.cubeville.cvgames.managers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.cubeville.cvgames.CVGames;
import org.cubeville.cvgames.models.Game;

import java.util.Objects;

public class ConfigImportManager {

	public static void importConfiguration() {
		FileConfiguration config = CVGames.getInstance().getConfig();
		if (config.getConfigurationSection("arenas") == null) {
			return;
		}

		for (String arenaName : Objects.requireNonNull(config.getConfigurationSection("arenas")).getKeys(false)) {
			ArenaManager.addArena(arenaName);

			ConfigurationSection arenaConfig = config.getConfigurationSection("arenas." + arenaName);

			String game = arenaConfig.getString("game");
			if (game != null) { ArenaManager.setArenaGame(arenaName, game); }
			else continue;


			if (!arenaConfig.contains("variables")) continue;
			Game arenaGame = ArenaManager.getArena(arenaName).getGame();
			parseArenaVariables("variables", arenaConfig, arenaName);
		}
	}

	private static void parseArenaVariables(String sectionPath, ConfigurationSection arenaConfig, String arenaName) {
		ConfigurationSection config = arenaConfig.getConfigurationSection(sectionPath);
		if (config == null) { return; }

		for (String var : Objects.requireNonNull(config).getKeys(false)) {
			String path = sectionPath + "." + var;
			if (config.isConfigurationSection(var)) {
				parseArenaVariables(path, arenaConfig, arenaName);
			} else {
				ArenaManager.getArena(arenaName).getGame().setVarFromValue(path, arenaName, config.getString(var));
			}
		}
	}
}
