package org.cubeville.cvgames.commands;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Help extends RunnableCommand {
    @Override
    public TextComponent execute(CommandSender sender, List<Object> params) throws Error {
        String[] lines = {
                "§e§l§oCVGames Commands",
                "§b§lArena Setup",
                "§3/cvgames arena create <arena>§f - Create Arena",
                "§3/cvgames arena delete <arena>§f - Delete Arena",
                "§3/cvgames arena <arena> addgame <game>§f - Add Arena Game",
                "§3/cvgames arena <arena> removegame <game>§f - Remove Arena Game",
                "§3/cvgames arena <arena> verify [path]§f - Verify Arena",
                "§3/cvgames arena <arena> setvar [input]§f - Set Arena Variable",
                "§3/cvgames arena <arena> addvar [input]§f - Add Arena Variable",
                "§3/cvgames arena <arena> setedit <variable> <index>§f - Set Editing Object",
                "§3/cvgames arena <arena> clearedit§f - Edit Main Object",
                "§b§lArena Queueing",
                "§3/cvgames queue join <arena> <player> <game>§f - Join Queue",
                "§3/cvgames queue leave <arena> <player> <game>§f - Leave Queue",
                "§b§lOther",
                "§3/cvgames center§f - Center Position in Block",
                "§3/cvgames giveitem <path>§f - Get Item at Path",
        };

        for (String line : lines) {
            sender.sendMessage(line);
        }
        return null;
    }
}
