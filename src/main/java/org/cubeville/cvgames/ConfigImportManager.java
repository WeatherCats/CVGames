package org.cubeville.cvgames;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
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

			ConfigurationSection varsConfig = arenaConfig.getConfigurationSection("variables");
			if (varsConfig == null) continue;
			Game arenaGame = ArenaManager.getArena(arenaName).getGame();
			for (String arenaVar : Objects.requireNonNull(varsConfig).getKeys(false)) {
				if (varsConfig.isList(arenaVar)) {
					arenaGame.setVarFromValue(arenaVar, arenaName, varsConfig.getStringList(arenaVar));
				} else {
					arenaGame.setVarFromValue(arenaVar, arenaName, varsConfig.getString(arenaVar));
				}
			}
		}

	}
}
