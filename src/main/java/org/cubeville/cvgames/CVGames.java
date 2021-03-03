package org.cubeville.cvgames;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import org.cubeville.commons.commands.CommandParser;

public class CVGames extends JavaPlugin implements Listener {

    private CommandParser commandParser;

    public void onEnable() {
        System.out.println("Hey look at me I started!");
        commandParser = new CommandParser();
        Paintball pb = new Paintball();
        commandParser.addCommand(pb);

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
