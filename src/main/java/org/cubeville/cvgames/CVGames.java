package org.cubeville.cvgames;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParser;
import org.cubeville.cvgames.commands.*;
import org.cubeville.cvgames.games.TestGame;

import java.util.ArrayList;
import java.util.List;

public class CVGames extends JavaPlugin implements Listener {

    private CommandParser commandParser;

    private static CVGames instance;
    private static GameManager<TestGame> games;

    private List<ArenaCommand> arenaCommandList = new ArrayList<>();
    private final String[] arenaBlockList = new String[]{"create" , "delete"};

    public void onEnable() {
        instance = this;
        games = new GameManager<>();
        commandParser = new CommandParser();
        commandParser.addCommand(new CreateArena());
        commandParser.addCommand(new DeleteArena());

        arenaCommandList.add(new SetArenaGame());
        arenaCommandList.add(new AddArenaSign());

        games.registerGame("test", TestGame.class);

        saveDefaultConfig();

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new EventHandlers(), this);

    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equals("cvgames")) {
            if (args[0] != null && args[0].toLowerCase().equals("arena") && args[1] != null && !isArenaNameBlocked(args[1].toLowerCase())) {
                String arenaName = args[1].toLowerCase();
                if (!ArenaManager.hasArena(arenaName)) {
                    sender.sendMessage("&cArena with name " + arenaName + " does not exist!");
                } else {
                    buildArenaCommandParser(args[1].toLowerCase()).execute(sender, args);
                }
            }
            return commandParser.execute(sender, args);
        }
        return false;
    }

    private CommandParser buildArenaCommandParser(String arenaName) {
        CommandParser cp = new CommandParser();
        arenaCommandList.forEach(arenaCommand -> {
            arenaCommand.setArenaName(arenaName);
            cp.addCommand(arenaCommand);
        });
        return cp;
    }

    private boolean isArenaNameBlocked(String arenaName) {
        for (String s : arenaBlockList) {
            if (arenaName.equals(s)) {
                return true;
            }
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
