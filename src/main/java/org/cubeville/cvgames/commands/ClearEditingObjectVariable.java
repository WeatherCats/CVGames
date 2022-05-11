package org.cubeville.cvgames.commands;

import org.bukkit.entity.Player;
import org.cubeville.commons.commands.*;
import org.cubeville.cvgames.managers.ArenaManager;
import org.cubeville.cvgames.managers.EditingManager;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClearEditingObjectVariable extends Command {

    public ClearEditingObjectVariable() {
        super("clearedit");
        addBaseParameter(new CommandParameterString()); // arena name
        setPermission("cvgames.arenas.clearedit");
    }

    @Override
    public CommandResponse execute(Player player, Set<String> set, Map<String, Object> map, List<Object> baseParameters)
            throws CommandExecutionException {
        String arenaName = ArenaManager.filterArenaInput((String) baseParameters.get(0));
        EditingManager.clearEditObject(arenaName, player);
        return new CommandResponse("&aYou are back to editing the main object for arena " + arenaName);
    }
}
