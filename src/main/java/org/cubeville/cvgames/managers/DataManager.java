package org.cubeville.cvgames.managers;

import org.bukkit.entity.Player;
import org.cubeville.cvgames.database.GamesDB;
import org.cubeville.cvgames.models.Arena;
import org.cubeville.cvgames.models.BaseGame;
import org.cubeville.cvgames.models.MetricKey;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DataManager {
    private GamesDB db;
    private Map<String, List<UUID>> flags = new HashMap<>();
    private Map<MetricKey, Long> metrics = new HashMap<>();

    public DataManager() {
        // database setup
        db = new GamesDB();
        try {
            db.createBackup();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        db.load();
        importFlags();
        importMetrics();
    }

    private void importFlags() {
        try {
            ResultSet flagsSet = db.getAllFlags();
            if (flagsSet == null) { return; }
            while (flagsSet.next()) {
                UUID playerUUID = UUID.fromString(flagsSet.getString("uuid"));
                String flagName = flagsSet.getString("name");
                importFlag(playerUUID, flagName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void importMetrics() {
        try {
            ResultSet flagsSet = db.getAllMetrics();
            if (flagsSet == null) { return; }
            while (flagsSet.next()) {
                String arenaName = flagsSet.getString("arena");
                String gameName = flagsSet.getString("game");
                String metricName = flagsSet.getString("name");
                long metricValue = flagsSet.getLong("value");
                importMetric(arenaName, gameName, metricName, metricValue);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add a flag to a specific player
     * @param player The player you are adding a flag for
     * @param flagName The name of the flag you are adding
     */
    public void addFlag(Player player, String flagName) {
        if (!flags.containsKey(flagName)) {
            flags.put(flagName, List.of(player.getUniqueId()));
        } else {
            flags.get(flagName).add(player.getUniqueId());
        }
        db.addFlag(player, flagName);
    }

    private void importFlag(UUID uuid, String flagName) {
        if (!flags.containsKey(flagName)) {
            flags.put(flagName, List.of(uuid));
        } else {
            flags.get(flagName).add(uuid);
        }
    }

    /**
     * Remove a flag from a specific player
     * @param player The player you are removing a flag for
     * @param flagName The name of the flag you are removing
     */
    public void removeFlag(Player player, String flagName) {
        // if the flag is not defined, then by default the player doesn't have the flag
        // therefore we can safely return without doing anything
        if (!flags.containsKey(flagName)) {
            return;
        }
        flags.get(flagName).remove(player.getUniqueId());
        db.removeFlag(player, flagName);
    }

    /**
     * Check whether a player has a flag or not
     * @param player The player you are checking the flag for
     * @param flagName The name of the flag you are checking
     * @return Whether the flag is true for the player or not
     */
    public boolean hasFlag(Player player, String flagName) {
        if (!flags.containsKey(flagName)) return false;
        return flags.get(flagName).contains(player.getUniqueId());
    }

    /**
     * Increase the value of the metric by 1
     * @param game The game the metric is being sent from
     * @param metricName The name of the metric being increased
     */
    public void increaseMetric(BaseGame game, String metricName) {
        MetricKey metricKey = new MetricKey(game.getArena().getName(), game.getId(), metricName);
        if (!metrics.containsKey(metricKey)) {
            metrics.put(metricKey, 1L);
            db.addMetric(game, metricName);
        } else {
            long newValue = metrics.get(metricKey) + 1;
            metrics.put(metricKey, newValue);
            db.setMetric(game, metricName, newValue);
        }
    }

    public Map<MetricKey, Long> getArenaMetrics(Arena arena) {
        Map<MetricKey, Long> output = new HashMap<>();
        for (MetricKey key : metrics.keySet()) {
            if (!key.arenaName.equalsIgnoreCase(arena.getName())) continue;
            output.put(key, metrics.get(key));
        }
        return output;
    }

    public Map<MetricKey, Long> getGameMetrics(String gameName) {
        Map<MetricKey, Long> output = new HashMap<>();
        for (MetricKey key : metrics.keySet()) {
            if (!key.gameName.equalsIgnoreCase(gameName)) continue;
            output.put(key, metrics.get(key));
        }
        return output;
    }

    public Map<MetricKey, Long> getSearchMetrics(String search) {
        Map<MetricKey, Long> output = new HashMap<>();
        for (MetricKey key : metrics.keySet()) {
            if (!key.metricName.contains(search.toLowerCase())) continue;
            output.put(key, metrics.get(key));
        }
        return output;
    }

    private void importMetric(String arenaName, String gameName, String metricName, long value) {
        metrics.put(new MetricKey(arenaName, gameName, metricName), value);
    }

}
