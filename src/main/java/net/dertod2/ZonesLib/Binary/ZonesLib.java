package net.dertod2.ZonesLib.Binary;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import net.dertod2.ZonesLib.Classes.ZoneControl;
import net.dertod2.ZonesLib.Commands.ZonesLibCommand;
import net.dertod2.ZonesLib.DisplayAPI.ParticleEffect.ParticlePacket;

/**
 * TODO: - Polygon Zones collisions
 * 
 * @author DerTod2
 *
 */

public class ZonesLib extends JavaPlugin {
    public static ZonesLib zonesLib;

    protected static ZoneControl zoneControl;
    protected static Thread zoneThread;

    public static Updater updater;

    public void onEnable() {
        ZonesLib.zonesLib = this;

        ZonesLib.zoneControl = new ZoneControl();
        ZonesLib.zoneControl.load();

        ZonesLib.zoneThread = new Thread(new ZoneThread());
        ZonesLib.zoneThread.setPriority(Thread.MAX_PRIORITY);
        ZonesLib.zoneThread.start();

        ParticlePacket.initialize();

        ZonesLib.updater = new Updater(this.getFile());
        ZonesLib.updater.check(Bukkit.getConsoleSender());

        getCommand("zoneslib").setExecutor(new ZonesLibCommand());
    }

    public void onDisable() {
        ZonesLib.zoneControl = null;

        try {
            if (ZonesLib.zoneThread != null) {
                ZonesLib.zoneThread.interrupt();
                ZonesLib.zoneThread = null;
            }
        } catch (Exception exc) {
        }
    }

    public static ZoneControl getControl() {
        return ZonesLib.zoneControl;
    }

    public static ZonesLib getInstance() {
        return ZonesLib.zonesLib;
    }
}