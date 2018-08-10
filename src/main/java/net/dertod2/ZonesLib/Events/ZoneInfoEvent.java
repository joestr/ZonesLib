package net.dertod2.ZonesLib.Events;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

import net.dertod2.ZonesLib.Classes.Zone;

public class ZoneInfoEvent extends Event {
	private static final HandlerList handlerList = new HandlerList();
	
	private Zone zone;
	
	private Plugin zonePlugin;
	private Map<String, Object> zoneInformations = new HashMap<String, Object>();
	
	public ZoneInfoEvent(Zone zone) {
		this.zone = zone;
	}
	
	public Zone getZone() {
		return this.zone;
	}
	
	public Plugin getPlugin() {
		return this.zonePlugin;
	}
	
	public void setPlugin(Plugin zonePlugin) {
		this.zonePlugin = zonePlugin;
		this.zoneInformations.clear();
	}
	
	public Object getInformation(String information) {
		return this.zoneInformations.get(information);
	}
	
	public Map<String, Object> getInformations() {
		return this.zoneInformations;
	}
	
	public void setInformations(Map<String, Object> infoMap) {
		this.zoneInformations = infoMap;
	}
	
	public void addInformation(String information, Object value) {
		this.zoneInformations.put(information, value);
	}

	public HandlerList getHandlers() {
		return ZoneInfoEvent.handlerList;
	}
	
    public static HandlerList getHandlerList() {
        return handlerList;
    }
}