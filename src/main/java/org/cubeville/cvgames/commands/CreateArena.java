package org.cubeville.cvgames.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.commons.commands.BaseCommand;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvgames.ArenaManager;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CreateArena extends BaseCommand {

	public CreateArena() {
		super("arena create");
		addBaseParameter(new CommandParameterString()); // arena name
		setPermission("cvgames.arenas.create");
	}

	@Override
	public CommandResponse execute(CommandSender commandSender, Set<String> set, Map<String, Object> map,
		List<Object> baseParameters) throws CommandExecutionException {
		String arenaName = ((String) baseParameters.get(0)).toLowerCase();

		if (ArenaManager.hasArena(arenaName)) {
			throw new CommandExecutionException("Arena with name " + arenaName + " already exists!");
		}

		ArenaManager.createArena(arenaName);
		return new CommandResponse("&aCreated the arena " + arenaName + "!");
	}
}
