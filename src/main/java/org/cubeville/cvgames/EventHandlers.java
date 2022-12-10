package org.cubeville.cvgames;

import com.sk89q.worldedit.bukkit.adapter.BukkitImplAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.SkullMeta;
import org.cubeville.cvgames.enums.ArenaStatus;
import org.cubeville.cvgames.managers.PlayerManager;
import org.cubeville.cvgames.managers.SignManager;
import org.cubeville.cvgames.models.Arena;
import org.cubeville.cvgames.models.QueueSign;
import org.cubeville.cvgames.utils.GameUtils;

import java.util.Objects;

public class EventHandlers implements Listener {

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Event.Result result;
		if (spectatorCancel(event.getPlayer())) result = Event.Result.DENY;
		else result = Event.Result.DEFAULT;
		event.setUseInteractedBlock(result);
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
		if (arena != null && arena.getQueue() != null && (arena.getStatus().equals(ArenaStatus.IN_QUEUE) || arena.getStatus().equals(ArenaStatus.HOSTING))) {
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
		if (arena != null && arena.getQueue() != null && (arena.getStatus().equals(ArenaStatus.IN_USE) || arena.getStatus().equals(ArenaStatus.HOSTING))) {
			if (event.getItem() != null) {
				if (arena.getQueue().spectatorLeaveItem().isSimilar(event.getItem())) {
					event.setCancelled(true);
					arena.getQueue().getGame().removeSpectator(event.getPlayer());
					arena.getQueue().removeSpectatorFromLobby(event.getPlayer());
				}
				if (arena.getQueue().playerCompassItem().isSimilar(event.getItem())) {
					event.setCancelled(true);
					event.getPlayer().openInventory(arena.getQueue().getGame().getPlayerCompassInventory(event.getPlayer(), 1));
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
				if (arena != null && arena.getQueue() != null && (arena.getStatus().equals(ArenaStatus.IN_QUEUE) || arena.getStatus().equals(ArenaStatus.HOSTING))) {
					arena.getQueue().setSelectedTeam((Player) event.getWhoClicked(), event.getSlot());
					event.setCancelled(true);
					event.getWhoClicked().closeInventory();
				}
			}
		}
		if (event.getView().getTitle().contains("Player Compass")) {
			if (event.getWhoClicked() instanceof Player && event.getSlot() >= 0) {
				Arena arena = PlayerManager.getPlayerArena((Player) event.getWhoClicked());
				if (arena != null && arena.getQueue() != null && (arena.getStatus().equals(ArenaStatus.IN_USE) || arena.getStatus().equals(ArenaStatus.HOSTING))) {
					event.setCancelled(true);
					if (Objects.isNull(event.getCurrentItem()) || !(event.getCurrentItem().getItemMeta() instanceof SkullMeta)) return;
					SkullMeta meta = (SkullMeta) event.getCurrentItem().getItemMeta();
					if (Objects.isNull(meta.getOwningPlayer().getPlayer())) return;
					event.getWhoClicked().teleport(meta.getOwningPlayer().getPlayer());
					event.getWhoClicked().closeInventory();
				}
			}
		}

		if (event.getViewers().size() == 0) return;
		// Is the player in a queue for an arena
		Arena arena = PlayerManager.getPlayerArena((Player) event.getViewers().get(0));
		if (arena != null && arena.getQueue() != null && (arena.getStatus().equals(ArenaStatus.IN_QUEUE) || arena.getStatus().equals(ArenaStatus.HOSTING))) {
			// If the player is moving the item to leave the queue
			if (event.getCurrentItem() != null && (event.getCurrentItem().isSimilar(arena.getQueue().queueLeaveItem()) || event.getCurrentItem().isSimilar(arena.getQueue().teamSelectorItem()))) {
				event.setCancelled(true);
			}
		}
	}
	private boolean spectatorCancel(Player player) {
		Arena arena = PlayerManager.getPlayerArena((Player) player);
		if (arena == null || !arena.getQueue().getGame().getSpectators().contains(player)) return false;
		return true;
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerHit(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player)) return;
		event.setCancelled(spectatorCancel((Player) event.getDamager()));
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerBreak(PlayerHarvestBlockEvent event) {
		event.setCancelled(spectatorCancel(event.getPlayer()));
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerPlace(BlockPlaceEvent event) {
		event.setCancelled(spectatorCancel(event.getPlayer()));
	}
}