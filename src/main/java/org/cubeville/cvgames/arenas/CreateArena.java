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

public class CreateArena extends BaseCommand {

	private JavaPlugin plugin;

	public CreateArena(JavaPlugin plugin) {
		super("arenas create");
		addBaseParameter(new CommandParameterString()); // arenas name
		setPermission("cvgames.arenas.create");

		this.plugin = plugin;
	}

	@Override
	public CommandResponse execute(CommandSender commandSender, Set<String> set, Map<String, Object> map,
		List<Object> baseParameters) throws CommandExecutionException {
		FileConfiguration config = plugin.getConfig();

		if (config.contains("arenas." + baseParameters.get(0))) {
			throw new CommandExecutionException("Arena with name " + baseParameters.get(0) + " already exists!");
		}

		System.out.println(baseParameters.get(0));
		config.createSection("arenas." + (String) baseParameters.get(0));
		plugin.saveConfig();
		return new CommandResponse("&aCreated the arena " + baseParameters.get(0) + "!");
	}
}
