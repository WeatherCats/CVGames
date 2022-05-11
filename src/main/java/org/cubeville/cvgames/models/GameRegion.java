package org.cubeville.cvgames.models;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GameRegion {
    Location min;
    Location max;

    public GameRegion(Location min, Location max) {
        this.min = min;
        this.max = max;
    }

    public Location getMin() {
        return min;
    }

    public Location getMax() {
        return max;
    }

    public boolean containsPlayer(Player player) {
        Location loc = player.getLocation();
        return (max.getX() >= loc.getX() && loc.getX() >= min.getX()) &&
        (max.getY() >= loc.getY() && loc.getY() >= min.getY()) &&
        (max.getZ() >= loc.getZ() && loc.getZ() >= min.getZ());
    }
}

