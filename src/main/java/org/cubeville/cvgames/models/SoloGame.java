package org.cubeville.cvgames.models;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class SoloGame extends BaseGame {

    @Override
    public void processPlayerMap(Map<Integer, List<Player>> playerTeamMap) {
        Player player = playerTeamMap.get(-1).get(0);
        onGameStart(player);
    }

    public abstract void onGameStart(Player player);

    protected abstract Object getState();

    public SoloGame(String id, String arenaName) {
        super(id, arenaName);
    }

    public List<ItemStack> getPlayerCompassContents() {
        List<ItemStack> items = new ArrayList<>();
        state.keySet().forEach(p -> {
            ItemStack item = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            meta.setDisplayName("§f" + p.getDisplayName());
            meta.setOwningPlayer(p);
            item.setItemMeta(meta);
            items.add(item);
        });
        return List.of();
    }
}
