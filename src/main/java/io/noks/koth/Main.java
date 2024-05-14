package io.noks.koth;

import javax.annotation.Nullable;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	public @Nullable KoTH active = null;
	
	@Override
	public void onEnable() {
		new Listeners(this);
	}
	
	@Override
	public void onDisable() {
		if (this.active != null) {
			this.active.clearMemory();
			this.active = null;
		}
	}
}
