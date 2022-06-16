package org.cubeville.cvgames.models;

import org.bukkit.entity.Player;

import java.util.*;

public abstract class TeamSelectorGame extends BaseGame {

    public TeamSelectorGame(String id, String arenaName) {
        super(id, arenaName);
    }

    @Override
    public void processPlayerMap(Map<Integer, List<Player>> playerTeamMap) {
        // Should map team index to player list
        // -1 means they do not have a team selected
        List<Set<Player>> teamPlayers = new ArrayList<>();
        for (int i = 0; i < playerTeamMap.size() - 1; i++) {
            if (playerTeamMap.containsKey(i)) {
                teamPlayers.add(new HashSet<>(playerTeamMap.get(i)));
            } else {
                teamPlayers.add(new HashSet<>());
            }
        }

        List<Player> unselectedPlayers = playerTeamMap.get(-1);
        Collections.shuffle(unselectedPlayers);

        for (Player player : unselectedPlayers) {
            // find the smallest team and add the unselected players to the smallest teams
            int smallestTeamSize = Integer.MAX_VALUE;
            int smallestTeamIndex = -1;
            for (int i = 0; i < teamPlayers.size(); i++) {
                if (teamPlayers.get(i).size() < smallestTeamSize) {
                    smallestTeamSize = teamPlayers.get(i).size();
                    smallestTeamIndex = i;
                }
            }
            teamPlayers.get(smallestTeamIndex).add(player);
        }

        onGameStart(teamPlayers);
    }

    public abstract void onGameStart(List<Set<Player>> teamPlayers);

    public List<HashMap<String, Object>> getTeamVariable() {
        List<HashMap<String, Object>> teams = (List<HashMap<String, Object>>) getVariable("teams");
        return teams;
    }
}
