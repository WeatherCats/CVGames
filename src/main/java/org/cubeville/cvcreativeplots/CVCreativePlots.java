package org.cubeville.cvcreativeplots;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import org.cubeville.commons.commands.CommandParser;

import org.cubeville.cvtools.commands.*;

public class CVCreativePlots extends JavaPlugin {

    private CommandParser commandParser;

    public void onEnable() {
        commandParser = new CommandParser();
        commandParser.addCommand(new CreatePlot());
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equals("cvcreativeplots")) {
            return commandParser.execute(sender, args);
        }
        return false;
    }

}
