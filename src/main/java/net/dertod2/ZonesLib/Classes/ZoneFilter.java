package net.dertod2.ZonesLib.Classes;

import java.util.ArrayList;
import java.util.List;

import net.dertod2.ZonesLib.Util.AdvancedVector;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class ZoneFilter {
    private Zone zone;

    private final double horizontalGap = 1.0;
    private final double verticalGap = 1.0;

    protected ZoneFilter(Zone zone) {
        this.zone = zone;
    }

    public List<Location> getOuterLine() {
        List<AdvancedVector> positionList = new ArrayList<AdvancedVector>();

        Vector minimum = this.zone.getMinimum().toVector();
        Vector maximum = this.zone.getMaximum().toVector().add(new Vector(1, 0, 1));

        switch (this.zone.getType()) {
        case CUBOID: {
            List<AdvancedVector> bottomCorners = new ArrayList<AdvancedVector>();

            bottomCorners.add(new AdvancedVector(minimum.getX(), minimum.getY(), minimum.getZ()));
            bottomCorners.add(new AdvancedVector(maximum.getX(), minimum.getY(), minimum.getZ()));
            bottomCorners.add(new AdvancedVector(maximum.getX(), minimum.getY(), maximum.getZ()));
            bottomCorners.add(new AdvancedVector(minimum.getX(), minimum.getY(), maximum.getZ()));

            for (int i = 0; i < bottomCorners.size(); i++) {
                AdvancedVector down1 = new AdvancedVector(bottomCorners.get(i));
                AdvancedVector down2 = new AdvancedVector(
                        ((i + 1) < bottomCorners.size()) ? bottomCorners.get(i + 1) : bottomCorners.get(0));

                AdvancedVector up1 = down1.setY(maximum.getY());
                AdvancedVector up2 = down2.setY(maximum.getY());

                positionList.addAll(this.getDottedLine(down1, down2));
                positionList.addAll(this.getDottedLine(up1, up2));
                positionList.addAll(this.getDottedLine(down1, up1));

                for (double offset = this.verticalGap; offset < maximum.getY(); offset += this.verticalGap) {
                    AdvancedVector mid1 = down1.add(0, offset, 0);
                    AdvancedVector mid2 = down2.add(0, offset, 0);

                    positionList.addAll(this.getDottedLine(mid1, mid2));
                }
            }

            break;
        }
        case CYLINDER: {
            CylinderZone cylinderZone = (CylinderZone) this.zone;

            AdvancedVector center = new AdvancedVector(cylinderZone.getX() + 0.5D, minimum.getY(),
                    cylinderZone.getZ() + 0.5D);
            double radius = cylinderZone.getRadius() + 0.5D;

            List<AdvancedVector> bottomCorners = this.getDottedEllipse(center, new AdvancedVector(radius, 0, radius));
            positionList.addAll(bottomCorners);

            for (AdvancedVector vector : bottomCorners) {
                positionList.add(vector.setY(maximum.getY()));
            }

            AdvancedVector p1 = new AdvancedVector((maximum.getX() + minimum.getX()) / 2.0D, minimum.getY(),
                    minimum.getZ());
            AdvancedVector p2 = new AdvancedVector((maximum.getX() + minimum.getX()) / 2.0D, minimum.getY(),
                    maximum.getZ());
            AdvancedVector p3 = new AdvancedVector(minimum.getX(), minimum.getY(),
                    (maximum.getZ() + minimum.getZ()) / 2.0D);
            AdvancedVector p4 = new AdvancedVector(maximum.getX(), minimum.getY(),
                    (maximum.getZ() + minimum.getZ()) / 2.0D);

            positionList.addAll(this.getDottedLine(p1, p1.setY(maximum.getY())));
            positionList.addAll(this.getDottedLine(p2, p2.setY(maximum.getY())));
            positionList.addAll(this.getDottedLine(p3, p3.setY(maximum.getY())));
            positionList.addAll(this.getDottedLine(p4, p4.setY(maximum.getY())));

            for (double offset = this.verticalGap; offset < maximum.getY(); offset += this.verticalGap) {
                for (AdvancedVector vector : bottomCorners) {
                    positionList.add(vector.add(0, offset, 0));
                }
            }

            break;
        }
        case POLYGON: {
            PolygonZone polygonZone = (PolygonZone) this.zone;

            List<AdvancedVector> bottomCorners = new ArrayList<AdvancedVector>();
            for (Vector vector : polygonZone.getPolygons()) {
                bottomCorners.add(new AdvancedVector(vector.getX(), minimum.getY(), vector.getZ()));
            }

            for (int i = 0; i < bottomCorners.size(); i++) {
                AdvancedVector down1 = new AdvancedVector(bottomCorners.get(i));
                AdvancedVector down2 = new AdvancedVector(
                        ((i + 1) < bottomCorners.size()) ? bottomCorners.get(i + 1) : bottomCorners.get(0));

                AdvancedVector up1 = down1.setY(maximum.getY());
                AdvancedVector up2 = down2.setY(maximum.getY());

                positionList.addAll(this.getDottedLine(down1, down2));
                positionList.addAll(this.getDottedLine(up1, up2));
                positionList.addAll(this.getDottedLine(down1, up1));

                for (double offset = this.verticalGap; offset < maximum.getY(); offset += this.verticalGap) {
                    AdvancedVector mid1 = down1.add(0, offset, 0);
                    AdvancedVector mid2 = down2.add(0, offset, 0);

                    positionList.addAll(this.getDottedLine(mid1, mid2));
                }
            }

            break;
        }
        case SPHERE: {
            SphereZone sphereZone = (SphereZone) this.zone;

            AdvancedVector radius = new AdvancedVector(sphereZone.getRadius() + 0.5D, sphereZone.getRadius() + 0.5D,
                    sphereZone.getRadius() + 0.5D);
            AdvancedVector center = new AdvancedVector(sphereZone.getX() + 0.5D, sphereZone.getY() + 0.5D,
                    sphereZone.getZ() + 0.5D);

            positionList.addAll(this.getDottedEllipse(center, new AdvancedVector(0.0D, radius.getY(), radius.getZ())));
            positionList.addAll(this.getDottedEllipse(center, new AdvancedVector(radius.getX(), 0.0D, radius.getZ())));
            positionList.addAll(this.getDottedEllipse(center, new AdvancedVector(radius.getX(), radius.getY(), 0.0D)));

            for (double offset = this.verticalGap; offset < radius.getY(); offset += this.verticalGap) {
                AdvancedVector center1 = new AdvancedVector(center.getX(), center.getY() - offset, center.getZ());
                AdvancedVector center2 = new AdvancedVector(center.getX(), center.getY() + offset, center.getZ());

                double difference = Math.abs(center1.getY() - center.getY());
                double radiusRatio = Math.cos(Math.asin(difference / radius.getY()));

                double rx = radius.getX() * radiusRatio;
                double rz = radius.getZ() * radiusRatio;

                positionList.addAll(this.getDottedEllipse(center1, new AdvancedVector(rx, 0.0D, rz)));
                positionList.addAll(this.getDottedEllipse(center2, new AdvancedVector(rx, 0.0D, rz)));
            }

            break;
        }
        default: {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Tried to fetch OuterLine for undefined ZoneType "
                    + ChatColor.GOLD + this.zone.getType());
            break;
        }
        }

        World world = this.zone.getWorld();

        List<Location> locationList = new ArrayList<Location>(positionList.size());
        for (AdvancedVector vector : positionList) {
            locationList.add(vector.toLocation(world));
        }

        return locationList;
    }

    private List<AdvancedVector> getDottedLine(AdvancedVector pos1, AdvancedVector pos2) {
        List<AdvancedVector> positionList = new ArrayList<AdvancedVector>();

        double length = pos1.distance(pos2);
        int points = (int) (length / this.horizontalGap) + 1;
        double gap = length / (points - 1);

        AdvancedVector gapVector = pos2.subtract(pos1).normalize().multiply(gap);

        for (int i = 0; i < points; i++) {
            AdvancedVector currentPoint = pos1.add(gapVector.multiply(i));
            positionList.add(currentPoint);
        }

        return positionList;
    }

    private List<AdvancedVector> getDottedEllipse(AdvancedVector center, AdvancedVector radius) {
        List<AdvancedVector> positionList = new ArrayList<AdvancedVector>();

        double biggestR = Math.max(radius.getX(), Math.max(radius.getY(), radius.getZ()));
        double circleCircumference = 2.0D * biggestR * Math.PI;
        double deltaTheta = this.horizontalGap / circleCircumference;

        for (double i = 0.0D; i < 1.0D; i += deltaTheta) {
            double x = center.getX();
            double y = center.getY();
            double z = center.getZ();
            if (radius.getX() == 0.0D) {
                y = center.getY() + Math.cos(i * 2.0D * Math.PI) * radius.getY();
                z = center.getZ() + Math.sin(i * 2.0D * Math.PI) * radius.getZ();
            } else if (radius.getY() == 0.0D) {
                x = center.getX() + Math.cos(i * 2.0D * Math.PI) * radius.getX();
                z = center.getZ() + Math.sin(i * 2.0D * Math.PI) * radius.getZ();
            } else if (radius.getZ() == 0.0D) {
                x = center.getX() + Math.cos(i * 2.0D * Math.PI) * radius.getX();
                y = center.getY() + Math.sin(i * 2.0D * Math.PI) * radius.getY();
            }

            positionList.add(new AdvancedVector(x, y, z));
        }

        return positionList;
    }
}