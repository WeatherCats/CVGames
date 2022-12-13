package org.cubeville.cvgames.models;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.cubeville.cvgames.enums.ArenaStatus;
import org.cubeville.cvgames.managers.SignManager;

import java.util.ArrayList;
import java.util.List;

public class QueueSign {

	private Sign sign;
	private Arena arena;
	private String gameName;

	public QueueSign(Sign sign, Arena arena, String gameName) {
		this.sign = sign;
		this.arena = arena;
		this.gameName = gameName;
		if (this.sign != null) {
			this.sign.setLine(0, arena.getName());
			this.displayStatus(arena.getStatus());
			this.displayFill();
			this.displayGameName(null);
		}
	}

	public void displayStatus(ArenaStatus status) {
		switch (status) {
			case OPEN:
				this.sign.setLine(2, "§a§lOPEN");
				break;
			case IN_QUEUE:
				this.sign.setLine(2, "§e§lIN_QUEUE");
				break;
			case IN_USE:
				this.sign.setLine(2, "§7§lIN USE");
				break;
			case HOSTING:
				this.sign.setLine(2, "§b§lHOSTING");
				break;
			case CLOSED:
				this.sign.setLine(2, "§c§lCLOSED");
				break;
		}
		this.sign.update();
	}

	public void displayFill() {
		if (arena.getStatus() != ArenaStatus.IN_USE) {
			this.sign.setLine(1,"§l" + arena.getQueue().size() + "/" + arena.getQueue().getMaxPlayers());
		}
		else {
			String spectatorString = "";
			List<Player> spectators = new ArrayList<>(arena.getQueue().getGame().getSpectators());
			spectators.removeAll(arena.getQueue().getGame().state.keySet());
			if (arena.getQueue().getGame().spectators.size() > 0)
				spectatorString = " §7§o(+" + spectators.size() + ")";
			this.sign.setLine(1, "§l" + arena.getQueue().getGame().state.keySet().size() + "/" + arena.getQueue().getMaxPlayers() + spectatorString);
		}
		this.sign.update();
	}

	public void displayGameName(String selectedGame) {
		if (selectedGame == null) {
			this.sign.setLine(3, gameName);
		} else {
			this.sign.setLine(3, selectedGame);
		}
	}

	public String getArenaName() {
		return arena.getName();
	}

	public void onRightClick(Player p) {
		this.arena.getQueue().join(p, gameName);
		SignManager.updateArenaSignsFill(getArenaName());
	}
}
