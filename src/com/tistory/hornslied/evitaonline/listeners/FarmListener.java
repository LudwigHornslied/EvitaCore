package com.tistory.hornslied.evitaonline.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class FarmListener implements Listener {

	@EventHandler
	public void onBuild(BlockPlaceEvent e) {
		Block block = e.getBlock();
		Material type = block.getType();
		if (type == Material.CROPS || type == Material.BEETROOT_BLOCK || type == Material.MELON_STEM
				|| type == Material.PUMPKIN_STEM || type == Material.POTATO || type == Material.CARROT
				|| type == Material.NETHER_WARTS || type == Material.SUGAR_CANE_BLOCK || type == Material.COCOA) {
			if (block.getLightFromSky() < 15) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Block block = e.getBlock();
		Material type = block.getType();

		if (block.getType() == Material.SUGAR_CANE_BLOCK && block.getLightFromSky() < 15 ) {
			Block breakBlock = e.getBlock();
			while (breakBlock.getRelative(BlockFace.DOWN).getType() == Material.SUGAR_CANE_BLOCK) {
				breakBlock = breakBlock.getRelative(BlockFace.DOWN);
			}

			breakBlock.breakNaturally();
		} else if ((type == Material.PUMPKIN || type == Material.MELON) && block.getRelative(BlockFace.UP).getLightFromSky() < 15) {
			for (BlockFace bf : BlockFace.values()) {
				if (block.getRelative(bf).getType() == Material.MELON_STEM
						|| block.getRelative(bf).getType() == Material.PUMPKIN_STEM) {
					block.getRelative(bf).breakNaturally();
				}
			}
		}
	}
}
