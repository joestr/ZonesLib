package net.dertod2.ZonesLib.Classes;

import java.awt.Rectangle;
import java.awt.geom.Area;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import net.dertod2.ZonesLib.Util.ChunkPosition;

public class CuboidZone extends OriginZone implements HeightZone {
	protected double length;
	protected double width;
	
	private boolean fullHeight;
	private double height;

	public CuboidZone(int zoneId, UUID creator, Timestamp created, boolean deleted, String world, Vector position, boolean fullHeight, double height, double width, double length) {
		super(zoneId, creator, created, deleted, world, position);

		this.length = length;
		this.width = width;
		
		this.fullHeight = fullHeight;
		this.height = height;
	}

	public double getLength() {
		return (this.length * 2) + 1;
	}
	
	public double getWidth() {
		return (this.width * 2) + 1;
	}
	
	public double getHeight() {
		return this.fullHeight ? 256 : (this.height * 2) + 1;
	}
	
	public double getPlainHeight() {
		return this.height;
	}
	
	public boolean isFullHeight() {
		return this.fullHeight;
	}

	public ZoneBoundingBox getBoundingBox() {
		return new ZoneBoundingBox(this.getX(), this.fullHeight ? 128 : this.getY(), this.getZ(), this.length, this.fullHeight ? 128 : this.height, this.width);
	}
	
	public double getArea() {
		return this.getLength() * this.getWidth();
	}
	
	public double getVolume() {
		return this.getArea() * this.getHeight();
	}
	
	public Area asArea() {
		Location min = this.getMinimum();
		Location max = this.getMaximum();
		
		return new Area(new Rectangle(min.getBlockX(), min.getBlockZ(), max.getBlockX() - min.getBlockX(), max.getBlockZ() - min.getBlockZ()));
	}

	public boolean intersect(World world, Vector position) {
		if (!this.world.equals(world.getName())) return false;
		return this.getBoundingBox().intersect(position);
	}
	
	public boolean intersect(Zone zone) {
		if (!this.world.equals(zone.world)) return false;
		
		if (zone instanceof CuboidZone) {
			return this.getBoundingBox().intersect(zone.getBoundingBox());
		} else if (zone instanceof SphereZone) {
			SphereZone sphereZone = (SphereZone) zone;
			
			if(this.intersect(sphereZone.getWorld(), sphereZone.getCenter())) return true;
			
			ZoneLine[] zoneLines = this.getBoundingBox().getCuboidLines();
			for(int i = 0; i < zoneLines.length; i++) {
				if (sphereZone.getCenter().distance(zoneLines[i].getOpposite(sphereZone.getCenter())) <= sphereZone.getRadius()) {
					return true;
				}	
			}
			
			return false;
		} else if (zone instanceof CylinderZone) {
			CylinderZone cylinderZone = (CylinderZone) zone;
			
			if((this.isFullHeight() || cylinderZone.isFullHeight()) || 
					(cylinderZone.getCenter().getY() + cylinderZone.getHeight() >= this.getCenter().getY() - this.height && 
							cylinderZone.getCenter().getY() - cylinderZone.getHeight() <= this.getCenter().getY() + this.height)) {
				
				Vector center = cylinderZone.getCenter().clone();
				center.setY(0);
				
				ZoneLine[] zoneLines = this.getBoundingBox().getSquareLines();
				for(int i = 0; i < zoneLines.length; i++) {
					if (center.distance(zoneLines[i].getOpposite(center)) <= cylinderZone.getRadius()) {
						return true;
					}
				}
			}
			
			return false;
		} else if (zone instanceof PolygonZone) {
			if (this.getMinimum().getY() < zone.getMinimum().getY() && this.getMaximum().getY() < zone.getMinimum().getY()) return false;
			if (this.getMinimum().getY() > zone.getMaximum().getY() && this.getMaximum().getY() > zone.getMaximum().getY()) return false;
			
			Area intersectArea = this.asArea();
			intersectArea.intersect(zone.asArea());
			
			return !intersectArea.isEmpty();
		}
		
		return false;
	}
	
	public boolean contains(Zone zone) {
		if (!this.world.equals(zone.world)) return false;
		return this.getBoundingBox().contains(zone.getBoundingBox());
	}

	public List<Block> getBlocks(int min, int max) {
		List<Block> blockList = new ArrayList<Block>();
		
		World world = this.getWorld();
		double downX = this.getX() - this.length;
		double downZ = this.getZ() - this.width;
		double upX = this.getX() + this.length;
		double upZ = this.getZ() + this.width;
		
		for (double x = downX; x <= upX; x++) {
			for (double z = downZ; z <= upZ; z++) {
				for (double y = min; y <= max; y++) {
					blockList.add(world.getBlockAt((int) x, (int) y, (int) z));
				}
			}
		}
		
		return blockList;
	}
	
	public Block getBlock(int x, int y, int z) {
		Location minimum = this.getMinimum();
		return this.getWorld().getBlockAt(minimum.getBlockX() + x, minimum.getBlockY() + y, minimum.getBlockZ() + z);
	}

	public List<ChunkPosition> getChunks() {
		List<ChunkPosition> chunkList = new ArrayList<ChunkPosition>();

		World world = this.getWorld();
		int start[] = { (int) (this.getX() - this.length) / 16, (int) (this.getZ() - this.width) / 16 };
		int end[] = { (int) (this.getX() + this.length) / 16, (int) (this.getZ() + this.width) / 16 };
		
		for (int x = start[0]; x <= end[0]; ++x) {
			for (int z = start[1]; z <= end[1]; ++z) {
				chunkList.add(new ChunkPosition(world, x, z));
			}
		}
		
		return chunkList;
	}
}