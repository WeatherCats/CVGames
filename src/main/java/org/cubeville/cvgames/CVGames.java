package org.cubeville.cvgames;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import org.cubeville.commons.commands.CommandParser;
import org.cubeville.cvgames.commands.*;
import org.cubeville.cvgames.games.Paintball;
import org.cubeville.cvgames.games.TestGame;
import org.cubeville.cvgames.managers.ConfigImportManager;
import org.cubeville.cvgames.managers.GameManager;

public class CVGames extends JavaPlugin implements Listener {

    private CommandParser commandParser;

    private static CVGames instance;
    private static GameManager games;

    private final String[] arenaBlockList = new String[]{"create" , "delete"};

    public void onEnable() {
        instance = this;
        games = new GameManager<>();
        commandParser = new CommandParser();
        commandParser.addCommand(new CreateArena());
        commandParser.addCommand(new DeleteArena());
        commandParser.addCommand(new SetArenaGame());
        commandParser.addCommand(new SetArenaVariable());
        commandParser.addCommand(new VerifyArena());
        commandParser.addCommand(new CenterPosition());
        commandParser.addCommand(new AddArenaVariable());
        commandParser.addCommand(new ClearEditingObjectVariable());
        commandParser.addCommand(new SetEditingObjectVariable());


        games.registerGame("paintball", Paintball.class);
        games.registerGame("test", TestGame.class);


        saveDefaultConfig();

        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new EventHandlers(), this);

        ConfigImportManager.importConfiguration();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equals("cvgames")) {
            return commandParser.execute(sender, args);
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
