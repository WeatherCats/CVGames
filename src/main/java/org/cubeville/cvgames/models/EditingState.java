package org.cubeville.cvgames.models;

import org.cubeville.cvgames.vartypes.GameVariableObject;

public class EditingState {

    public GameVariableObject gameVariableObject;
    public String path;

    public EditingState(GameVariableObject gameVariableObject, String path) {
        this.gameVariableObject = gameVariableObject;
        this.path = path;
    }
}
