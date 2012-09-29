package br.com.eliti.kroltan.MicroWelcome;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
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
		if (MicroWelcome.instance.motdMode.equals("auth")) {
			Player p = event.getPlayer();
			if (p.hasMetadata("authorized")) {
				MicroWelcome.instance.ShowMOTD(p, false);
			}
		}
	}
}
