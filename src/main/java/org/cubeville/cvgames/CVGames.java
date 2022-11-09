package org.cubeville.cvgames;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import org.cubeville.cvgames.database.GamesDB;
import org.cubeville.cvgames.managers.CommandManager;
import org.cubeville.cvgames.managers.DataManager;
import org.cubeville.cvgames.managers.GameManager;

import java.io.IOException;

public class CVGames extends JavaPlugin implements Listener {

    private static CVGames instance;
    private static GameManager games;
    private static DataManager data;

    public void onEnable() {
        instance = this;
        games = new GameManager();
        data = new DataManager();

        saveDefaultConfig();
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new EventHandlers(), this);
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

    public static DataManager dataManager() {
        return data;
    }



}
