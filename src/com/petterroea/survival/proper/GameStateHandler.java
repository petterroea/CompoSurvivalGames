package com.petterroea.survival.proper;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GameStateHandler {
	private SurvivalGamesMain main;
	private boolean grace = true;
	private boolean gameStarted = false;
	private boolean countdownStarted = false;
	private int pointPot = 0;
	
	public GameStateHandler(SurvivalGamesMain main) {
		this.main = main;
	}
	public void startGameIn(int seconds) {
		new GameStartTimer(this, main, seconds).runTaskTimer(main, 20, 20);
		countdownStarted = true;
		healAndClearInv();
		main.getServer().getWorld("world").setTime(0L);
	}
	public void startGame() {
		main.getServer().broadcastMessage(ChatColor.RED+"The games have started!");
		main.getServer().broadcastMessage(ChatColor.RED+"You have a 30 second grace period");
		main.logParticipants();
		gameStarted = true;
		countdownStarted = false;
		healAndClearInv();
		new GracePeriodTimer(main).runTaskLater(main, 20*30);
	}
	public void graceOver() {
		grace = false;
	}
	public void reset() {
		grace = true;
		gameStarted = false;
		pointPot = 0;
		countdownStarted = false;
		main.resetChestsAndItems();
		main.getServer().getWorld("world").setTime(0L);
	}
	public boolean hasGameStarted() {
		return gameStarted;
	}
	public boolean isCountingDown() {
		return countdownStarted;
	}
	public void healAndClearInv() {
		Player[] players = main.getServer().getOnlinePlayers();
		for(int i = 0; i < players.length; i++) {
			players[i].getInventory().clear();
			players[i].setHealth(20.0);
			players[i].setFoodLevel(20);
		}
	}
	public void playerScore(Player entity) {
		main.addScore(entity, pointPot);
		pointPot++;
		System.out.println("PointPot increased to " + pointPot);
		main.getServer().broadcastMessage(ChatColor.GREEN + entity.getName() + ChatColor.RED + " has fallen! There are " + ChatColor.GREEN + (main.getPlayersLeft()) + ChatColor.RED + " players left!");
	}
	public boolean isGrace() {
		return grace;
	}
	public void initDeathmatch() {
		main.getServer().broadcastMessage(ChatColor.RED+"Deathmatch will start in 5 seconds!");
		new DeathmatchTimer(main).runTaskLater(main, 20*5);
	}
}
