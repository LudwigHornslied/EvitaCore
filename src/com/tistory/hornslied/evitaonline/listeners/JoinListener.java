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

import com.tistory.hornslied.evitaonline.core.EvitaCoreMain;
import com.tistory.hornslied.evitaonline.db.DB;
import com.tistory.hornslied.evitaonline.utils.HeaderFooter;
import com.tistory.hornslied.evitaonline.utils.Resources;

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
						+ "EVITAONLINE.KR\n");
	}
}
