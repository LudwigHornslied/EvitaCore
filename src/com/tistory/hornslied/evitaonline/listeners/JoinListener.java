package com.tistory.hornslied.evitaonline.listeners;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;

import com.palmergames.bukkit.util.ChatTools;
import com.tistory.hornslied.evitaonline.core.EvitaCoreMain;
import com.tistory.hornslied.evitaonline.db.DB;
import com.tistory.hornslied.evitaonline.utils.HeaderFooter;
import com.tistory.hornslied.evitaonline.utils.Resources;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class JoinListener implements Listener {
	
	@EventHandler()
	public void onServerListPing(ServerListPingEvent e) {
		e.setMotd(ChatColor.GREEN + ChatColor.BOLD.toString() + "국가전쟁 에비타 온라인" + "\n" + ChatColor.YELLOW
				+ "1.8 ~ 1.12.2 버전 지원");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin_Lowest(PlayerJoinEvent e) {
		Player player = e.getPlayer();

		if (player.hasPlayedBefore()) {
			e.setJoinMessage(
					Resources.tagConnect + ChatColor.GRAY + player.getName() + ChatColor.DARK_GRAY + " 님이 접속하셨습니다.");
		} else {
			e.setJoinMessage(
					Resources.tagConnect + ChatColor.LIGHT_PURPLE + player.getName() + "님, 에비타 온라인에 오신 것을 환영합니다!");
		}

		DB db = EvitaCoreMain.getInstance().getDB();

		ResultSet rs = db.select("SELECT * FROM playerinfo WHERE uuid = '" + player.getUniqueId() + "';");

		try {
			if (rs.next()) {
				if (!rs.getString("nickname").equals(player.getName())) {
					db.query("UPDATE playerinfo SET nickname = '" + player.getName() + "' WHERE uuid = '"
							+ player.getUniqueId() + "';");
				}
			} else {
				db.query("INSERT INTO playerinfo (uuid, nickname) VALUES ('" + player.getUniqueId() + "', '"
						+ player.getName() + "');");
			}

			rs.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit_Lowest(PlayerQuitEvent e) {
		Player player = e.getPlayer();

		e.setQuitMessage(
				Resources.tagConnect + ChatColor.GRAY + player.getName() + ChatColor.DARK_GRAY + " 님이 퇴장하셨습니다.");
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin_Monitor(PlayerJoinEvent e) {
		Player player = e.getPlayer();

		HeaderFooter.setPlayerHeaderAndFooter(player,
				"\n" + ChatColor.YELLOW.toString() + ChatColor.BOLD + "- Evita Online -\n\n" + ChatColor.GRAY + "접속 중:",
				"\n" + ChatColor.GOLD + "국가전쟁 에비타 온라인 " + ChatColor.DARK_GRAY + "| " + ChatColor.GREEN
						+ EvitaCoreMain.getInstance().getConfiguration().getString("address").toUpperCase() + "\n");

		TextComponent cafe = new TextComponent(
				TextComponent.fromLegacyText("* " + ChatColor.RED + ChatColor.BOLD.toString() + "카페 주소: "));
		TextComponent cafeURL = new TextComponent("[클릭]");
		cafeURL.setColor(net.md_5.bungee.api.ChatColor.GOLD);
		cafeURL.setBold(true);
		cafeURL.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new ComponentBuilder("http://cafe.naver.com/evitaonline").create()));
		cafeURL.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://cafe.naver.com/evitaonline"));
		cafe.addExtra(cafeURL);
		TextComponent rule = new TextComponent(
				TextComponent.fromLegacyText("* " + ChatColor.BLUE + ChatColor.BOLD.toString() + "서버 규칙: "));
		TextComponent ruleURL = new TextComponent("[클릭]");
		ruleURL.setColor(net.md_5.bungee.api.ChatColor.GOLD);
		ruleURL.setBold(true);
		ruleURL.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new ComponentBuilder("http://cafe.naver.com/evitaonline/book5092832").create()));
		ruleURL.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://cafe.naver.com/evitaonline/book5092832"));
		rule.addExtra(ruleURL);
		TextComponent discord = new TextComponent(
				TextComponent.fromLegacyText("* " + ChatColor.YELLOW + ChatColor.BOLD.toString() + "디스코드 주소: "));
		TextComponent discordURL = new TextComponent("[클릭]");
		discordURL.setColor(net.md_5.bungee.api.ChatColor.GOLD);
		discordURL.setBold(true);
		discordURL.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new ComponentBuilder("https://discord.gg/fmW4wkq").create()));
		discordURL.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/fmW4wkq"));
		discord.addExtra(discordURL);

		player.sendMessage(ChatColor.STRIKETHROUGH + ChatTools.formatLine());
		player.sendMessage(Resources.blank);
		player.sendMessage("* " + ChatColor.AQUA + ChatColor.BOLD.toString() + player.getName() + ChatColor.GRAY
				+ " 님, 오늘도 좋은 하루 되세요.");
		player.sendMessage(Resources.blank);
		player.spigot().sendMessage(cafe);
		player.spigot().sendMessage(rule);
		player.spigot().sendMessage(discord);
		player.sendMessage(Resources.blank);
		player.sendMessage(ChatColor.STRIKETHROUGH + ChatTools.formatLine());
	}
}
