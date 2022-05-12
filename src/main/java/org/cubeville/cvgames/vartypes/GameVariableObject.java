package org.cubeville.cvgames.vartypes;

import org.bukkit.entity.Player;
import org.cubeville.cvgames.managers.EditingManager;

import javax.annotation.Nullable;
import java.util.HashMap;

public abstract class GameVariableObject extends GameVariable {

    String name;
    HashMap<String, GameVariable> fields = new HashMap<>();

    public GameVariableObject(String name) {
        this.name = name;
    }

    @Override
    public Object getItem() {
        HashMap<String, Object> publicItem = new HashMap<>();
        for (String key: fields.keySet()) {
            publicItem.put(key, fields.get(key).getItem());
        }
        return publicItem;
    }

    @Override
    public Object itemString() {
        StringBuilder items = new StringBuilder();
        items.append("&f{\n");
        for (String key : fields.keySet()) {
            items.append("  ")
                    .append(fields.get(key).isValid() ? "&a" : "&c")
                    .append(key)
                    .append(" [")
                    .append(fields.get(key).displayString())
                    .append("]: ")
                    .append(fields.get(key).itemString())
                    .append("&r\n");
        }
        items.append("&f}");
        return items.toString();
    }

    @Override
    public boolean isValid() {
        for (GameVariable gameVariable : fields.values()) {
            if (!gameVariable.isValid()) { return false; }
        }
        return true;
    }

    @Override
    public void setItem(Player player, String input, String arenaName) throws Error {
        // add presets later pls
    }

    @Override
    public void setItem(@Nullable String string, String arenaName) {
        // lol ignoring this too
    }

    public void addToField(String arenaName, String fieldName, Player player, String input) throws Error {
        if (!fields.containsKey(fieldName)) throw new Error("Field " + fieldName + " does not exist for object " + name);
        String path = EditingManager.getEditPath(arenaName, player) + "." + fieldName;
        fields.get(fieldName).addVariable(arenaName, path, player, input);
    }

    public void addField(String fieldName, GameVariable gameVariable) {
        fields.put(fieldName, gameVariable);
    }

    public void setField(String arenaName, String fieldName, Player player, String input) throws Error {
        if (!fields.containsKey(fieldName)) throw new Error("Field " + fieldName + " does not exist for object " + name);
        String path = EditingManager.getEditPath(arenaName, player) + "." + fieldName;
        fields.get(fieldName).setVariable(arenaName, path, player, input);
    }

    public GameVariable getVariableAtField(String field) {
        return fields.get(field);
    }

    @Override
    public String displayString() {
        return name;
    }

    @Override public void storeItem(String arenaName, String path) {
        for (String key: fields.keySet()) {
            fields.get(key).storeItem(arenaName, path + "." + key);
        }
    }
}
