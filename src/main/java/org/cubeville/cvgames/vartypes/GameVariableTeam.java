package org.cubeville.cvgames.vartypes;

public class GameVariableTeam extends GameVariableObject {
    public GameVariableTeam(String name) {
        super(name);
        addField("item", new GameVariableItem());
        addField("name", new GameVariableString());
        addField("chat-color", new GameVariableChatColor());
    }
}
