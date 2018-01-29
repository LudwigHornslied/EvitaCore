package com.tistory.hornslied.evitaonline.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.tistory.hornslied.evitaonline.core.EvitaCoreMain;

public class TablistNameUpdater implements Listener {
	
	public TablistNameUpdater() {
		new BukkitRunnable() {

			@Override
			public void run() {
				for(Player p : Bukkit.getOnlinePlayers()) {
					updateName(p);
				}
			}
		}.runTaskTimer(EvitaCoreMain.getInstance(), 0, 60);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		updateName(e.getPlayer());
	}
	
	private void updateName(Player player) {
		Resident resident;
		
		try {
			resident = TownyUniverse.getDataSource().getResident(player.getName());
		} catch (NotRegisteredException e) {
			return;
		}
		
		try {
			player.setPlayerListName(ChatColor.GRAY + "[" + resident.getTown().getNation().getName() + "] " + ChatColor.WHITE + player.getName());
		} catch (NotRegisteredException e) {
			player.setPlayerListName(ChatColor.GRAY + "[무소속] " + ChatColor.WHITE + player.getName());
		}
	}
}
