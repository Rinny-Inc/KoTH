package io.noks.koth.managers;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import io.noks.koth.KoTH;
import io.noks.koth.Main;

public class ConfigManager {
	public List<KoTH> list = new ArrayList<>();

	public ConfigManager(Main main) {
		FileConfiguration config = main.getConfig();
		ConfigurationSection kothsSection = config.getConfigurationSection("koths");

		if (kothsSection == null) {
			return;
		}

		Set<String> kothKeys = kothsSection.getKeys(false);
		for (String kothName : kothKeys) {
			ConfigurationSection kothData = kothsSection.getConfigurationSection(kothName);
			if (kothData == null) {
				continue;
			}
			Date[] schedule = parseSchedule(kothData.getString("schedule"));
			ConfigurationSection zoneSection = kothData.getConfigurationSection("zone");
			if (zoneSection == null) {
				continue;
			}

			String worldName = zoneSection.getString("world-name");
			World world = Bukkit.getWorld(worldName);
			if (world == null) {
				Bukkit.getLogger().warning("Invalid world for KoTH " + kothName);
				continue;
			}
			String[] split = zoneSection.getString("middle-position").split(",");
			double x = Double.parseDouble(split[0]);
			double y = Double.parseDouble(split[1]);
			double z = Double.parseDouble(split[2]);
			Location loc = new Location(world, x, y, z);

			list.add(new KoTH(main, kothName, false, loc, schedule));
		}
	}

	private static Date[] parseSchedule(String schedule) {
		List<Date> dates = new ArrayList<>();
		LocalDateTime now = LocalDateTime.now();
		ZoneId zoneId = ZoneId.systemDefault();

		for (String entry : schedule.split(";")) {
			String[] parts = entry.split("\\|");
			if (parts.length != 2) {
				continue;
			}

			DayOfWeek dayOfWeek;
			try {
				dayOfWeek = DayOfWeek.valueOf(parts[0].toUpperCase());
			} catch (IllegalArgumentException e) {
				Bukkit.getLogger().warning("Invalid KoTH day: " + parts[0]);
				continue;
			}

			LocalTime time;
			try {
				time = LocalTime.parse(parts[1], DateTimeFormatter.ofPattern("HH:mm"));
			} catch (Exception e) {
				Bukkit.getLogger().warning("Invalid KoTH time format: " + parts[1]);
				continue;
			}

			LocalDate nextDate = now.toLocalDate().with(TemporalAdjusters.nextOrSame(dayOfWeek));
			LocalDateTime nextOccurrence = nextDate.atTime(time);

			if (nextOccurrence.isBefore(now)) {
				nextOccurrence = nextOccurrence.plusWeeks(1);
			}

			dates.add(Date.from(nextOccurrence.atZone(zoneId).toInstant()));
		}
		return dates.toArray(new Date[0]);
	}
}
