package com.tistory.hornslied.evitaonline.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.tistory.hornslied.evitaonline.utils.Resources;

public class ItemCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Resources.messageConsole);
			return true;
		}

		Player player = (Player) sender;
		ItemStack item = player.getInventory().getItemInMainHand();

		if (args.length < 1) {

		} else {
			switch (args[0].toLowerCase()) {
			case "목록":
			case "list":
				break;
			case "불러오기":
			case "load":
				break;
			case "저장":
			case "save":
				break;
			case "삭제":
			case "delete":
				break;
			case "이름":
			case "name":
				if (args.length < 2) {
					sender.sendMessage(Resources.tagServer + ChatColor.RED + "명령어 사용 방법: /아이템 이름 <이름>");
					break;
				}

				if (item == null) {
					sender.sendMessage(Resources.tagServer + ChatColor.RED + "아이템을 손에 들고 있어야 합니다!");
					break;
				}

				StringBuilder sb1 = new StringBuilder();

				for (int i = 1; i < args.length; i++) {
					sb1.append(args[i]);
					if ((i + 1) != args.length) {
						sb1.append(" ");
					}
				}

				ItemMeta meta1 = item.getItemMeta();
				meta1.setDisplayName(ChatColor.translateAlternateColorCodes('&', sb1.toString()));
				item.setItemMeta(meta1);

				break;
			case "로어":
			case "설명":
			case "lore":
				if (args.length < 2) {

				} else {
					switch (args[1].toLowerCase()) {
					case "추가":
					case "add":
						if (args.length < 3) {
							sender.sendMessage(Resources.tagServer + ChatColor.RED + "명령어 사용 방법: /아이템 설명 추가 <설명>");
							break;
						}

						if (item == null) {
							sender.sendMessage(Resources.tagServer + ChatColor.RED + "아이템을 손에 들고 있어야 합니다!");
							break;
						}

						StringBuilder sb2 = new StringBuilder();

						for (int i = 2; i < args.length; i++) {
							sb2.append(args[i]);
							if ((i + 1) != args.length) {
								sb2.append(" ");
							}
						}

						ItemMeta meta2 = item.getItemMeta();
						if(meta2.hasLore()) {
							meta2.getLore().add(ChatColor.translateAlternateColorCodes('&', sb2.toString()));
						} else {
							meta2.setLore(new ArrayList<String>());
							meta2.getLore().add(ChatColor.translateAlternateColorCodes('&', sb2.toString()));
						}
						
						item.setItemMeta(meta2);
						break;
					case "제거":
					case "삭제":
					case "delete":
					case "remove":
						if (item == null) {
							sender.sendMessage(Resources.tagServer + ChatColor.RED + "아이템을 손에 들고 있어야 합니다!");
							break;
						}
						
						ItemMeta meta3 = item.getItemMeta();
						
						if(!meta3.hasLore() || meta3.getLore().isEmpty()) {
							sender.sendMessage(Resources.tagServer + ChatColor.RED + "해당 아이템은 설명을 가지고 있지 않습니다!");
							break;
						}
						
						if (args.length < 3) {
							meta3.setLore(null);
						} else {
							int line;
							try {
								line = Integer.parseInt(args[2]);
							} catch(NumberFormatException e) {
								sender.sendMessage(Resources.tagServer + ChatColor.RED + "명령어 사용 방법: /아이템 설명 삭제 <줄>");
								break;
							}
							
							if(line < 1) {
								sender.sendMessage(Resources.tagServer + ChatColor.RED + "줄 번호는 양수여야 합니다.");
								break;
							}
							
							List<String> lore = meta3.getLore();
							
							if(lore.size() < line) {
								sender.sendMessage(Resources.tagServer + ChatColor.RED + "해당하는 줄 번호가 없습니다.");
								break;
							}
							
							lore.remove(line -1);
						}
						break;
					default:
						break;
					}
				}

				break;
			case "인챈트":
			case "enchant":
				if(args.length < 2) {
					
				} else {
					switch(args[1].toLowerCase()) {
					case "추가":
					case "add":
						if(args.length < 4) {
							sender.sendMessage(Resources.tagServer + ChatColor.RED + "명령어 사용 방법: /아이템 인챈트 추가 <인챈트> <레벨>");
							break;
						}
						
						if (item == null) {
							sender.sendMessage(Resources.tagServer + ChatColor.RED + "아이템을 손에 들고 있어야 합니다!");
							break;
						}
						
						Enchantment enchant1 = Enchantment.getByName(args[2]);
						
						if(enchant1 == null) {
							sender.sendMessage(Resources.tagServer + ChatColor.RED + "존재하지 않는 인챈트입니다.");
							break;
						}
						
						int level;
						try {
							level = Integer.parseInt(args[3]);
						} catch(NumberFormatException e) {
							sender.sendMessage(Resources.tagServer + ChatColor.RED + "명령어 사용 방법: /아이템 인챈트 추가 <인챈트> <레벨>");
							break;
						}
						
						ItemMeta meta4 = item.getItemMeta();
						meta4.addEnchant(enchant1, level, true);
						item.setItemMeta(meta4);
						break;
					case "제거":
					case "delete":
					case "remove":
						if(args.length < 3) {
							sender.sendMessage(Resources.tagServer + ChatColor.RED + "명령어 사용 방법: /아이템 인챈트 제거 <인챈트>");
							break;
						}
						
						if (item == null) {
							sender.sendMessage(Resources.tagServer + ChatColor.RED + "아이템을 손에 들고 있어야 합니다!");
							break;
						}
						
						Enchantment enchant2 = Enchantment.getByName(args[2]);
						
						if(enchant2 == null) {
							sender.sendMessage(Resources.tagServer + ChatColor.RED + "존재하지 않는 인챈트입니다.");
							break;
						}
						
						ItemMeta meta5 = item.getItemMeta();
						meta5.removeEnchant(enchant2);
						item.setItemMeta(meta5);
						
						break;
					default:
						break;
					}
				}
				break;
			}
		}
		return true;
	}

}
