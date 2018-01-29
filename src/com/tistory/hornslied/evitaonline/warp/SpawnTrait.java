package com.tistory.hornslied.evitaonline.warp;

import org.bukkit.event.EventHandler;

import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;

@TraitName("spawn")
public class SpawnTrait extends Trait {

	public SpawnTrait() {
		super("spawn");
	}
	
	@EventHandler
	public void click(NPCClickEvent event){
		if(event.getNPC() == this.getNPC()) {
			
		}
	}
}
