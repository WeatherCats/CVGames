package org.cubeville.cvgames.vartypes;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.cubeville.cvgames.utils.GameUtils;

import javax.annotation.Nullable;

public class GameVariableBlock extends GameVariable {

    private Block block;

    public GameVariableBlock() {}

    public GameVariableBlock(String description) {
        super(description);
    }

    @Override
    public void setItem(Player player, String input, String arenaName) throws Error {
        Block b = player.getTargetBlock(null, 20);
        if (b.isEmpty()) throw new Error("You need to be looking at a block to execute this command");
        block = b;
    }

    @Override
    public void setItem(@Nullable Object string, String arenaName) {
        if (!(string instanceof String)) {
            block = null;
        } else {
            block = GameUtils.parseBlockLocation((String) string).getBlock();
        }
    }

    public final String typeString() {
        return "Block";
    }

    @Override
    public Block getItem() {
        return block;
    }

    @Override
    public String itemString() {
        if (block == null) {
            return null;
        }
        return GameUtils.blockLocToString(block.getLocation());
    }

    @Override
    public boolean isValid() {
        return block != null;
    }

}
