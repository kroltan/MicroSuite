package br.com.eliti.kroltan.MicroAuth;


import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class MicroAuth extends JavaPlugin {

	static MicroAuth instance;
	static String MSG_HEADER = ChatColor.GREEN+"[MicroAuth] "+ChatColor.DARK_GREEN;
	Logger logger;
	Server server;
	FileConfiguration config;
	
	public boolean loginOnJoin;
	
	@Override
	public void onEnable() {
		logger = getLogger();
		server = getServer();
		config = getConfig();
		saveDefaultConfig();
		server.getPluginManager().registerEvents(new MicroAuthListener(), this);
		instance = this;
		loginOnJoin = !config.getBoolean("require_login_on_join");
		logger.info("MicroAuth has been enabled");
	}
	
	@Override
	public void onDisable() {
		logger.info("MicroAuth has been disabled");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("login") && sender instanceof Player && args.length == 1) {
			return Authorize((Player)sender, args[0]);
		} else if (cmd.getName().equalsIgnoreCase("register") && sender instanceof Player && args.length == 2) {
			Register((Player)sender, args[0], args[1]);
			return true;
		}
		return false;
	}
	
	
	/**
	 * Authorizes a player, freeing him/her of the login lock. <br>
	 * This method doesn't validate the player's password. <br>
	 * See <i>Authorize(Player, String)</i> for login validation.
	 * @param p Player to authenticate
	 * @param informLogin Whether or not display the "login successful" message to the player
	 */
	public void Authorize(Player p, boolean informLogin) {
		p.setMetadata("authorized", new FixedMetadataValue(this, true));
		if (informLogin) {
			p.sendMessage(MSG_HEADER+"You logged in successfully.");
		}
	}
	
	
	/**
	 * Authorizes a player if the provided password is correct <br>
	 * This method validates the player's password. <br>
	 * See <i>Authorize(Player, boolean)</i> for instant login
	 * @param p Player to authenticate
	 * @param password The player's password (or not), to be checked.
	 * @return
	 */
	public boolean Authorize(Player p, String password) {
		if (password.equals(config.getString("users."+p.getName()+".password").replace("'", ""))) {
			p.setMetadata("authorized", new FixedMetadataValue(this, true));
			p.sendMessage(MSG_HEADER+"You logged in successfully.");
			return true;
		} else {
			p.sendMessage(MSG_HEADER+"Wrong password or arguments, please try again");
			return false;
		}
	}
	

	/**
	 * Logs off a player, if he was logged in
	 * @param p Player to logout
	 * @return Whether the player was sucessfully logged out or not
	 */
	public boolean Unauthorize(Player p) {
		if (p.hasMetadata("authorized")) {
			p.removeMetadata("authorized", this);
			return true;
		}
		return false;
	}
	
	
	/**
	 * Registers a player with the informed credentials. <br>
	 * USE WITH EXTREME CAUTION: Always check if the player is already registered.
	 * @param p Player to register
	 * @param password Player's password
	 * @param email Player's email
	 */
	public void Register(Player p, String password, String email) {
		if (!isRegistered(p) || isAuthorized(p)) {
			String username = p.getName();
			if (!email.contains("@") || !email.contains(".")) {
				return;
			}
			config.set("users."+username+".password", password);
			config.set("users."+username+".email", email);
			saveConfig();
			p.sendMessage(MSG_HEADER+"You were registered with email "+ChatColor.GOLD+email);
		} else {
			p.sendMessage(MSG_HEADER+"You're already registered and not logged in!");
			p.sendMessage(MSG_HEADER+"You can change your credentials when logged in,");
			p.sendMessage(MSG_HEADER+"Or ask the server's admin to change it for you.");
		}
	}
	
	
	/**
	 * Checks if the specified player is registered
	 * @param p The player to check registration
	 * @return The player is registered or not
	 */
	public boolean isRegistered(Player p) {
		return config.isSet("users."+p.getName());
	}
	
	/**
	 * Checks if the specified player is logged in
	 * @param p The player to check login state
	 * @return The player's login state
	 */
	public static boolean isAuthorized(Player p) {
		return p.hasMetadata("authorized");
	}
	
}