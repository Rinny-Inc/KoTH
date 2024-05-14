package io.noks.koth;

import java.util.LinkedHashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
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
	
	public KoTH(Main main, boolean guildAllowed, Location location, int duration) {
		this.main = main;
		this.inZone = Maps.newLinkedHashMap();
		this.guildAllowed = guildAllowed;
		this.location = location;
		this.duration = duration;
		this.startTask();
	}
	
	public boolean isWon() {
		return this.inZone.values().stream().findFirst().orElse(0L) / 1000 >= this.duration * 60;
	}
	
	private void endKoTH() {
		// TODO: check if the dimension isnt overworld; kill all remaining players in the dimension after 1 hour.
		// TODO: spawn Bosses or spawn bosses at the start of the koth
		// TODO: Close the portail in the overworld after the dimension is closed
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
	
	private void startTask() {
		this.task = new BukkitRunnable() {
			
			@Override
			public void run() {
				if (KoTH.this.isWon()) {
					KoTH.this.endKoTH();
				}
			}
		}.runTaskTimerAsynchronously(main, 0, 20);
	}
	
	private void startDimensionCloseTask() {
		
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
