package com.tistory.hornslied.evitaonline.play;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import com.tistory.hornslied.evitaonline.core.EvitaCoreMain;
import com.tistory.hornslied.evitaonline.db.DB;
import com.tistory.hornslied.evitaonline.events.EvitaCloseEvent;

public class PlayManager implements Listener {
	private volatile static PlayManager instance;

	private EvitaCoreMain evitaCore;
	private DB db;

	private HashMap<Player, BukkitRunnable> playTimeCounters;
	private HashMap<Player, Long> refreshTimes;

	private PlayManager() {
		evitaCore = EvitaCoreMain.getInstance();
		db = evitaCore.getDB();
		playTimeCounters = new HashMap<>();
		refreshTimes = new HashMap<>();

		loadWarps();

		Bukkit.getPluginManager().registerEvents(this, evitaCore);
	}

	private void loadWarps() {
		ResultSet rs = evitaCore.getDB().select("SELECT * FROM warps");

		try {
			while (rs.next()) {
				Block block = Bukkit.getWorld(rs.getString("world")).getBlockAt(rs.getInt("x"), rs.getInt("y"),
						rs.getInt("z"));
				Location warp = new Location(Bukkit.getWorld(rs.getString("warpworld")), rs.getDouble("warpx"),
						rs.getDouble("warpy"), rs.getDouble("warpz"));
				block.setMetadata("warp", new FixedMetadataValue(evitaCore, warp));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static PlayManager getInstance() {
		if (instance == null) {
			synchronized (PlayManager.class) {
				if (instance == null) {
					instance = new PlayManager();
				}
			}
		}

		return instance;
	}

	public void addPlayTime(Player player) {
		refreshTimes.put(player, System.currentTimeMillis());
		playTimeCounters.put(player, new BukkitRunnable() {

			@Override
			public void run() {
				try {
					updatePlayTime(player);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});

		playTimeCounters.get(player).runTaskTimer(evitaCore, 12000, 12000);
	}

	public void updatePlayTime(Player player) throws SQLException {
		ResultSet rs = db.select("SELECT * FROM playerinfo WHERE uuid = '" + player.getUniqueId().toString() + "';");

		if (rs.next()) {
			long currentTime = System.currentTimeMillis();
			long value = rs.getLong("playtime") + (System.currentTimeMillis() - refreshTimes.get(player));
			db.query("UPDATE playerinfo SET playtime = " + value + " WHERE uuid = '" + player.getUniqueId().toString()
					+ "';");
			refreshTimes.put(player, currentTime);
		}
	}

	// Listeners

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e) {
		addPlayTime(e.getPlayer());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		try {
			updatePlayTime(e.getPlayer());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	@EventHandler
	public void onClose(EvitaCloseEvent e) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			try {
				updatePlayTime(p);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().hasMetadata("warp")) {
			e.getPlayer().teleport((Location) e.getClickedBlock().getMetadata("warp").get(0).value()); 
		}
	}
}
