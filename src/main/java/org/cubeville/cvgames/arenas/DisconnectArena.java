package org.cubeville.cvgames.arenas;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.commons.commands.BaseCommand;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DisconnectArena extends BaseCommand {

	private JavaPlugin plugin;

	public DisconnectArena(JavaPlugin plugin) {
		super("arenas disconnect");
		addBaseParameter(new CommandParameterString()); // arena name
		addBaseParameter(new CommandParameterString()); // game name
		setPermission("cvgames.arenas.disconnect");

		this.plugin = plugin;
	}

	@Override
	public CommandResponse execute(CommandSender commandSender, Set<String> set, Map<String, Object> map,
		List<Object> baseParameters) throws CommandExecutionException {
		FileConfiguration config = plugin.getConfig();

		if (!config.contains("arenas." + baseParameters.get(0))) {
			throw new CommandExecutionException("Arena with name " + baseParameters.get(0) + " does not exist!");
		}

		if (!config.contains("games." + baseParameters.get(1))) {
			throw new CommandExecutionException("Game with name " + baseParameters.get(1) + " does not exist!");
		}

		List<String> arenas = config.getStringList("games." + baseParameters.get(1));
		if (!arenas.contains((String) baseParameters.get(0))) {
			throw new CommandExecutionException("Arena " + baseParameters.get(0) + " is not in the game "  + baseParameters.get(1) + ".");
		}
		arenas.remove((String) baseParameters.get(0));
		config.set("games." + baseParameters.get(1), arenas);
		plugin.saveConfig();
		return new CommandResponse("&bDisconnected the arenas " + baseParameters.get(0) + " from the game " + baseParameters.get(1) + "!");
	}
}
