package org.cubeville.cvgames;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import org.cubeville.cvgames.commands.*;
import org.cubeville.cvgames.games.Paintball;
import org.cubeville.cvgames.games.TestGame;
import org.cubeville.cvgames.managers.CommandManager;
import org.cubeville.cvgames.managers.ConfigImportManager;
import org.cubeville.cvgames.managers.GameManager;

public class CVGames extends JavaPlugin implements Listener {

    private static CVGames instance;
    private static GameManager games;

    private final String[] arenaBlockList = new String[]{"create" , "delete"};

    public void onEnable() {
        instance = this;
        games = new GameManager<>();
        games.registerGame("paintball", Paintball.class);
        games.registerGame("test", TestGame.class);


        saveDefaultConfig();
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new EventHandlers(), this);
        ConfigImportManager.importConfiguration();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equals("cvgames")) {
            return CommandManager.parse(sender, args);
        }
        return false;
    }

    public static CVGames getInstance() {
        return instance;
    }

    public static GameManager gameManager() {
        return games;
    }


}
