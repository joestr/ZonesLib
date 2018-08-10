package net.dertod2.ZonesLib.DisplayAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.dertod2.ZonesLib.Binary.ZonesLib;
import net.dertod2.ZonesLib.Classes.Zone;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class DisplayControl {
	protected static Map<UUID, List<Integer>> displayList = new HashMap<UUID, List<Integer>>();
	protected static Map<UUID, Integer> pathList = new HashMap<UUID, Integer>();
	
	public static Map<Player, Integer> startViewing(Zone zone, ParticleEffect particleEffect, long milliseconds, Player... playerList) {
		return DisplayControl.startViewing(zone.getFilter().getOuterLine(), particleEffect, milliseconds, playerList);
	}
	
	public static Map<Player, Integer> startViewing(List<Location> locations, ParticleEffect particleEffect, long milliseconds, Player... playerList) {
		long displayLength = milliseconds > 0 ? System.currentTimeMillis() + milliseconds : 0;
		
		Map<Player, Integer> viewingList = new HashMap<Player, Integer>();
		
		for (Player player : playerList) {
			if (!DisplayControl.displayList.containsKey(player.getUniqueId())) DisplayControl.displayList.put(player.getUniqueId(), new ArrayList<Integer>());
			
			AreaDisplayThread areaDisplayThread = new AreaDisplayThread(player, locations, particleEffect, displayLength);
			int taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(ZonesLib.getInstance(), areaDisplayThread, 10L, 5L).getTaskId();	
			areaDisplayThread.setTaskId(taskId);
			
			DisplayControl.displayList.get(player.getUniqueId()).add(taskId);
			viewingList.put(player, taskId);
		}
		
		return viewingList;
	}
	
	public static int startViewing(Zone zone, ParticleEffect particleEffect, long milliseconds, Player player) {
		return DisplayControl.startViewing(zone.getFilter().getOuterLine(), particleEffect, milliseconds, player);
	}
	
	public static int startViewing(List<Location> locations, ParticleEffect particleEffect, long milliseconds, Player player) {
		long displayLength = milliseconds > 0 ? System.currentTimeMillis() + milliseconds : 0;
		
		if (!DisplayControl.displayList.containsKey(player.getUniqueId())) DisplayControl.displayList.put(player.getUniqueId(), new ArrayList<Integer>());
			
		AreaDisplayThread areaDisplayThread = new AreaDisplayThread(player, locations, particleEffect, displayLength);
		int taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(ZonesLib.getInstance(), areaDisplayThread, 10L, 5L).getTaskId();	
		areaDisplayThread.setTaskId(taskId);
			
		DisplayControl.displayList.get(player.getUniqueId()).add(taskId);
		return taskId;
	}
	
	public static boolean isViewing(Player player) {
		if (!DisplayControl.displayList.containsKey(player.getUniqueId())) return false;
		return DisplayControl.displayList.get(player.getUniqueId()).size() > 0;
	}
	
	public static boolean stopViewing(Player player, Integer taskId) {
		if (!DisplayControl.displayList.containsKey(player.getUniqueId())) return false;
		if (taskId != null && !DisplayControl.displayList.get(player.getUniqueId()).remove((taskId))) return false;
		
		if (taskId == null) {
			List<Integer> taskList = DisplayControl.displayList.remove(player.getUniqueId());
			if (taskList == null || taskList.isEmpty()) return false;
			
			for (Integer taskListId : taskList) Bukkit.getScheduler().cancelTask(taskListId);
		} else {
			Bukkit.getScheduler().cancelTask(taskId);
		}
		
		return true;
	}
}