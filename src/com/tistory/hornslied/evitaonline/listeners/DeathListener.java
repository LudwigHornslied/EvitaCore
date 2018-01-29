package com.tistory.hornslied.evitaonline.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.tistory.hornslied.evitaonline.utils.Resources;

import org.bukkit.ChatColor;

public class DeathListener implements Listener {

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player player = e.getEntity();
		Entity killer = player.getKiller();
		player.getWorld().strikeLightningEffect(player.getLocation());
		String deathMessage = e.getDeathMessage();

		if (deathMessage.contains("was shot by arrow")) {
			e.setDeathMessage(Resources.tagDeath + ChatColor.RED + "플레이어 " + ChatColor.DARK_RED + player.getName()
					+ ChatColor.RED + " 가 화살에 맞아 죽었습니다.");
		} else if (deathMessage.contains("was shot by")) {
			if (killer instanceof Player) {
				e.setDeathMessage(Resources.tagDeath + ChatColor.RED + "플레이어 " + ChatColor.DARK_RED + player.getName()
						+ ChatColor.RED + " 가 " + ChatColor.DARK_RED + killer.getName() + ChatColor.RED
						+ " 에게 저격당했습니다.");
			} else {
				e.setDeathMessage(Resources.tagDeath + ChatColor.RED + "플레이어 " + ChatColor.DARK_RED + player.getName()
						+ ChatColor.RED + " 가 " + killer.getName() + " 에게 저격당했습니다.");
			}
		} else if (deathMessage.contains("was pricked to death") 
				|| deathMessage.contains("hugged a cactus")
				|| deathMessage.contains("walked into a cactus while trying to escape")) {
			e.setDeathMessage(Resources.tagDeath + ChatColor.RED + "플레이어 " + ChatColor.DARK_RED + player.getName()
					+ ChatColor.RED + " 가 선인장에 찔려 죽었습니다.");
		} else if (deathMessage.contains("drowned")) {
			e.setDeathMessage(Resources.tagDeath + ChatColor.RED + "플레이어 " + ChatColor.DARK_RED + player.getName()
					+ ChatColor.RED + " 가 익사했습니다.");
		} else if (deathMessage.contains("fell out of the world")) {
			e.setDeathMessage(Resources.tagDeath + ChatColor.RED + "플레이어 " + ChatColor.DARK_RED + player.getName()
					+ ChatColor.RED + " 가 시공의 폭풍으로 빨려들어갔습니다.");
		} else if (deathMessage.contains("experienced kinetic energy")
				|| deathMessage.contains("removed an elytra while flying")
				|| deathMessage.contains("hit the ground too hard")
				|| deathMessage.contains("fell from")
				|| deathMessage.contains("fell off")
				|| deathMessage.contains("fell out")
				|| deathMessage.contains("fell into")
				|| deathMessage.contains("was doomed to fall by")
				|| deathMessage.contains("was shot off some vines by")
				|| deathMessage.contains("was shot off a ladder by")
				|| deathMessage.contains("was blown from a high place by")) {
			e.setDeathMessage(Resources.tagDeath + ChatColor.RED + "플레이어 " + ChatColor.DARK_RED + player.getName()
					+ ChatColor.RED + " 가 낙사했습니다.");
		} else if (deathMessage.contains("was squashed by a falling")) {
			e.setDeathMessage(Resources.tagDeath + ChatColor.RED + "플레이어 " + ChatColor.DARK_RED + player.getName()
					+ ChatColor.RED + " 가 떨어지는 블록에 깔려 죽었습니다.");
		} else if (deathMessage.contains("went up in flames")
				|| deathMessage.contains("burned to death")
				|| deathMessage.contains("was burnt to a crisp whilst")
				|| deathMessage.contains("walked into a fire whilst")) {
			e.setDeathMessage(Resources.tagDeath + ChatColor.RED + "플레이어 " + ChatColor.DARK_RED + player.getName()
					+ ChatColor.RED + " 가 불타 죽었습니다.");
		} else if (deathMessage.contains("tried to swim in lava")) {
			e.setDeathMessage(Resources.tagDeath + ChatColor.RED + "플레이어 " + ChatColor.DARK_RED + player.getName()
					+ ChatColor.RED + " 가 용암에 빠져 죽었습니다.");
		} else if (deathMessage.contains("went off with a bang")) {
			e.setDeathMessage(Resources.tagDeath + ChatColor.RED + "플레이어 " + ChatColor.DARK_RED + player.getName()
					+ ChatColor.RED + " 가 폭죽 사고로 죽었습니다.");
		} else if (deathMessage.contains("was struck by lightning")) {
			e.setDeathMessage(Resources.tagDeath + ChatColor.RED + "플레이어 " + ChatColor.DARK_RED + player.getName()
					+ ChatColor.RED + " 가 폭죽 사고로 죽었습니다.");
		} else if (deathMessage.contains("discovered floor was lava")) {
			e.setDeathMessage(Resources.tagDeath + ChatColor.RED + "플레이어 " + ChatColor.DARK_RED + player.getName()
					+ ChatColor.RED + " 가 마그마 블록에 의해 죽었습니다.");
		} else if (deathMessage.contains("was slain by")
				|| deathMessage.contains("got finished off by")) {
			if (killer instanceof Player) {
				e.setDeathMessage(Resources.tagDeath + ChatColor.RED + "플레이어 " + ChatColor.DARK_RED + player.getName()
						+ ChatColor.RED + " 가 " + ChatColor.DARK_RED + killer.getName() + ChatColor.RED
						+ " 에게 살해당했습니다.");
			} else {
				e.setDeathMessage(Resources.tagDeath + ChatColor.RED + "플레이어 " + ChatColor.DARK_RED + player.getName()
						+ ChatColor.RED + " 가 " + killer.getName() + " 에게 살해당했습니다.");
			}
		} else if (deathMessage.contains("was fireballed by")) {
			e.setDeathMessage(Resources.tagDeath + ChatColor.RED + "플레이어 " + ChatColor.DARK_RED + player.getName()
					+ ChatColor.RED + " 가 파이어볼에 맞아 죽었습니다.");
		} else if (deathMessage.contains("was killed by magic")) {
			e.setDeathMessage(Resources.tagDeath + ChatColor.RED + "플레이어 " + ChatColor.DARK_RED + player.getName()
					+ ChatColor.RED + " 가 포션 효과에 의해 죽었습니다.");
		} else if (deathMessage.contains("using magic")) {
			if (killer instanceof Player) {
				e.setDeathMessage(Resources.tagDeath + ChatColor.RED + "플레이어 " + ChatColor.DARK_RED + player.getName()
						+ ChatColor.RED + " 가 " + ChatColor.DARK_RED + killer.getName() + ChatColor.RED
						+ " 에게 살해당했습니다.");
			} else {
				e.setDeathMessage(Resources.tagDeath + ChatColor.RED + "플레이어 " + ChatColor.DARK_RED + player.getName()
						+ ChatColor.RED + " 가 " + killer.getName() + " 에게 살해당했습니다.");
			}
		} else if (deathMessage.contains("starved to death")) {
			e.setDeathMessage(Resources.tagDeath + ChatColor.RED + "플레이어 " + ChatColor.DARK_RED + player.getName()
					+ ChatColor.RED + " 가 아사했습니다.");
		} else if (deathMessage.contains("suffocated in a wall")
				|| deathMessage.contains("was squished too much")) {
			e.setDeathMessage(Resources.tagDeath + ChatColor.RED + "플레이어 " + ChatColor.DARK_RED + player.getName()
					+ ChatColor.RED + " 가 압사했습니다.");
		} else if (deathMessage.contains("was killed while trying to hurt")) {
			e.setDeathMessage(Resources.tagDeath + ChatColor.RED + "플레이어 " + ChatColor.DARK_RED + player.getName()
					+ ChatColor.RED + " 가 가시 데미지에 의해 죽었습니다.");
		} else if (deathMessage.contains("withered away")) {
			e.setDeathMessage(Resources.tagDeath + ChatColor.RED + "플레이어 " + ChatColor.DARK_RED + player.getName()
					+ ChatColor.RED + " 가 위더 효과에 의해 죽었습니다.");
		} else if (deathMessage.contains("was pummeled by")) {
			e.setDeathMessage(Resources.tagDeath + ChatColor.RED + "플레이어 " + ChatColor.DARK_RED + player.getName()
					+ ChatColor.RED + " 가 투사체에 맞아 죽었습니다.");
		} else {
			e.setDeathMessage(Resources.tagDeath + ChatColor.RED + "플레이어 " + ChatColor.DARK_RED + player.getName()
					+ ChatColor.RED + " 가 사망했습니다.");
		}
	}
}
