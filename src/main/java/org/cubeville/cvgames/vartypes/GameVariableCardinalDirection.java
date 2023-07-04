package org.cubeville.cvgames.vartypes;

import org.bukkit.entity.Player;
import org.cubeville.cvgames.enums.CardinalDirection;

import javax.annotation.Nullable;

public class GameVariableCardinalDirection extends GameVariable {

    public GameVariableCardinalDirection() {}

    public GameVariableCardinalDirection(String description) {
        super(description);
    }
    
    CardinalDirection direction;
    @Override
    public void setItem(Player player, String input, String arenaName) throws Error {
        double playerYaw = player.getLocation().getYaw();
        if (playerYaw >= 140 || playerYaw <= -140) {
            direction = CardinalDirection.NORTH;
        } else if (playerYaw >= -130 && playerYaw <= -50) {
            direction = CardinalDirection.EAST;
        } else if (playerYaw >= -40 && playerYaw <= 40) {
            direction = CardinalDirection.SOUTH;
        } else if (playerYaw >= 50 && playerYaw <= 130) {
            direction = CardinalDirection.WEST;
        } else {
            throw new Error("You must be clearly facing one of the cardinal directions for this variable to be assigned!");
        }
    }

    @Override
    public Object getItem() {
        return direction;
    }

    @Override
    public Object itemString() {
        return direction.toString();
    }

    @Override
    public boolean isValid() {
        return direction != null;
    }

    @Override
    public void setItem(@Nullable Object item, String arenaName) {
        if (item == null) { return; }
        direction = CardinalDirection.valueOf((String) item);
    }

    @Override
    public String typeString() {
        return "Cardinal Direction";
    }
}
