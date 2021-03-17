package org.cubeville.cvgames;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import org.cubeville.commons.commands.CommandParser;
import org.cubeville.cvgames.arenas.*;

public class CVGames extends JavaPlugin implements Listener {

    private CommandParser commandParser;

    public void onEnable() {
        System.out.println("Hey look at me I started!");
        commandParser = new CommandParser();
        Paintball pb = new Paintball(this);
        commandParser.addCommand(pb);
        commandParser.addCommand(new CreateArena(this));
        commandParser.addCommand(new DeleteArena(this));
        commandParser.addCommand(new AddArenaSpawn(this));
        commandParser.addCommand(new ConnectArena(this));
        commandParser.addCommand(new DisconnectArena(this));

        saveDefaultConfig();

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(pb, this);

    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equals("cvgames")) {
            return commandParser.execute(sender, args);
        }
        return false;
    }

}
