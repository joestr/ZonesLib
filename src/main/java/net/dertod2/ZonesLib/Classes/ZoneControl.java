package net.dertod2.ZonesLib.Classes;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.util.Vector;

import net.dertod2.DatabaseHandler.Binary.DatabaseHandler;
import net.dertod2.DatabaseHandler.Table.TableEntry;

public class ZoneControl {
	private Map<Integer, Zone> zoneList;	
	private Map<String, Map<Integer, Map<Integer, List<Integer>>>> chunkList;
	
	public ZoneControl() {		
		try {
			DatabaseHandler.getHandler().updateLayout(new ZoneSchema());	
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public void load() {
		List<TableEntry> dataList = new ArrayList<TableEntry>();
		
		if (this.zoneList == null) this.zoneList = new HashMap<Integer, Zone>();
		if (this.chunkList == null) this.chunkList = new HashMap<String, Map<Integer, Map<Integer, List<Integer>>>>();
		
		this.zoneList.clear();
		this.chunkList.clear();
		
		try {
			DatabaseHandler.getHandler().load(new ZoneSchema(), dataList);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		for (TableEntry tableEntry : dataList) {
			ZoneSchema zoneSchema = (ZoneSchema) tableEntry;
			Zone zone = zoneSchema.toZone();
			
			this.zoneList.put(zone.getId(), zone);
			this.loadToChunkList(zone);
		}
	}
	
	protected Zone getZone(int zoneId) {
		return this.zoneList.get(zoneId);
	}
	
	protected List<Zone> getZones(World world, Vector position, boolean includeDeleted) {
		List<Zone> tempList = this.getChunkZones(world, position, includeDeleted);
		List<Zone> zoneList = new ArrayList<Zone>();
		
		for (Zone zone : tempList) {
			if (zone.intersect(world, position)) {
				zoneList.add(zone);
			}
		}
		
		return zoneList;
	}
	
	protected List<Zone> getZones(Zone zone, boolean includeDeleted) {
		List<Zone> tempList = this.getChunkZones(zone, includeDeleted);
		List<Zone> zoneList = new ArrayList<Zone>();
		
		for (Zone collisionZone : tempList) {
			if (zone.intersect(collisionZone)) {
				zoneList.add(collisionZone);
			}
		}

		return zoneList;
	}
	
	protected Zone add(Zone zone) {
		ZoneSchema zoneSchema = ZoneSchema.toSchema(zone);
		
		try {
			DatabaseHandler.getHandler().insert(zoneSchema);
		} catch (Exception exc) {
			exc.printStackTrace();
			return null;
		} finally {
			zone = zoneSchema.toZone();
		}
		
		if (zone != null) {
			this.zoneList.put(zone.getId(), zone);
			this.loadToChunkList(zone);
			
			return zone;
		}
		
		return null;
	}
	
	protected Zone add(OfflinePlayer player, List<Location> positions, boolean fullHeight, ZoneType zoneType, boolean onlyTemporary) {
		if (positions.isEmpty() || positions.size() < zoneType.getMinimum() || (zoneType.getMaximum() != -1 && positions.size() > zoneType.getMaximum())) return null;
		
		Zone zone = null;
		
		String world = positions.get(0).getWorld().getName();
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		
		switch (zoneType) {
			case CUBOID:
				Vector first = positions.get(0).toVector();
				Vector second = positions.get(1).toVector();
				
				Vector center = first.getMidpoint(second);
				double length = Math.abs(second.getX() - center.getX());
				double width = Math.abs(second.getZ() - center.getZ());
				double height = Math.abs(second.getY() - center.getY());
				
				zone = new CuboidZone(-1, player.getUniqueId(), currentTime, false, world, center, fullHeight, height, width, length);
				break;
			case CYLINDER:
				first = positions.get(0).toVector();
				second = positions.get(1).toVector();
				
				center = first.clone();
				double radius = center.distance(second.clone().setY(center.getY()));
				height = Math.abs(second.getY() - center.getY());

				zone = new CylinderZone(-1, player.getUniqueId(), currentTime, false, world, center, fullHeight, height, radius);
				break;
			case POLYGON:
				List<Double> polysX = new ArrayList<Double>(positions.size());
				List<Double> polysZ = new ArrayList<Double>(positions.size());
				
				double minY = Double.MAX_VALUE;
				double maxY = Double.MIN_VALUE;

				for (Location location : positions) {
					polysX.add(location.getX());
					polysZ.add(location.getZ());
					
					double y = location.getY();
					if (y < minY) minY = y;
					if (y > maxY) maxY = y;
				}
				
				double centerY = (minY + maxY) / 2;
				
				zone = new PolygonZone(-1, player.getUniqueId(), currentTime, false, world, fullHeight, centerY, maxY - centerY, polysX, polysZ);
				break;
			case SPHERE:
				first = positions.get(0).toVector();
				second = positions.get(1).toVector();
				
				center = first.clone();
				radius = center.distance(second);

				zone = new SphereZone(-1, player.getUniqueId(), currentTime, false, world, center, radius);
				break;		
		}
		
		if (onlyTemporary) return zone;
		return Zone.create(zone);
	}
	
	private void loadToChunkList(Zone zone) {
		ZoneBoundingBox zoneBoundingBox = zone.getBoundingBox();
		
		int start[] = { (int) (zoneBoundingBox.getX() - zoneBoundingBox.getLength()) / 16, (int) (zoneBoundingBox.getZ() - zoneBoundingBox.getWidth()) / 16 };
		int end[] = { (int) (zoneBoundingBox.getX() + zoneBoundingBox.getLength()) / 16, (int) (zoneBoundingBox.getZ() + zoneBoundingBox.getWidth()) / 16 };
		
		for (int x = start[0]; x <= end[0]; ++x) {
			for (int z = start[1]; z <= end[1]; ++z) {
				if (!this.chunkList.containsKey(zone.world)) this.chunkList.put(zone.world, new HashMap<Integer, Map<Integer, List<Integer>>>());
				if (!this.chunkList.get(zone.world).containsKey(x)) this.chunkList.get(zone.world).put(x, new HashMap<Integer, List<Integer>>());
				if (!this.chunkList.get(zone.world).get(x).containsKey(z)) this.chunkList.get(zone.world).get(x).put(z, new  ArrayList<Integer>());
				
				this.chunkList.get(zone.world).get(x).get(z).add(zone.getId());
			}
		}
	}
	
	private List<Zone> getChunkZones(World world, Vector position, boolean includeDeleted) {
		int chunkX = position.getBlockX() / 16;
		int chunkZ = position.getBlockZ() / 16;
		
		List<Zone> zoneList = new ArrayList<Zone>();
		
		if (!this.chunkList.containsKey(world.getName())) return zoneList;
		if (!this.chunkList.get(world.getName()).containsKey(chunkX)) return zoneList;
		if (!this.chunkList.get(world.getName()).get(chunkX).containsKey(chunkZ)) return zoneList;
		if (this.chunkList.get(world.getName()).get(chunkX).get(chunkZ) == null) return zoneList;
		
		for (Integer zoneId : this.chunkList.get(world.getName()).get(chunkX).get(chunkZ)) {
			Zone zone = this.zoneList.get(zoneId);
			if (!includeDeleted || zone.isDeleted()) zoneList.add(this.zoneList.get(zoneId));
		}
		
		return zoneList;
	}
	
	public List<Zone> getChunkZones(Zone zone, boolean includeDeleted) {
		ZoneBoundingBox zoneBoundingBox = zone.getBoundingBox();
		
		int start[] = { (int) (zoneBoundingBox.getX() - zoneBoundingBox.getLength()) / 16, (int) (zoneBoundingBox.getZ() - zoneBoundingBox.getWidth()) / 16 };
		int end[] = { (int) (zoneBoundingBox.getX() + zoneBoundingBox.getLength()) / 16, (int) (zoneBoundingBox.getZ() + zoneBoundingBox.getWidth()) / 16 };
		
		List<Zone> zoneList = new ArrayList<Zone>();
		
		for (int x = start[0]; x <= end[0]; ++x) {
			for (int z = start[1]; z <= end[1]; ++z) {
				if (!this.chunkList.containsKey(zone.world)) return zoneList;
				if (!this.chunkList.get(zone.world).containsKey(x)) continue;
				if (!this.chunkList.get(zone.world).get(x).containsKey(z)) continue;

				for (Integer zoneId : this.chunkList.get(zone.world).get(x).get(z)) {
					Zone checkZone = this.zoneList.get(zoneId);
					if (checkZone != null && !zoneList.contains(checkZone) && (!includeDeleted || zone.isDeleted())) zoneList.add(checkZone);
				}
			}
		}

		return zoneList;
	}
}