package com.tistory.hornslied.evitaonline.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.tistory.hornslied.evitaonline.core.EvitaCoreMain;
import com.tistory.hornslied.evitaonline.utils.Resources;

public class EvitaCoreCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length < 1) {
			
		} else {
			switch(args[0].toLowerCase()) {
			case "reload":
				EvitaCoreMain.getInstance().reload();
				sender.sendMessage(Resources.tagServer + ChatColor.YELLOW + "EvitaCore 리로드 완료");
				break;
			default:
				break;
			}
		}
		return true;
	}

}
