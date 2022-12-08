package org.cubeville.cvgames.models;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.cubeville.cvgames.utils.GameUtils;

import java.util.*;

public abstract class Game extends BaseGame {
    public Game(String id, String arenaName) {
        super(id, arenaName);
    }

    @Override
    public void processPlayerMap(Map<Integer, List<Player>> playerTeamMap) {
        Set<Player> players = new HashSet<>();
        playerTeamMap.values().forEach(players::addAll);
        onGameStart(players);
    }

    public abstract void onGameStart(Set<Player> players);

    protected abstract PlayerState getState(Player p);

    public List<ItemStack> getPlayerCompassContents() {
        List<ItemStack> items = new ArrayList<>();
        state.keySet().stream().sorted(Comparator.comparingInt(o -> -1 * getState(o).getSortingValue())).forEach(p -> {
            ItemStack item = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            meta.setOwningPlayer(p);
            item.setItemMeta(meta);
            items.add(item);
        });
        return items;
    }

}
