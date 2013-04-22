package com.untamedears.JukeAlert;

import com.untamedears.JukeAlert.sql.Database;
import com.untamedears.JukeAlert.sql.JukeAlertLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

public class JukeAlert extends JavaPlugin {
	private Manager manager;
	public  JukeAlertLogger jaLogger;
	private JukeAlertCommands jaCommandExecutor;
	private List<JukeAlertSnitch> snitches = new ArrayList<>(); //TODO: Add snitches to memory so it's not server intensive going to the SQL everytime.
	
	@Override
	public void onEnable() {
		this.manager.load();
		this.jaLogger = new JukeAlertLogger(this);

		// create our class that executes commands, and then set it 
		// to be the CommandExecutor for all of our commands defined in our plugin.yaml
		this.jaCommandExecutor = new JukeAlertCommands(this);
		for (String command : getDescription().getCommands().keySet()) {

			getCommand(command).setExecutor(jaCommandExecutor);
		}
	
	}

	@Override
	public void onDisable() {
		
		//TODO: Make sure everything saves properly and does save.
	}
	
	// Logs a message with the level of Info.
	public void log(String message) {
		this.getLogger().log(Level.INFO, message);
	}

}
