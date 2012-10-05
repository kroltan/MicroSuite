package br.com.eliti.kroltan.MicroWelcome;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class MicroWelcomeListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		p.setMetadata("noMOTD", new FixedMetadataValue(MicroWelcome.instance, false));
		MicroWelcome.instance.Welcome(p);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (MicroWelcome.instance.motdMode.equalsIgnoreCase("auth")) {
			if (player.hasMetadata("noMOTD") && player.getMetadata("noMOTD").get(0).asBoolean() == false) {
				MicroWelcome.instance.Welcome(player);
				player.removeMetadata("noMOTD", MicroWelcome.instance);
			}
		}
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		p.setMetadata("noMOTD", new FixedMetadataValue(MicroWelcome.instance, true));
		MicroWelcome.instance.GoodbyeBroadcast(p);
	}
}
