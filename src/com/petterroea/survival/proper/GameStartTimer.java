package com.petterroea.survival.proper;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class GameStartTimer extends BukkitRunnable {
	private GameStateHandler stateHandler;
	private SurvivalGamesMain main;
	private int seconds = 0;
	private int countedSeconds = 0;
	public GameStartTimer(GameStateHandler stateHandler, SurvivalGamesMain main, int seconds) {
		this.stateHandler = stateHandler;
		this.main = main;
		this.seconds = seconds;
	}
	@Override
	public void run() {
		if(seconds==countedSeconds) {
			stateHandler.startGame();	
			this.cancel();
		} else {
			int timeLeft = seconds - countedSeconds;
			if(timeLeft % 10 == 0) {
				main.getServer().broadcastMessage(ChatColor.RED + "The game starts in " + ChatColor.GREEN + timeLeft + ChatColor.RED + " seconds!");
			} else if(timeLeft < 5) {
				main.getServer().broadcastMessage(ChatColor.RED + "The game starts in " + ChatColor.GREEN + timeLeft + ChatColor.RED + " seconds!");
			}
			countedSeconds++;
		}
	}

}
