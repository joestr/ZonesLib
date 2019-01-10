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

public class CylinderZone extends OriginZone implements HeightZone, RoundedZone {
    private double radius;

    private boolean fullHeight;
    private double height;

    public CylinderZone(int zoneId, UUID creator, Timestamp created, boolean deleted, String world, Vector position,
            boolean fullHeight, double height, double radius) {
        super(zoneId, creator, created, deleted, world, position);

        this.radius = radius;

        this.fullHeight = fullHeight;
        this.height = height;
    }

    public double getRadius() {
        return this.radius;
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
        return new ZoneBoundingBox(this.getX() - this.radius, this.isFullHeight() ? 128 : this.getY() - this.height,
                this.getZ() - this.radius, this.radius * 2, this.isFullHeight() ? 128 : this.height * 2,
                this.radius * 2);
    }

    public double getArea() {
        return Math.PI * Math.pow(this.radius, 2);
    }

    public double getVolume() {
        return this.getArea() * (this.height * 2);
    }

    public Area asArea() {
        Location min = this.getMinimum();
        Location max = this.getMaximum();

        return new Area(new Ellipse2D.Double(min.getX(), min.getZ(), max.getX() - min.getX(), max.getZ() - min.getZ()));
    }

    public boolean intersect(World world, Vector position) {
        if (!this.world.equals(world.getName()))
            return false;

        Vector distanceLocation = position.clone();
        distanceLocation.setY(this.getCenter().getY());

        double distance = this.getCenter().distance(distanceLocation);
        if (distance > this.getRadius())
            return false;

        if (this.fullHeight
                || Zone.isBetween(position.getBlockY(), this.getY() - this.height, this.getY() + this.height))
            return true;
        return false;
    }

    public boolean intersect(Zone zone) {
        if (!this.world.equals(zone.world))
            return false;

        if (zone instanceof CylinderZone) {
            CylinderZone cylinderZone = (CylinderZone) zone;

            if (this.getCenter().distance(cylinderZone.getCenter()) > (this.getRadius() + cylinderZone.getRadius()))
                return false;

            if (this.fullHeight || cylinderZone.fullHeight)
                return true;

            if (Math.abs(cylinderZone.getY() - this.getY()) > (cylinderZone.height * 2))
                return false;

            return true;
        } else if (zone instanceof CuboidZone || zone instanceof SphereZone) {
            return zone.intersect(this);
        } else if (zone instanceof PolygonZone) {
            if (this.getMinimum().getY() < zone.getMinimum().getY()
                    && this.getMaximum().getY() < zone.getMinimum().getY())
                return false;
            if (this.getMinimum().getY() > zone.getMaximum().getY()
                    && this.getMaximum().getY() > zone.getMaximum().getY())
                return false;

            Area intersectArea = this.asArea();
            intersectArea.intersect(zone.asArea());

            return !intersectArea.isEmpty();
        }

        return false;
    }

    public boolean contains(Zone zone) {
        if (!this.world.equals(zone.world))
            return false;

        if (zone instanceof CuboidZone) {
            Vector[] corners = zone.getBoundingBox().getCorners();

            for (int i = 0; i < corners.length; i++) {
                if (!this.intersect(zone.getWorld(), corners[i]))
                    return false;
            }

            return true;
        } else if (zone instanceof SphereZone) {
            SphereZone sphereZone = (SphereZone) zone;

            if (this.getCenter().getY() + this.getHeight() >= sphereZone.getCenter().getY() + sphereZone.getRadius()
                    && this.getCenter().getY() - this.getHeight() <= sphereZone.getCenter().getY()
                            - sphereZone.getRadius()) {

                Vector cylCenter = this.getCenter().clone().setY(0);
                Vector sphCenter = sphereZone.getCenter().clone().setY(0);

                return cylCenter.distance(sphCenter) <= (this.getRadius() - sphereZone.getRadius());
            }

            return false;
        } else if (zone instanceof CylinderZone) {
            CylinderZone cylinderZone = (CylinderZone) zone;

            if (this.getCenter().getY() + this.getHeight() >= cylinderZone.getCenter().getY() + cylinderZone.getHeight()
                    && this.getCenter().getY() - this.getHeight() <= cylinderZone.getCenter().getY()
                            - cylinderZone.getHeight()) {

                Vector cylCenter = cylinderZone.getCenter().clone();
                cylCenter.setY(this.getCenter().getY());

                return this.getCenter().distance(cylCenter) <= (this.getRadius() - cylinderZone.getRadius());
            }

            return false;
        } else if (zone instanceof PolygonZone) {
            if (this.getMinimum().getY() < zone.getMinimum().getY()
                    && this.getMaximum().getY() < zone.getMinimum().getY())
                return false;
            if (this.getMinimum().getY() > zone.getMaximum().getY()
                    && this.getMaximum().getY() > zone.getMaximum().getY())
                return false;

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