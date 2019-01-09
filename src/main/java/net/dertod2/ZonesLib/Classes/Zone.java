package net.dertod2.ZonesLib.Classes;

import java.awt.geom.Area;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.dertod2.DatabaseHandler.Binary.DatabaseHandler;
import net.dertod2.ZonesLib.Binary.ZonesLib;
import net.dertod2.ZonesLib.Events.ZoneInfoEvent;
import net.dertod2.ZonesLib.Util.ChunkPosition;

public abstract class Zone {
    protected int zoneId;

    protected UUID creator;
    private Timestamp created;

    private boolean deleted;

    protected String world;

    public Zone(int zoneId, UUID creator, Timestamp created, boolean deleted, String world) {
        this.zoneId = zoneId;

        this.creator = creator;
        this.created = created;

        this.deleted = deleted;

        this.world = world;
    }

    /**
     * Get the {@link Zone} by ID
     * 
     * @param zoneId
     *            The Zone ID
     * @return {@link Zone} or null when not existing
     */
    public static Zone getZone(int zoneId) {
        return ZonesLib.getControl().getZone(zoneId);
    }

    /**
     * Get all existing {@link Zone}s found at the {@link Position} at the given
     * {@link World}.
     * 
     * @param location
     *            The {@link Location} for searching {@link Zone}s
     * @return A {@link List} contains all found {@link Zone}s
     */
    public static List<Zone> getZones(Location location) {
        return Zone.getZones(location, false);
    }

    /**
     * Get all {@link Zone}s found at the {@link Position} at the given
     * {@link World}.
     * 
     * @param location
     *            The {@link Location} for searching {@link Zone}s
     * @param includeDeleted
     *            Whenever deleted zones should be included
     * @return A {@link List} contains all found {@link Zone}s
     */
    public static List<Zone> getZones(Location location, boolean includeDeleted) {
        return ZonesLib.getControl().getZones(location.getWorld(),
                new Vector(location.getX(), location.getY(), location.getZ()), includeDeleted);
    }

    /**
     * Searches for all existing {@link Zone}s colliding with the given
     * {@link Zone}.
     * 
     * @param zone
     * @return A {@link List} contains all colliding {@link Zone}s
     */
    public static List<Zone> getZones(Zone zone) {
        return Zone.getZones(zone, false);
    }

    /**
     * Searches all {@link Zone}s colliding with the given {@link Zone}.
     * 
     * @param zone
     * @param includeDeleted
     *            Whenever deleted zones should be included
     * @return A {@link List} contains all colliding {@link Zone}s
     */
    public static List<Zone> getZones(Zone zone, boolean includeDeleted) {
        return ZonesLib.getControl().getZones(zone, includeDeleted);
    }

    /**
     * Creates an new {@link Zone}'s Class with the {@link ZoneType} specified as
     * the last argument<br />
     * 
     * @param player
     *            The {@link OfflinePlayer} or {@link Player} that created the zone
     * @param positions
     *            A List of all needed Positions to create the Zone. The minimum
     *            needed Points and maximum allowed points are defined in
     *            {@link ZoneType}
     * @param fullHeight
     *            Defines if the new zone should be over the full Y coordinates or
     *            not
     * @param zoneType
     *            The Type of this Zone. {@link ZoneType#CUBOID},
     *            {@link ZoneType#CYLINDER}, {@link ZoneType#SPHERE} or
     *            {@link ZoneType#POLYGON}.
     * @return
     */
    public static Zone create(OfflinePlayer player, List<Location> positions, boolean fullHeight, ZoneType zoneType) {
        return ZonesLib.getControl().add(player, positions, fullHeight, zoneType, false);
    }

    /**
     * Creates a real existing {@link Zone} out of an temporary Zone.
     * 
     * @param zone
     *            The temporary Zone
     * @return The new created Zone or null when an error occured
     */
    public static Zone create(Zone zone) {
        return ZonesLib.getControl().add(zone);
    }

    /**
     * Creates an new TEST {@link Zone}'s Class with the {@link ZoneType} specified
     * as the last argument<br />
     * 
     * @param player
     *            The {@link OfflinePlayer} or {@link Player} that created the zone
     * @param positions
     *            A List of all needed Positions to create the Zone. The minimum
     *            needed Points and maximum allowed points are defined in
     *            {@link ZoneType}
     * @param fullHeight
     *            Defines if the new zone should be over the full Y coordinates or
     *            not
     * @param zoneType
     *            The Type of this Zone. {@link ZoneType#CUBOID},
     *            {@link ZoneType#CYLINDER}, {@link ZoneType#SPHERE} or
     *            {@link ZoneType#POLYGON}.
     * @return
     */
    public static Zone test(OfflinePlayer player, List<Location> positions, boolean fullHeight, ZoneType zoneType) {
        return ZonesLib.getControl().add(player, positions, fullHeight, zoneType, true);
    }

    /**
     * Get's the internal unique ID of this {@link Zone} class.
     * 
     * @return Unique Zone ID
     */
    public int getId() {
        return this.zoneId;
    }

    /**
     * Get the {@link ZoneType} of this {@link Zone} class<br />
     * Can be {@link ZoneType#CUBOID}, {@link ZoneType#CYLINDER} or
     * {@link ZoneType#SPHERE}
     * 
     * @return The {@link ZoneType} of this {@link Zone}
     */
    public ZoneType getType() {
        return ZoneType.byClass(this.getClass());
    }

    /**
     * Get the {@link PlayerReference} that created this {@link Zone} class.
     * 
     * @return The OfflinePlayer or null when created by the Server itself
     */
    public OfflinePlayer getCreator() {
        if (this.creator == null)
            return null;

        OfflinePlayer player = Bukkit.getPlayer(this.creator);
        if (player == null)
            player = Bukkit.getOfflinePlayer(this.creator);

        return player;
    }

    public Timestamp getCreated() {
        return this.created;
    }

    public String getWorldName() {
        return this.world;
    }

    public World getWorld() {
        return Bukkit.getWorld(this.world);
    }

    public Location getMinimum() {
        return this.getBoundingBox().getMinimum().toLocation(this.getWorld());
    }

    public Location getMaximum() {
        return this.getBoundingBox().getMaximum().toLocation(this.getWorld());
    }

    public abstract double getArea();

    public abstract double getVolume();

    /**
     * Creates an {@link Area} Object contains the Zone as the specified form.
     * 
     * @return
     */
    public abstract Area asArea();

    /**
     * Get the Cuboid Sized {@link ZoneBoundingBox} of this {@link Zone}.
     * 
     * @return ZoneBoundingBox of this Zone
     */
    public abstract ZoneBoundingBox getBoundingBox();

    public ZoneFilter getFilter() {
        return new ZoneFilter(this);
    }

    /**
     * Checks if this {@link Zone} class intersects with the given {@link Location}.
     * 
     * @param location
     *            Location for intersect check
     * @return true when colliding or false when not colliding
     */
    public boolean intersect(Location location) {
        return this.intersect(location.getWorld(), location.toVector());
    }

    /**
     * Checks if this {@link Zone} class intersects with the given {@link World} and
     * {@link ZoneVector}.
     * 
     * @param world
     *            The World of the Position
     * @param position
     *            The Position to check
     * @return true when colliding or false when not colliding
     */
    public abstract boolean intersect(World world, Vector position);

    /**
     * Checks if this {@link Zone} class intersects with the given Zone class.
     * 
     * @param zone
     *            The Zone to check if colliding
     * @return true when colliding or false when not colliding
     */
    public abstract boolean intersect(Zone zone);

    /**
     * Checks if this {@link Zone} class fully contains the given Zone class.
     * 
     * @param zone
     *            The Zone to check
     * @return true when fully contains the given Zone or false when not
     */
    public abstract boolean contains(Zone zone);

    /**
     * Gets a {@link List} of all {@link Block}s inside this {@link Zone} class.
     * 
     * @return A {@link List} contains all {@link Block}s
     */
    public List<Block> getBlocks() {
        return this.getBlocks(this.getMinimum().getBlockY(), this.getMaximum().getBlockY());
    }

    public abstract List<Block> getBlocks(int min, int max);

    public abstract Block getBlock(int x, int y, int z);

    /**
     * Gets a {@link List} of all {@link ChunkPosition}s inside this {@link Zone}
     * class.
     * 
     * @return A {@link List} contains all {@link ChunkPosition}s
     */
    public abstract List<ChunkPosition> getChunks();

    /**
     * Gets a {@link List} with all Entities with the given class type inside this
     * zone
     * 
     * @param entities
     * @return A {@link List} contains all {@link Entity} elements inside this zone
     */
    public List<Entity> getEntities(@SuppressWarnings("unchecked") Class<? extends Entity>... entities) {
        Collection<Entity> classEntities = this.getWorld().getEntitiesByClasses(entities);
        List<Entity> entityList = new ArrayList<Entity>();

        for (Entity entity : classEntities) {
            if (this.intersect(entity.getLocation()))
                entityList.add(entity);
        }

        return entityList;
    }

    public boolean isTemporary() {
        return this.zoneId == -1;
    }

    public boolean isDeleted() {
        return this.deleted;
    }

    public static boolean isBetween(double point, double a, double b) {
        if ((point >= a && point <= b) || (point <= a && point >= b))
            return true;
        return false;
    }

    /**
     * Calls an new ZoneInfoEvent to fetch specific Plugin related Informations to
     * this Zone Object.<br />
     * The Plugin must listen to this hook
     * 
     * @return
     */
    public ZoneInfoEvent getZoneHook() {
        ZoneInfoEvent event = new ZoneInfoEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        return event;
    }

    @SuppressWarnings("deprecation")
    private boolean update() {
        if (this.isTemporary())
            return false;

        try {
            DatabaseHandler.getHandler().update(ZoneSchema.toSchema(this));
        } catch (Exception exc) {
            exc.printStackTrace();
        }

        return false;
    }

    public boolean delete() {
        this.deleted = true;

        if (this.update()) {
            return true;
        } else {
            this.deleted = false;
            return false;
        }
    }
}