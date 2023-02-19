package org.cubeville.cvgames.vartypes;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class GameVariableBlockData extends GameVariable {

        private BlockData blockData;

        public GameVariableBlockData() {}

        public GameVariableBlockData(String description) {
                super(description);
        }

        @Override
        public void setItem(Player player, String input, String arenaName) throws Error {
                Block b = player.getTargetBlock(null, 20);
                blockData = b.getBlockData();
                if (b.isEmpty()) {
                        TextComponent warning = new TextComponent("§lWARNING: You set this block data variable to be an empty block. Was this intended?");
                        warning.setColor(ChatColor.of("#ffcc00"));
                        player.spigot().sendMessage(warning);
                }
        }

        @Override
        public Object getItem() {
                return blockData;
        }

        @Override
        public Object itemString() {
                if (blockData == null) {
                        return null;
                }
                return blockData.getAsString();
        }

        @Override
        public boolean isValid() {
                return blockData == null;
        }

        @Override
        public void setItem(@Nullable Object item, String arenaName) {
                if (!(item instanceof String)) {
                        blockData = null;
                } else {
                        blockData = Bukkit.createBlockData((String) item);
                }
        }

        @Override
        public String typeString() {
                return "Block Data";
        }
}
