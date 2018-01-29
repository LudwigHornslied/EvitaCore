package com.tistory.hornslied.evitaonline.warp;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.tistory.hornslied.evitaonline.core.EvitaCoreMain;

public class WarpManager implements Listener {
	private volatile static WarpManager instance;

	private ArrayList<Location> warps;
	private Inventory warpGUI;

	private WarpManager() {
		warps = new ArrayList<>();
		
		loadWarps();
	}
	
	private void loadWarps() {
		FileConfiguration config = EvitaCoreMain.getInstance().getWarp();

		warpGUI = Bukkit.createInventory(null, config.getInt("gui.size"), ChatColor.translateAlternateColorCodes('&', config.getString("gui.title")));
		ArrayList<String> keys = new ArrayList<>(config.getConfigurationSection("warps").getKeys(false));

		for (String key : keys) {
			warps.add(new Location(Bukkit.getWorld(config.getString("warps." + key + ".world")),
					config.getDouble("warps." + key + ".x"), config.getDouble("warps." + key + ".y"),
					config.getDouble("warps." + key + ".z"),
					Float.parseFloat(config.getString("warps." + key + ".yaw")),
					Float.parseFloat(config.getString("warps." + key + ".pitch"))));
			warpGUI.addItem(newItem(Material.valueOf(config.getString("warps." + key + ".material")), ChatColor.translateAlternateColorCodes('&', config.getString("warps." + key + ".name"))));
		}
		
		Bukkit.getPluginManager().registerEvents(this, EvitaCoreMain.getInstance());
	}
	
	private ItemStack newItem(Material material, String name) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		return item;
	}

	public static WarpManager getInstance() {
		if (instance == null) {
			synchronized (WarpManager.class) {
				if (instance == null) {
					instance = new WarpManager();
				}
			}
		}

		return instance;
	}
	
	public Inventory getWarpGUI() {
		return warpGUI;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory().equals(warpGUI)) {
			e.getWhoClicked().teleport(warps.get(e.getSlot()));
		}
	}
}
