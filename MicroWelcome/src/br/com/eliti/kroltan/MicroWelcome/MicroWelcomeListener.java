package br.com.eliti.kroltan.MicroWelcome;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class MicroWelcomeListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		MicroWelcome.instance.Welcome(event.getPlayer());
	}
}
