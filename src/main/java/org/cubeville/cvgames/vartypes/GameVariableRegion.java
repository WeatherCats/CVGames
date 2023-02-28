package org.cubeville.cvgames.vartypes;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.cubeville.cvgames.utils.BlockUtils;
import org.cubeville.cvgames.utils.GameUtils;
import org.cubeville.cvgames.models.GameRegion;

import javax.annotation.Nullable;

public class GameVariableRegion extends GameVariable {
    GameRegion region;

    public GameVariableRegion() {}

    public GameVariableRegion(String description) {
        super(description);
    }

    @Override
    public void setItem(Player player, String input, String arenaName) throws Error {
        try {
            Location min = BlockUtils.getWESelectionMin(player);
            Location max = BlockUtils.getWESelectionMax(player);
            region = new GameRegion(min, max);
        }
        catch(IllegalArgumentException e) {
            throw new Error("Please make a cuboid worldedit selection before running this command.");
        }
    }

    @Override
    public Object getItem() {
        return region;
    }

    @Override
    public Object itemString() {
        if (region == null) return null;
        return GameUtils.blockLocToString(region.getMin()) + " ~ " + GameUtils.blockLocToString(region.getMax());
    }

    @Override
    public TextComponent displayString(String arenaName) {
        if (region == null) { return new TextComponent("null"); }
        TextComponent tc = new TextComponent("[Select Region]");
        tc.setBold(true);
        tc.setColor(ChatColor.AQUA);
        String fullPath = "arenas." + arenaName + ".variables." + path;
        tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cvgames selectrg " + fullPath));
        tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to select this region")));
        return tc;
    }

    @Override
    public boolean isValid() {
        return region != null;
    }

    @Override
    public void setItem(@Nullable Object object, String arenaName) {
        if (!(object instanceof String)) {
            region = null;
        } else {
            String[] locations = ((String) object).split(" ~ ");
            if (locations.length != 2) {
                region = null;
            } else {
                region = new GameRegion(
                    GameUtils.parseBlockLocation(locations[0]), // min
                    GameUtils.parseBlockLocation(locations[1]) // max
                );
            }
        }
    }

    @Override
    public String typeString() {
        return "Region";
    }
}
