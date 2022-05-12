package org.cubeville.cvgames.commands;

import org.bukkit.entity.Player;
import org.cubeville.cvgames.managers.EditingManager;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClearEditingObjectVariable extends RunnableCommand {

    @Override
    public String execute(Player player, List<Object> baseParameters)
            throws Error {
        String arenaName = (String) baseParameters.get(0);
        EditingManager.clearEditObject(arenaName, player);
        return "&aYou are back to editing the main object for arena " + arenaName;
    }
}
