package br.com.eliti.kroltan.MicroWelcome;


import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class MicroWelcome extends JavaPlugin {

	static MicroWelcome instance;
	String MSG_HEADER = ChatColor.GREEN+"[MicroWelcome] "+ChatColor.DARK_GREEN;
	Logger logger;
	Server server;
	FileConfiguration config;
	public String motdMode = "join";
	
	@Override
	public void onEnable() {
		logger = getLogger();
		server = getServer();
		config = getConfig();
		saveDefaultConfig();
		motdMode = config.getString("motd.mode");
		server.getPluginManager().registerEvents(new MicroWelcomeListener(), this);
		instance = this;
		logger.info("MicroWelcome has been enabled");
	}
	
	@Override
	public void onDisable() {
		logger.info("MicroWelcome has been disabled");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("motd") && sender instanceof Player) {
			ShowMOTD((Player)sender, true);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("rules") && sender instanceof Player) {
			ShowRules((Player)sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("welcome") && args.length > 0) {
			String finalMessage = args[1];
			for (int i = 2; i < args.length; i+=1) {
				finalMessage = finalMessage+" "+args[i];
			}
			SetLoginBroadcast(getServer().getPlayer(args[0]), finalMessage);
			return true;
		}
		return false;
	}
	
	
	/**
	 * Welcomes a player. This function simply calls the <i>ShowMOTD</i>, <i>TryGiveStartingItems</i> and <i>LoginBroadcast</i> functions
	 * @param player The player to welcome
	 */
	public void Welcome(Player player) {
		if (motdMode.equalsIgnoreCase("join")) {
			ShowMOTD(player, true);
		}
		TryGiveStartingItems(player);
		if (config.getBoolean("login.enabled")) {
			LoginBroadcast(player);
		}
		
	}
	
	
	/**
	 * Shows the MOTD to the specified player
	 * @param player The player
	 */
	public void ShowMOTD(Player player, boolean force) {
		if (player.hasMetadata("noMOTD") && !force) {
			List<String> motd = config.getStringList("motd.message");
			for (int i = 0; i < motd.size(); i+=1) {
				String line = motd.get(i).replace("%SERVER%", server.getMotd()).replace("f:", "§").replace("%IP%", server.getIp())
						.replace("%CAP%", ""+server.getMaxPlayers()).replace("%PLAYERS%", ""+server.getOnlinePlayers().length);
				player.sendMessage(line);
			}
			player.removeMetadata("noMOTD", this);
			logger.info("Shown the MOTD to "+player.getDisplayName());
		} else if (force) {
			List<String> motd = config.getStringList("motd.message");
			for (int i = 0; i < motd.size(); i+=1) {
				String line = motd.get(i).replace("%SERVER%", server.getMotd()).replace("f:", "§").replace("%IP%", server.getIp())
						.replace("%CAP%", ""+server.getMaxPlayers()).replace("%PLAYERS%", ""+server.getOnlinePlayers().length);
				player.sendMessage(line);
			}
			player.removeMetadata("noMOTD", this);
			logger.info("Shown the MOTD to "+player.getDisplayName());
		}
	}
	
	
	/**
	 * Shows the rules to the specified player
	 * @param player The player
	 */
	public void ShowRules(Player player) {
		List<String> rules = config.getStringList("rules");
		for (int i = 0; i < rules.size(); i+=1) {
			String line = rules.get(i).replace("%SERVER%", server.getMotd()).replace("f:", "§").replace("%IP%", server.getIp())
						.replace("%CAP%", ""+server.getMaxPlayers()).replace("%PLAYERS%", ""+server.getOnlinePlayers().length);
			player.sendMessage(line);
		}
		logger.info("Shown the rules to "+player.getDisplayName());
	}
	
	
	/**
	 * Tries to give the starting kit to the specifed player
	 * @param player The player to give the items to
	 * @return If he received the kit or not
	 */
	public boolean TryGiveStartingItems(Player player) {
		if (config.getBoolean("kit.enabled")&& !config.getBoolean("kit.has."+player.getName())) {
			List<String> kit = config.getStringList("kit.items");
			for (int i = 0; i < kit.size(); i+=1) {
				int id, amount;
				byte data = 0;
				if (kit.get(i).contains(":")) {
					id = Integer.parseInt(kit.get(i).split(",")[0]);
					amount = Integer.parseInt(kit.get(i).split(",")[1].split(":")[0]);
					data = (byte) Integer.parseInt(kit.get(i).split(":")[1]);
				} else {
					id = Integer.parseInt(kit.get(i).split(",")[0]);
					amount = Integer.parseInt(kit.get(i).split(",")[1]);
				}
				player.getInventory().addItem(new ItemStack(id, amount, (short)0, data));
			}
			config.set("kit.has."+player.getName(), true);
			saveConfig();
			logger.info("Gave starter kit to "+player.getName());
			return true;
		}
		return false;
	}
	
	/**
	 * Broadcasts to all players the join message of the specified player
	 * @param player The player to inform
	 */
	public void LoginBroadcast(Player player) {
		if (config.contains("login."+player.getName())) {
			String message = config.getString("login."+player.getName())
					.replace("%SERVER%", server.getMotd()).replace("f:", "§").replace("%IP%", server.getIp()).replace("%HIM%", player.getDisplayName())
					.replace("%CAP%", ""+server.getMaxPlayers()).replace("%PLAYERS%", ""+server.getOnlinePlayers().length);
			server.broadcastMessage(message);
		}
	}
	
	/**
	 * Sets the login blroadcast message of the specified player
	 * @param player The player to set the broadcast
	 * @param message The broadcast message
	 */
	public void SetLoginBroadcast(Player player, String message) {
		config.set("login."+player.getName(), message);
		saveConfig();
	}
	
	/**
	 * Sets the logout broadcast message of the specified player
	 * @param player The player to set the broadcast
	 * @param message The broadcast message
	 */
	public void SetLogoutBroadcast(Player player, String message) {
		config.set("logout."+player.getName(), message);
		saveConfig();
	}
	
	/**
	 * Broadcasts to all players the quit message of the specified player
	 * @param player The player to inform
	 */
	public void GoodbyeBroadcast(Player player) {
		if (config.getBoolean("logout.enabled")) {
			if (config.contains("logout."+player.getName())) {
				String message = config.getString("logout."+player.getName())
						.replace("%SERVER%", server.getMotd()).replace("f:", "§").replace("%IP%", server.getIp()).replace("%HIM%", player.getDisplayName())
						.replace("%CAP%", ""+server.getMaxPlayers()).replace("%PLAYERS%", ""+server.getOnlinePlayers().length);
				server.broadcastMessage(message);
			}
		}
	}
}