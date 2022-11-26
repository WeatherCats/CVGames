package org.cubeville.cvgames.vartypes;

public class GameVariableTeam extends GameVariableObject {
    public GameVariableTeam() {
        super();
        this.addFields();
    }

    public GameVariableTeam(String description) {
        super(description);
        this.addFields();
    }

    private void addFields() {
        addField("item", new GameVariableItem("The item used to represent the team in the team selector GUI"));
        addField("name", new GameVariableString("This is the display name for the team"));
        addField("chat-color", new GameVariableChatColor("This is the text color used to display info along with the team"));
    }

    @Override
    public String typeString() {
        return "Team";
    }
}
