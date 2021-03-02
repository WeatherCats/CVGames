package org.cubeville.cvgames;

import org.bukkit.command.CommandSender;
import org.cubeville.commons.commands.BaseCommand;
import org.cubeville.commons.commands.CommandResponse;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class GamesTestCommand extends BaseCommand {

	public GamesTestCommand() {
		super("test");
		setPermission("cvgames.test");
	}

	@Override
	public CommandResponse execute(CommandSender sender, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters) {
		return new CommandResponse("&bHello world!");
	}

}
