package org.cubeville.cvgames.commands;

import org.bukkit.command.CommandSender;
import org.cubeville.commons.commands.BaseCommand;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvgames.managers.ArenaManager;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DeleteArena extends BaseCommand {

	public DeleteArena() {
		super("arena delete");
		addBaseParameter(new CommandParameterString()); // arena name
		setPermission("cvgames.arenas.delete");
	}

	@Override
	public CommandResponse execute(CommandSender commandSender, Set<String> set, Map<String, Object> map,
		List<Object> baseParameters) throws CommandExecutionException {

		String arenaName = ArenaManager.filterArenaInput((String) baseParameters.get(0));
		ArenaManager.deleteArena(arenaName);

		return new CommandResponse("&aDeleted the arena " + arenaName + "!");
	}
}
