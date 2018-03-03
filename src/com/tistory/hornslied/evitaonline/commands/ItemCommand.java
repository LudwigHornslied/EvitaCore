package com.tistory.hornslied.evitaonline.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.tistory.hornslied.evitaonline.item.ItemManager;
import com.tistory.hornslied.evitaonline.utils.Resources;

public class ItemCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender.hasPermission("evita.mod"))) {
			sender.sendMessage(Resources.messagePermission);
			return true;
		}

		ItemManager itemManager = ItemManager.getInstance();

		Player player;
		ItemStack item;

		if (sender instanceof Player) {
			player = (Player) sender;
			item = player.getInventory().getItemInMainHand();
		} else {
			player = null;
			item = null;
		}

		if (args.length < 1) {

		} else {
			switch (args[0].toLowerCase()) {
			case "목록":
			case "list":
				break;
			case "불러오기":
			case "load":
				if (!(sender instanceof Player)) {
					sender.sendMessage(Resources.messageConsole);
					break;
				}

				if (args.length < 2) {
					sender.sendMessage(Resources.tagServer + ChatColor.RED + "명령어 사용 방법: /아이템 불러오기 <이름>");
					break;
				}

				if (!itemManager.hasItem(args[1])) {
					sender.sendMessage(Resources.tagServer + ChatColor.RED + "해당하는 아이템이 없습니다!");
					break;
				}

				player.getInventory().addItem(itemManager.getItem(args[1]));
				break;
			case "보내기":
			case "give":
				if (args.length < 4) {
					sender.sendMessage(Resources.tagServer + ChatColor.RED + "명령어 사용 방법: /아이템 보내기 <플레이어> <이름> <수량>");
					break;
				}

				Player target = Bukkit.getPlayer(args[1]);

				if (target == null) {
					sender.sendMessage(Resources.messagePlayerNotExist);
					break;
				}

				if (!itemManager.hasItem(args[2])) {
					sender.sendMessage(Resources.tagServer + ChatColor.RED + "해당하는 아이템이 없습니다!");
					break;
				}

				int number;

				try {
					number = Integer.parseInt(args[3]);
				} catch (NumberFormatException e) {
					sender.sendMessage(Resources.tagServer + ChatColor.RED + "명령어 사용 방법: /아이템 보내기 <플레이어> <이름> <수량>");
					break;
				}

				ItemStack item2 = itemManager.getItem(args[2]);
				item2.setAmount(number);
				target.getInventory().addItem(item2);

				break;
			case "저장":
			case "save":
				if (!(sender instanceof Player)) {
					sender.sendMessage(Resources.messageConsole);
					return true;
				}

				if (args.length < 2) {
					sender.sendMessage(Resources.tagServer + ChatColor.RED + "명령어 사용 방법: /아이템 저장 <이름>");
					break;
				}

				if (item == null) {
					sender.sendMessage(Resources.tagServer + ChatColor.RED + "아이템을 손에 들고 있어야 합니다!");
					break;
				}

				itemManager.setItem(args[1], item);
				break;
			case "삭제":
			case "remove":
			case "delete":
				if (args.length < 2) {
					sender.sendMessage(Resources.tagServer + ChatColor.RED + "명령어 사용 방법: /아이템 삭제 <이름>");
					break;
				}

				if (!itemManager.hasItem(args[1])) {
					sender.sendMessage(Resources.tagServer + ChatColor.RED + "해당하는 아이템이 없습니다!");
					break;
				}

				itemManager.removeItem(args[1]);

				break;
			case "이름":
			case "name":
				if (!(sender instanceof Player)) {
					sender.sendMessage(Resources.messageConsole);
					return true;
				}

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
				if (!(sender instanceof Player)) {
					sender.sendMessage(Resources.messageConsole);
					return true;
				}

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
						List<String> lore1;
						if (meta2.hasLore()) {
							lore1 = meta2.getLore();
						} else {
							lore1 = new ArrayList<String>();
						}

						lore1.add(ChatColor.translateAlternateColorCodes('&', sb2.toString()));
						meta2.setLore(lore1);
						item.setItemMeta(meta2);
						break;
					case "제거":
					case "삭제":
					case "delete":
					case "remove":
						if (!(sender instanceof Player)) {
							sender.sendMessage(Resources.messageConsole);
							return true;
						}

						if (item == null) {
							sender.sendMessage(Resources.tagServer + ChatColor.RED + "아이템을 손에 들고 있어야 합니다!");
							break;
						}

						ItemMeta meta3 = item.getItemMeta();

						if (!meta3.hasLore() || meta3.getLore().isEmpty()) {
							sender.sendMessage(Resources.tagServer + ChatColor.RED + "해당 아이템은 설명을 가지고 있지 않습니다!");
							break;
						}

						if (args.length < 3) {
							meta3.setLore(null);
						} else {
							int line;
							try {
								line = Integer.parseInt(args[2]);
							} catch (NumberFormatException e) {
								sender.sendMessage(Resources.tagServer + ChatColor.RED + "명령어 사용 방법: /아이템 설명 삭제 <줄>");
								break;
							}

							if (line < 1) {
								sender.sendMessage(Resources.tagServer + ChatColor.RED + "줄 번호는 양수여야 합니다.");
								break;
							}

							List<String> lore2 = meta3.getLore();

							if (lore2.size() < line) {
								sender.sendMessage(Resources.tagServer + ChatColor.RED + "해당하는 줄 번호가 없습니다.");
								break;
							}

							lore2.remove(line - 1);
							meta3.setLore(lore2);
						}

						item.setItemMeta(meta3);
						break;
					default:
						break;
					}
				}

				break;
			case "인챈트":
			case "enchant":
				if (!(sender instanceof Player)) {
					sender.sendMessage(Resources.messageConsole);
					return true;
				}

				if (args.length < 2) {

				} else {
					switch (args[1].toLowerCase()) {
					case "추가":
					case "add":
						if (args.length < 4) {
							sender.sendMessage(
									Resources.tagServer + ChatColor.RED + "명령어 사용 방법: /아이템 인챈트 추가 <인챈트> <레벨>");
							break;
						}

						if (item == null) {
							sender.sendMessage(Resources.tagServer + ChatColor.RED + "아이템을 손에 들고 있어야 합니다!");
							break;
						}

						Enchantment enchant1 = Enchantment.getByName(args[2].toUpperCase());

						if (enchant1 == null) {
							sender.sendMessage(Resources.tagServer + ChatColor.RED + "존재하지 않는 인챈트입니다.");
							break;
						}

						int level;
						try {
							level = Integer.parseInt(args[3]);
						} catch (NumberFormatException e) {
							sender.sendMessage(
									Resources.tagServer + ChatColor.RED + "명령어 사용 방법: /아이템 인챈트 추가 <인챈트> <레벨>");
							break;
						}

						ItemMeta meta4 = item.getItemMeta();
						meta4.addEnchant(enchant1, level, true);
						item.setItemMeta(meta4);
						break;
					case "제거":
					case "delete":
					case "remove":
						if (args.length < 3) {
							sender.sendMessage(Resources.tagServer + ChatColor.RED + "명령어 사용 방법: /아이템 인챈트 제거 <인챈트>");
							break;
						}

						if (item == null) {
							sender.sendMessage(Resources.tagServer + ChatColor.RED + "아이템을 손에 들고 있어야 합니다!");
							break;
						}

						Enchantment enchant2 = Enchantment.getByName(args[2].toUpperCase());

						if (enchant2 == null) {
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
			case "수리":
			case "repair":
				if (!(sender instanceof Player)) {
					sender.sendMessage(Resources.messageConsole);
					return true;
				}

				if (item == null) {
					sender.sendMessage(Resources.tagServer + ChatColor.RED + "아이템을 손에 들고 있어야 합니다!");
					break;
				}

				item.setDurability((short) 0);
				break;
			}
		}
		return true;
	}

}
