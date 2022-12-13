package org.cubeville.cvgames.vartypes;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.cubeville.cvgames.utils.GameUtils;

import javax.annotation.Nullable;

public class GameVariableDyeColor extends GameVariable {

        private DyeColor dyeColor;

        public GameVariableDyeColor() {}

        public GameVariableDyeColor(String description) {
                super(description);
        }

        @Override
        public void setItem(Player player, String input, String arenaName) {
                try {
                        if (input == null) {
                                dyeColor = null;
                        } else {
                                dyeColor = DyeColor.valueOf(input);
                        }
                } catch (IllegalArgumentException e) {
                        throw new Error("Dye color with name " + input + " does not exist.");
                }
        }

        @Override
        public DyeColor getItem() {
                return dyeColor;
        }

        @Override
        public String typeString() {
                return "Dye Color";
        }

        @Override
        public String itemString() {
                if (dyeColor == null) {
                        return null;
                }
                return dyeColor.name();
        }

        @Override
        public void setItem(@Nullable Object string, String arenaName) {
                if (!(string instanceof String)) {
                        dyeColor = null;
                } else {
                        dyeColor = DyeColor.valueOf((String) string);
                }
        }

        @Override
        public boolean isValid() {
                return dyeColor != null;
        }
}
