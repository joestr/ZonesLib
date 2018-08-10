package net.dertod2.ZonesLib.Classes;

import java.sql.Timestamp;
import java.util.UUID;

import org.bukkit.util.Vector;

public abstract class OriginZone extends Zone {
	
	private double centerX;
	private double centerY;
	private double centerZ;
	
	public OriginZone(int zoneId, UUID creator, Timestamp created, boolean deleted, String world, Vector position) {
		super(zoneId, creator, created, deleted, world);

		this.centerX = position.getX();
		this.centerY = position.getY();
		this.centerZ = position.getZ();
	}
	
	public double getX() {
		return this.centerX;
	}
	
	public double getY() {
		return this.centerY;
	}
	
	public double getZ() {
		return this.centerZ;
	}
	
	public Vector getCenter() {
		return new Vector(this.centerX, this.centerY, this.centerZ);
	}
}