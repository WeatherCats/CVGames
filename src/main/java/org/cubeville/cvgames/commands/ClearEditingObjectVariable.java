package org.cubeville.cvgames.commands;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.cubeville.cvgames.managers.EditingManager;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClearEditingObjectVariable extends RunnableCommand {

    @Override
    public TextComponent execute(Player player, List<Object> baseParameters)
            throws Error {
        String arenaName = (String) baseParameters.get(0);
        EditingManager.clearEditObject(arenaName, player);
        return new TextComponent("§aYou are back to editing the main object for arena " + arenaName);
    }
}
