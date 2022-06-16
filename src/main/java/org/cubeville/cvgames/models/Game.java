package org.cubeville.cvgames.models;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

}
