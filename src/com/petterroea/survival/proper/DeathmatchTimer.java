package com.petterroea.survival.proper;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathmatchTimer extends BukkitRunnable {
	SurvivalGamesMain main;
	public DeathmatchTimer(SurvivalGamesMain main) {
		this.main = main;
	}
	@Override
	public void run() {
		if(main.getPlayersLeft()>1) {
			main.teleportForDeathmatch();
		}
	}

}
