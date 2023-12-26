package org.cubeville.cvgames.models;

import com.google.common.collect.ImmutableSet;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.cubeville.cvgames.CVGames;
import org.cubeville.cvgames.InterfaceItems;
import org.cubeville.cvgames.enums.ArenaStatus;
import org.cubeville.cvgames.managers.PlayerManager;
import org.cubeville.cvgames.managers.SignManager;
import org.cubeville.cvgames.utils.GameUtils;
import org.cubeville.cvgames.vartypes.*;

import javax.annotation.Nullable;
import java.util.*;

public class GameQueue implements PlayerContainer {

    private Map<Integer, List<Player>> playerTeams = new HashMap<>();
    private Set<Player> playerLobby = new HashSet<>();
    private Arena arena;
    private Integer countdownTimer = null;
    private int counter;
    private Integer arenaLobbyRegionTask = null;
    private Player host;
    private String selectedGame;

    public GameQueue(Arena arena) {
        this.arena = arena;
    }

    public void setGameQueueVariables(BaseGame game) {
        if (!(game instanceof SoloGame)) {
            arena.addGameVariable("queue-min", new GameVariableInt("The minimum players needed to start the game"));
            arena.addGameVariable("queue-max", new GameVariableInt("The maximum players the game can hold"));
            arena.addGameVariable("lobby", new GameVariableLocation("The waiting lobby for players"));
            arena.addGameVariable("countdown-length", new GameVariableInt("The time it takes for a countdown when the minimum is reached"), 20);
        }
        arena.addGameVariable("exit", new GameVariableLocation("The exit location for players leaving the arena"));
        arena.addGameVariable("signs", new GameVariableList<>(GameVariableQueueSign.class, "The signs that players can right click to join this arena"));
        if (game instanceof TeamSelectorGame) {
            arena.addGameVariable("team-selector", new GameVariableFlag("If true, players will be able to select their own teams in this game"), true);
        }
    }

    private boolean canJoinQueue(Player p) {
        Set<Player> players = getPlayerSet();
        if (arena.getStatus().equals(ArenaStatus.IN_USE)) {
            p.sendMessage(ChatColor.RED + "This arena is currently in use. Please try again later.");
            return false;
        }
        if (arena.getStatus().equals(ArenaStatus.CLOSED)) {
            p.sendMessage(ChatColor.RED + "This arena is currently closed. Please try again later.");
            return false;
        }
        if (players.contains(p)) {
            p.sendMessage(ChatColor.RED + "You are already in this queue!");
            return false;
        }
        if (players.size() >= getMaxPlayers() && !arena.getStatus().equals(ArenaStatus.HOSTING)) {
            p.sendMessage(ChatColor.RED + "This arena is full!");
            return false;
        }
        return true;
    }

    public void startArenaLobbyRegionCheck() {
        GameRegion gameRegion = (GameRegion) arena.getVariable("region");

        arenaLobbyRegionTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(CVGames.getInstance(), () -> {
            Set<Player> lobby = ImmutableSet.copyOf(playerLobby);

            for (Player player : lobby) {
                // Catches the case where a host leaves and kicks the players out, so they are no longer in the lobby.
                if (!playerLobby.contains(player)) { continue; }
                if (!gameRegion.containsPlayer(player) && (getGame() == null || !getGame().isRunningGame)) {
                    leave(player, false);
                }
            }
        }, 0L, 20L);
    }

    private void killArenaLobbyRegionCheck() {
        if (arenaLobbyRegionTask == null) { return; }
        Bukkit.getScheduler().cancelTask(arenaLobbyRegionTask);
        arenaLobbyRegionTask = null;
    }

    public Set<Player> getPlayerSet() {
        return playerLobby;
    }

    private void setSelectedGame(@Nullable String gameName) {
        selectedGame = gameName;
        SignManager.updateArenaSignsGame(arena.getName(), gameName);
    }

    public boolean join(Player p) {
        return join(p, null);
    }

    public boolean join(Player p, @Nullable String gameName) {
        if ((boolean) arena.getVariable("spectate-enabled") && !Objects.isNull(this.getGame()) && this.getGame().isRunningGame) {
            this.setSpectatorToLobby(p);
            this.getGame().addSpectator(p);
            // they successfully joined the game as a spectator, so don't do error handling
            return true;
        }
        if (!canJoinQueue(p)) {
            return false;
        }
        if (selectedGame == null && gameName != null) {
            setSelectedGame(gameName);
        }
        if (getGame() instanceof SoloGame) {
            // Start the game with the single player
            PlayerManager.setPlayer(p, arena.getName());
            this.startGame(Map.of(-1, List.of(p)));
            // they successfully joined the solo game, so don't do error handling
            return true;
        }

        if (arena.getStatus().equals(ArenaStatus.OPEN)) {
            arena.setStatus(ArenaStatus.IN_QUEUE);
        }

        // let's be extra careful, there's no harm in making sure the map is consistent with each join
        if (getGame() instanceof TeamSelectorGame) {
            int numberOfTeams = ((TeamSelectorGame) getGame()).getTeamVariable().size();
            for (int i = -1; i < numberOfTeams; i++) {
                playerTeams.putIfAbsent(i, new ArrayList<>());
            }
        } else {
            playerTeams.putIfAbsent(-1, new ArrayList<>());
        }
        PlayerManager.setPlayer(p, arena.getName());
        setPlayerToLobby(p);
        if (!arena.getStatus().equals(ArenaStatus.HOSTING)) {
            playerTeams.get(-1).add(p);
            getGame().onPlayerJoinGame(p);
            Set<Player> players = getPlayerSet();
            if (players.size() == getMinPlayers()) {
                int countdownLength = 20;
                if (arena.getVariable("countdown-length") != null) {
                        if (arena.getGameVariable("countdown-length") instanceof GameVariableInt) {
                                countdownLength = (int) arena.getVariable("countdown-length");
                        }
                }
                startCountdown(countdownLength);
            }
            final int SPEED_COUNTDOWN = 6;
            if (players.size() == getMaxPlayers() && counter > SPEED_COUNTDOWN) {
                endCountdown();
                GameUtils.messagePlayerList(players, "§bQueue has been filled, starting game.", Sound.BLOCK_DISPENSER_DISPENSE);
                startCountdown(SPEED_COUNTDOWN);
            }
        }
        return true;
    }

    public void addToHostedGame(Player p) throws Error {
        if (!getPlayerSet().contains(p)) throw new Error("Player " + p.getDisplayName() + " is not in the lobby!");
        int playerLobbySize = 0;
        for (Integer key : playerTeams.keySet()) {
            playerLobbySize += playerTeams.get(key).size();
        }
        if (playerLobbySize >= getMaxPlayers()) throw new Error("The next game is at capacity!");
        if (playerTeams.keySet().stream().anyMatch(key -> playerTeams.get(key).contains(p))) {
            throw new Error("Player " + p.getDisplayName() + " is already in the next game!");
        }
        playerTeams.computeIfAbsent(-1, k -> new ArrayList<>());
        playerTeams.get(-1).add(p);
        getGame().onPlayerJoinGame(p);
        GameUtils.messagePlayerList(getPlayerSet(),"§b" + p.getName() + " will be playing in the next game!");
    }

    public void removeFromHostedGame(Player p) {
        if (!getPlayerSet().contains(p)) throw new Error("Player " + p.getDisplayName() + " is not in the lobby!");
        if (playerTeams.keySet().stream().noneMatch(key -> playerTeams.get(key).contains(p))) {
                throw new Error("Player " + p.getDisplayName() + " is already absent from the next game!");
        }
        playerTeams.keySet().forEach(i -> playerTeams.get(i).remove(p));
        getGame().onPlayerLeaveGame(p);
        GameUtils.messagePlayerList(getPlayerSet(),"§b" + p.getName() + " will no longer be playing in the next game!");
    }

    public BaseGame getGame() {
        return arena.getGame(selectedGame);
    }

    public String getSelectedGame() {
        return selectedGame;
    }

    private void setPlayerToLobby(Player p) {
        p.teleport((Location) arena.getVariable("lobby"));
        PlayerInventory inv = p.getInventory();
        inv.clear();
        if (arenaLobbyRegionTask == null) {
            startArenaLobbyRegionCheck();
        }
        playerLobby.add(p);
        Bukkit.getScheduler().scheduleSyncDelayedTask(CVGames.getInstance(), () -> {
            setLobbyInventory(inv);
        }, 20L);
        final String lobbyJoinMessage = "§b" + p.getName() + " has joined the lobby!";
        if (!arena.getStatus().equals(ArenaStatus.HOSTING)) {
            GameUtils.messagePlayerList(getPlayerSet(), lobbyJoinMessage, Sound.BLOCK_DISPENSER_DISPENSE);
        } else {
            host.sendMessage(lobbyJoinMessage);
        }
        p.setHealth(20);
        p.setFoodLevel(20);
        p.setSaturation(20);
    }

    public void setSpectatorToLobby(Player p) {
        playerLobby.add(p);
        PlayerManager.setPlayer(p, arena.getName());
        PlayerInventory inv = p.getInventory();
        inv.clear();
        SignManager.updateArenaSignsFill(arena.getName());
        Bukkit.getScheduler().scheduleSyncDelayedTask(CVGames.getInstance(), () -> {
            setSpectatorInventory(inv);
        }, 20L);
    }
    public void setLobbyInventory(PlayerInventory inv) {
        int numberOfTeams = 0;
        if (getGame() instanceof TeamSelectorGame) {
            numberOfTeams = ((TeamSelectorGame) getGame()).getTeamVariable().size();
        }
        if (numberOfTeams > 1 && (Boolean) arena.getVariable("team-selector") && !arena.getStatus().equals(ArenaStatus.HOSTING)) inv.setItem(7, InterfaceItems.TEAM_SELECTOR_ITEM);
        inv.setItem(8, InterfaceItems.QUEUE_LEAVE_ITEM);
    }

    public void setSpectatorInventory(PlayerInventory inventory) {
        setSpectatorInventory(inventory, true);
    }

    public void setSpectatorInventory(PlayerInventory inv, boolean includeLeaveItem) {
        inv.setItem(0, InterfaceItems.SPECTATE_PLAYER_NAV_ITEM);
        if (!includeLeaveItem) return;
        inv.setItem(8, InterfaceItems.SPECTATE_LEAVE_ITEM);
    }

    private void removePlayerFromLobby(Player p) {
        removePlayerFromLobby(p, true);
    }

    private void removePlayerFromLobby(Player p, boolean shouldSendToExit) {
        if (getGame() != null && getGame().getSpectators().contains(p)) {
            getGame().removeSpectator(p);
            removeSpectatorFromLobby(p);
        }
        if (shouldSendToExit) { p.teleport((Location) arena.getVariable("exit")); }
        playerLobby.remove(p);
        if (playerLobby.size() == 0 && arenaLobbyRegionTask != null) {
            killArenaLobbyRegionCheck();
        }
        SignManager.updateArenaSignsFill(arena.getName());

        if (getPlayerSet().size() == 0 && host == null) {
            setSelectedGame(null);
            return;
        }

        p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    public void removeSpectatorFromLobby(Player p) {
        playerLobby.remove(p);
        PlayerManager.removePlayer(p);
        p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        SignManager.updateArenaSignsFill(arena.getName());
        p.teleport((Location) arena.getVariable("exit"));
    }

    public void leave(Player p) {
        leave(p, true);
    }

    public void leave( Player p, boolean shouldSendToExit ) {
        if (!getPlayerSet().contains(p)) { return; }
        playerTeams.values().forEach(playerSet -> playerSet.remove(p));
        getGame().onPlayerLeaveGame(p);
        Set<Player> players = getPlayerSet();
        PlayerManager.removePlayer(p);
//        removePlayerFromLobby(p, shouldSendToExit);
        if (arena.getStatus() != ArenaStatus.IN_QUEUE && arena.getStatus() != ArenaStatus.HOSTING) { return; }
        p.sendMessage("§cYou have left the lobby.");
        removePlayerFromLobby(p, shouldSendToExit);
        final String lobbyLeaveMessage = "§c" + p.getName() + " has left the lobby.";
        if (!arena.getStatus().equals(ArenaStatus.HOSTING)) {
            GameUtils.messagePlayerList(players, lobbyLeaveMessage, Sound.BLOCK_DISPENSER_DISPENSE);
        } else {
            host.sendMessage(lobbyLeaveMessage);
        }

        if (p.equals(host)) {
            GameUtils.messagePlayerList(players, "§cThe host has left the lobby.");
            clearHostedLobby();
        }

        int playerCount = playerTeams.values().stream().mapToInt(List::size).sum();
        if (playerCount < getMinPlayers() && countdownTimer != null) {
            GameUtils.messagePlayerList(players, "§cCountdown cancelled -- Not enough players!");
            endCountdown();
        }

        if (getPlayerSet().size() == 0 && host == null) {
            arena.setStatus(ArenaStatus.OPEN);
            setSelectedGame(null);
            return;
        }
        // If using the team selector, make sure the teams are now balanced out
        if (!(getGame() instanceof TeamSelectorGame)) return;
        int numberOfTeams = ((TeamSelectorGame) getGame()).getTeamVariable().size();
        int maxTeamSize = (getPlayerSet().size() / numberOfTeams) + 1;
        for (Integer index : playerTeams.keySet() ) {
            if (index == -1) return;
            if (playerTeams.get(index).size() > maxTeamSize) {
                Player player = playerTeams.get(index).get(new Random().nextInt(playerTeams.get(index).size()));
                player.sendMessage("§cYou were randomly removed from your selected team because the team is too large!");
                playerTeams.get(index).remove(player);
                playerTeams.get(-1).add(player);
            }
        }
    }

    public int size() {
        return getPlayerSet().size();
    }

    public int getMaxPlayers() {
        Integer max = (Integer) arena.getVariable("queue-max");
        if (max == null) { return 1; }
        return max;
    }

    public int getMinPlayers() {
        Integer min = (Integer) arena.getVariable("queue-min");
        if (min == null) { return 0; }
        return min;
    }

    public void startHostedCountdown(int startCount) throws Error {
        int playerCount = playerTeams.values().stream().mapToInt(List::size).sum();
        if (playerCount < getMinPlayers()) {
            throw new Error("There are not enough players to start the game!");
        }
        startCountdown(startCount);
    }

    private void startCountdown(int startCount) {
        counter = startCount;
        this.countdownTimer = Bukkit.getScheduler().scheduleSyncRepeatingTask(CVGames.getInstance(), () -> {
            Set<Player> players = getPlayerSet();
            if (counter > 0) {
                if (counter % 10 == 0 || counter <= 5)
                GameUtils
                    .messagePlayerList(players, "§e" + counter + " seconds until the game starts",
                        Sound.BLOCK_NOTE_BLOCK_PLING);
                getGame().onCountdown(counter);
                counter--;
            } else {
                endCountdown();
                this.startGame(playerTeams);
            }
        }, 0L, 20L);
    }

    private void startGame(Map<Integer, List<Player>> playerTeamMap) {
        arena.setUsingGame(selectedGame);
        getGame().startGame(playerTeamMap);
        if (!arena.getStatus().equals(ArenaStatus.HOSTING)) { arena.setStatus(ArenaStatus.IN_USE); }
    }

    private void endCountdown() {
        Bukkit.getScheduler().cancelTask(this.countdownTimer);
        this.countdownTimer = null;
    }

    @Override
    public void whenPlayerLogout(Player p, Arena a) {
        leave(p);
    }

    public void clear() {
        playerTeams.keySet().forEach(key -> playerTeams.get(key).clear());
        if (!arena.getStatus().equals(ArenaStatus.HOSTING)) {
            playerLobby.clear();
            setSelectedGame(null);
            SignManager.updateArenaSignsFill(arena.getName());
        }
    }

    public void setSelectedTeam(Player player, int index) {
        if (!(getGame() instanceof TeamSelectorGame)) return;
        int numberOfTeams = ((TeamSelectorGame) getGame()).getTeamVariable().size();
        if (index >= numberOfTeams && index % 9 != 8) return;
        // when a player is selecting one of the teams
        if (index < numberOfTeams) {
            int maxTeamSize = (int) Math.ceil((double) getPlayerSet().size() / numberOfTeams);
            if (playerTeams.get(index).contains(player)) {
                player.sendMessage("§cYou are already on that team!");
                player.playSound(player.getLocation(),Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, .7F);
                return;
            }
            if (playerTeams.get(index).size() >= maxTeamSize) {
                player.sendMessage("§cYou cannot join that team, it is full!");
                player.playSound(player.getLocation(),Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, .7F);
                return;
            }
            HashMap<String, Object> team = ((TeamSelectorGame) getGame()).getTeamVariable().get(index);
            setPlayerAsTeam(player, index);
            ChatColor chatColor = (ChatColor) team.get("chat-color");
            player.sendMessage(chatColor + "You have joined " + team.get("name"));
            player.playSound(player.getLocation(),Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 0.8F, 0.5F);
        } else {
            setPlayerAsTeam(player, -1);
            player.sendMessage("§eYou are no longer selecting a team");
            player.playSound(player.getLocation(),Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 0.8F, 0.5F);
        }
    }

    public void setPlayerAsTeamMember(Player player, int index) throws Error {
        if (!getPlayerSet().contains(player)) throw new Error("Player " + player.getDisplayName() + " is not in the lobby!");
        if (!(getGame() instanceof TeamSelectorGame)) throw new Error("Game " + getGame().getId() + " does not support teams!");
        List<HashMap<String, Object>> teams = ((TeamSelectorGame) getGame()).getTeamVariable();
        if (teams.size() <= index || index < 0) throw new Error("There is no team at index " + (index + 1));
        setPlayerAsTeam(player, index);
        GameUtils.messagePlayerList(getPlayerSet(),"§b" + player.getName() + " will be joining " + teams.get(index).get("name"));
    }

    public void removePlayerFromTeams(Player player) throws Error {
        if (!getPlayerSet().contains(player)) throw new Error("Player " + player.getDisplayName() + " is not in the lobby!");
        setPlayerAsTeam(player, -1);
        GameUtils.messagePlayerList(getPlayerSet(),"§b" + player.getName() + " is no longer assigned a team.");
    }

    private void setPlayerAsTeam(Player player, int index) {
        playerTeams.values().forEach(playerSet -> playerSet.remove(player));
        playerTeams.computeIfAbsent(index, k -> new ArrayList<>());
        playerTeams.get(index).add(player);
    }

    public void openTeamSelector(Player player) {
        Inventory teamSelector = teamSelectorGUI();
        if (teamSelector == null) return;
        player.openInventory(teamSelector);
    }

    private Inventory teamSelectorGUI() {
        if (!(getGame() instanceof TeamSelectorGame)) return null;
        List<HashMap<String, Object>> teams = ((TeamSelectorGame) getGame()).getTeamVariable();
        if (teams.size() <= 1) return null;
        int invSize = (1 + (teams.size() / 9)) * 9;
        Inventory inv = Bukkit.createInventory(null, invSize, arena.teamSelectorInventoryName());
        for (int i = 0; i < teams.size(); i++) {
            HashMap<String, Object> team = teams.get(i);
            ItemStack teamItem = (ItemStack) team.get("item");
            teamItem.setAmount(1);
            ItemMeta teamItemMeta;
            if (teamItem.hasItemMeta()) {
                teamItemMeta = teamItem.getItemMeta();
            } else {
                teamItemMeta = Bukkit.getItemFactory().getItemMeta(teamItem.getType());
            }
            teamItemMeta.setDisplayName((String) team.get("name"));
            if (playerTeams.containsKey(i)) {
                ChatColor chatColor = (ChatColor) team.get("chat-color");
;                ArrayList<String> itemLore = new ArrayList<>();
                for (Player player : playerTeams.get(i)) {
                    itemLore.add(chatColor + "- " + player.getDisplayName());
                }
                teamItemMeta.setLore(itemLore);
            }
            teamItem.setItemMeta(teamItemMeta);
            inv.setItem(i, teamItem);
        }
        inv.setItem(invSize - 1, InterfaceItems.RANDOMIZE_TEAM_ITEM);
        return inv;
    }

    public Player getHost() {
        return host;
    }

    public void setHostedLobby(Player player, String selectedGame) throws Error {
        if (!this.arena.getGameNames().contains(selectedGame)) {
            throw new Error("Game with name \"" + selectedGame + "\" is not on this arena!");
        }
        if (this.arena.getGame(selectedGame) instanceof SoloGame) {
            throw new Error("You cannot host a solo game!");
        }
        PlayerManager.setPlayer(player, arena.getName());
        this.host = player;
        setSelectedGame(selectedGame);
        arena.setStatus(ArenaStatus.HOSTING);
        playerLobby.add(player);
        SignManager.updateArenaSignsFill(arena.getName());
        setLobbyInventory(player.getInventory());
        player.teleport((Location) arena.getVariable("lobby"));
    }

    public void clearHostedLobby() {
        this.host = null;
        Set<Player> playerSet = new HashSet<>(getPlayerSet());
        for (Player player : playerSet) {
            PlayerManager.removePlayer(player);
            removePlayerFromLobby(player);
        }
        playerTeams.clear();
        setSelectedGame(null);
        arena.setStatus(ArenaStatus.OPEN);
        SignManager.updateArenaSignsFill(arena.getName());
    }

    public Map<Integer, List<Player>> getPlayerTeams() {
        return playerTeams;
    }
}
