package org.cubeville.cvgames.models;

import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.cubeville.cvgames.enums.ArenaStatus;
import org.cubeville.cvgames.managers.SignManager;

import java.util.ArrayList;
import java.util.List;

public class QueueSign {

    private Sign sign;
    private Arena arena;
    private String gameName;

    public QueueSign(Sign sign, Arena arena, String gameName) {
        this.sign = sign;
        this.arena = arena;
        this.gameName = gameName;
        if (this.sign != null) {
            setLine(0, arena.getName());
            this.displayStatus(arena.getStatus());
            this.displayFill();
            this.displayGameName(null);
        }
    }

    public void displayStatus(ArenaStatus status) {
        switch (status) {
            case OPEN:
                 setLine(2, "§a§lOPEN");
                break;
            case IN_QUEUE:
                 setLine(2, "§e§lIN_QUEUE");
                break;
            case IN_USE:
                 setLine(2, "§7§lIN USE");
                break;
            case HOSTING:
                 setLine(2, "§b§lHOSTING");
                break;
            case CLOSED:
                 setLine(2, "§c§lCLOSED");
                break;
        }
    }

    public void displayFill() {
        if (arena.getStatus().equals(ArenaStatus.HOSTING)) {
            setLine(1,"§l" + arena.getQueue().size() + " in lobby");
            return;
        }
        if (arena.getStatus() != ArenaStatus.IN_USE || arena.getQueue().getGame() == null || !((boolean) arena.getVariable("spectate-enabled"))) {
             setLine(1,"§l" + arena.getQueue().size() + "/" + arena.getQueue().getMaxPlayers());
        }
        else {
            String spectatorString = "";
            List<Player> spectators = new ArrayList<>(arena.getQueue().getGame().getSpectators());
            spectators.removeAll(arena.getQueue().getGame().state.keySet());
            if (arena.getQueue().getGame().spectators.size() > 0)
                    spectatorString = " §7§o(+" + spectators.size() + ")";
            setLine(1, "§l" + arena.getQueue().getGame().state.keySet().size() + "/" + arena.getQueue().getMaxPlayers() + spectatorString);
        }
    }

    public void displayGameName(String selectedGame) {
        if (selectedGame == null) {
             setLine(3, gameName);
        } else {
             setLine(3, selectedGame);
        }
    }

    private void setLine(int index, String string) {
        Sign editSign = (Sign) sign.getLocation().getBlock().getState();
        editSign.getSide(Side.FRONT).setLine(index, string);
        editSign.update(true, false);
    }

    public String getArenaName() {
        return arena.getName();
    }

    public void onRightClick(Player p) {
        this.arena.getQueue().join(p, gameName);
        SignManager.updateArenaSignsFill(getArenaName());
    }
}
