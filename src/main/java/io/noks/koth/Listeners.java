package io.noks.koth;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listeners implements Listener {
	private Main main;
	public Listeners(Main main) {
		this.main = main;
		main.getServer().getPluginManager().registerEvents(this, main);
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		this.leaveAction(event.getPlayer());
	}
	
	@EventHandler
	public void onKick(PlayerKickEvent event) {
		this.leaveAction(event.getPlayer());
	}
	
	private void leaveAction(Player player) {
		if (main.active != null && main.active.getPlayers().containsKey(player.getUniqueId())) {
			main.active.removePlayer(player.getUniqueId());
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		if (event.getEntity() instanceof Player) {
			Player killed = event.getEntity();
			if (main.active != null && main.active.getPlayers().containsKey(killed.getUniqueId())) {
				main.active.removePlayer(killed.getUniqueId());
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPortalTook(PlayerPortalEvent event) {
		if (main.active == null) {
			event.setCancelled(true);
			return;
		}
		// TODO: Portal TP
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		if (main.active != null) {
			Player player = event.getPlayer();
			if (main.active.getPlayers().containsKey(player.getUniqueId()) && !main.active.isLocationInZone(player.getLocation())) {
				main.active.removePlayer(player.getUniqueId());
				return;
			}
			if (!main.active.getPlayers().containsKey(player.getUniqueId()) && main.active.isLocationInZone(player.getLocation())) {
				main.active.addPlayer(player.getUniqueId());
				return;
			}
		}
	}
}
