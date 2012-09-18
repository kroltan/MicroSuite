package br.com.eliti.kroltan.MicroTP;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Kroltan
 *
 */
/**
 * @author Kroltan
 *
 */
public class MicroTP extends JavaPlugin {

	String MSG_HEADER = ChatColor.GREEN+"[MicroTP] "+ChatColor.DARK_GREEN;
	Logger logger;
	Server server;
	FileConfiguration config;
	
	@Override
	public void onEnable() {
		logger = getLogger();
		server = getServer();
		config = getConfig();
		saveDefaultConfig();
		logger.info("MicroTP has been enabled");
	}
	
	@Override
	public void onDisable() {
		logger.info("MicroTP has been disabled");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("tp") && sender instanceof Player && args.length > 0) {
			Player player = (Player)sender;
			Player target = server.getPlayer(args[0]);
			player.teleport(target);
			player.sendMessage(MSG_HEADER+"Teleported to "+ChatColor.GOLD+target.getName());
			return true;
		} else if (cmd.getName().equalsIgnoreCase("setwarp") && args.length > 0) {
			if (sender instanceof Player) {
				Player player = (Player)sender;
				addWarp(args[0], player.getLocation());
				player.sendMessage(MSG_HEADER+"Warp '"+ChatColor.GOLD+args[0]+ChatColor.DARK_GREEN+"' has been created");return true;
			}
		} else if (cmd.getName().equalsIgnoreCase("warp") && sender instanceof Player && args.length > 0) {
			Player player = (Player)sender;
			player.teleport(getWarpFromConfig(args[0]));
			player.sendMessage(MSG_HEADER+"Warped to '"+ChatColor.GOLD+args[0]+ChatColor.DARK_GREEN+"'.");
			return true;
		} else if (cmd.getName().equalsIgnoreCase("summon") && sender instanceof Player && args.length > 0) {
			if (!args[0].equalsIgnoreCase("accept") && server.getPlayer(args[0]) != null) {
				Player other = server.getPlayer(args[0]);
				Player player = (Player)sender;
				setMetadata(other, "summon_request", player, this);
				player.sendMessage(MSG_HEADER+"Sent summon request to "+ChatColor.GOLD+other.getDisplayName());
				other.sendMessage(MSG_HEADER+ChatColor.GOLD+player.getDisplayName()+ChatColor.DARK_GREEN+" wants to summon you. Type "+ChatColor.GREEN+"/summon accept"+ChatColor.DARK_GREEN+" to accept.");
			} else if (args[0].equalsIgnoreCase("accept")) {
				Player player = (Player)sender;
				if (getMetadata(player, "summon_request", this) != null) {
					Player caller = (Player)getMetadata(player, "summon_request", this);
					player.teleport(caller);
					player.sendMessage(MSG_HEADER+"Summon complete.");
					caller.sendMessage(MSG_HEADER+"Summon complete.");
					player.removeMetadata("summon_request", this);
				} else {
					player.sendMessage(MSG_HEADER+"You don't have any pending summons.");
				}
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("warplist")) {
			Set<String> list = config.getKeys(true);
			ArrayList<String> warps = new ArrayList<String>();
			for (String s : list) {
				warps.add(s.replace("warps.", "").replace("warps", ""));
			}
			sender.sendMessage(MSG_HEADER+ChatColor.GREEN+"Available warps: "+ChatColor.DARK_GREEN+warps.toString().replace("[, ", "").replace("]", "").replace(", ", ChatColor.GREEN+", "+ChatColor.DARK_GREEN));
			return true;
		} else if (cmd.getName().equalsIgnoreCase("delwarp") && args.length > 0) {
			addWarp(args[0], null);
			sender.sendMessage(MSG_HEADER+"Deleted warp '"+ChatColor.GOLD+args[0]+ChatColor.DARK_GREEN+"'.");
			return true;
		}
		return false;
	}
	
	
	/**
	 * Sets a new warp with the specified name and location. Will override if there is a warp with the same name
	 * @param name The warp's name
	 * @param pos The Location to set the warp at
	 * @return Whether a warp was overriden or not
	 */
	public boolean addWarp(String name, Location pos) {
		boolean had = config.isSet("warps."+name);
		config.set("warps."+name, pos);
		saveConfig();
		return had;
	}
	
	/**
	 * Gets a existing warp's location. If it doesn't exist, returns null
	 * @param warpName
	 * @return The warp's Location
	 */
	public Location getWarpFromConfig(String warpName) {
		if (config.isSet("warps."+warpName)){
			Location pos = (Location) config.get("warps."+warpName);
			if (pos != null) {
				World w = pos.getWorld();
				double x = pos.getX();
				double y = pos.getY();
				double z = pos.getZ();
				return new Location(w, x, y, z);
			}
		}
		return null;
	}
	
	private void setMetadata(Player player, String key, Object value, Plugin plugin){
		player.setMetadata(key,new FixedMetadataValue(plugin,value));
	}
	private Object getMetadata(Player player, String key, Plugin plugin){
		List<MetadataValue> values = player.getMetadata(key);  
		for(MetadataValue value : values){
			if(value.getOwningPlugin().getDescription().getName().equals(plugin.getDescription().getName())){
				return value.value();
			}
		}
		return null;
	}
}