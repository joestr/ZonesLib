package net.dertod2.ZonesLib.Binary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.dertod2.ZonesLib.Classes.Zone;
import net.dertod2.ZonesLib.Events.ZoneEnterEvent;
import net.dertod2.ZonesLib.Events.ZoneLeaveEvent;

public class ZoneThread implements Runnable {
	private Map<UUID, List<Integer>> resistingList = new HashMap<UUID, List<Integer>>();
	private Map<UUID, Location> locationList = new HashMap<UUID, Location>();
	
	public void run() {
		Thread currentThread = Thread.currentThread();
		while (currentThread == ZonesLib.zoneThread) {
			try {
				Collection<? extends Player> playerList = Bukkit.getOnlinePlayers();
				for (Player player : playerList) {
					if (!this.leaveTick(player)) continue;
					if (!this.enterTick(player)) continue;
					
					this.locationList.put(player.getUniqueId(), player.getLocation());
				}
			} catch (Exception exc) {
				
			}
			
			try { Thread.sleep(150L); } catch (InterruptedException exc) { }
		}
	}

	private boolean leaveTick(Player player) {
		if (!this.resistingList.containsKey(player.getUniqueId())) return true;		
		List<Integer> idList = this.resistingList.get(player.getUniqueId());
		
		if (idList.size() > 0) {
			for (Integer zoneId : idList) {
				Zone zone = Zone.getZone(zoneId);
				
				if (zone != null && !zone.isDeleted()) {
					if (!zone.intersect(player.getLocation())) {
						ZoneLeaveEvent event = new ZoneLeaveEvent(player, zone);
						Bukkit.getPluginManager().callEvent(event);
						
						if (event.isCancelled()) {
							player.leaveVehicle();
							player.teleport(this.locationList.get(player.getUniqueId()));
							return false;
						} else {
							this.resistingList.get(player.getUniqueId()).remove(zoneId);
						}
					}
				} else {
					this.resistingList.get(player.getUniqueId()).remove(zoneId); // Without event throw
				}
			}
		} else {
			this.resistingList.remove(player.getUniqueId());
		}
		
		return true;
	}
	
	private boolean enterTick(Player player) {
		List<Zone> zoneList = Zone.getZones(player.getLocation());
		
		for (Zone zone : zoneList) {
			if (this.resistingList.containsKey(player.getUniqueId()) && this.resistingList.get(player.getUniqueId()).contains(zone.getId())) continue;
			
			ZoneEnterEvent event = new ZoneEnterEvent(player, zone);
			Bukkit.getPluginManager().callEvent(event);
			
			if (event.isCancelled()) {
				player.leaveVehicle();
				player.teleport(this.locationList.get(player.getUniqueId()));
				return false;
			} else {
				if (!this.resistingList.containsKey(player.getUniqueId())) this.resistingList.put(player.getUniqueId(), new ArrayList<Integer>());
				this.resistingList.get(player.getUniqueId()).add(zone.getId());
			}
		}
		
		return true;
	}
}