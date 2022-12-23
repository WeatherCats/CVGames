package org.cubeville.cvgames.commands;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cubeville.cvgames.managers.ArenaManager;

import java.util.List;

public class CreateArena extends RunnableCommand {

    @Override
    public TextComponent execute(CommandSender sender, List<Object> parameters) throws Error {
        String arenaName = ((String) parameters.get(0)).toLowerCase();

        if (ArenaManager.hasArena(arenaName)) {
            throw new Error("Arena with name " + arenaName + " already exists!");
        }

        ArenaManager.createArena(arenaName);
        return new TextComponent("§aCreated the arena " + arenaName + "!");
    }
}
