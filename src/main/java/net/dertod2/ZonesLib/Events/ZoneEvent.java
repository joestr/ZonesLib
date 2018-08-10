package net.dertod2.ZonesLib.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.dertod2.ZonesLib.Classes.Zone;

public class ZoneEvent extends Event implements Cancellable {
	private static final HandlerList handlerList = new HandlerList();
	
	private Player player;	
	private Zone zone;
	
	private boolean cancel;
	
	public ZoneEvent(Player player, Zone zone) {
		this.player = player;
		this.zone = zone;
	}

	public Player getPlayer() {
		return this.player;
	}
	
	public Zone getZone() {
		return this.zone;
	}
	
	public boolean isCancelled() {
		return this.cancel;
	}
	
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

	public HandlerList getHandlers() {
		return ZoneEvent.handlerList;
	}
	
    public static HandlerList getHandlerList() {
        return handlerList;
    }
}