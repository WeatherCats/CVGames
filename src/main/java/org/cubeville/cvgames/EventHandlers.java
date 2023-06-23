package org.cubeville.cvgames;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
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
                if (InterfaceItems.QUEUE_LEAVE_ITEM.isSimilar(event.getItem())) {
                    event.setCancelled(true);
                    arena.getQueue().leave(event.getPlayer());
                // If the player is holding the team selector item, open the team selector
                } else if (InterfaceItems.TEAM_SELECTOR_ITEM.isSimilar(event.getItem())) {
                    event.setCancelled(true);
                    arena.getQueue().openTeamSelector(event.getPlayer());
                }
            }
        }
        if (arena != null && arena.getQueue() != null && (arena.getStatus().equals(ArenaStatus.IN_USE) || arena.getStatus().equals(ArenaStatus.HOSTING))) {
            if (event.getItem() != null) {
                if (InterfaceItems.SPECTATE_LEAVE_ITEM.isSimilar(event.getItem())) {
                    event.setCancelled(true);
                    arena.getQueue().getGame().removeSpectator(event.getPlayer());
                    arena.getQueue().removeSpectatorFromLobby(event.getPlayer());
                }
                if (InterfaceItems.SPECTATE_PLAYER_NAV_ITEM.isSimilar(event.getItem())) {
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
            if (event.getCurrentItem() != null && (
                event.getCurrentItem().isSimilar(InterfaceItems.QUEUE_LEAVE_ITEM) ||
                event.getCurrentItem().isSimilar(InterfaceItems.TEAM_SELECTOR_ITEM) ||
                event.getCurrentItem().isSimilar(InterfaceItems.SPECTATE_LEAVE_ITEM) ||
                event.getCurrentItem().isSimilar(InterfaceItems.SPECTATE_PLAYER_NAV_ITEM)
            )) {
                event.setCancelled(true);
            }
        }
    }
    private boolean spectatorCancel(Player player) {
        Arena arena = PlayerManager.getPlayerArena(player);
        if (arena == null || arena.getQueue().getGame() == null || !arena.getQueue().getGame().getSpectators().contains(player)) return false;
        return true;
    }
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerHitByPlayer(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) { return; }
        Player player;
        if (!(event.getDamager() instanceof Player)) {
            if ((event.getDamager() instanceof Projectile)) {
                Projectile projectile = (Projectile) event.getDamager();
                if (!(projectile.getShooter() instanceof Player)) return;
                else player = (Player) projectile.getShooter();
            }
            else return;
        }
        else player = (Player) event.getDamager();
        if (spectatorCancel(player)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerHit(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) { return; }
        if (spectatorCancel((Player) event.getEntity())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerBreak(PlayerHarvestBlockEvent event) {
        if (spectatorCancel(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerPlace(BlockPlaceEvent event) {
        if (spectatorCancel(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerPickupArrow(PlayerPickupArrowEvent event) {
        if (spectatorCancel(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (spectatorCancel((Player) event.getEntity())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (spectatorCancel(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (spectatorCancel((Player) event.getEntity())) event.setCancelled(true);
    }
}