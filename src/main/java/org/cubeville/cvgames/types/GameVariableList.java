package org.cubeville.cvgames.types;

import org.bukkit.entity.Player;
import org.cubeville.commons.commands.CommandExecutionException;

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
	public void addItem(Player player, String input) throws CommandExecutionException {
		try {
			if (maximumSize != null && currentValue.size() >= maximumSize) throw new CommandExecutionException("This list is at max capacity.");

			GV variable = variableClass.getDeclaredConstructor().newInstance();
			variable.setItem(player, input);
			this.currentValue.add(variable);
		}
		catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
			throw new CommandExecutionException("Could not create list variable properly, please contact a system administrator.");
		}
	}

	@Override
	public void setItem(Player player, String input) throws CommandExecutionException
	{
		throw new CommandExecutionException("Cannot set a list variable, must use add or remove");
	}

	public List<Object> getItem() {
		return this.currentValue.stream().map(GameVariable::getItem).collect(Collectors.toList());
	}

	@Override
	public List<Object> storeFormat() {
		return this.currentValue.stream().map(GameVariable::storeFormat).collect(Collectors.toList());
	}

	@Override
	public boolean isValid() {
		return currentValue.size() >= minimumSize && (maximumSize == null || currentValue.size() <= maximumSize);
	}

}
