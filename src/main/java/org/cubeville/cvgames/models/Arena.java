package org.cubeville.cvgames.models;

import org.bukkit.entity.Player;
import org.cubeville.cvgames.managers.SignManager;
import org.cubeville.cvgames.enums.ArenaStatus;
import org.cubeville.cvgames.utils.GameUtils;

public class Arena {

	private String name;
	private BaseGame game;
	private GameQueue queue;
	private ArenaStatus status;


	public Arena(String name) {
		this.name = name;
	}

	public void setGame(BaseGame game) {
		this.game = game;
		this.queue = new GameQueue(this);
		this.status = ArenaStatus.OPEN;
	}

	public BaseGame getGame() {
		return game;
	}

	public String getName() {
		return name;
	}

	public void setStatus(ArenaStatus status) {
		SignManager.updateArenaSignsStatus(getName(), status);
		this.status = status;
	}

	public ArenaStatus getStatus() {
		return status;
	}

	public GameQueue getQueue() {
		return queue;
	}

	public void playerLogoutCleanup(Player p) {
		queue.whenPlayerLogout(p, this);
		if (status != ArenaStatus.OPEN) {
			game.whenPlayerLogout(p, this);
		}
	}

	public String teamSelectorInventoryName() {
		return GameUtils.teamSelectorPrefix + name;
	}
}
