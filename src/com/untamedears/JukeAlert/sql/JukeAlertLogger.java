/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.untamedears.JukeAlert.sql;

import com.untamedears.JukeAlert.JukeAlert;

import java.awt.Event;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;


/**
 * Class that "logs" information about our 'snitch' blocks and other actions
 * that occur near a snitch block that we need to log.
 * 
 * Note that this isn't a logger like the java.util.logging.Logger class
 *
 * @author Dylan Holmes
 */
public class JukeAlertLogger {


    private JukeAlert plugin;
    private Database db;

    
    public JukeAlertLogger(JukeAlert plugin) {
        this.plugin = plugin;

        Configuration c = plugin.getConfig();

        String host = c.getString("db.host");
        String dbname = c.getString("db.name");
        String user = c.getString("db.user");
        String pass = c.getString("db.pass");
        String prefix = c.getString("db.prefix");

        if (host == null) {
            host = "localhost";
            c.set("db.host", host);
        }

        if (dbname == null) {
            dbname = "mydb";
            c.set("db.name", dbname);
        }

        if (user == null) {
            user = "root";
            c.set("db.user", user);
        }

        if (pass == null) {
            pass = "admin";
            c.set("db.pass", pass);
        }

        if (prefix == null) {
            prefix = "pvp_";
            c.set("db.prefix", prefix);
        }

        plugin.saveConfig();

        this.db = new Database(host, dbname, user, pass, prefix, this.plugin.getLogger());
        boolean connected = this.db.connect();
        if (connected) {
            genTables();
        } else {
            this.plugin.getLogger().log(Level.SEVERE, "Could not connect to the database! Fill out your config.yml!");
        }
    }

    public Database getDb() {
        return db;
    }

    /**
     * Table generator
     */
    private void genTables() {
    	
    	// TODO: MARK
    	// since the whole point of this plugin is to be faster / space efficent, we should look at these values and see if we
    	// really need such high limits, example, the snitch_x is a integer with 10 digits, but since a block can't be placed on a 
    	// half coordinate (or else it would be float), and the current map is 15,000 x 15,000, why do we need to store space for 5 
    	// more digits for every record if we don't need to? Same for group (do we store the ID that the citadel plugin uses to reference a group
    	// or just the group name? also why 40 characters), and the second table, we should probably have a details_id be the unique key,
    	// and then either a FOREIGN KEY or just a integer that references the row in the 'snitches' table.
        //Snitches
        db.execute("CREATE TABLE IF NOT EXISTS `" + db.getPrefix() + "snitches` ("
                + "`snitch_id` int(10) unsigned NOT NULL AUTO_INCREMENT,"
                + "`snitch_world` varchar(40) NOT NULL,"
                + "`snitch_x` int(10) NOT NULL,"
                + "`snitch_y` int(10) NOT NULL,"
                + "`snitch_z` int(10) NOT NULL,"
                + "`snitch_group` varchar(40) NOT NULL,"
                + "`snitch_cuboid_x` int(10) NOT NULL,"
                + "`snitch_cuboid_y` int(10) NOT NULL,"
                + "`snitch_cuboid_z` int(10) NOT NULL,"
                + "PRIMARY KEY (`snitch_id`));");
        //Snitch Details
        db.execute("CREATE TABLE IF NOT EXISTS `" + db.getPrefix() + "snitch_details` ("
                + "`snitch_id` int(10) unsigned NOT NULL AUTO_INCREMENT,"
                + "`snitch_location` varchar(40) NOT NULL,"
                + "`snitch_log_time` datetime,"
                + "`snitch_info` text,"
                + "PRIMARY KEY (`snitch_id`));");
    }

    /**
     * Gets @limit events about that snitch. 
     * @param loc
     * @param limit
     * @return
     */
    public Map<String, Date> getSnitchInfo(Location loc, int limit) {
        Map<String, Date> info = new HashMap<>();
    	// TODO MARK: oh god sql injection BAD BAD BAD

        String query = "SELECT snitch_info, snitch_log_time"
                + " FROM " + db.getPrefix() + "snitch_details WHERE snitch_location='"
                + "World: " + loc.getWorld().getName() + " X: "
                + loc.getBlockX() + " Y: " + loc.getBlockY() + " Z: " + loc.getBlockZ()
                + "'"
                + "GROUP BY snitch_location LIMIT " + limit + ";";
        ResultSet set = db.getResultSet(query);
        try {
            while (set.next()) {
                info.put(set.getString("snitch_info"), set.getDate("snitch_log_time"));
            }
        } catch (SQLException ex) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not get Snitch Details!", ex);
        }
        return info;
    }

    /**
     * 
     * @param player
     * @param entity
     */
    public void logSnitchEntityKill(Player player, Entity entity) {
    }

    /**
     * @param player
     * @param victim
     */
    public void logSnitchPlayerKill(Player player, Player victim) {
    }

    /**
     * @param player
     * @param field
     */
    public void logSnitchEntry(Player player) {
    }

    /**
     * @param player
     * @param block
     */
    public void logSnitchBlockBreak(Player player, Block block) {
    }

    /**
     * @param player
     * @param block
     */
    public void logSnitchBucketEmpty(Player player, Block block, String type) {
    }

    /**
     * @param player
     * @param block
     */
    public void logSnitchBucketFill(Player player, Block block) {
    }

    /**
     * @param player
     * @param block
     */
    public void logSnitchBlockPlace(Player player, Block block) {
    }

    /**
     * @param player
     * @param block
     */
    public void logSnitchUsed(Player player, Block block) {
    }

    //Logs the snitch being placed at World, x, y, z in the database.
    public void logSnitchPlace(String world, String group, int x, int y, int z) {
    	
    	// TODO MARK: oh god sql injection BAD BAD BAD
        db.execute("INSERT INTO " + db.getPrefix() + "snitches "
                + "(snitch_world, snitch_x, snitch_y, snitch_z, snitch_group, snitch_cuboid_x, snitch_cuboid_y, snitch_cuboid_z) "
                + "VALUES("
                + "'" + world + "',"
                + "'" + x + "',"
                + "'" + y + "',"
                + "'" + z + "',"
                + "'" + group + "',"
                + "'" + 11 + "',"
                + "'" + 11 + "',"
                + "'" + 11 + "'"
                + ")");
    }

    //Removes the snitch at the location of World, X, Y, Z from the database.
    public void logSnitchBreak(String world, double x, double y, double z) {
    	// TODO MARK: oh god sql injection BAD BAD BAD

        db.execute("DELETE FROM " + db.getPrefix() + "snitches WHERE snitch_world='" + world + "' AND snitch_x='" + x + "' AND snitch_y='" + y + "' AND snitch_z='" + z + "'");
    }

    //Changes the group of which the snitch is registered to at the location of loc in the database.
    public void updateGroupSnitch(Location loc, String group) {
        int lX = loc.getBlockX();
        int lY = loc.getBlockY();
        int lZ = loc.getBlockZ();
    	// TODO MARK: oh god sql injection BAD BAD BAD

        db.execute("UPDATE " + db.getPrefix() + "snitches SET snitch_group='" + group + "' WHERE snitch_world='"
                + loc.getWorld().getName() + "' AND snitch_x='" + lX + "' AND snitch_y='" + lY + "' AND snitch_z='" + lZ + "'");
    }

    //Updates the cuboid size of the snitch in the database.
    public void updateCubiodSize(Location loc, int x, int y, int z) {
        int lX = loc.getBlockX();
        int lY = loc.getBlockY();
        int lZ = loc.getBlockZ();
    	// TODO MARK: oh god sql injection BAD BAD BAD

        db.execute("UPDATE " + db.getPrefix() + "snitches SET snitch_cuboid_x='" + x + "', snitch_cuboid_y='" + y
                + "', snitch_cuboid_z='" + z + "' WHERE snitch_world='" + loc.getWorld().getName() + "' AND snitch_x='" + lX + "' AND snitch_y='" + lY + "' AND snitch_z='" + lZ + "'");
    }

}
