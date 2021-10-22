package org.cubeville.cvgames;

import org.cubeville.commons.commands.Command;

abstract public class ArenaCommand extends Command {

	protected String arenaName;

	public ArenaCommand(String fullCommand) {
		super(fullCommand);
	}

	public void setArenaName(String arenaName) {
		this.arenaName = arenaName;
	}
}
