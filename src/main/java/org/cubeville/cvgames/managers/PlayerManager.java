package org.cubeville.cvgames.managers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.cubeville.cvgames.models.Arena;

import java.util.HashMap;
import java.util.UUID;

public class PlayerManager {

    private static HashMap<UUID, String> uuidToArenaName = new HashMap<>();
    private static HashMap<UUID, ItemStack[]> playerInventories = new HashMap<>();


    public static void setPlayer(Player p, String name) {
        uuidToArenaName.put(p.getUniqueId(), name);
        playerInventories.put(p.getUniqueId(), p.getInventory().getContents());
        p.getInventory().clear();
    }

    public static void removePlayer(Player p) {
        uuidToArenaName.remove(p.getUniqueId());
        ItemStack[] restoredInv = playerInventories.get(p.getUniqueId());
        if (restoredInv != null) {
            p.getInventory().setContents(restoredInv);
            playerInventories.remove(p.getUniqueId());
        } else {
            p.getInventory().clear();
        }
    }

    public static Arena getPlayerArena(Player p) {
        Arena result = ArenaManager.getArena(uuidToArenaName.get(p.getUniqueId()));
        return result;
    }
}
