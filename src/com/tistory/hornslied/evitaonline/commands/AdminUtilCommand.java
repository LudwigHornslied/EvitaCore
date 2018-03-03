package com.tistory.hornslied.evitaonline.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.tistory.hornslied.evitaonline.utils.Resources;

public class AdminUtilCommand implements CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender.hasPermission("evita.mod"))) {
			sender.sendMessage(Resources.messagePermission);
			return true;
		}
		
		switch(cmd.getLabel()) {
		case "tp":
			if(!(sender instanceof Player)) {
				sender.sendMessage(Resources.messageConsole);
				break;
			}
			
			if(args.length < 1) {
				sender.sendMessage(Resources.tagServer + ChatColor.RED + "명령어 사용 방법: /티피 <닉네임>");
				break;
			}
			
			Player target1 = Bukkit.getPlayer(args[0]);
			
			if(target1 == null) {
				sender.sendMessage(Resources.messagePlayerNotExist);
				break;
			}
			
			Player player1 = (Player) sender;
			player1.teleport(target1.getLocation());
			sender.sendMessage(Resources.tagMove + ChatColor.AQUA + target1.getName() + " 플레이어에게 이동하였습니다.");
			
			break;
		case "call":
			if(!(sender instanceof Player)) {
				sender.sendMessage(Resources.messageConsole);
				break;
			}
			
			if(args.length < 1) {
				sender.sendMessage(Resources.tagServer + ChatColor.RED + "명령어 사용 방법: /호출 <닉네임>");
				break;
			}
			
			Player target2 = Bukkit.getPlayer(args[0]);
			
			if(target2 == null) {
				sender.sendMessage(Resources.messagePlayerNotExist);
				break;
			}
			
			Player player2 = (Player) sender;
			target2.teleport(player2.getLocation());
			sender.sendMessage(Resources.tagMove + ChatColor.AQUA + target2.getName() + " 플레이어를 당신에게 이동시켰습니다.");
			target2.sendMessage(Resources.tagMove + ChatColor.AQUA + player2.getName() + "플레이어에게 이동합니다.");
			
			break;
		case "skull":
			if(!(sender instanceof Player)) {
				sender.sendMessage(Resources.messageConsole);
				break;
			}
			
			if(args.length < 1) {
				sender.sendMessage(Resources.tagServer + ChatColor.RED + "명령어 사용 방법: /머리 <닉네임>");
				break;
			}
			ItemStack skull = new ItemStack(Material.SKULL_ITEM);
			SkullMeta sm = (SkullMeta) skull.getItemMeta();
			skull.setDurability((short)3); 
			sm.setOwner(args[0]);
			skull.setItemMeta(sm);
			((Player) sender).getInventory().addItem(skull);
			sender.sendMessage(Resources.tagServer + ChatColor.AQUA + args[0] + " 의 머리가 지급되었습니다.");
			break;
		case "world":
			if(!(sender instanceof Player)) {
				sender.sendMessage(Resources.messageConsole);
				break;
			}
			
			if(args.length < 1) {
				sender.sendMessage(Resources.tagServer + ChatColor.RED + "명령어 사용 방법: /월드 <월드이름>");
				break;
			}
			
			World world = Bukkit.getWorld(args[0]);
			
			if(world == null) {
				sender.sendMessage(Resources.tagServer + ChatColor.RED + "존재하지 않는 월드입니다!");
				break;
			}
			
			((Player) sender).teleport(world.getSpawnLocation());
			sender.sendMessage(Resources.tagServer + ChatColor.AQUA + world.getName() + " 월드로 이동하였습니다.");
			break;
		case "say":
			if (args.length < 1) {
				sender.sendMessage(Resources.tagServer + ChatColor.RED + "명령어 사용 방법: /알림 <메시지>");
				break;
			}
			
			StringBuilder messages = new StringBuilder();
			
			for (int i = 0; i < args.length; i++) {
				messages.append(args[i] + " ");
			}
			Bukkit.broadcastMessage(Resources.tagAlert + messages.toString());
			
			break;
		case "walkspeed":
			if (!(sender instanceof Player)) {
				sender.sendMessage(Resources.messageConsole);
				break;
			}
			
			if (args.length < 1) {
				sender.sendMessage(Resources.tagServer + ChatColor.RED + "명령어 사용 방법: /걷기속도 <실수>");
				break;
			}
			
			float speed1;
			
			try {
				speed1 = Float.parseFloat(args[0]);
			} catch(NumberFormatException e) {
				sender.sendMessage(Resources.tagServer + ChatColor.RED + "명령어 사용 방법: /날기속도 <실수>");
				break;
			}
			
			((Player) sender).setWalkSpeed(speed1);
			
			break;
		case "flyspeed":
			if (!(sender instanceof Player)) {
				sender.sendMessage(Resources.messageConsole);
				break;
			}
			
			if (args.length < 1) {
				sender.sendMessage(Resources.tagServer + ChatColor.RED + "명령어 사용 방법: /날기속도 <실수>");
				break;
			}
			
			float speed2;
			
			try {
				speed2 = Float.parseFloat(args[0]);
			} catch(NumberFormatException e) {
				sender.sendMessage(Resources.tagServer + ChatColor.RED + "명령어 사용 방법: /날기속도 <실수>");
				break;
			}
			
			((Player) sender).setFlySpeed(speed2);
			
			break;
		case "currentspeed":
			if (!(sender instanceof Player)) {
				sender.sendMessage(Resources.messageConsole);
				break;
			}
			
			if (args.length < 1) {
				sender.sendMessage(Resources.tagServer + ChatColor.RED + "명령어 사용 방법: /현재속도 [걷기/날기]");
				break;
			}
			
			switch(args[0].toLowerCase()) {
			case "걷기":
			case "walk":
				sender.sendMessage(Resources.tagServer + ChatColor.WHITE + "걷기 속도: " + ChatColor.GREEN + ((Player) sender).getWalkSpeed());
				break;
			case "날기":
			case "fly":
				sender.sendMessage(Resources.tagServer + ChatColor.WHITE + "날기 속도: " + ChatColor.GREEN + ((Player) sender).getWalkSpeed());
				break;
			default:
				sender.sendMessage(Resources.tagServer + ChatColor.RED + "명령어 사용 방법: /현재속도 [걷기/날기]");
				break;
			}
			
			break;
		case "tppos":
			if (!(sender instanceof Player)) {
				sender.sendMessage(Resources.messageConsole);
				break;
			}
			
			if(args.length < 3) {
				sender.sendMessage(Resources.tagServer + ChatColor.RED + "명령어 사용 방법: /좌표이동 <x> <y> <z>");
				break;
			}
			
			double x;
			double y;
			double z;
			
			try {
				x = Double.parseDouble(args[0]);
				y = Double.parseDouble(args[1]);
				z = Double.parseDouble(args[2]);
			} catch(NumberFormatException e) {
				sender.sendMessage(Resources.tagServer + ChatColor.RED + "명령어 사용 방법: /좌표이동 <x> <y> <z>");
				break;
			}
			
			Player player = (Player) sender;
			player.teleport(new Location(player.getWorld(), x, y, z));
			player.sendMessage(Resources.tagMove + ChatColor.AQUA + "해당 좌표로 이동했습니다.");
			break;
		}
		return true;
	}

}
