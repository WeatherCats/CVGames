package org.cubeville.cvgames.vartypes;

import org.bukkit.entity.Player;
import org.cubeville.commons.commands.CommandExecutionException;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GameVariableList<GV extends GameVariable> extends GameVariable {

	private Class<GV> variableClass;
	private Integer minimumSize, maximumSize;
	private List<GV> currentValue = new ArrayList<>();

	public GameVariableList(Class<GV> variableClass) {
		this.variableClass = variableClass;
		this.minimumSize = 1;
	}

	public GameVariableList(Class<GV> variableClass, int minimumSize, int maximumSize) {
		this.variableClass = variableClass;
		this.minimumSize = minimumSize;
		this.maximumSize = maximumSize;
	}

	@Override
	public void addItem(Player player, String input, String arenaName) throws CommandExecutionException {
		try {
			if (maximumSize != null && currentValue.size() >= maximumSize) throw new CommandExecutionException("This list is at max capacity.");

			GV variable = variableClass.getDeclaredConstructor().newInstance();
			variable.setItem(player, input, arenaName);
			this.currentValue.add(variable);
		}
		catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
			throw new CommandExecutionException("Could not create list variable properly, please contact a system administrator.");
		}
	}

	@Override
	public void setItem(Player player, String input, String arenaName) throws CommandExecutionException
	{
		throw new CommandExecutionException("Cannot set a list variable, must use add or remove");
	}

	@Override
	public void setItem(String string, String arenaName) {
		throw new Error("Can't do setItem with string on list var");
	}

	private void addItem(String string, String arenaName) {
		try {
			if (maximumSize != null && currentValue.size() >= maximumSize) throw new Error("This list is at max capacity.");

			GV variable = variableClass.getDeclaredConstructor().newInstance();
			variable.setItem(string, arenaName);
			this.currentValue.add(variable);
		}
		catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
			throw new Error("Could not create list variable properly, please contact a system administrator.");
		}
	}

	@Override
	public String displayString() {
		try {
			return "List of " + variableClass.getDeclaredConstructor().newInstance().displayString() + "s";
		}
		catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
			return "ERROR";
		}
	}


	public void setItems(List<String> strings, String arenaName) {
		currentValue.clear();
		for (String string : strings) {
			addItem(string, arenaName);
		}
	}

	public List<Object> getItem() {
		return this.currentValue.stream().map(GameVariable::getItem).collect(Collectors.toList());
	}

	@Override
	public List<Object> itemString() {
		return this.currentValue.stream().map(GameVariable::itemString).collect(Collectors.toList());
	}

	@Override
	public void addVariable(String arenaName, String variableName, Player player, String input) throws CommandExecutionException {
		addItem(player, input, arenaName);
		storeItem(arenaName, variableName);
	}

	@Override
	public boolean isValid() {
		return currentValue.size() >= minimumSize && (maximumSize == null || currentValue.size() <= maximumSize);
	}

}
