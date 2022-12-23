package org.cubeville.cvgames.models;

import com.google.common.collect.ImmutableSet;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.cubeville.cvgames.CVGames;
import org.cubeville.cvgames.enums.ArenaStatus;
import org.cubeville.cvgames.managers.ArenaManager;
import org.cubeville.cvgames.managers.PlayerManager;
import org.cubeville.cvgames.utils.GameUtils;
import org.cubeville.cvgames.vartypes.*;

import javax.annotation.Nullable;
import java.util.*;

abstract public class BaseGame implements PlayerContainer, Listener {
    private final String id;
    protected Arena arena;
    protected HashMap<Player, PlayerState> state = new HashMap<>();
    protected List<Player> spectators = new ArrayList<>();
    private int arenaRegionTask;
    public boolean isRunningGame = false;

    public BaseGame(String id, String arenaName) {
        this.id = id;
        this.arena = ArenaManager.getArena(arenaName);
        this.arena.addGameVariable("region", new GameVariableRegion("A region surrounding both the game and the lobby of an arena -- if players leave this arena, they will automatically be removed from the game and queue"));
        this.arena.addGameVariable("spectator-spawn", new GameVariableLocation("The location spectators spawn at."));

    }

    @Override
    public void whenPlayerLogout(Player p, Arena a) {
        kickPlayerFromGame(p, true);
    }

    private void kickPlayerFromGame(Player p, boolean teleportToExit) {
        if (spectators.contains(p)) removeSpectator(p);
        if (state.containsKey(p)) {
            PlayerManager.removePlayer(p);
            onPlayerLeave(p);
            state.remove(p);
            p.getInventory().clear();
            showSpectators(p);
        }
        if (teleportToExit) { p.teleport((Location) this.getVariable("exit")); }
        if (isRunningGame && state.isEmpty()) { finishGame(); }
    }

    public abstract void onPlayerLeave(Player p);

    protected abstract PlayerState getState(Player p);

    public String getId() {
        return id;
    }
    public List<Player> getSpectators() {
        return spectators;
    }
    public void addSpectator(Player player) {
        spectators.add(player);
        player.teleport((Location) getVariable("spectator-spawn"));
        player.setAllowFlight(true);
        for (Player gp : getArena().getQueue().getPlayerSet()) {
            gp.hidePlayer(CVGames.getInstance(), player);
        }
        hideSpectators(player);
    }

    public void removeSpectator(Player player) {
        spectators.remove(player);
        player.setAllowFlight(false);
        for (Player gp : getArena().getQueue().getPlayerSet()) {
            gp.showPlayer(CVGames.getInstance(), player);
        }
    }
    public void showSpectators(Player player) {
        for (Player sp : spectators) {
            player.showPlayer(CVGames.getInstance(), sp);
        }
    }
    public void hideSpectators(Player player) {
        for (Player sp : spectators) {
            player.hidePlayer(CVGames.getInstance(), sp);
        }
    }

    public Inventory getPlayerCompassInventory(Player player, int page) {
        List<ItemStack> contents = getPlayerCompassContents();
        int invSize = (1 + (contents.size() / 9)) * 9;
        if (invSize > 45) {
            invSize = 54;
            contents = contents.subList(0, 45);
        }
        Inventory inv = Bukkit.createInventory(player, invSize, "Player Compass");
        int i = 0;
        for (ItemStack item : contents) {
            inv.setItem(i, item);
            i++;
        }
        /* TODO Page logic (If I really need to bother)
        if (invSize > 45) {
            for (i = invSize - 9; i < invSize; i++) {
                if (i == 45) {

                }
            }
        } */
        return inv;
    }

    //TODO Would be nice to have team game compass split players into team-based sections
    public abstract List<ItemStack> getPlayerCompassContents();

    public void startGame(Map<Integer, List<Player>> playerTeamMap) {
        playerTeamMap.values().forEach(players ->
                players.forEach(player -> {
                    player.closeInventory();
                    player.getInventory().clear();
                })
        );
        startArenaRegionCheck();
        processPlayerMap(playerTeamMap);
        if (arena.getStatus().equals(ArenaStatus.HOSTING)) {
            arena.getQueue().getPlayerSet().forEach(player -> {
                if (state.keySet().contains(player)) return;
                player.getInventory().setItem(0, this.getArena().getQueue().playerCompassItem());
                addSpectator(player);
            });
        }
        isRunningGame = true;
    };

    public void finishGame() {
        if (!arena.getStatus().equals(ArenaStatus.HOSTING)) { arena.setStatus(ArenaStatus.OPEN); }
        List<Player> spectatorList = new ArrayList<>(this.spectators);
        spectatorList.forEach(player -> {
            removeSpectator(player);
            if (state.containsKey(player)) return;
            if (arena.getStatus().equals(ArenaStatus.HOSTING)) {
                player.teleport((Location) getVariable("lobby"));
                player.getInventory().clear();
                arena.getQueue().setLobbyInventory(player.getInventory());
            } else {
                PlayerManager.removePlayer(player);
                player.teleport((Location) getVariable("exit"));
                player.getInventory().clear();
            }
        });
        arena.getQueue().clear();
        killArenaRegionCheck();
        this.state.keySet().forEach(player -> {
            showSpectators(player);
            if (arena.getStatus().equals(ArenaStatus.HOSTING)) {
                player.teleport((Location) getVariable("lobby"));
                player.getInventory().clear();
                arena.getQueue().setLobbyInventory(player.getInventory());
            } else {
                PlayerManager.removePlayer(player);
                player.teleport((Location) getVariable("exit"));
                player.getInventory().clear();
            }
        });
        onGameFinish();
        state.clear();
        isRunningGame = false;
    };

    public abstract void processPlayerMap(Map<Integer, List<Player>> playerTeamMap);

    public abstract void onGameFinish();

    private void startArenaRegionCheck() {
        GameRegion gameRegion = (GameRegion) getVariable("region");

        arenaRegionTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(CVGames.getInstance(), () -> {
            Set<Player> players = ImmutableSet.copyOf(state.keySet());
            for (Player player : players) {
                if (!gameRegion.containsPlayer(player)) {
                    kickPlayerFromGame(player, false);
                    player.sendMessage("§cYou have left the game!");
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, .7F);
                }
            }
            Set<Player> spectators = ImmutableSet.copyOf(this.spectators);
            for (Player player : spectators) {
                if (!gameRegion.containsPlayer(player)) {
                    player.teleport((Location) getVariable("spectator-spawn"));
                    player.sendMessage("§cYou left the playing area!");
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, .7F);
                }
            }
        }, 0L, 20L);
    }

    private void killArenaRegionCheck() {
        Bukkit.getScheduler().cancelTask(arenaRegionTask);
        arenaRegionTask = -1;
    }

    public Object getVariable(String var) {
        return arena.getVariable(var);
    }

    public void addGameVariable(String varName, GameVariable variable) {
        addGameVariable(varName, variable, null);
    }

    public void addGameVariable(String varName, GameVariable variable, @Nullable Object defaultValue) {
        arena.addGameVariable(varName, variable, defaultValue);
    }

    public void addGameVariableObjectList(String varName, HashMap<String, GameVariable> fields, String description) {
        arena.addGameVariableObjectList(varName, fields, description);
    }

    public void addGameVariableObjectList(String varName, HashMap<String, GameVariable> fields) {
        arena.addGameVariableObjectList(varName, fields, "");
    }

    public void addGameVariableTeamsList(HashMap<String, GameVariable> fields) {
        arena.addGameVariableTeamsList(fields);
    }

    protected void sendScoreboardToArena(Scoreboard scoreboard) {
        arena.getQueue().getPlayerSet().forEach(p -> p.setScoreboard(scoreboard));
    }

    public void sendMessageToArena(String message) {
        arena.getQueue().getPlayerSet().forEach(p -> p.sendMessage(message));
    }

    public void updateDefaultScoreboard(int currentTime, String key, boolean isReverse) {
        Scoreboard scoreboard;
        ArrayList<String> scoreboardLines = new ArrayList<>();

        scoreboardLines.add("§bTime remaining: §f" +
                String.format("%d:%02d", currentTime / 60000, (currentTime / 1000) % 60)
        );
        scoreboardLines.add("   ");

        state.keySet().stream().sorted(Comparator.comparingInt(o -> (isReverse ? 1 : -1) * getState(o).getSortingValue())).forEach(p -> {
            int points = getState(p).getSortingValue();
            scoreboardLines.add("§a" + p.getDisplayName() + "§f: " + points + " " + key);
        });
        scoreboard = GameUtils.createScoreboard(arena, "§b§lFFA " + getId(), scoreboardLines);
        sendScoreboardToArena(scoreboard);
    }

    public Arena getArena() {
        return arena;
    }

    public void onCountdown(int counter) {}

}
