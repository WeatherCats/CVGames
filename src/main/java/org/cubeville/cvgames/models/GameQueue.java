package org.cubeville.cvgames.models;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.cubeville.cvgames.CVGames;
import org.cubeville.cvgames.utils.GameUtils;
import org.cubeville.cvgames.enums.ArenaStatus;
import org.cubeville.cvgames.managers.PlayerManager;
import org.cubeville.cvgames.managers.SignManager;
import org.cubeville.cvgames.vartypes.*;

import java.util.*;

public class GameQueue implements PlayerContainer {

	private Map<Integer, List<Player>> playerTeams = new HashMap<>();
	private Arena arena;
	private int countdownTimer;
	private int counter;

	public GameQueue(Arena arena) {
		this.arena = arena;
		arena.getGame().addGameVariable("queue-min", new GameVariableInt());
		arena.getGame().addGameVariable("queue-max", new GameVariableInt());
		arena.getGame().addGameVariable("lobby", new GameVariableLocation());
		arena.getGame().addGameVariable("exit", new GameVariableLocation());
		arena.getGame().addGameVariable("signs", new GameVariableList<>(GameVariableQueueSign.class));
		if (arena.getGame() instanceof TeamSelectorGame) {
			arena.getGame().addGameVariable("team-selector", new GameVariableFlag(), true);
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
		if (players.size() >= getMaxPlayers()) {
			p.sendMessage(ChatColor.RED + "This arena is full!");
			return false;
		}
		return true;
	}

	private Set<Player> getPlayerSet() {
		Set<Player> players = new HashSet<>();
		playerTeams.values().forEach(players::addAll);
		return players;
	}

	public boolean join(Player p) {
		Set<Player> players = getPlayerSet();
		if (!canJoinQueue(p)) {
			return false;
		}
		if (!playerTeams.containsKey(-1)) playerTeams.put(-1, new ArrayList<>());
		playerTeams.get(-1).add(p);
		setPlayerToLobby(p);
		PlayerManager.setPlayer(p, arena.getName());
		if (players.size() == getMinPlayers()) {
			startCountdown(20);
		}
		final int SPEED_COUNTDOWN = 6;
		if (players.size() == getMaxPlayers() && counter > SPEED_COUNTDOWN) {
			endCountdown();
			GameUtils.messagePlayerList(players, "§bQueue has been filled, starting game.", Sound.BLOCK_DISPENSER_DISPENSE);
			startCountdown(SPEED_COUNTDOWN);
		}
		return true;
	}

	private void setPlayerToLobby(Player p) {
		p.teleport((Location) arena.getGame().getVariable("lobby"));
		PlayerInventory inv = p.getInventory();
		inv.clear();
		Bukkit.getScheduler().scheduleSyncDelayedTask(CVGames.getInstance(), () -> {
			int numberOfTeams = 0;
			if (arena.getGame() instanceof TeamSelectorGame) {
				numberOfTeams = ((TeamSelectorGame) arena.getGame()).getTeamVariable().size();
			}
			if (numberOfTeams > 1) inv.setItem(7, teamSelectorItem());
			inv.setItem(8, queueLeaveItem());
		}, 20L);
		GameUtils.messagePlayerList(getPlayerSet(), "§b" + p.getName() + " has joined the queue!", Sound.BLOCK_DISPENSER_DISPENSE);
	}

	private void removePlayerFromLobby(Player p) {
		p.teleport((Location) arena.getGame().getVariable("exit"));
		p.getInventory().clear();
 		GameUtils.messagePlayerList(getPlayerSet(), "§b" + p.getName() + " has left the queue.", Sound.BLOCK_DISPENSER_DISPENSE);
	}

	public void leave( Player p ) {
		Set<Player> players = getPlayerSet();
		if (!players.contains(p)) { return; }
		playerTeams.values().forEach(playerSet -> playerSet.remove(p));
		SignManager.updateArenaSignsFill(arena.getName());
		PlayerManager.removePlayer(p);
		if (arena.getStatus() != ArenaStatus.OPEN) { return; }
		p.sendMessage("§bYou have left the queue.");
		removePlayerFromLobby(p);
		if (players.size() == (getMinPlayers() - 1)) {
			GameUtils.messagePlayerList(players, "§cCountdown cancelled -- Not enough players!");
			endCountdown();
		}
		// If using the team selector, make sure the teams are now balanced out
		if (!(arena.getGame() instanceof TeamSelectorGame)) return;
		int numberOfTeams = ((TeamSelectorGame) arena.getGame()).getTeamVariable().size();
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
		Integer max = (Integer) arena.getGame().getVariable("queue-max");
		if (max == null) { return 0; }
		return max;
	}

	public int getMinPlayers() {
		Integer min = (Integer) arena.getGame().getVariable("queue-min");
		if (min == null) { return 0; }
		return min;
	}


	private void startCountdown(int startCount) {
		counter = startCount;
		this.countdownTimer = Bukkit.getScheduler().scheduleSyncRepeatingTask(CVGames.getInstance(), new Runnable() {
			@Override
			public void run() {
				Set<Player> players = getPlayerSet();
				if (counter > 0) {
					if (counter % 10 == 0 || counter <= 5)
					GameUtils
						.messagePlayerList(players, "§e" + counter + " seconds until the game starts",
							Sound.BLOCK_NOTE_BLOCK_PLING);
					counter--;
				} else {
					endCountdown();
					arena.setStatus(ArenaStatus.IN_USE);
					arena.getGame().startGame(playerTeams, arena);
				}
			}
		}, 0L, 20L);
	}

	private void endCountdown() {
		Bukkit.getScheduler().cancelTask(this.countdownTimer);
	}

	@Override
	public void whenPlayerLogout(Player p, Arena a) {
		leave(p);
	}

	public ItemStack queueLeaveItem() {
		return GameUtils.customItem(Material.RED_BED, "§c§l§oLeave Queue §7§o(Right Click)");
	}

	public ItemStack teamSelectorItem() {
		return GameUtils.customItem(Material.GHAST_TEAR, "§b§l§oSelect Team §7§o(Right Click)");
	}

	public ItemStack removeTeamSelectionItem() {
		return GameUtils.customItem(Material.BARRIER, "§c§l§oRandomized Team");
	}

	public void clear() {
		playerTeams.clear();
		SignManager.updateArenaSignsFill(arena.getName());
	}

	public void setSelectedTeam(Player player, int index) {
		if (!(arena.getGame() instanceof TeamSelectorGame)) return;
		int numberOfTeams = ((TeamSelectorGame) arena.getGame()).getTeamVariable().size();
		if (index >= numberOfTeams && index % 9 != 8) return;
		// when a player is selecting one of the teams
		if (index < numberOfTeams) {
			int maxTeamSize = (getPlayerSet().size() / numberOfTeams) + 1;
			if (!playerTeams.containsKey(index)) playerTeams.put(index, new ArrayList<>());
			if (playerTeams.get(index).size() >= maxTeamSize) {
				player.sendMessage("§cYou cannot join that team, it is full!");
				player.playSound(player.getLocation(),Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, 1.0F);
				return;
			}
			playerTeams.values().forEach(playerSet -> playerSet.remove(player));
			playerTeams.get(index).add(player);
		} else {
			playerTeams.values().forEach(playerSet -> playerSet.remove(player));
			playerTeams.get(-1).add(player);
		}
	}

	public void openTeamSelector(Player player) {
		Inventory teamSelector = teamSelectorGUI();
		if (teamSelector == null) return;
		player.openInventory(teamSelector);
	}

	private Inventory teamSelectorGUI() {
		if (!(arena.getGame() instanceof TeamSelectorGame)) return null;
		List<HashMap<String, Object>> teams = ((TeamSelectorGame) arena.getGame()).getTeamVariable();
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
;				ArrayList<String> itemLore = new ArrayList<>();
				for (Player player : playerTeams.get(i)) {
					itemLore.add(chatColor + "- " + player.getDisplayName());
				}
				teamItemMeta.setLore(itemLore);
			}
			teamItem.setItemMeta(teamItemMeta);
			inv.setItem(i, teamItem);
		}
		inv.setItem(invSize - 1, removeTeamSelectionItem());
		return inv;
	}
}
