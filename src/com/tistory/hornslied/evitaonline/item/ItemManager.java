package com.tistory.hornslied.evitaonline.item;

public class ItemManager {
	private volatile static ItemManager instance;
	
	private ItemManager() {
		
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
}
