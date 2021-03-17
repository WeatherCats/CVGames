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

public class DeleteArena extends BaseCommand {


	private JavaPlugin plugin;

	public DeleteArena(JavaPlugin plugin) {
		super("arenas delete");
		addBaseParameter(new CommandParameterString()); // arenas name
		setPermission("cvgames.arenas.delete");

		this.plugin = plugin;
	}

	@Override
	public CommandResponse execute(CommandSender commandSender, Set<String> set, Map<String, Object> map,
		List<Object> baseParameters) throws CommandExecutionException {
		FileConfiguration config = plugin.getConfig();

		if (!config.contains("arenas." + baseParameters.get(0))) {
			throw new CommandExecutionException("Arena with name " + baseParameters.get(0) + " does not exist!");
		}

		config.set("arenas." + baseParameters.get(0), null);
		plugin.saveConfig();
		return new CommandResponse("&aDeleted the arenas " + baseParameters.get(0) + "!");
	}
}
