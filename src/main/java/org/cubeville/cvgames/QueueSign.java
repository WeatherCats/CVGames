package org.cubeville.cvgames;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class QueueSign {

	private Sign sign;
	private Arena arena;

	public QueueSign(Sign sign, Arena arena) {
		this.sign = sign;
		this.arena = arena;
		this.sign.setLine(0, arena.getName());
		this.displayStatus(arena.getStatus());
		this.displayFill();
	}


	public void displayStatus(ArenaStatus status) {
		switch (status) {
			case OPEN:
				this.sign.setLine(2, "§a§lOPEN");
				break;
			case IN_USE:
				this.sign.setLine(2, "§7§lIN USE");
				break;
			case CLOSED:
				this.sign.setLine(2, "§c§lCLOSED");
				break;
		}
		this.sign.update();
	}

	public void displayFill() {
		this.sign.setLine(1,"§l" + arena.getQueue().size() + "/" + arena.getQueue().getMaxPlayers());
		this.sign.update();
	}

	public Sign getSign() {
		return sign;
	}

	public String getArenaName() {
		return arena.getName();
	}

	public void onRightClick(Player p) {
		this.arena.getQueue().join(p);
		SignManager.updateArenaSignsFill(getArenaName());
	}
}
