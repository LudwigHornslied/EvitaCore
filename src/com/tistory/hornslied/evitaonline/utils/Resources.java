package com.tistory.hornslied.evitaonline.utils;

import net.md_5.bungee.api.ChatColor;

public final class Resources {
	public static final String tagConnect = "[" + ChatColor.DARK_GREEN + "접속" + ChatColor.WHITE + "] ";
	public static final String tagInfo = "[" + ChatColor.DARK_GREEN + "정보" + ChatColor.WHITE + "] ";
	public static final String tagCombat = "[" + ChatColor.DARK_GREEN + "전투" + ChatColor.WHITE + "] ";
	public static final String tagWar = "[" + ChatColor.DARK_GREEN + "전쟁" + ChatColor.WHITE + "] ";
	public static final String tagDeath = "[" + ChatColor.DARK_GREEN + "사망" + ChatColor.WHITE + "] ";
	public static final String tagServer = "[" + ChatColor.DARK_GREEN + "서버" + ChatColor.WHITE + "] ";
	public static final String tagMove = "[" + ChatColor.DARK_GREEN + "이동" + ChatColor.WHITE + "] ";
	public static final String tagNation = "[" + ChatColor.DARK_GREEN + "국가" + ChatColor.WHITE + "] ";
	public static final String tagAlert = "[" + ChatColor.DARK_RED + "서버" + ChatColor.WHITE + "] ";
	public static final String messagePermission = tagServer + ChatColor.RED + "명령어 사용 권한이 없습니다.";
	public static final String messagePlayerNotExist = tagServer + ChatColor.RED + "플레이어가 존재하지 않거나 접속중이지 않습니다.";
	public static final String messageConsole = tagServer + ChatColor.RED + "플레이어만 사용가능한 명령어입니다.";
	public static final String blank = ChatColor.RESET.toString();
}
