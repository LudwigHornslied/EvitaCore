package com.tistory.hornslied.evitaonline.core;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.palmergames.bukkit.TownyChat.channels.Channel;
import com.tistory.hornslied.evitaonline.commands.AdminUtilCommand;
import com.tistory.hornslied.evitaonline.commands.EvitaCoreCommand;
import com.tistory.hornslied.evitaonline.commands.ItemCommand;
import com.tistory.hornslied.evitaonline.commands.UserUtilCommand;
import com.tistory.hornslied.evitaonline.db.DB;
import com.tistory.hornslied.evitaonline.events.EvitaCloseEvent;
import com.tistory.hornslied.evitaonline.listeners.ChatListener;
import com.tistory.hornslied.evitaonline.listeners.DeathListener;
import com.tistory.hornslied.evitaonline.listeners.FarmListener;
import com.tistory.hornslied.evitaonline.listeners.JoinListener;
import com.tistory.hornslied.evitaonline.listeners.RespawnListener;
import com.tistory.hornslied.evitaonline.listeners.TablistNameUpdater;
import com.tistory.hornslied.evitaonline.play.NoticeManager;
import com.tistory.hornslied.evitaonline.play.PlayManager;
import com.tistory.hornslied.evitaonline.warp.WarpManager;
import com.tistory.hornslied.evitaonline.warp.WarpTrait;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitFactory;
import net.citizensnpcs.api.trait.TraitInfo;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;

public class EvitaCoreMain extends JavaPlugin {
	private static EvitaCoreMain instance;

	private FileConfiguration config;
	private FileConfiguration messages;
	private FileConfiguration warp;

	private DB db;

	private Economy economy;
	private Chat vaultChat;
	private com.palmergames.bukkit.TownyChat.Chat townyChat;
	private Channel generalChannel;

	public static EvitaCoreMain getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
		instance = this;
		loadConfig();
		loadWorlds();
		setupDB();
		setupVault();
		setupTownyChat();
		PlayManager.getInstance();
		NoticeManager.getInstance();
		WarpManager.getInstance();

		registerTraits();
		registerListeners();
		initCommands();
		scheduleReboot();
	}

	@Override
	public void onDisable() {
		Bukkit.getPluginManager().callEvent(new EvitaCloseEvent());
	}

	private void loadConfig() {
		if (!new File(getDataFolder(), "config.yml").exists())
			saveDefaultConfig();
		config = getConfig();

		if (!(new File(getDataFolder(), "messages.yml").exists()))
			saveResource("messages.yml", false);
		messages = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "messages.yml"));
		
		if (!(new File(getDataFolder(), "warp.yml").exists()))
			saveResource("warp.yml", false);
		warp = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "warp.yml"));
	}

	private void loadWorlds() {
		for (String s : config.getStringList("worlds")) {
			getServer().createWorld(new WorldCreator(s));
		}
	}

	private void setupDB() {
		String dburl;

		if (config.getBoolean("db.useSSL")) {
			dburl = "jdbc:mysql://" + config.getString("db.host") + ":" + config.getString("db.port") + "/"
					+ config.getString("db.dbname");
		} else {
			dburl = "jdbc:mysql://" + config.getString("db.host") + ":" + config.getString("db.port") + "/"
					+ config.getString("db.dbname") + "?useSSL=false";
		}

		db = new DB(dburl, config.getString("db.user"), config.getString("db.password"));

		createTables();
	}

	private void createTables() {
		db.query("CREATE TABLE IF NOT EXISTS playerinfo (" + "uuid varchar(40) NOT NULL PRIMARY KEY,"
				+ "nickname varchar(50) NOT NULL," + "playtime bigint NOT NULL DEFAULT 0,"
				+ "unit int NOT NULL DEFAULT 0," + "playerkill int NOT NULL DEFAULT 0,"
				+ "playerdeath int NOT NULL DEFAULT 0," + "pvpprot int DEFAULT NULL" + ");");
		db.query("CREATE TABLE IF NOT EXISTS joinlogs (" + "uuid varchar(40) NOT NULL PRIMARY KEY,"
				+ "nickname varchar(50) NOT NULL," + "date varchar(20) NOT NULL," + "ip varchar(50) NOT NULL" + ");");
		db.query("CREATE TABLE IF NOT EXISTS warps (" + "id int NOT NULL PRIMARY KEY AUTO_INCREMENT,"
				+ "x long NOT NULL," + "y long NOT NULL," + "z long NOT NULL," + "world varchar(50) NOT NULL,"
				+ "warpx double NOT NULL," + "warpy double NOT NULL," + "warpz long NOT NULL,"
				+ "warpworld varchar(50) NOT NULL" + ");");
	}

	private void setupVault() {
		PluginManager pm = Bukkit.getPluginManager();

		if (pm.getPlugin("Vault") == null) {
			pm.disablePlugin(this);
		}
		RegisteredServiceProvider<Economy> rsp1 = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp1 == null) {
			pm.disablePlugin(this);
		}
		RegisteredServiceProvider<Chat> rsp2 = getServer().getServicesManager().getRegistration(Chat.class);
		if (rsp2 == null) {
			pm.disablePlugin(this);
		}
		economy = rsp1.getProvider();
		vaultChat = rsp2.getProvider();
	}

	private boolean setupTownyChat() {
		townyChat = (com.palmergames.bukkit.TownyChat.Chat) Bukkit.getPluginManager().getPlugin("TownyChat");
		generalChannel = townyChat.getChannelsHandler().getChannel(config.getString("globalchannel"));
		return townyChat != null;
	}
	
	private void registerTraits() {
		TraitFactory tf = CitizensAPI.getTraitFactory();
		
		tf.registerTrait(TraitInfo.create(WarpTrait.class));
	}

	private void registerListeners() {
		PluginManager pm = Bukkit.getPluginManager();

		pm.registerEvents(new JoinListener(), this);
		pm.registerEvents(new RespawnListener(), this);
		pm.registerEvents(new ChatListener(), this);
		pm.registerEvents(new DeathListener(), this);
		pm.registerEvents(new FarmListener(), this);
		pm.registerEvents(new TablistNameUpdater(), this);
		
		registerPackets();
	}
	
	private void registerPackets() {
		ProtocolManager pm = ProtocolLibrary.getProtocolManager();
		
		pm.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Server.PLAYER_INFO) {
			@Override
		    public void onPacketSending(PacketEvent event) {
		        if (event.getPacketType() == 
		                PacketType.Play.Server.NAMED_SOUND_EFFECT) {
		            event.setCancelled(true);
		        }
		    }
		});
	}

	private void initCommands() {
		getCommand("evitacore").setExecutor(new EvitaCoreCommand());
		getCommand("item").setExecutor(new ItemCommand());
		
		AdminUtilCommand adminUtilCommand = new AdminUtilCommand();
		UserUtilCommand userUtilCommand = new UserUtilCommand();

		getCommand("tp").setExecutor(adminUtilCommand);
		getCommand("tppos").setExecutor(adminUtilCommand);
		getCommand("call").setExecutor(adminUtilCommand);
		getCommand("world").setExecutor(adminUtilCommand);
		getCommand("skull").setExecutor(adminUtilCommand);
		getCommand("restart").setExecutor(adminUtilCommand);
		getCommand("say").setExecutor(adminUtilCommand);
		getCommand("walkspeed").setExecutor(adminUtilCommand);
		getCommand("flyspeed").setExecutor(adminUtilCommand);
		getCommand("currentspeed").setExecutor(adminUtilCommand);
		getCommand("help").setExecutor(userUtilCommand);
		getCommand("spawn").setExecutor(userUtilCommand);
		getCommand("kit").setExecutor(userUtilCommand);
		getCommand("whisper").setExecutor(userUtilCommand);
		getCommand("reply").setExecutor(userUtilCommand);
		getCommand("playtime").setExecutor(userUtilCommand);
		getCommand("ping").setExecutor(userUtilCommand);
	}

	private void scheduleReboot() {
		Calendar now = Calendar.getInstance();
		now.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) + 1);
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);

		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				Bukkit.shutdown();
			}
		}, now.getTime());

		new Timer().schedule(new TimerTask() {
			int time = 301;

			@Override
			public void run() {
				if (time > 0)
					time--;
				switch (time) {
				case 300:
				case 180:
				case 60:
				case 30:
				case 10:
				case 5:
					sendRebootAlarm(time);
					break;
				}
			}

			private void sendRebootAlarm(int time) {
				if (time >= 60) {
					for (Player p : Bukkit.getOnlinePlayers()) {
						p.sendTitle(ChatColor.RED + ChatColor.BOLD.toString() + "재시작까지 남은 시간",
								ChatColor.GRAY + ChatColor.BOLD.toString() + time / 60 + "분", 10, 70, 10);
					}
				} else {
					for (Player p : Bukkit.getOnlinePlayers()) {
						p.sendTitle(ChatColor.RED + ChatColor.BOLD.toString() + "재시작까지 남은 시간",
								ChatColor.GRAY + ChatColor.BOLD.toString() + time + "초", 10, 70, 10);
					}
				}
			}
		}, new Date(now.getTimeInMillis() - 300000), 1000);
	}

	public FileConfiguration getConfiguration() {
		return config;
	}

	public FileConfiguration getMessages() {
		return messages;
	}
	
	public FileConfiguration getWarp() {
		return warp;
	}

	public DB getDB() {
		return db;
	}

	public Economy getEconomy() {
		return economy;
	}

	public Chat getChat() {
		return vaultChat;
	}

	public Channel getGeneralChannel() {
		return generalChannel;
	}
	
	public void reload() {
		loadConfig();
		
		NoticeManager.getInstance().reload();
	}
}
