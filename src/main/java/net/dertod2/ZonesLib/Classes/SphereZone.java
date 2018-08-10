package net.dertod2.ZonesLib.Classes;

import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import net.dertod2.ZonesLib.Util.ChunkPosition;

public class SphereZone extends OriginZone implements RoundedZone {
	private double radius;
	
	public SphereZone(int zoneId, UUID creator, Timestamp created, boolean deleted, String world, Vector position, double radius) {
		super(zoneId, creator, created, deleted, world, position);
		
		this.radius = radius;
	}
	
	public double getRadius() {
		return this.radius;
	}
	
	public ZoneBoundingBox getBoundingBox() {
		return new ZoneBoundingBox(this.getX() - this.radius, this.getY() - this.radius, this.getZ() - this.radius, this.radius * 2, this.radius * 2, this.radius * 2);
	}
	
	public double getArea() {
		return Math.PI * Math.pow(this.radius, 2);
	}
	
	public double getVolume() {
		return (4 / 3) * Math.PI * Math.pow(this.radius, 3);
	}
	
	public Area asArea() {
		Location min = this.getMinimum();
		Location max = this.getMaximum();
		
		return new Area(new Ellipse2D.Double(min.getX(), min.getZ(), max.getX() - min.getX(), max.getZ() - min.getZ()));
	}
	
	public boolean intersect(World world, Vector position) {
		if (!this.world.equals(world.getName())) return false;
		
		double distance = this.getCenter().distance(position);
		if (distance > this.getRadius()) return false;
		
		return true;
	}

	public boolean intersect(Zone zone) {
		if (!this.world.equals(zone.world)) return false;
		
		if (zone instanceof SphereZone) {
			SphereZone sphereZone = (SphereZone) zone;
			
			return this.getCenter().distance(sphereZone.getCenter()) <= (this.getRadius() + sphereZone.getRadius());
		} else if (zone instanceof CuboidZone) {
			return zone.intersect(this);
		} else if (zone instanceof CylinderZone) {
			CylinderZone cylinderZone = (CylinderZone) zone;
			
			if(cylinderZone.getCenter().getY() + cylinderZone.getHeight() >= this.getCenter().getY() - this.getRadius() && 
					cylinderZone.getCenter().getY() - cylinderZone.getHeight() <= this.getCenter().getY() + this.getRadius()) {
				
				Vector center;
				double radius;
				
				if(cylinderZone.getCenter().getY() - cylinderZone.getHeight() <= this.getCenter().getY() && cylinderZone.getCenter().getY() + cylinderZone.getHeight() >= this.getCenter().getY()) {
					center = this.getCenter();
					radius = this.getRadius();
				} else if(this.getCenter().getY() >= cylinderZone.getCenter().getY() + cylinderZone.getHeight()) {
					center = this.getCenter().clone();
					center.setY(cylinderZone.getCenter().getY() + cylinderZone.getHeight());
					radius = Math.sqrt((this.getRadius() * this.getRadius()) - ((center.getY() - this.getCenter().getY()) * (center.getY() - this.getCenter().getY())));
				} else {
					center = this.getCenter().clone();
					center.setY(cylinderZone.getCenter().getY() - cylinderZone.getHeight());
					radius = Math.sqrt((this.getRadius() * this.getRadius()) - ((center.getY() - this.getCenter().getY()) * (center.getY() - this.getCenter().getY())));
				}
				
				center.setY(0);
				Vector cylCenter = cylinderZone.getCenter().clone().setY(0);
				
				return center.distance(cylCenter) <= (radius + cylinderZone.getRadius());
			}
			
			return false;
		} else if (zone instanceof PolygonZone) { // TODO Height intersect checks
			Area intersectArea = this.asArea();
			intersectArea.intersect(zone.asArea());
			
			return !intersectArea.isEmpty();
		}
		
		return false;
	}

	public boolean contains(Zone zone) {
		if (!this.world.equals(zone.world)) return false;
		
		if (zone instanceof CuboidZone) {
			Vector[] corners = zone.getBoundingBox().getCorners();
			
			for(int i = 0; i < corners.length; i++) {
				if(!this.intersect(zone.getWorld(), corners[i])) return false;
			}
			
			return true;
		} else if (zone instanceof SphereZone) {
			SphereZone sphereZone = (SphereZone) zone;
			
			return this.getCenter().distance(sphereZone.getCenter()) + sphereZone.getRadius() <= this.getRadius();
		} else if (zone instanceof CylinderZone) {
			CylinderZone cylinderZone = (CylinderZone) zone;
			
			if (cylinderZone.getCenter().getY() - cylinderZone.getHeight() < this.getCenter().getY() - this.getRadius() || cylinderZone.getCenter().getY() + cylinderZone.getHeight() < this.getCenter().getY() + this.getRadius()) {
				return false;
			}
					
			Vector lowerCenter, upperCenter;
			double lowerRadius, upperRadius;
			
			lowerCenter = this.getCenter().clone();
			lowerCenter.setY(cylinderZone.getCenter().getY() - cylinderZone.getHeight());
			lowerRadius = Math.sqrt((this.getRadius() * this.getRadius()) - ((this.getCenter().getY() - lowerCenter.getY()) * (this.getCenter().getY() - lowerCenter.getY())));  
			upperCenter = this.getCenter().clone();
			upperCenter.setY(cylinderZone.getCenter().getY() + cylinderZone.getHeight());
			upperRadius = Math.sqrt((this.getRadius() * this.getRadius()) - ((this.getCenter().getY() - upperCenter.getY()) * (this.getCenter().getY() - upperCenter.getY())));  
			lowerCenter.setY(0);
			upperCenter.setY(0);
			Vector cylCenter = cylinderZone.getCenter().clone().setY(0);
			
			return lowerCenter.distance(cylCenter) <= lowerRadius && upperCenter.distance(cylCenter) <= upperRadius;
		} else if (zone instanceof PolygonZone) { // TODO Height intersect checks
			Area intersectArea = this.asArea();
			intersectArea.intersect(zone.asArea());
			
			return intersectArea.equals(zone.getArea());
		}
		
		return false;
	}

	public List<Block> getBlocks(int min, int max) {
		throw new UnsupportedOperationException();
	}
	
	public Block getBlock(int x, int y, int z) {
		throw new UnsupportedOperationException();
	}

	public List<ChunkPosition> getChunks() {
		throw new UnsupportedOperationException();
	}
}