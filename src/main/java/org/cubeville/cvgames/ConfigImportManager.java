package org.cubeville.cvgames;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class ConfigImportManager {

	public static void importConfiguration() {
		FileConfiguration config = CVGames.getInstance().getConfig();
		if (config.getConfigurationSection("arenas") == null) {
			return;
		}

		for (String arenaName : Objects.requireNonNull(config.getConfigurationSection("arenas")).getKeys(false)) {

		}

	}
}
