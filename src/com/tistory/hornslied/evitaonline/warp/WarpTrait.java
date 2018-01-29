package com.tistory.hornslied.evitaonline.warp;

import org.bukkit.event.EventHandler;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;

@TraitName("warp")
public class WarpTrait extends Trait {

	public WarpTrait() {
		super("warp");
	}
	
	@EventHandler
	public void click(NPCRightClickEvent event){
		if(event.getNPC() == this.getNPC()) {
			event.getClicker().openInventory(WarpManager.getInstance().getWarpGUI());
		}
	}
}
