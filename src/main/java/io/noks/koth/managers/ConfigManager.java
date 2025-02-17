package io.noks.koth.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import io.noks.koth.KoTH;
import io.noks.koth.Main;

public class ConfigManager {
	public static List<KoTH> list = new ArrayList<>();

	public ConfigManager(Main main) {
		File configFile = new File(main.getDataFolder(), "config.yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		ConfigurationSection kothsSection = config.getConfigurationSection("koths");

		if (kothsSection == null) {
			return;
		}

		Set<String> kothKeys = kothsSection.getKeys(false);
		for (String kothName : kothKeys) {
			ConfigurationSection kothData = kothsSection.getConfigurationSection(kothName);
			if (kothData == null)
				continue;

			String schedule = kothData.getString("schedule", "SUNDAY|12:00");
			ConfigurationSection zoneSection = kothData.getConfigurationSection("zone");
			if (zoneSection == null)
				continue;

			String worldName = zoneSection.getString("world-name");
			World world = Bukkit.getWorld(worldName);
			if (world == null) {
				Bukkit.getLogger().warning("Invalid world for KoTH " + kothName);
				continue;
			}

			// TODO: location

			// KoTH koth = new KoTH(main...);
			// list.add(koth);
		}
	}
}
