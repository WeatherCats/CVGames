package org.cubeville.cvgames.games;

import org.bukkit.entity.Player;
import org.cubeville.cvgames.models.Game;

import java.util.List;

public class SurvivalGames extends Game {

    public SurvivalGames(String id) {
        super(id);
    }

    @Override
    public void onPlayerLogout(Player p) {

    }

    @Override
    public void onGameStart(List<Player> players) {

    }

    @Override
    public void onGameFinish(List<Player> players) {

    }
}
