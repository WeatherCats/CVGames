package org.cubeville.cvgames.models;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scoreboard.Scoreboard;
import org.cubeville.cvgames.utils.GameUtils;

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

    protected abstract PlayerState getState(Player p);

    public List<HashMap<String, Object>> getTeamVariable() {
        return (List<HashMap<String, Object>>) getVariable("teams");
    }

    public void updateDefaultScoreboard(int currentTime, ArrayList<Integer[]> teamScores, String key) {
        updateDefaultScoreboard(currentTime, teamScores, key, false);
    }

    public void updateDefaultScoreboard(int currentTime, ArrayList<Integer[]> teamScores, String key, boolean isReverse) {
        List<HashMap<String, Object>> teams = getTeamVariable();
        if (teams.size() == 1) {
            updateDefaultScoreboard(currentTime, key, isReverse);
            return;
        }
        Scoreboard scoreboard;
        ArrayList<String> scoreboardLines = new ArrayList<>();

        scoreboardLines.add("§bTime remaining: §f" +
                String.format("%d:%02d", currentTime / 60000, (currentTime / 1000) % 60)
        );
        scoreboardLines.add("   ");
        for (int i = 0; i < teamScores.size(); i++) {
            String line = teams.get(i).get("name") + "§f: ";
            line += teamScores.get(i)[1];
            line += " ";
            line += key;
            scoreboardLines.add(line);
        }
        scoreboard = GameUtils.createScoreboard(arena, "§b§lTeam " + getId(), scoreboardLines);
        sendScoreboardToArena(scoreboard);
    }
    public List<ItemStack> getPlayerCompassContents() {
        List<ItemStack> items = new ArrayList<>();
        state.keySet().stream().sorted(Comparator.comparingInt(o -> -1 * getState(o).getSortingValue())).forEach(p -> {
            ItemStack item = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            meta.setDisplayName("§f" + p.getDisplayName());
            meta.setOwningPlayer(p);
            item.setItemMeta(meta);
            items.add(item);
        });
        return items;
    }
}
