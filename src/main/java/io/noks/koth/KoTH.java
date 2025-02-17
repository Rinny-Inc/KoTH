package io.noks.koth;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.minecraft.util.com.google.common.collect.Maps;

public class KoTH {
	private LinkedHashMap<UUID, Long> inZone;
	private boolean guildAllowed;
	private Location location;
	private int duration;
	private BukkitTask task;
	private Main main;
	private Date[] schedule;
	
	public KoTH(Main main, boolean guildAllowed, Location location, Date[] schedule) {
		this.main = main;
		this.inZone = Maps.newLinkedHashMap();
		this.guildAllowed = guildAllowed;
		this.location = location;
		this.duration = 20 * 60 * 5;
		this.schedule = schedule;
		this.startKoTHTask(this);
	}
	
	public boolean isWon() {
		return this.inZone.values().stream().findFirst().orElse(1L) / 1000 >= this.duration * 60;
	}
	
	private void endKoTH() {
		this.task.cancel();
		this.task = null;
		if (location.getWorld().getName().equals("world")) {
			return;
		}
		this.startDimensionCloseTask(this);
		// TODO: spawn Bosses or spawn bosses at the start of the koth
	}
	
	public boolean isLocationInZone(Location location) {
		return location.distanceSquared(this.location) <= 5 * 5;
	}

	public LinkedHashMap<UUID, Long> getPlayers() {
		return this.inZone;
	}
	
	public void addPlayer(UUID uuid) {
		this.inZone.put(uuid, System.currentTimeMillis());
	}
	
	public void removePlayer(UUID uuid) {
		this.inZone.remove(uuid);
	}
	
	public UUID getCapper() {
		return this.inZone.keySet().stream().findFirst().orElse(null);
	}

	public boolean isGuildAllowed() {
		return this.guildAllowed;
	}
	
	public Location getLocation() {
		return this.location;
	}
	
	public int getDuration() {
		return this.duration;
	}
	
	private void createPortal() {
		final World world = this.location.getWorld();
		if (world.getName().equals("world")) {
			return;
		}
		if (world.getName().contains("end")) {
			// TODO: open the End portal
			return;
		}
		// TODO: open Nether portal
	}
	
	private void closePortal() {
		final World world = this.location.getWorld();
		if (world.getName().equals("world")) {
			return;
		}
		if (world.getName().contains("end")) {
			// TODO: close the End portal
			return;
		}
		// TODO: close Nether portal
	}
	
	private void startKoTHTask(KoTH koth) {
		this.createPortal();
		this.task = new BukkitRunnable() {
			
			@Override
			public void run() {
				if (!koth.isWon()) {
					return;
				}
				koth.endKoTH();
			}
		}.runTaskTimerAsynchronously(main, 0, 20);
	}
	
	private void startDimensionCloseTask(KoTH koth) {
		this.task = new BukkitRunnable() {
			
			@Override
			public void run() {
				for (Player players : koth.location.getWorld().getPlayers()) {
					players.setHealth(0.0D);
				}
				koth.closePortal();
			}
		}.runTaskLaterAsynchronously(main, 20 * 60 * 60);
	}
	
	public void clearMemory() {
		if (this.task != null) {
			this.task.cancel();
			this.task = null;
		}
		if (!this.inZone.isEmpty()) {
			this.inZone.clear();
			this.inZone = null;
		}
		this.location = null;
		this.main = null;
	}
}
