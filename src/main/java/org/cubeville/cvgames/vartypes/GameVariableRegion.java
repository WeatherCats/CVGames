package org.cubeville.cvgames.vartypes;

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
            Location max = BlockUtils.getWESelectionMax(player).add(1.0, 1.0, 1.0);
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
