package org.cubeville.cvgames.vartypes;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;
import org.cubeville.cvgames.managers.ArenaManager;
import org.cubeville.cvgames.managers.EditingManager;
import org.cubeville.cvgames.models.Arena;
import org.cubeville.cvgames.utils.GameUtils;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class GameVariableObject extends GameVariable {

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
    public TextComponent displayString(String arenaName) {
        TextComponent out = new TextComponent("§f{");

        for (String key : fields.keySet()) {
            out.addExtra("\n");
            out.addExtra(GameUtils.addGameVarString(key + " [" + fields.get(key).typeString() + "]: §f", fields.get(key), arenaName, key));
            if (fields.get(key) instanceof GameVariableList) {
                TextComponent tc = new TextComponent("[Show Contents]");
                tc.setBold(true);
                tc.setColor(ChatColor.AQUA);
                tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cvgames arena " + arenaName + " verify " + path + "." + key));
                tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to view the contents of this list")));
                out.addExtra(tc);
            } else {
                out.addExtra(fields.get(key).displayString(arenaName));
            }
            out.addExtra("§r");
        }
        out.addExtra("§f\n}");
        return out;
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
    public void setItem(@Nullable Object object, String arenaName) {
        // lol ignoring this too
    }

    public void addToField(String arenaName, String fieldName, Player player, String input) throws Error {
        if (!fields.containsKey(fieldName)) throw new Error("Field " + fieldName + " does not exist for object " + name);
        String fieldPath = EditingManager.getEditPath(arenaName, player) + "." + fieldName;
        fields.get(fieldName).path = fieldPath;
        fields.get(fieldName).addVariable(arenaName, fieldPath, player, input);
    }

    public void addField(String fieldName, GameVariable gameVariable) {
        fields.put(fieldName, gameVariable);
    }

    public void setField(String arenaName, String fieldName, Player player, String input) throws Error {
        if (!fields.containsKey(fieldName)) throw new Error("Field " + fieldName + " does not exist for object " + name);
        String fieldPath = EditingManager.getEditPath(arenaName, player) + "." + fieldName;
        fields.get(fieldName).path = fieldPath;
        fields.get(fieldName).setVariable(arenaName, fieldPath, player, input);
    }

    public GameVariable getVariableAtField(String field) {
        return fields.get(field);
    }

    @Override
    public String typeString() {
        return name;
    }

    @Override public void storeItem(String arenaName, String path) {
        for (String key: fields.keySet()) {
            fields.get(key).storeItem(arenaName, path + "." + key);
        }
    }

    public void populateFields(String arenaName, String path) {
        Arena arena = ArenaManager.getArena(arenaName);
        Map<String, GameVariable> objectFields = arena.getObjectFields(path);
        for (String fieldName : objectFields.keySet()) {
            addField(fieldName, objectFields.get(fieldName));
        }
    }

    @Override
    public Object itemString() {
        return null;
    }
}
