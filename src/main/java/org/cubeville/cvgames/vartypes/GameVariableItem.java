package org.cubeville.cvgames.vartypes;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import static org.cubeville.cvgames.CVGames.getInstance;

public class GameVariableItem extends GameVariable {
    ItemStack item;

    @Override
    public void setItem(Player player, String input, String arenaName) throws Error {
        item = player.getInventory().getItemInMainHand();
        item.setAmount(1);
    }

    @Override
    public Object getItem() {
        return item;
    }

    @Override
    public Object itemString() {
        return item;
    } // this will serialize i think?

    @Override
    public boolean isValid() {
        return item != null;
    }

    @Override
    public void setItem(@Nullable Object object, String arenaName) {
        if (!(object instanceof ItemStack)) {
            item = null;
        } else {
            item = (ItemStack) object;
        }
    }

    @Override
    public ItemStack getFromPath(String path) {
        this.path = path;
        return getInstance().getConfig().getItemStack(path);
    }

    @Override
    public String typeString() {
        return "Item";
    }

    @Override
    public TextComponent displayString(String arenaName) {
        if (item == null) { return new TextComponent("null"); }
        TextComponent tc = new TextComponent("[Get Item]");
        tc.setBold(true);
        tc.setColor(ChatColor.AQUA);
        tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cvgames giveitem " + path));
        tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to get this item")));
        return tc;
    }
}
