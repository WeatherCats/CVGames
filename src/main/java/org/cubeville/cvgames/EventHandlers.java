package org.cubeville.cvgames;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.cubeville.cvgames.enums.ArenaStatus;
import org.cubeville.cvgames.managers.PlayerLogoutManager;
import org.cubeville.cvgames.managers.SignManager;
import org.cubeville.cvgames.models.Arena;
import org.cubeville.cvgames.models.QueueSign;

public class EventHandlers implements Listener {

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getClickedBlock() != null &&
			(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) &&
			SignManager.signMaterials.contains(event.getClickedBlock().getType())
		) {
			QueueSign sign = SignManager.getSign(event.getClickedBlock().getLocation());
			if (sign == null) {
				return;
			}
			sign.onRightClick(event.getPlayer());
		}

		// Is the player in a queue for an arena
		Arena arena = PlayerLogoutManager.getPlayerArena(event.getPlayer());
		if (arena != null && arena.getQueue() != null && arena.getStatus().equals(ArenaStatus.OPEN)) {
			// If the player is holding the item to leave the queue
			if (event.getPlayer().getInventory().getItemInMainHand().equals(arena.getQueue().queueLeaveItem())) {
				event.setCancelled(true);
				arena.getQueue().leave(event.getPlayer());
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Arena arena = PlayerLogoutManager.getPlayerArena(e.getPlayer());
		if (arena != null) {
			arena.playerLogoutCleanup(e.getPlayer());
			PlayerLogoutManager.removePlayer(e.getPlayer());
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getViewers().size() == 0) return;
		// Is the player in a queue for an arena
		Arena arena = PlayerLogoutManager.getPlayerArena((Player) event.getViewers().get(0));
		if (arena != null && arena.getQueue() != null && arena.getStatus().equals(ArenaStatus.OPEN)) {
			// If the player is moving the item to leave the queue
			if (event.getCurrentItem() != null && event.getCurrentItem().equals(arena.getQueue().queueLeaveItem())) {
				event.setCancelled(true);
			}
		}
	}
}

