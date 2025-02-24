package io.noks.koth.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import io.noks.koth.Main;

public class KoTHCommand implements CommandExecutor {
	private Main main;
	
	public KoTHCommand(Main main) {
		this.main = main;
		main.getCommand("koth").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// TODO Auto-generated method stub
		return false;
	}
}
