package org.cubeville.cvgames;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import org.cubeville.commons.commands.CommandParser;

public class CVGames extends JavaPlugin implements Listener {

    private CommandParser commandParser;

    public void onEnable() {
        System.out.println("Hey look at me I started!");
        commandParser = new CommandParser();
        commandParser.addCommand(new GamesTestCommand());

    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        System.out.println("Hey look at me I ran a command!");
        if(command.getName().equals("cvgames")) {
            System.out.println("cvgames was run!");
            return commandParser.execute(sender, args);
        }
        return false;
    }

}
