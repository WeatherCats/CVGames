package org.cubeville.cvgames;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.cubeville.cvgames.enums.ArenaStatus;
import org.cubeville.cvgames.managers.PlayerManager;
import org.cubeville.cvgames.managers.SignManager;
import org.cubeville.cvgames.models.Arena;
import org.cubeville.cvgames.models.QueueSign;
import org.cubeville.cvgames.utils.GameUtils;

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
		Arena arena = PlayerManager.getPlayerArena(event.getPlayer());
		if (arena != null && arena.getQueue() != null && arena.getStatus().equals(ArenaStatus.OPEN)) {
			if (event.getItem() != null) {
				// If the player is holding the item to leave the queue, have them leave it
				if (arena.getQueue().queueLeaveItem().isSimilar(event.getItem())) {
					event.setCancelled(true);
					arena.getQueue().leave(event.getPlayer());
				// If the player is holding the team selector item, open the team selector
				} else if (arena.getQueue().teamSelectorItem().isSimilar(event.getItem())) {
					event.setCancelled(true);
					arena.getQueue().openTeamSelector(event.getPlayer());
				}
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Arena arena = PlayerManager.getPlayerArena(e.getPlayer());
		if (arena != null) {
			arena.playerLogoutCleanup(e.getPlayer());
			PlayerManager.removePlayer(e.getPlayer());
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getView().getTitle().contains(GameUtils.teamSelectorPrefix)) {
			if (event.getWhoClicked() instanceof Player && event.getClick().isLeftClick() && event.getSlot() >= 0) {
				Arena arena = PlayerManager.getPlayerArena((Player) event.getWhoClicked());
				if (arena != null && arena.getQueue() != null && arena.getStatus().equals(ArenaStatus.OPEN)) {
					arena.getQueue().setSelectedTeam((Player) event.getWhoClicked(), event.getSlot());
					event.setCancelled(true);
					event.getWhoClicked().closeInventory();
				}
			}
		}

		if (event.getViewers().size() == 0) return;
		// Is the player in a queue for an arena
		Arena arena = PlayerManager.getPlayerArena((Player) event.getViewers().get(0));
		if (arena != null && arena.getQueue() != null && arena.getStatus().equals(ArenaStatus.OPEN)) {
			// If the player is moving the item to leave the queue
			if (event.getCurrentItem() != null && event.getCurrentItem().equals(arena.getQueue().queueLeaveItem())) {
				event.setCancelled(true);
			}
		}
	}
}

