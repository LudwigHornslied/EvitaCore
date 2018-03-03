package com.tistory.hornslied.evitaonline.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.tistory.hornslied.evitaonline.core.EvitaCoreMain;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;

public class VoteListener implements Listener {
	private int voteTime = 30;
	
	@EventHandler
	public void voteMade(VotifierEvent e) {
		Vote vote = e.getVote();
		
		System.out.println("[추천] Votifier로 추천 신호 받음.");
		
		String name = vote.getUsername();
		
		if (Bukkit.getPlayer(name) == null) {
			System.out.println("[추천] 알수 없는 사람이 추천했습니다.");
		} else {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "archoncrates key " + name + " 1 vote physical");
			voteTime--;
			Bukkit.broadcastMessage("[" + ChatColor.GREEN + "추천" + ChatColor.WHITE + "] " + ChatColor.GOLD + name
					+ ChatColor.YELLOW + " 님이 마인리스트 추천으로 " + ChatColor.GOLD + ChatColor.BOLD + "추천 열쇠"
					+ ChatColor.YELLOW + "를 받으셨습니다.");

			if (voteTime == 0) {
				voteTime = 30;
				Bukkit.broadcastMessage("[" + ChatColor.GREEN + "추천" + ChatColor.WHITE + "] " + ChatColor.GOLD
						+ "추천 파티로 인해 30초 후에 접속자 전원에게 추천 키 2개가 지급됩니다!");

				new BukkitRunnable() {
					@Override
					public void run() {
						for (Player p : Bukkit.getOnlinePlayers()) {
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
									"archoncrates key " + p.getName() + " 2 vote physical");
						}
					}
				}.runTaskLater(EvitaCoreMain.getInstance(), 600);
			} else {
				Bukkit.broadcastMessage("[" + ChatColor.GREEN + "추천" + ChatColor.WHITE + "] 추천 파티까지 남은 추천 횟수: "
						+ ChatColor.GREEN + voteTime + "회");
			}
		}
	}
}
