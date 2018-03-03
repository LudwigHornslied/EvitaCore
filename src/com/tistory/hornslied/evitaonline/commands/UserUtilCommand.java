package com.tistory.hornslied.evitaonline.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.tistory.hornslied.evitaonline.core.EvitaCoreMain;
import com.tistory.hornslied.evitaonline.db.DB;
import com.tistory.hornslied.evitaonline.events.SpawnEvent;
import com.tistory.hornslied.evitaonline.events.WhisperEvent;
import com.tistory.hornslied.evitaonline.play.PlayManager;
import com.tistory.hornslied.evitaonline.utils.ChatTools;
import com.tistory.hornslied.evitaonline.utils.Resources;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class UserUtilCommand implements CommandExecutor, Listener {
	private static ArrayList<String> help = new ArrayList<>();
	private static ArrayList<String> helpChat = new ArrayList<>();
	private static ArrayList<String> helpOther = new ArrayList<>();

	private ConcurrentHashMap<Player, Player> lastWhisper;
	private HashMap<OfflinePlayer, Integer> kitWaitingTime;

	static {
		help.add(ChatColor.STRIKETHROUGH + ChatTools.formatLine());
		help.add(ChatColor.GOLD.toString() + ChatColor.BOLD + "             에비타 온라인 도움말");
		help.add(ChatColor.STRIKETHROUGH + ChatTools.formatLine());
		help.add(ChatTools.formatCommand("", "/카페", "", ""));
		help.add(ChatTools.formatCommand("", "/디스코드", "", ""));
		help.add(ChatTools.formatCommand("", "/맵", "", ""));
		help.add(ChatTools.formatCommand("", "/위키", "", ""));
		help.add(ChatTools.formatCommand("", "/타우니", "", "타우니 관련 명령어 도움말"));
		help.add(ChatTools.formatCommand("", "/돈", "?", "돈 관련 명령어 도움말"));
		help.add(ChatTools.formatCommand("", "/도움말", "채팅", "채팅 관련 명령어 도움말"));
		help.add(ChatTools.formatCommand("", "/도움말", "기타", "기타 명령어 도움말"));
		help.add(ChatColor.STRIKETHROUGH + ChatTools.formatLine());

		helpChat.add(ChatTools.formatTitle("채팅 도움말", ChatColor.GOLD));
		helpChat.add(ChatColor.YELLOW + "기본 채팅은 모두 지역채팅으로 반경 50블록 이내에서만 들을 수 있습니다.");
		helpChat.add(ChatColor.YELLOW + "메시지 앞에 !를 붙이면 1000 페론을 지불하고 전체채팅이 가능합니다.");
		helpChat.add(ChatTools.formatCommand("", "/마을채팅", "", "마을 채팅 전환"));
		helpChat.add(ChatTools.formatCommand("", "/국가채팅", "", "국가 채팅 전환"));
		helpChat.add(ChatTools.formatCommand("", "/동맹채팅", "", "동맹 채팅 전환"));
		helpChat.add(ChatTools.formatCommand("", "/지역채팅", "", "지역 채팅 전환"));
		helpChat.add(ChatTools.formatCommand("", "/귓속말", "<플레이어> <메시지>", "플레이어에게 귓속말을 보냅니다."));
		helpChat.add(ChatTools.formatCommand("", "/대답", "<메시지>", "최근 귓속말을 주고받은 플레이어에게 답장합니다."));

		helpOther.add(ChatTools.formatTitle("기타 도움말", ChatColor.GOLD));
		helpOther.add(ChatTools.formatCommand("무소속", "/기본셋", "", "곡괭이, 고기 등 기본 아이템을 받습니다."));
		helpOther.add(ChatTools.formatCommand("", "/플레이타임", "", "플레이한 시간을 봅니다."));
		helpOther.add(ChatTools.formatCommand("", "/핑", "", "서버와의 지연 시간을 봅니다."));
	}

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
			if (args.length > 0) {
				switch (args[0].toLowerCase()) {
				case "채팅":
				case "chat":
					for (String line : helpChat)
						sender.sendMessage(line);
					break;
				case "기타":
				case "other":
					for (String line : helpOther)
						sender.sendMessage(line);
					break;
				default:
					for (String line : help)
						sender.sendMessage(line);
					break;
				}
			} else {
				for (String line : help)
					sender.sendMessage(line);
			}
			break;
		case "cafe":
			TextComponent cafeInfo = new TextComponent(
					TextComponent.fromLegacyText(Resources.tagInfo + ChatColor.YELLOW + "카페 주소: "));
			TextComponent cafeURL = new TextComponent("[클릭]");
			cafeURL.setColor(net.md_5.bungee.api.ChatColor.GOLD);
			cafeURL.setBold(true);
			cafeURL.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new ComponentBuilder(EvitaCoreMain.getInstance().getConfiguration().getString("cafe")).create()));
			cafeURL.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
					EvitaCoreMain.getInstance().getConfiguration().getString("cafe")));
			cafeInfo.addExtra(cafeURL);
			((Player) sender).spigot().sendMessage(cafeInfo);
			break;
		case "discord":
			TextComponent discordInfo = new TextComponent(
					TextComponent.fromLegacyText(Resources.tagInfo + ChatColor.YELLOW + "디스코드 초대링크: "));
			TextComponent discordURL = new TextComponent("[클릭]");
			discordURL.setColor(net.md_5.bungee.api.ChatColor.GOLD);
			discordURL.setBold(true);
			discordURL.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new ComponentBuilder("https://discord.gg/BF5fNhV").create()));
			discordURL.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/BF5fNhV"));
			discordInfo.addExtra(discordURL);
			((Player) sender).spigot().sendMessage(discordInfo);
			break;
		case "wiki":
			TextComponent wikiInfo = new TextComponent(
					TextComponent.fromLegacyText(Resources.tagInfo + ChatColor.YELLOW + "위키 주소: "));
			TextComponent wikiURL = new TextComponent("[클릭]");
			wikiURL.setColor(net.md_5.bungee.api.ChatColor.GOLD);
			wikiURL.setBold(true);
			wikiURL.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new ComponentBuilder("http://ko.evita.wikia.com/wiki").create()));
			wikiURL.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://ko.evita.wikia.com/wiki"));
			wikiInfo.addExtra(wikiURL);
			((Player) sender).spigot().sendMessage(wikiInfo);
			break;
		case "vote":
			TextComponent voteInfo = new TextComponent(
					TextComponent.fromLegacyText(Resources.tagInfo + ChatColor.YELLOW + "마인리스트 주소: "));
			TextComponent voteURL = new TextComponent("[클릭]");
			voteURL.setColor(net.md_5.bungee.api.ChatColor.GOLD);
			voteURL.setBold(true);
			voteURL.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new ComponentBuilder("https://minelist.kr/servers/5054").create()));
			voteURL.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://minelist.kr/servers/5054"));
			voteInfo.addExtra(voteURL);
			((Player) sender).spigot().sendMessage(voteInfo);
			break;
		case "map":
			TextComponent mapInfo = new TextComponent(
					TextComponent.fromLegacyText(Resources.tagInfo + ChatColor.YELLOW + "다이나믹 맵 주소: "));
			TextComponent mapURL = new TextComponent("[클릭]");
			mapURL.setColor(net.md_5.bungee.api.ChatColor.GOLD);
			mapURL.setBold(true);
			mapURL.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new ComponentBuilder("https://minelist.kr/servers/5054").create()));
			mapURL.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://minelist.kr/servers/5054"));
			mapInfo.addExtra(mapURL);
			((Player) sender).spigot().sendMessage(mapInfo);
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

			try {
				if (TownyUniverse.getDataSource().getResident(player.getName()).hasTown())
					sender.sendMessage(Resources.tagServer + ChatColor.RED + "마을에 소속되지 않은 유저만 받을 수 있습니다.");
			} catch (NotRegisteredException e) {
				break;
			}

			if (kitWaitingTime.containsKey(player)) {
				sender.sendMessage(Resources.tagServer + ChatColor.RED + "기본 세트 재사용 대기 시간: "
						+ kitWaitingTime.get(player) / 60 + "분 " + kitWaitingTime.get(player) % 60 + "초");
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
					if (kitWaitingTime.get(player) == 0) {
						kitWaitingTime.remove(player);
						cancel();
					} else {
						kitWaitingTime.put(player, kitWaitingTime.get(player) - 1);
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

		if (!spawnWorld.equals(player.getWorld())) {
			player.sendMessage(
					Resources.tagMove + ChatColor.RED + "본 월드에서만 스폰으로 갈 수 있습니다. 채집 월드는 중앙부에 스폰 귀환 표지판이 있습니다.");
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
