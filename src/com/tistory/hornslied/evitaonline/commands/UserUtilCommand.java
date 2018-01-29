package com.tistory.hornslied.evitaonline.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.tistory.hornslied.evitaonline.core.EvitaCoreMain;
import com.tistory.hornslied.evitaonline.db.DB;
import com.tistory.hornslied.evitaonline.events.SpawnEvent;
import com.tistory.hornslied.evitaonline.events.WhisperEvent;
import com.tistory.hornslied.evitaonline.play.PlayManager;
import com.tistory.hornslied.evitaonline.utils.Resources;

public class UserUtilCommand implements CommandExecutor, Listener {
	private ConcurrentHashMap<Player, Player> lastWhisper;
	private HashMap<OfflinePlayer, Integer> kitWaitingTime;

	public UserUtilCommand() {
		lastWhisper = new ConcurrentHashMap<>();
		kitWaitingTime = new HashMap<>();

		Bukkit.getPluginManager().registerEvents(this, EvitaCoreMain.getInstance());
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		switch (cmd.getLabel()) {
		case "spawn":
			if (!(sender instanceof Player)) {
				sender.sendMessage(Resources.messageConsole);
				break;
			}
			
			spawn((Player) sender);
			break;
		case "help":
			break;
		case "whisper":
			if (!(sender instanceof Player)) {
				sender.sendMessage(Resources.messageConsole);
				break;
			}

			if (args.length < 2) {
				sender.sendMessage(Resources.tagServer + ChatColor.RED + "명령어 사용 방법: /귓속말 <플레이어> <메시지>");
				break;
			}

			Player receiver = Bukkit.getPlayer(args[0]);

			if (receiver == null) {
				sender.sendMessage(Resources.messagePlayerNotExist);
				break;
			}

			StringBuilder message1 = new StringBuilder();

			for (int i = 1; i < args.length; i++) {
				message1.append(args[i] + " ");
			}

			Player player1 = (Player) sender;

			whisper(player1, receiver, message1.toString());
			break;
		case "reply":
			if (!(sender instanceof Player)) {
				sender.sendMessage(Resources.messageConsole);
				break;
			}

			if (args.length < 1) {
				sender.sendMessage(Resources.tagServer + ChatColor.RED + "명령어 사용 방법: /대답 <메시지>");
				break;
			}

			StringBuilder message2 = new StringBuilder();

			for (String s : args) {
				message2.append(s + " ");
			}

			Player player2 = (Player) sender;

			if (lastWhisper.contains(player2)) {
				whisper(player2, lastWhisper.get(player2), message2.toString());
			} else {
				sender.sendMessage(Resources.tagServer + ChatColor.RED + "최근 대화를 주고받은 사람이 없습니다!");
			}
			break;
		case "playtime":
			DB db = EvitaCoreMain.getInstance().getDB();

			if (args.length > 0) {
				Player player = Bukkit.getServer().getPlayer(args[0]);
				ResultSet rs;

				try {
					if (player == null) {
						rs = db.select("SELECT * FROM playerinfo WHERE nickname = '" + args[0] + "';");
					} else {
						PlayManager.getInstance().updatePlayTime(player);
						rs = db.select("SELECT * FROM playerinfo WHERE nickname = '" + player.getName() + "';");
					}

					if (rs.next()) {
						long millisec = rs.getLong("playtime");

						String time = String.format("%02d시간 %02d분 %02d초", TimeUnit.MILLISECONDS.toHours(millisec),
								TimeUnit.MILLISECONDS.toMinutes(millisec) % TimeUnit.HOURS.toMinutes(1),
								TimeUnit.MILLISECONDS.toSeconds(millisec) % TimeUnit.MINUTES.toSeconds(1));
						sender.sendMessage(Resources.tagInfo + ChatColor.GREEN + player.getName() + ChatColor.WHITE
								+ " 님의 플레이 시간: " + ChatColor.GREEN + time);
					} else {
						sender.sendMessage(Resources.tagServer + ChatColor.RED + "해당 플레이어가 존재하지 않습니다.");
						break;
					}

					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				if (!(sender instanceof Player)) {
					sender.sendMessage(Resources.messageConsole);
					break;
				}

				try {
					PlayManager.getInstance().updatePlayTime((Player) sender);
					ResultSet rs = db.select("SELECT * FROM playerinfo WHERE uuid = '"
							+ ((Player) sender).getUniqueId().toString() + "';");

					if (rs.next()) {
						long millisec = rs.getLong("playtime");

						String time = String.format("%02d시간 %02d분 %02d초", TimeUnit.MILLISECONDS.toHours(millisec),
								TimeUnit.MILLISECONDS.toMinutes(millisec) % TimeUnit.HOURS.toMinutes(1),
								TimeUnit.MILLISECONDS.toSeconds(millisec) % TimeUnit.MINUTES.toSeconds(1));
						sender.sendMessage(Resources.tagInfo + "플레이 시간: " + ChatColor.GREEN + time);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			break;
		case "ping":
			if (args.length > 0) {
				Player player = Bukkit.getServer().getPlayer(args[0]);

				if (player == null) {
					sender.sendMessage(Resources.messagePlayerNotExist);
					break;
				}

				sender.sendMessage(Resources.tagInfo + ChatColor.GREEN + player.getName() + ChatColor.WHITE + " 님의 핑: "
						+ ChatColor.GREEN + ((CraftPlayer) player).getHandle().ping + "ms");
			} else {
				if (!(sender instanceof Player)) {
					sender.sendMessage(Resources.messageConsole);
					break;
				}
				
				sender.sendMessage(Resources.tagInfo + "핑: " + ChatColor.GREEN
						+ ((CraftPlayer) (Player) sender).getHandle().ping + "ms");
			}
			break;
		case "kit":
			if (!(sender instanceof Player)) {
				sender.sendMessage(Resources.messageConsole);
				break;
			}
			
			Player player = (Player) sender;
			
			if(kitWaitingTime.containsKey(player)) {
				sender.sendMessage(Resources.tagServer + ChatColor.RED + "기본 세트 재사용 대기 시간: " + kitWaitingTime.get(player)/60 + "분 " + kitWaitingTime.get(player)%60 + "초");
				break;
			}
			
			ItemStack pickaxe = new ItemStack(Material.WOOD_PICKAXE);
			ItemMeta meta = pickaxe.getItemMeta();
			meta.setDisplayName(ChatColor.YELLOW + ChatColor.BOLD.toString() + "기본 곡괭이");
			pickaxe.setItemMeta(meta);
			
			ItemStack porks = new ItemStack(Material.GRILLED_PORK);
			porks.setAmount(32);
			ItemMeta meta1 = porks.getItemMeta();
			meta1.setDisplayName(ChatColor.YELLOW + ChatColor.BOLD.toString() + "기본 돼지고기");
			porks.setItemMeta(meta1);
			
			ItemStack sticks = new ItemStack(Material.STICK);
			sticks.setAmount(32);
			ItemMeta meta2 = sticks.getItemMeta();
			meta2.setDisplayName(ChatColor.YELLOW + ChatColor.BOLD.toString() + "기본 막대기");
			sticks.setItemMeta(meta2);
			
			ItemStack ices = new ItemStack(Material.ICE);
			ices.setAmount(5);
			ItemMeta meta3 = ices.getItemMeta();
			meta3.setDisplayName(ChatColor.YELLOW + ChatColor.BOLD.toString() + "기본 얼음");
			ices.setItemMeta(meta3);
			
			player.getInventory().addItem(pickaxe);
			player.getInventory().addItem(porks);
			player.getInventory().addItem(sticks);
			player.getInventory().addItem(ices);
			kitWaitingTime.put(player, 1800);
			
			new BukkitRunnable() {
				@Override
				public void run() {
					if(kitWaitingTime.get(player) == 0) {
						kitWaitingTime.remove(player);
						cancel();
					} else {
						kitWaitingTime.put(player, kitWaitingTime.get(player) -1);
					}
				}
			}.runTaskTimer(EvitaCoreMain.getInstance(), 20, 20);
			break;
		}
		return true;
	}

	private void whisper(Player sender, Player receiver, String message) {
		String senderPrefix = EvitaCoreMain.getInstance().getChat().getPlayerPrefix(sender);
		String receiverPrefix = EvitaCoreMain.getInstance().getChat().getPlayerPrefix(receiver);

		WhisperEvent event = new WhisperEvent(sender, receiver, message);
		Bukkit.getPluginManager().callEvent(event);

		if (!event.isCancelled()) {
			sender.sendMessage("[" + ChatColor.GOLD + "귓속말" + ChatColor.WHITE + "]<" + ChatColor.YELLOW + "나 "
					+ ChatColor.GRAY + "-> " + ChatColor.WHITE + receiverPrefix.replaceAll("&", "§")
					+ receiver.getName() + "> " + message);
			receiver.sendMessage("[" + ChatColor.GOLD + "귓속말" + ChatColor.WHITE + "]<"
					+ senderPrefix.replaceAll("&", "§") + sender.getName() + ChatColor.GRAY + " -> " + ChatColor.YELLOW
					+ "나" + ChatColor.WHITE + "> " + message);

			lastWhisper.put(sender, receiver);
			lastWhisper.put(receiver, sender);
		}
	}
	
	private void spawn(Player player) {
		World spawnWorld = Bukkit.getWorld("Evita");
		
		if(!spawnWorld.equals(player.getWorld())) {
			player.sendMessage(Resources.tagMove + ChatColor.RED + "본 월드에서만 스폰으로 갈 수 있습니다. 채집 월드는 중앙부에 스폰 귀환 표지판이 있습니다.");
			return;
		}
		
		Bukkit.getPluginManager().callEvent(new SpawnEvent(player));
	}

	// Listeners

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();

		if (lastWhisper.containsValue(player)) {
			for (Player p : lastWhisper.keySet()) {
				if (lastWhisper.get(p).equals(player))
					lastWhisper.remove(p);
			}
		}

		if (lastWhisper.containsKey(player))
			lastWhisper.remove(player);
	}
}
