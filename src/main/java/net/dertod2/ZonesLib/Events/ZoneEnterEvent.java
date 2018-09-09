package net.dertod2.ZonesLib.Events;

import org.bukkit.entity.Player;

import net.dertod2.ZonesLib.Classes.Zone;

public class ZoneEnterEvent extends ZoneEvent {

    public ZoneEnterEvent(Player player, Zone zone) {
        super(player, zone);
    }

}