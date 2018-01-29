package com.tistory.hornslied.evitaonline.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_12_R1.Packet;

public class HeaderFooter {

	public static void setPlayerHeaderAndFooter(Player player, String header, String footer) {
		try {
			Object playerConnection = getConnection(player);
			Class<?> packetClass = getNMSClass("PacketPlayOutPlayerListHeaderFooter");
			Object packet = packetClass.newInstance();
			
			IChatBaseComponent icbcHeader = ChatSerializer.a("{\"text\": \"" + header + "\"}");
			IChatBaseComponent icbcFooter = ChatSerializer.a("{\"text\": \"" + footer + "\"}");
			
			setValue(packet, "a", icbcHeader);
			setValue(packet, "b", icbcFooter);
			playerConnection.getClass().getMethod("sendPacket", Packet.class).invoke(playerConnection, packet);
		} catch (SecurityException | NoSuchMethodException | NoSuchFieldException | IllegalArgumentException
				| IllegalAccessException | InvocationTargetException | ClassNotFoundException
				| InstantiationException e1) {
			e1.printStackTrace();
		}
	}

	private static Class<?> getNMSClass(String nmsClassString) throws ClassNotFoundException {
		String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
		String name = "net.minecraft.server." + version + nmsClassString;
		Class<?> nmsClass = Class.forName(name);
		return nmsClass;
	}

	private static Object getConnection(Player player) throws SecurityException, NoSuchMethodException,
			NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Method getHandle = player.getClass().getMethod("getHandle");
		Object nmsPlayer = getHandle.invoke(player);
		Field conField = nmsPlayer.getClass().getField("playerConnection");
		Object con = conField.get(nmsPlayer);
		return con;
	}

	public static void setValue(Object ins, String fieldName, Object value) {
		try {
			Field f = ins.getClass().getDeclaredField(fieldName);
			f.setAccessible(true);
			f.set(ins, value);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

}
