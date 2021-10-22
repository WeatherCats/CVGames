package org.cubeville.cvgames;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventHandlers implements Listener {

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getClickedBlock() != null &&
			event.getAction() == Action.RIGHT_CLICK_BLOCK &&
			SignManager.signMaterials.contains(event.getClickedBlock().getType())
		) {
			QueueSign sign = SignManager.getSign(event.getClickedBlock().getLocation());
			if (sign == null) {
				return;
			}
			sign.onRightClick(event.getPlayer());
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		PlayerLogoutManager.getPlayerArena(e.getPlayer()).playerLogoutCleanup(e.getPlayer());
	}
}

