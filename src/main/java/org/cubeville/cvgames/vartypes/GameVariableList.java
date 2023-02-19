package org.cubeville.cvgames.vartypes;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.cubeville.cvgames.CVGames.getInstance;

public class GameVariableList<GV extends GameVariable> extends GameVariable {

    private Class<GV> variableClass;
    private Integer minimumSize, maximumSize;
    private List<GV> currentValue = new ArrayList<>();

    public GameVariableList(Class<GV> variableClass) {
        init(variableClass, 1, null);
    }

    public GameVariableList(Class<GV> variableClass, String description) {
        super(description);
        init(variableClass, 1, null);
    }

    public GameVariableList(Class<GV> variableClass, Integer minimumSize, @Nullable Integer maximumSize) {
        init(variableClass, minimumSize, maximumSize);
    }

    public GameVariableList(Class<GV> variableClass, Integer minimumSize, @Nullable Integer maximumSize, String description) {
        super(description);
        init(variableClass, minimumSize, maximumSize);
    }

    private void init(Class<GV> variableClass, @Nullable Integer minimumSize, @Nullable Integer maximumSize) {
        this.variableClass = variableClass;
        if (minimumSize != null) {
            this.minimumSize = minimumSize;
        }
        if (maximumSize != null) {
            this.maximumSize = maximumSize;
        }
    }

    @Override
    public void addItem(Player player, String input, String arenaName) throws Error {
        try {
            if (maximumSize != null && currentValue.size() >= maximumSize) throw new Error("This list is at max capacity.");

            GV variable = variableClass.getDeclaredConstructor().newInstance();
            variable.setItem(player, input, arenaName);
            if (variable instanceof GameVariableObject) {
                ((GameVariableObject) variable).populateFields(arenaName, path);
            }
            variable.path = path + "." + currentValue.size();
            this.currentValue.add(variable);
        }
        catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            throw new Error("Could not create list variable properly, please contact a system administrator.");
        }
    }

    public void removeVariable(String arenaName, String path, int index) {
        this.currentValue.get(index).clearItem();
        this.currentValue.remove(index);
        storeItem(arenaName, path);
    }

    @Override
    public void setItem(Player player, String input, String arenaName) throws Error
    {
        throw new Error("Cannot set a list variable, must use add or remove");
    }

    @Override
    public void setItem(Object obj, String arenaName) {
        throw new Error("Can't do setItem with string on list var");
    }

    private void addItem(Object object, String arenaName) {
        try {
            if (maximumSize != null && currentValue.size() >= maximumSize) throw new Error("This list is at max capacity.");

            GV variable = variableClass.getDeclaredConstructor().newInstance();
            variable.setItem(object, arenaName);
            if (variable instanceof GameVariableObject) {
                ((GameVariableObject) variable).populateFields(arenaName, path);
            }
            variable.path = path + "." + currentValue.size();
            this.currentValue.add(variable);
        }
        catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            throw new Error("Could not create list variable properly, please contact a system administrator.");
        }
    }

    public GameVariable addBlankGameVariable(String arenaName) {
        try {
            if (maximumSize != null && currentValue.size() >= maximumSize) throw new Error("This list is at max capacity.");
            GV variable = variableClass.getDeclaredConstructor().newInstance();
            if (variable instanceof GameVariableObject) {
                ((GameVariableObject) variable).populateFields(arenaName, path);
            }
            variable.path = path + "." + currentValue.size();
            this.currentValue.add(variable);
            return variable;
        }
        catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            throw new Error("Could not create list variable properly, please contact a system administrator.");
        }
    }

    @Override
    public String typeString() {
        try {
            return "List of " + variableClass.getDeclaredConstructor().newInstance().typeString() + "s";
        }
        catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            return "ERROR";
        }
    }


    public void setItems(List<Object> objects, String arenaName) {
        currentValue.clear();
        for (Object obj : objects) {
            addItem(obj, arenaName);
        }
    }

    public List<Object> getItem() {
        return this.currentValue.stream().map(GameVariable::getItem).collect(Collectors.toList());
    }

    public GameVariable getVariableAtIndex(int index) {
        if (index >= this.currentValue.size() || index < 0) { return null; }
        return this.currentValue.get(index);
    }

    @Override
    public List<Object> itemString() {
        List<Object> out = new ArrayList<>();
        for (GameVariable var : this.currentValue) {
            out.add(var.itemString());
        }
        return out;
    }

    @Override
    public void addVariable(String arenaName, String variableName, Player player, String input) throws Error {
        addItem(player, input, arenaName);
        storeItem(arenaName, variableName);
    }

    @Override
    public boolean isValid() {
        for (GameVariable var : this.currentValue) {
            if (!var.isValid()) return false;
        }
        return currentValue.size() >= minimumSize && (maximumSize == null || currentValue.size() <= maximumSize);
    }

    @Override public void storeItem(String arenaName, String path) {
        final String fullPath = "arenas." + arenaName + ".variables." + path;
        // clear the full path
        getInstance().getConfig().set("arenas." + arenaName + ".variables." + path, null);
        // for each item in the list, store the child item under <path>.<i>
        for (int i = 0; i < currentValue.size(); i++) {
            currentValue.get(i).storeItem(arenaName, path + "." + i);
        }
        getInstance().saveConfig();
    }

    public Class<GV> getVariableClass() {
        return variableClass;
    }

    @Override
    public TextComponent displayString(String arenaName) {
        if (this.currentValue.size() == 0) { return new TextComponent("[]"); }
        TextComponent out = new TextComponent();
        for (int i = 0; i < currentValue.size(); i++) {
            GameVariable var = currentValue.get(i);
            out.addExtra("\n  ");
            if (var instanceof GameVariableObject) {
                out.addExtra("§f- ");
                String[] splitPath =  var.path.split("\\.");
                if (splitPath.length == 2) {
                    try {
                        TextComponent tc = new TextComponent("[Edit This " + var.typeString() +"]");
                        tc.setColor(ChatColor.AQUA);
                        tc.setBold(true);
                        tc.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/cvgames arena " + arenaName + " setedit " + splitPath[0] + " " + (Integer.parseInt(splitPath[1]) + 1)));
                        tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Set editing item to this object")));
                        out.addExtra(tc);
                        out.addExtra("  ");
                    } catch (NumberFormatException e) { /* then just dont suggest the command */ }
                }
            } else {
                out.addExtra("§f- ");
            }
            out.addExtra(var.isValid() ? "§a" : "§c");
            out.addExtra(var.displayString(arenaName));
            out.addExtra("§r");
        }
        return out;
    }
}
