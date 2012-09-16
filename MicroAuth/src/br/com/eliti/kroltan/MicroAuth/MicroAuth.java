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
	
	public void Authorize(Player p, boolean informLogin) {
		p.setMetadata("authorized", new FixedMetadataValue(this, true));
		if (informLogin) {
			p.sendMessage(MSG_HEADER+"You logged in successfully.");
		}
	}
	
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
	
	public void Unauthorize(Player p) {
		p.removeMetadata("authorized", this);
	}
	
	public void Register(Player p, String password, String email) {
		String username = p.getName();
		if (!email.contains("@") || !email.contains(".")) {
			return;
		}
		config.set("users."+username+".password", password);
		config.set("users."+username+".email", email);
		saveConfig();
		p.sendMessage(MSG_HEADER+"You were registered with email "+ChatColor.GOLD+email);
	}
	
	public boolean isRegistered(Player p) {
		if (!config.getString("users."+p.getName()+"email").equals(null)) {
			return true;
		}
		return false;
	}
	public static boolean isAuthorized(Player p) {
		if (p.hasMetadata("authorized")) {
			return true;
		} else {
			return false;
		}
	}
}