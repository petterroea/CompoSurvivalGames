package com.petterroea.survival.proper;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class GracePeriodTimer extends BukkitRunnable {
	SurvivalGamesMain main;
	public GracePeriodTimer(SurvivalGamesMain main) {
		this.main = main;
	}
	@Override
	public void run() {
		main.getServer().broadcastMessage(ChatColor.RED+"The grace period has ended. Happy fighting!");
		main.getStateHandler().graceOver();
	}

}
