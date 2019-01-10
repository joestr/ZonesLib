package net.dertod2.ZonesLib.Classes;

import org.bukkit.util.Vector;

public class ZoneLine {
    private Vector lineFoot;
    private Vector lineDirection;

    public ZoneLine(Vector lineFoot, Vector lineDirection) {
        this.lineFoot = lineFoot;
        this.lineDirection = lineDirection;
    }

    public Vector getFoot() {
        return this.lineFoot;
    }

    public Vector getDirection() {
        return this.lineDirection;
    }

    public void setFoot(Vector lineFoot) {
        this.lineFoot = lineFoot;
    }

    public void setDirection(Vector lineDirection) {
        this.lineDirection = lineDirection;
    }

    public Vector getPoint(double multiplier) {
        Vector lineFoot = this.lineFoot.clone();
        Vector lineDirection = this.lineDirection.clone();

        lineDirection.multiply(multiplier);
        lineFoot.add(lineDirection);

        return lineFoot;
    }

    public Vector getOpposite(Vector position) {
        return this.getFootPointOrCorner(new Vector(position.getX(), position.getY(), position.getZ()));
    }

    public Vector getFootPointOrCorner(Vector point) {
        double pointCoefficient = this.getCoefficient(point);
        return (pointCoefficient < 0 ? this.lineFoot
                : (pointCoefficient > 1 ? this.getPoint(1) : this.getPoint(pointCoefficient)));
    }

    private double getCoefficient(Vector point) {
        Vector position = point.clone();
        Vector lineFoot = this.lineFoot.clone();
        Vector lineDirection = this.lineDirection.clone();

        return (position.dot(lineDirection) - lineFoot.dot(lineDirection)) / lineDirection.dot(lineDirection);
    }
}