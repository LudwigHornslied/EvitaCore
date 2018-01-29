package com.tistory.hornslied.evitaonline.listeners;

import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.tistory.hornslied.evitaonline.core.EvitaCoreMain;
import com.tistory.hornslied.evitaonline.utils.Resources;

public class RespawnListener implements Listener {
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		if (EvitaCoreMain.getInstance().getConfiguration().getBoolean("acrespawn")) {
			try {
				Resident resident = TownyUniverse.getDataSource().getResident(e.getPlayer().getName());
				if (!resident.hasTown()) {
					List<Town> towns = TownyUniverse.getDataSource().getACs();

					if (!towns.isEmpty()) {
						Random rd = new Random();
						Town town = towns.get(rd.nextInt(towns.size()));
						try {
							Location location = town.getSpawn();
							e.setRespawnLocation(location);
							e.getPlayer().sendMessage(Resources.tagNation + ChatColor.AQUA + "소속 마을이 없어 고대 도시 "
									+ town.getName() + " 에 리스폰 되었습니다.");
						} catch (TownyException e1) {
							return;
						}
					}
				}
			} catch (NotRegisteredException e1) {
				return;
			}
		}
	}
}
