package net.dertod2.ZonesLib.Classes;

import java.awt.Polygon;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import com.google.common.collect.ImmutableList;

import net.dertod2.ZonesLib.Util.ChunkPosition;

public class PolygonZone extends Zone implements HeightZone {
    private List<Vector> polygonList = new ArrayList<Vector>();

    private Vector minimum;
    private Vector maximum;

    private boolean fullHeight;

    private double heightY;
    private double height;

    // Cache
    private Area cachedArea;

    public PolygonZone(int zoneId, UUID creator, Timestamp created, boolean deleted, String world, boolean fullHeight,
            double heightY, double height, List<Double> polysX, List<Double> polysZ) {
        super(zoneId, creator, created, deleted, world);

        this.fullHeight = fullHeight;

        this.heightY = heightY;
        this.height = height;

        for (int i = 0; i < polysX.size(); i++) {
            this.polygonList.add(new Vector(polysX.get(i), 0, polysZ.get(i)));
        }

        int minX = this.polygonList.get(0).getBlockX();
        int minZ = this.polygonList.get(0).getBlockZ();
        int maxX = minX;
        int maxZ = minZ;

        for (Vector vector : this.polygonList) {
            int x = vector.getBlockX();
            int z = vector.getBlockZ();

            if (x < minX)
                minX = x;
            if (z < minZ)
                minZ = z;

            if (x > maxX)
                maxX = x;
            if (z > maxZ)
                maxZ = z;
        }

        this.minimum = new Vector(minX, this.fullHeight ? 0 : heightY - height, minZ);
        this.maximum = new Vector(maxX, this.fullHeight ? 256 : heightY + height, maxZ);
    }

    public List<Double> getPolysX() {
        List<Double> polyList = new ArrayList<Double>();
        for (Vector vector : this.polygonList)
            polyList.add(vector.getX());

        return polyList;
    }

    public List<Double> getPolysZ() {
        List<Double> polyList = new ArrayList<Double>();
        for (Vector vector : this.polygonList)
            polyList.add(vector.getZ());

        return polyList;
    }

    public int getPolygonCount() {
        return this.polygonList.size();
    }

    public List<Vector> getPolygons() {
        return ImmutableList.<Vector>copyOf(this.polygonList);
    }

    public double getY() {
        return this.heightY;
    }

    public double getHeight() {
        return this.fullHeight ? 256 : this.height * 2;
    }

    public double getPlainHeight() {
        return this.height;
    }

    public boolean isFullHeight() {
        return this.fullHeight;
    }

    public double getArea() {
        int num = this.polygonList.size();

        double[] polysX = new double[num];
        double[] polysZ = new double[num];

        int current = 0;
        for (Vector vector : this.polygonList) {
            polysX[current] = vector.getBlockX();
            polysZ[current++] = vector.getBlockZ();
        }

        double area = 0;
        int x, z;

        for (z = 0; z < num; z++) {
            x = (z + 1) % num;

            area += polysX[z] * polysZ[x];

            area -= polysX[x] * polysZ[z];
        }

        area /= 2.0;
        return Math.abs(area);
    }

    public double getVolume() {
        return this.getArea() * this.getHeight();
    }

    public Area asArea() {
        if (this.cachedArea != null)
            return this.cachedArea;

        int num = this.polygonList.size();

        int[] polysX = new int[num];
        int[] polysZ = new int[num];

        int current = 0;
        for (Vector vector : this.polygonList) {
            polysX[current] = vector.getBlockX();
            polysZ[current++] = vector.getBlockZ();
        }

        this.cachedArea = new Area(new Polygon(polysX, polysZ, num));
        return this.cachedArea;
    }

    public ZoneBoundingBox getBoundingBox() {
        Vector center = this.minimum.getMidpoint(this.maximum);

        return new ZoneBoundingBox(center.getX(), this.isFullHeight() ? 128 : center.getY(), center.getZ(),
                this.maximum.getX() - center.getX(), this.isFullHeight() ? 128 : this.maximum.getY() - center.getY(),
                this.maximum.getZ() - center.getZ());
    }

    public Location getMinimum() {
        return this.minimum.toLocation(this.getWorld());
    }

    public Location getMaximum() {
        return this.maximum.toLocation(this.getWorld());
    }

    public boolean intersect(World world, Vector position) {
        if (!this.world.equals(world.getName()))
            return false;

        Area intersectArea = this.asArea();
        return intersectArea.contains(new Point2D.Double(position.getBlockX(), position.getBlockZ()));
    }

    public boolean intersect(Zone zone) { // Relatively easy - use this at any position?
        if (!this.world.equals(zone.world))
            return false;

        if (!this.isFullHeight() || !(zone instanceof HeightZone)
                || (zone instanceof HeightZone && !((HeightZone) zone).isFullHeight())) {
            if (this.getMinimum().getY() < zone.getMinimum().getY()
                    && this.getMaximum().getY() < zone.getMinimum().getY())
                return false;
            if (this.getMinimum().getY() > zone.getMaximum().getY()
                    && this.getMaximum().getY() > zone.getMaximum().getY())
                return false;
        }

        Area intersectArea = this.asArea();
        intersectArea.intersect(zone.asArea());

        return !intersectArea.isEmpty();
    }

    public boolean contains(Zone zone) {
        if (!this.world.equals(zone.world))
            return false;

        if (!this.isFullHeight() || !(zone instanceof HeightZone)
                || (zone instanceof HeightZone && !((HeightZone) zone).isFullHeight())) {
            if (!Zone.isBetween(this.getMinimum().getY(), zone.getMinimum().getY(), zone.getMaximum().getY()))
                return false;
            if (!Zone.isBetween(this.getMaximum().getY(), zone.getMinimum().getY(), zone.getMaximum().getY()))
                return false;
        }

        Area intersectArea = this.asArea();
        intersectArea.intersect(zone.asArea());

        return intersectArea.equals(zone.asArea());
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