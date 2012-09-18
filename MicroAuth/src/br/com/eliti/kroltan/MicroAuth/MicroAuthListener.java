package br.com.eliti.kroltan.MicroAuth;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;

public class MicroAuthListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		if (MicroAuth.instance.loginOnJoin) {
			MicroAuth.instance.Authorize(p, false);
		} else {
			if (!MicroAuth.instance.isRegistered(p)) {
				p.sendMessage(MicroAuth.MSG_HEADER+"You must register before logging in. Register using "+ChatColor.GREEN+"/register <password> <email>");
			}
			if (!MicroAuth.isAuthorized(p)) {
				p.sendMessage(MicroAuth.MSG_HEADER+"Please login by using "+ChatColor.GREEN+"/login <password>"+ChatColor.DARK_GREEN+" to continue.");
			}
		}
	}
	@EventHandler
	public void onPlayerActionBed(PlayerBedEnterEvent e) {
		if (!MicroAuth.isAuthorized(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	@EventHandler
	public void onPlayerActionBucket(PlayerBucketEmptyEvent e) {
		if (!MicroAuth.isAuthorized(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	@EventHandler
	public void onPlayerActionBucket(PlayerBucketFillEvent e) {
		if (!MicroAuth.isAuthorized(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	@EventHandler
	public void onPlayerActionDrop(PlayerDropItemEvent e) {
		if (!MicroAuth.isAuthorized(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	@EventHandler
	public void onPlayerActionFish(PlayerFishEvent e) {
		if (!MicroAuth.isAuthorized(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	@EventHandler
	public void onPlayerActionInteractEntity(PlayerInteractEntityEvent e) {
		if (!MicroAuth.isAuthorized(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	@EventHandler
	public void onPlayerActionInteract(PlayerInteractEvent e) {
		if (!MicroAuth.isAuthorized(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	@EventHandler
	public void onPlayerActionMove(PlayerMoveEvent e) {
		if (!MicroAuth.isAuthorized(e.getPlayer())) {
			e.getPlayer().teleport(e.getPlayer());
			e.setCancelled(true);
		}
	}
	@EventHandler
	public void onPlayerActionPickup(PlayerPickupItemEvent e) {
		if (!MicroAuth.isAuthorized(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	@EventHandler
	public void onPlayerActionShear(PlayerShearEntityEvent e) {
		if (!MicroAuth.isAuthorized(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		MicroAuth.instance.Unauthorize(e.getPlayer());
	}
}
