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
	private final String name;
	private final boolean guildAllowed;
	private final Location location;
	private final int duration;
	private BukkitTask task;
	private Main main;
	private final Date[] schedule;
	
	public KoTH(Main main, String name, boolean guildAllowed, Location location, Date[] schedule) {
		this.main = main;
		this.name = name;
		this.inZone = Maps.newLinkedHashMap();
		this.guildAllowed = guildAllowed;
		this.location = location;
		this.duration = 20 * 60 * 5;
		this.schedule = schedule;
	}
	
	public String getName() {
		return this.name;
	}
	
	public boolean isWon() {
	    long currentTime = System.currentTimeMillis();
	    long requiredMillis = (this.duration / 20) * 1000;

	    return this.inZone.values().stream()
	        .anyMatch(joinTime -> (currentTime - joinTime) >= requiredMillis);
	}
	
	private void endKoTH() {
		this.task.cancel();
		this.task = null;
		this.main.active = null;
		if (location.getWorld().getName().equals("world")) {
			return;
		}
		this.startDimensionCloseTask(this);
		// TODO: spawn Bosses or spawn bosses at the start of the koth
	}
	
	public boolean isLocationInZone(Location location) {
		return location.distanceSquared(this.location) <= (9 * 9) && location.getBlockY() >= this.location.getBlockY();
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
	
	public Date[] getSchedule() {
		return this.schedule;
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
	
	public boolean isScheduledNow() {
	    long now = System.currentTimeMillis();
	    for (Date scheduled : schedule) {
	        long diff = Math.abs(scheduled.getTime() - now);
	        if (diff <= 60000) {
	            return true;
	        }
	    }
	    return false;
	}
	
	public void startKoTHTask() {
		this.createPortal();
		this.main.active = this;
		final KoTH koth = this;
		this.main.getServer().broadcastMessage("KoTH " + this.name + " just started!");
		this.task = new BukkitRunnable() {
			
			@Override
			public void run() {
				if (!koth.isWon()) {
					// TODO: send a boss bar
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
		this.main = null;
	}
}
