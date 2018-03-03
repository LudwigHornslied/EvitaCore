package com.tistory.hornslied.evitaonline.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.mewin.WGRegionEvents.events.RegionEnterEvent;

public class RegionListener implements Listener {

	@EventHandler
	public void onEnter(RegionEnterEvent e) {
		switch (e.getRegion().getId()) {
		case "mine":
			e.getPlayer().sendTitle(ChatColor.RED + ChatColor.BOLD.toString() + "광산", "", 10, 70, 10);
			break;
		}
	}
}
