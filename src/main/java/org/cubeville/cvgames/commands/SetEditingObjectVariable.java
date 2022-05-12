package org.cubeville.cvgames.commands;

import org.bukkit.entity.Player;
import org.cubeville.cvgames.managers.ArenaManager;
import org.cubeville.cvgames.managers.EditingManager;
import org.cubeville.cvgames.models.Game;
import org.cubeville.cvgames.vartypes.GameVariable;
import org.cubeville.cvgames.vartypes.GameVariableList;
import org.cubeville.cvgames.vartypes.GameVariableObject;

import java.util.List;

public class SetEditingObjectVariable extends RunnableCommand {

    @Override
    public String execute(Player player, List<Object> baseParameters)
            throws Error {
        String arenaName = (String) baseParameters.get(0);
        Game arenaGame = ArenaManager.getArena(arenaName).getGame();
        if (arenaGame == null) throw new Error("You need to set the game for the arena " + arenaName);
        String variable = ((String) baseParameters.get(1)).toLowerCase();
        if (!arenaGame.hasVariable(variable)) throw new Error("That variable does not exist for the game " + arenaGame.getId());
        GameVariable gameVariable = arenaGame.getGameVariable(variable);
        if (!(gameVariable instanceof GameVariableList)) throw new Error("The variable " + variable +" is not a list");
        int index;
        try {
            index = Integer.parseInt((String) baseParameters.get(2));
        } catch (NumberFormatException e) {
            throw new Error(baseParameters.get(2) + " is not a valid index!");
        }
        GameVariable editingVar = ((GameVariableList<?>) gameVariable).getVariableAtIndex(index - 1);
        if (editingVar == null) throw new Error("The list " + variable +" does not have an index of " + index);
        if (!(editingVar instanceof GameVariableObject)) throw new Error("The list " + variable + " is not a list of objects");

        String path = variable + "." + (index - 1);
        EditingManager.setEditObject(ArenaManager.getArena(arenaName), player, (GameVariableObject) editingVar, path);
        return "&aEditing object number " + index + " in list " + variable + " for arena " + arenaName;
    }

}
