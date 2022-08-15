package org.cubeville.cvgames.vartypes;

public class GameVariableTeam extends GameVariableObject {
    public GameVariableTeam() {
        super();
        addField("item", new GameVariableItem());
        addField("name", new GameVariableString());
        addField("chat-color", new GameVariableChatColor());
    }

    @Override
    public String typeString() {
        return "Team";
    }
}
