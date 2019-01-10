package net.dertod2.ZonesLib.DisplayAPI;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class AreaDisplayThread implements Runnable {
    private Player player;

    private List<Location> locations;
    private ParticleEffect particleEffect;

    private long displayLength;

    private Integer taskId;

    protected AreaDisplayThread(Player player, List<Location> locations, ParticleEffect particleEffect,
            long displayLength) {
        this.player = player;

        this.locations = locations;
        this.particleEffect = particleEffect;

        this.displayLength = displayLength;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public void run() {
        if (this.player != null && this.player.isOnline()) {
            for (Location location : this.locations) {
                if (!player.getWorld().equals(location.getWorld()))
                    continue;
                if (location.distance(this.player.getLocation()) > 16.0D)
                    continue;

                this.particleEffect.display(0F, 0F, 0F, 0F, 1, location, this.player);
            }
        }

        if ((displayLength > 0 && displayLength < System.currentTimeMillis()) || this.player == null
                || !this.player.isOnline()) {
            DisplayControl.displayList.get(this.player.getUniqueId()).remove(this.taskId);
            Bukkit.getScheduler().cancelTask(this.taskId);
        }
    }
}