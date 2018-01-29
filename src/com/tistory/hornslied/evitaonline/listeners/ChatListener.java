package com.tistory.hornslied.evitaonline.listeners;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.tistory.hornslied.evitaonline.core.EvitaCoreMain;
import com.tistory.hornslied.evitaonline.utils.Resources;

public class ChatListener implements Listener {
	private final EvitaCoreMain plugin;
	private HashMap<Player, Integer> chatCooldown;
	
	public ChatListener() {
		plugin = EvitaCoreMain.getInstance();
		chatCooldown = new HashMap<>();
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		Player player = e.getPlayer();
		String message = e.getMessage();
		
		if(e.getMessage().startsWith("!")) {
			if(chatCooldown.containsKey(player)) {
				e.setCancelled(true);
				player.sendMessage(Resources.tagServer + ChatColor.RED + "전체채팅 재사용 대기시간: " + chatCooldown.get(player) + "초");
			} else {
				if(plugin.getEconomy().getBalance(player) >= 1000) {
					plugin.getEconomy().withdrawPlayer(player, 1000);
					
					e.setMessage(message.replaceFirst("!", ""));
					plugin.getGeneralChannel().chatProcess(e);
					player.sendMessage(Resources.tagServer + ChatColor.YELLOW + "전체채팅 비용으로 1000 페론을 지불하였습니다.");
					
					chatCooldown.put(player, 5);
					new BukkitRunnable() {
						@Override
						public void run() {
							chatCooldown.put(player, chatCooldown.get(player) -1);
							
							if(chatCooldown.get(player) == 0) {
								chatCooldown.remove(player);
								cancel();
							}
						}
					}.runTaskTimer(plugin, 20, 20);
				} else {
					e.setCancelled(true);
					player.sendMessage(Resources.tagServer + ChatColor.RED + "전체채팅 비용으로 1000 페론이 필요합니다.");
				}
			}
		}
	}
}
