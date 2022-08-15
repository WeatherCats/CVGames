package org.cubeville.cvgames.commands;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cubeville.cvgames.managers.ArenaManager;
import org.cubeville.cvgames.managers.EditingManager;
import org.cubeville.cvgames.models.Arena;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClearEditingObjectVariable extends RunnableCommand {

    @Override
    public TextComponent execute(CommandSender sender, List<Object> baseParameters)
            throws Error {
        if (!(sender instanceof Player)) throw new Error("You cannot run this command from console!");
        Player player = (Player) sender;
        Arena arena = (Arena) baseParameters.get(0);
        EditingManager.clearEditObject(arena.getName(), player);
        return new TextComponent("§aYou are back to editing the main object for arena " + arena.getName());
    }
}
