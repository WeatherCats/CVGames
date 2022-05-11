package org.cubeville.cvgames.games;

import org.cubeville.cvgames.vartypes.*;

public class PaintballTeam extends GameVariableObject {
    public PaintballTeam() {
        super("PaintballTeam");
        addField("name", new GameVariableString());
        addField("snowball-name", new GameVariableString());
        addField("chat-color", new GameVariableChatColor());
        addField("armor-color", new GameVariableString());
        addField("armor-color-damaged", new GameVariableString());
        addField("tps", new GameVariableList<>(GameVariableLocation.class));
    }
}
