package com.tistory.hornslied.evitaonline.play;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.tistory.hornslied.evitaonline.core.EvitaCoreMain;

import net.minecraft.server.v1_12_R1.ChatMessageType;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;
import net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer;

public class NoticeManager {
	private volatile static NoticeManager instance;

	private String currentMessage;
	private ArrayList<String> messages;
	
	private BukkitRunnable messageUpdater;
	private BukkitRunnable messageSender;

	private NoticeManager() {
		messages = new ArrayList<>();
		loadMessages();
		init();
	}

	private void loadMessages() {
		messages.addAll(EvitaCoreMain.getInstance().getMessages().getStringList("messages"));
	}

	private void init() {
		messageUpdater = new BukkitRunnable() {

			@Override
			public void run() {
				if (messages.size() > 0) {
					Random rd = new Random();
					currentMessage = messages.get(rd.nextInt(messages.size()));
				}
			}
		};
		messageSender = new BukkitRunnable() {

			@Override
			public void run() {
				if (currentMessage != null) {
					for (Player p : Bukkit.getOnlinePlayers()) {
						sendToPlayer(p, currentMessage);
					}
				}
			}
		};
		
		messageUpdater.runTaskTimer(EvitaCoreMain.getInstance(), 0, 300);
		messageSender.runTaskTimer(EvitaCoreMain.getInstance(), 0, 20);
	}

	public static NoticeManager getInstance() {
		if (instance == null) {
			synchronized (NoticeManager.class) {
				if (instance == null) {
					instance = new NoticeManager();
				}
			}
		}

		return instance;
	}

	public void sendToPlayer(Player player, String message) {
		final String filteredMessage = message.replace("_", " ");
		String s = ChatColor.translateAlternateColorCodes('&', filteredMessage);
		IChatBaseComponent icbc = ChatSerializer.a("{\"text\": \"" + s + "\"}");
		PacketPlayOutChat bar = new PacketPlayOutChat(icbc, ChatMessageType.GAME_INFO);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(bar);
	}
	
	public void reload() {
		messageUpdater.cancel();
		messageSender.cancel();
		
		messages.clear();
		loadMessages();
		
		messageUpdater.runTaskTimer(EvitaCoreMain.getInstance(), 0, 300);
		messageSender.runTaskTimer(EvitaCoreMain.getInstance(), 0, 20);
	}
}
