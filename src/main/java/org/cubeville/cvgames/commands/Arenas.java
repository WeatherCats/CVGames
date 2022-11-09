package org.cubeville.cvgames.commands;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.cubeville.cvgames.managers.ArenaManager;
import org.cubeville.cvgames.models.Arena;

import java.util.List;

public class Arenas extends RunnableCommand {
    @Override
    public TextComponent execute(CommandSender sender, List<Object> params) throws Error {
        String gameName = "";
        String outputTitle = "§e§lArenas:\n";
        if (params.size() == 1) {
            gameName = (String) params.get(0);
            outputTitle = "§e§lArenas with game " + gameName + ":\n";
        }
        TextComponent output = new TextComponent(outputTitle);
        for (Arena arena : ArenaManager.getArenas()) {
            if (params.size() == 1 && !arena.getGameNames().contains(gameName)) continue;
            output.addExtra("§6 - " + arena.getName() + "\n");
        }
        return output;
    }
}
