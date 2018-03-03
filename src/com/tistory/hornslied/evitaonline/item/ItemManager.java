package com.tistory.hornslied.evitaonline.item;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import com.tistory.hornslied.evitaonline.core.EvitaCoreMain;

public class ItemManager {
	private volatile static ItemManager instance;
	
	private HashMap<String, ItemStack> items;
	
	private ItemManager() {
		items = new HashMap<>();
		
		loadItems();
	}
	
	private void loadItems() {
		FileConfiguration storage = EvitaCoreMain.getInstance().getItems();
		Set<String> keys = storage.getKeys(false);
		
		if(keys != null) {
			for(String key : keys) {
				items.put(key, storage.getItemStack(key));
			}
		}
	}
	
	public static ItemManager getInstance() {
		if (instance == null) {
			synchronized (ItemManager.class) {
				if (instance == null) {
					instance = new ItemManager();
				}
			}
		}

		return instance;
	}
	
	public ItemStack getItem(String key) {
		return items.get(key);
	}
	
	public boolean hasItem(String key) {
		return items.containsKey(key);
	}
	
	public void setItem(String key, ItemStack item) {
		items.put(key, item);
		
		FileConfiguration storage = EvitaCoreMain.getInstance().getItems();
		storage.set(key, item);
		
		try {
			storage.save(new File(EvitaCoreMain.getInstance().getDataFolder(), "items.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void removeItem(String key) {
		items.remove(key);
		
		FileConfiguration storage = EvitaCoreMain.getInstance().getItems();
		storage.set(key, null);
		
		try {
			storage.save(new File(EvitaCoreMain.getInstance().getDataFolder(), "items.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
