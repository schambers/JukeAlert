package com.untamedears.JukeAlert;

import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fusesource.jansi.Ansi.Color;

/**
 *  Represents a class which contains a single method for executing commands 
 * 	the commands for JukeAlert
 *
 */
public class JukeAlertCommands implements CommandExecutor {

	private JukeAlert plugin;

	/**
	 * constructor
	 * @param plugin - the JukeAlert plugin instance
	 */
	public JukeAlertCommands(JukeAlert plugin) {
		this.plugin = plugin;
		
		// < DEBUGGING >
		ArrayList<String> tmp = new ArrayList<String>();
		for (String command : this.plugin.getDescription().getCommands().keySet()) {
			tmp.add(command);
		}
		this.plugin.getLogger().log(Level.FINE, "Commands present in the plugin.yaml file: " + tmp);
		// < END DEBUGGING>
	}

	/**
	 * Executes a command and returns whether it was successful or not
	 * 
	 * @param sender - source of the command
	 * @param cmd - command that was executed
	 * @param label - alias of the command that was used
	 * @param args - passed command arguments
	 * @return True if the command was successful, false otherwise
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		// Did a player send this command?
		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
		}
		if (label.equalsIgnoreCase("jahelp")) {
			player.sendMessage(Color.RED + "Help \n ");
			return true;
		}
		return false;
	}
}
