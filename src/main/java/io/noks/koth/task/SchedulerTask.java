package io.noks.koth.task;

import org.bukkit.scheduler.BukkitRunnable;

import io.noks.koth.KoTH;
import io.noks.koth.Main;

public class SchedulerTask extends BukkitRunnable {
	
	private Main main;
	public SchedulerTask(Main main) {
		this.main = main;
		this.runTaskTimerAsynchronously(main, 0L, 20L);
	}

	@Override
	public void run() {
		if (this.main.active != null) {
			return;
		}
		for (KoTH koth : main.config.list) {
			if (!koth.isScheduledNow()) {
				continue;
			}
			this.main.active = koth;
			return;
		}
	}
}
