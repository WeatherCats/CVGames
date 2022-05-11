package org.cubeville.cvgames.managers;

import org.bukkit.entity.Player;
import org.cubeville.cvgames.models.Arena;
import org.cubeville.cvgames.models.EditingState;
import org.cubeville.cvgames.vartypes.GameVariableObject;

import java.util.HashMap;

public class EditingManager {

    private static HashMap<String, EditingState> editingState = new HashMap<>();

    public static void setEditObject(Arena arena, Player player, GameVariableObject gvo, String path) {
        editingState.put(generateKey(arena.getName(), player), new EditingState(gvo, path));
    }

    public static GameVariableObject getEditObject(Arena arena, Player player) {
        EditingState es = editingState.get(generateKey(arena.getName(), player));
        if (es == null) { return null; }
        return es.gameVariableObject;
    }

    public static String getEditPath(String arenaName, Player player) {
        EditingState es = editingState.get(generateKey(arenaName, player));
        if (es == null) { return null; }
        return es.path;
    }

    public static void clearEditObject(String arenaName, Player player) {
        editingState.remove(generateKey(arenaName, player));
    }

    private static String generateKey(String arenaName, Player player) {
        return arenaName + " " + player.getDisplayName();
    }
}

