package io.noks.koth;

import javax.annotation.Nullable;

import org.bukkit.plugin.java.JavaPlugin;

import io.noks.koth.managers.ConfigManager;
import io.noks.koth.task.SchedulerTask;

public class Main extends JavaPlugin {
	public ConfigManager config;
	public @Nullable KoTH active = null;
	
	@Override
	public void onEnable() {
		this.getConfig().options().copyDefaults(true);
		this.saveDefaultConfig();
		this.config = new ConfigManager(this);
		new Listeners(this);
		new SchedulerTask(this);
	}
	
	@Override
	public void onDisable() {
		if (this.active != null) {
			this.active.clearMemory();
			this.active = null;
		}
	}
}
