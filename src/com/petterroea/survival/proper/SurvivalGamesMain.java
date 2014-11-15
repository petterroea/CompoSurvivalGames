/*
 * EXPLOITS TO BE PATCHED
 * 
 * You can place items in furnaces, and it won't be cleared between rounds
 */
package com.petterroea.survival.proper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.petterroea.utils.*;

import org.json.*;

public class SurvivalGamesMain extends JavaPlugin implements Listener{
	//CONFIG
	private File userScoreLocation = new File("scores.json");
	private File spotLocation = new File("spots.json");
	private File jsonJarLocation = new File("survival/javax.json-1.0.4.jar");
	//VARIABLES
	private JSONArray userScores = null;
	private JSONArray mapSpots = null;
	//private boolean grace = true;
	private GameStateHandler stateHandler;
	private ArrayList<Location> generatedChests = new ArrayList<Location>();
	private ArrayList<ItemStack> items = new ArrayList<ItemStack>();
	private LinkedList<Player> allocatedSpots = new LinkedList<Player>();
	private ArrayList<Player> participants = new ArrayList<Player>();
	
	//No sneaky players n shit
	
	private WebServer server;
	//LOADING AND SAVING
	@Override
	public void onEnable() {
		server = new WebServer(this);
		server.start();
		this.getServer().getPluginManager().registerEvents(this, this);
		stateHandler = new GameStateHandler(this);
		if(userScoreLocation.exists()) {
			String text = FileUtils.readFile(userScoreLocation);
			userScores = new JSONArray(text);
			
			text = FileUtils.readFile(spotLocation);
			mapSpots = new JSONArray(text);
			for(int i = 0; i < mapSpots.length(); i++) {
				allocatedSpots.add(null);
			}
			for(Player p : getServer().getOnlinePlayers()) {
				if(!isPetterroea(p)) {
					allocateSpot(p);
				}
			}
		} else {
			userScores = new JSONArray();
			saveData();
		}
		//Build loot list
		//Food
		pushItemToList(new ItemStack(Material.GOLDEN_APPLE, 1), 20);
		pushItemToList(new ItemStack(Material.PORK, 1), 40);
		pushItemToList(new ItemStack(Material.GRILLED_PORK, 1), 20);
		pushItemToList(new ItemStack(Material.BREAD, 1), 80);
		pushItemToList(new ItemStack(Material.MELON, 3), 60);
		
		pushItemToList(new ItemStack(Material.WHEAT, 1), 30);
		pushItemToList(new ItemStack(Material.WHEAT, 2), 20);
		pushItemToList(new ItemStack(Material.WHEAT, 3), 10);
		//Weapons
		pushItemToList(new ItemStack(Material.WOOD_SWORD, 1), 80);
		pushItemToList(new ItemStack(Material.IRON_SWORD, 1), 20);
		pushItemToList(new ItemStack(Material.GOLD_SWORD, 1), 10);
		pushItemToList(new ItemStack(Material.DIAMOND_SWORD, 1), 5);
		
		pushItemToList(new ItemStack(Material.WOOD_AXE, 1), 20);
		pushItemToList(new ItemStack(Material.IRON_AXE, 1), 10);
		pushItemToList(new ItemStack(Material.GOLD_AXE, 1), 5);
		pushItemToList(new ItemStack(Material.DIAMOND_AXE, 1), 5);
		
		pushItemToList(new ItemStack(Material.BOW, 1), 30);
		
		pushItemToList(new ItemStack(Material.ARROW, 6), 10);
		pushItemToList(new ItemStack(Material.ARROW, 3), 20);
		pushItemToList(new ItemStack(Material.ARROW, 2), 30);
		//Armour
		pushItemToList(new ItemStack(Material.LEATHER_BOOTS, 1), 80);
		pushItemToList(new ItemStack(Material.LEATHER_CHESTPLATE, 1), 80);
		pushItemToList(new ItemStack(Material.LEATHER_HELMET, 1), 80);
		pushItemToList(new ItemStack(Material.LEATHER_LEGGINGS, 1), 80);
		
		pushItemToList(new ItemStack(Material.IRON_BOOTS, 1), 15);
		pushItemToList(new ItemStack(Material.IRON_CHESTPLATE, 1), 15);
		pushItemToList(new ItemStack(Material.IRON_HELMET, 1), 15);
		pushItemToList(new ItemStack(Material.IRON_LEGGINGS, 1), 15);
		
		pushItemToList(new ItemStack(Material.CHAINMAIL_BOOTS, 1), 15);
		pushItemToList(new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1), 15);
		pushItemToList(new ItemStack(Material.CHAINMAIL_HELMET, 1), 15);
		pushItemToList(new ItemStack(Material.CHAINMAIL_LEGGINGS, 1), 15);
		
		pushItemToList(new ItemStack(Material.GOLD_BOOTS, 1), 15);
		pushItemToList(new ItemStack(Material.GOLD_CHESTPLATE, 1), 15);
		pushItemToList(new ItemStack(Material.GOLD_HELMET, 1), 15);
		pushItemToList(new ItemStack(Material.GOLD_LEGGINGS, 1), 15);
		
		pushItemToList(new ItemStack(Material.DIAMOND_BOOTS, 1), 5);
		pushItemToList(new ItemStack(Material.DIAMOND_CHESTPLATE, 1), 5);
		pushItemToList(new ItemStack(Material.DIAMOND_HELMET, 1), 5);
		pushItemToList(new ItemStack(Material.DIAMOND_LEGGINGS, 1), 5);
		
		//Misc
		pushItemToList(new ItemStack(Material.ENDER_PEARL, 1), 10);
		
		pushItemToList(new ItemStack(Material.STICK, 1), 50);
		pushItemToList(new ItemStack(Material.FEATHER, 1), 50);
		pushItemToList(new ItemStack(Material.STRING, 1), 50);
		
		pushItemToList(new ItemStack(Material.DIAMOND, 1), 10);
		pushItemToList(new ItemStack(Material.IRON_INGOT, 1), 20);
		pushItemToList(new ItemStack(Material.GOLD_INGOT, 1), 20);
	}
	private void pushItemToList(ItemStack item, int amount) {
		for(int i = 0; i < amount; i++) {
			items.add(item);
		}
	}
	@Override
	public void onDisable() {
		saveData();
		server.stop();
	}
	public void saveData() {
		String data = userScores.toString();
		//System.out.println("Data: " + data);
		FileUtils.writeFile(userScoreLocation, data);
		
		String spotData = mapSpots.toString();
		//System.out.println("Spots: " + spotData);
		FileUtils.writeFile(spotLocation, spotData);
	}
	//LOGIC
	@EventHandler(priority=EventPriority.HIGH)
	public void playerMove(PlayerMoveEvent event) {
		if(!stateHandler.hasGameStarted() && !isPetterroea(event.getPlayer())) {
			event.getPlayer().teleport(event.getPlayer());
			//event.getPlayer().sendMessage(ChatColor.RED+"You cannot move at this stage!");
		}
	}
	@EventHandler(priority=EventPriority.HIGH)
	public void entityDamaged(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player) {
			if(stateHandler.isGrace()) {
				event.setCancelled(true);
			}
		}
	}
	@EventHandler(priority=EventPriority.HIGH)
	public void onDeath(PlayerDeathEvent event) {
		//stateHandler.playerScore(event.getEntity());
		event.getEntity().kickPlayer("You died!");
	}
	@EventHandler(priority=EventPriority.HIGH)
	public void playerDisconnect(PlayerQuitEvent event) {
		if(stateHandler.hasGameStarted()) {
			event.setQuitMessage("");
			//Only let person get score once
			for(int i = 0; i < participants.size(); i++) {
				if(participants.get(i).getUniqueId().equals(event.getPlayer().getUniqueId())) {
					stateHandler.playerScore(event.getPlayer());
					participants.remove(i);
					break;
				}
			}
			
			if(getPlayersLeft() == 5) {
				stateHandler.initDeathmatch();
			}
			if(getPlayersLeft() == 2) {
				Player p = null;
				for(Player pl : getServer().getOnlinePlayers()) {
					if(!isPetterroea(pl))
						p = pl;
				}
				p.kickPlayer("You won!");
				getServer().broadcastMessage(ChatColor.GREEN + p.getName() + " WON!");
			}
		} else {
			freeSpot(event.getPlayer());
		}
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender.isOp()) {
			if (cmd.getName().equalsIgnoreCase("start")) {
				if(args.length != 1 ) {
					sender.sendMessage(ChatColor.RED+"Missing args!");
					return true;
				}
					
				int seconds = Integer.parseInt(args[0]);
				
				if(!stateHandler.hasGameStarted()&&!stateHandler.isCountingDown()) {
					stateHandler.startGameIn(seconds);
				} else {
					sender.sendMessage(ChatColor.RED+"The game has allready started!");
				}
				
				return true;
			} else if(cmd.getName().equalsIgnoreCase("reset")) {
				stateHandler.reset();
				resetChestsAndItems();
				getServer().broadcastMessage(ChatColor.GREEN + "The game has been reset!");
				return true;
			} else if(cmd.getName().equalsIgnoreCase("point")) {
				if(sender instanceof Player) {
					Location loc = ( (Player)sender ).getLocation();
					JSONObject obj = new JSONObject();
					obj.put("x", loc.getBlockX());
					obj.put("y", loc.getBlockY());
					obj.put("z", loc.getBlockZ());
					mapSpots.put(obj);
					allocatedSpots.add(null);
					
					saveData();
					sender.sendMessage(ChatColor.GREEN + "Spot has been set!");
					return true;
				} else {
					sender.sendMessage(ChatColor.RED + "You are not a player!");
				}
			} else if(cmd.getName().equalsIgnoreCase("deathmatch")) {
				if(stateHandler.hasGameStarted()) {
					stateHandler.initDeathmatch();
				} else {
					sender.sendMessage(ChatColor.RED+"A game is not in action");
				}
				
			} else if(cmd.getName().equalsIgnoreCase("clean")) {
				for(Player p : getServer().getOnlinePlayers()) {
					clearPlayerInv(p);
					return true;
				}
				getServer().broadcastMessage(ChatColor.RED+"Inventories have been cleared");
			}
		} else {
			sender.sendMessage(ChatColor.RED + "You don't have the permissions for this!");
			return true;
		}
		
		return false; 
	}
	@EventHandler(priority=EventPriority.HIGH)
	public void playerJoin(PlayerJoinEvent event) {
		if(isPetterroea(event.getPlayer())) {
			System.out.println("ITS PETTERREOA!!1");
			for(Player player : getServer().getOnlinePlayers()) {
				player.hidePlayer(event.getPlayer());
			}
		} else {
			if(stateHandler.hasGameStarted()) {
				event.setJoinMessage("");
				event.getPlayer().kickPlayer("The game has allready started!");
			} else {
				clearPlayerInv(event.getPlayer());
				if(!isTrackingPlayer(event.getPlayer())) {
					registerPlayer(event.getPlayer());
				}
				if(getServer().getPlayer("petterroea") != null) {
					event.getPlayer().hidePlayer(getServer().getPlayer("petterroea"));
				}
				freeSpot(event.getPlayer());
				Location loc = allocateSpot(event.getPlayer());
				System.out.println("Spawning player at " + loc.toString());
				
				event.getPlayer().teleport(loc);
			}
		}
	}
	@EventHandler(priority=EventPriority.HIGH)
	public void blockPlaced(BlockPlaceEvent event) {
		if(!isPetterroea(event.getPlayer())) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED+"You are not allowed to place blocks");
		}
	}
	@EventHandler(priority=EventPriority.HIGH)
	public void blockBroken(BlockBreakEvent event) {
		if(!isPetterroea(event.getPlayer())) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED+"You are not allowed to break blocks");
		}
	}
	@EventHandler(priority=EventPriority.HIGH)
	public void inventoryOpen(InventoryOpenEvent event) {
		if(event.getInventory().getHolder() instanceof Chest) {
			Chest chest = (Chest)event.getInventory().getHolder();
			if(!generatedChests.contains(chest.getBlock().getLocation())) {
				event.getInventory().clear();
				generateLoot(event.getInventory());
				generatedChests.add(chest.getBlock().getLocation());
			}
		} else if(event.getInventory().getHolder() instanceof DoubleChest) {
			DoubleChest chest = (DoubleChest)event.getInventory().getHolder();
			if(!generatedChests.contains(chest.getLocation())) {
				event.getInventory().clear();
				generateLoot(event.getInventory());
				generatedChests.add(chest.getLocation());
			}
		} else if(event.getInventory().getHolder() instanceof CraftPlayer) {
			
		} else {
			System.out.println("Inventory open(Unhandled): " + event.getInventory().getHolder().getClass().getName());
			event.setCancelled(true);
		}
	}
	//API
	public void clearPlayerInv(Player p) {
		p.getInventory().setHelmet(new ItemStack(Material.AIR, 1));
		p.getInventory().setLeggings(new ItemStack(Material.AIR, 1));
		p.getInventory().setBoots(new ItemStack(Material.AIR, 1));
		p.getInventory().setChestplate(new ItemStack(Material.AIR, 1));
		p.getInventory().clear();
	}
	public void resetChestsAndItems() {
		generatedChests.clear();
		for(Entity ent : getServer().getWorld("world").getEntities()) {
			if(ent instanceof Item) {
				ent.remove();
			}
		}
		allocatedSpots.clear();
		for(int i = 0; i < mapSpots.length(); i++) {
			allocatedSpots.add(null);
		}
		for(Player p : getServer().getOnlinePlayers()) {
			if(!isPetterroea(p)) {
				p.teleport(allocateSpot(p));
			}
			clearPlayerInv(p);
		}
		participants.clear();
	}
	public int getPlayersLeft() {
		int count = 0;
		for(Player p : getServer().getOnlinePlayers()) {
			if(!isPetterroea(p)) {
				count++;
			}
		}
		return count;
	}
	public void registerPlayer(Player p) {
		JSONObject obj = new JSONObject();
		obj.put("uuid", p.getUniqueId().toString());
		obj.put("name", p.getName());
		obj.put("score", 0);
		
		userScores.put(obj);
		
		saveData();
	}
	private void freeSpot(Player player) {
		for(int i = 0; i < allocatedSpots.size(); i++) {
			if(allocatedSpots.get(i) != null && allocatedSpots.get(i).getUniqueId().equals(player.getUniqueId())) {
				allocatedSpots.set(i, null);
				return;
			}
		}
	}
	private Location allocateSpot(Player player) {
		//System.out.println("Allocating array that is " + allocatedSpots.size() + " long");
		for(int i = 0; i < allocatedSpots.size(); i++) {
			//System.out.println(allocatedSpots.get(9) == null);
			if(allocatedSpots.get(i) == null) {
				allocatedSpots.set(i, player);
				return new Location(getServer().getWorld("world"), (double)mapSpots.getJSONObject(i).getInt("x"), (double)mapSpots.getJSONObject(i).getInt("y"), (double)mapSpots.getJSONObject(i).getInt("z"));
			}
		}
		System.out.println("ERROR fetching spot!");
		return null;
	}
	public void addScore(Player p, int score) {
		for(int i = 0; i < userScores.length(); i++) {
			JSONObject user = userScores.getJSONObject(i);
			if(user.getString("uuid").equals(p.getUniqueId().toString())) {
				int newScore =  user.getInt("score") + score;
				user.put("score", ""+newScore);
			}
		}
		saveData();
	}
	public boolean isPetterroea(Player p) {
		//return false;
		return p.getUniqueId().toString().equals("c59b89a2-f8f4-4c1e-a154-c72874a67e30");
	}
	public boolean isTrackingPlayer(Player p) {
		for(int i = 0; i < userScores.length(); i++) {
			JSONObject user = userScores.getJSONObject(i);
			if(user.getString("uuid").equals(p.getUniqueId().toString())) 
				return true;
		}
		return false;
	}
	public GameStateHandler getStateHandler() {
		return stateHandler;
	}
	private void generateLoot(Inventory inventory) {
		System.out.println("Generating loot!");
		Random rand = new Random();
		for(int i = 0; i < inventory.getSize()/5; i++) {
			inventory.setItem(rand.nextInt(inventory.getSize()), items.get(rand.nextInt(items.size())));
		}
	}
	public void logParticipants() {
		participants.clear();
		for(Player p : getServer().getOnlinePlayers()) {
			if(!isPetterroea(p)) {
				participants.add(p);
			}
		}
	}
	//Default handler
	public static void main(String[] args) {
		System.out.println("Should not be started like this!");
	}
	public JSONArray getScoreArray() {
		return userScores;
	}
	public void teleportForDeathmatch() {
		int spotIndex = 0;
		for(Player p : getServer().getOnlinePlayers()) {
			if(!isPetterroea(p)) {
				p.teleport(new Location(getServer().getWorld("world"), (double)mapSpots.getJSONObject(spotIndex).getInt("x"), (double)mapSpots.getJSONObject(spotIndex).getInt("y"), (double)mapSpots.getJSONObject(spotIndex).getInt("z")));
				spotIndex++;
			}
		}
		getServer().broadcastMessage(ChatColor.RED+"You have been teleported to spawn to 1v1 club penguin. Good luck");
	}
}
