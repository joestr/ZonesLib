package net.dertod2.ZonesLib.Classes;

import org.bukkit.util.Vector;

public class ZoneBoundingBox {
	private final double x;
	private final double y;
	private final double z;
	
	private final double width;
	private final double height;
	private final double length;
	
	public ZoneBoundingBox(double x, double y, double z, double length, double height, double width) {
		this.x = x;
		this.y = y;
		this.z = z;
		
		this.length = length;
		this.width = width; 
		this.height = height;
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
	
	public double getZ() {
		return this.z;
	}
	
	public double getWidth() {
		return this.width;
	}
	
	public double getHeight() {
		return this.height;
	}
	
	public double getLength() {
		return this.length;
	}
	
	public Vector getMinimum() {
		return new Vector(this.getX() - this.length, this.y - this.height, this.getZ() - this.width);
	}
	
	public Vector getMaximum() {
		return new Vector(this.getX() + this.length, this.y + this.height, this.getZ() + this.width);
	}
	
	/**
	 * Returns an array with all eight corners of this BoundingBox
	 * @return
	 */
	public Vector[] getCorners() {
		Vector[] positions = new Vector[8];
		
		positions[0] = new Vector(this.x - this.length, this.y - this.height, this.z - this.width);
		positions[1] = new Vector(this.x - this.length, this.y - this.height, this.z + this.width);
		positions[2] = new Vector(this.x - this.length, this.y + this.height, this.z - this.width);
		positions[3] = new Vector(this.x - this.length, this.y + this.height, this.z + this.width);
		positions[4] = new Vector(this.x + this.length, this.y - this.height, this.z - this.width);
		positions[5] = new Vector(this.x + this.length, this.y - this.height, this.z + this.width);
		positions[6] = new Vector(this.x + this.length, this.y + this.height, this.z - this.width);
		positions[7] = new Vector(this.x + this.length, this.y + this.height, this.z + this.width);
		
		return positions;
	}
	
	/**
	 * Returns all twelve lines of this BoundingBox
	 * @return
	 */
	public ZoneLine[] getCuboidLines() {
		ZoneLine[] zoneLines = new ZoneLine[12];
		
		zoneLines[0] = new ZoneLine(new Vector(this.x - this.length, this.y - this.height, this.z - this.width), new Vector(this.length * 2, 0, 0));
		zoneLines[1] = new ZoneLine(new Vector(this.x - this.length, this.y - this.height, this.z - this.width), new Vector(0, this.height * 2, 0));
		zoneLines[2] = new ZoneLine(new Vector(this.x - this.length, this.y - this.height, this.z - this.width), new Vector(0, 0, this.width * 2));
		
		zoneLines[3] = new ZoneLine(new Vector(this.x - this.length, this.y + this.height, this.z + this.width), new Vector(this.length * 2, 0, 0));
		zoneLines[4] = new ZoneLine(new Vector(this.x - this.length, this.y + this.height, this.z + this.width), new Vector(0, -this.height * 2, 0));
		zoneLines[5] = new ZoneLine(new Vector(this.x - this.length, this.y + this.height, this.z + this.width), new Vector(0, 0, -this.width * 2));
		
		zoneLines[6] = new ZoneLine(new Vector(this.x + this.length, this.y + this.height, this.z - this.width), new Vector(-this.length * 2, 0, 0));
		zoneLines[7] = new ZoneLine(new Vector(this.x + this.length, this.y + this.height, this.z - this.width), new Vector(0, -this.height * 2, 0));
		zoneLines[8] = new ZoneLine(new Vector(this.x + this.length, this.y + this.height, this.z - this.width), new Vector(0, 0, this.width * 2));
		
		zoneLines[9]  = new ZoneLine(new Vector(this.x + this.length, this.y - this.height, this.z + this.width), new Vector(-this.length * 2, 0, 0));
		zoneLines[10] = new ZoneLine(new Vector(this.x + this.length, this.y - this.height, this.z + this.width), new Vector(0, this.height * 2, 0));
		zoneLines[11] = new ZoneLine(new Vector(this.x + this.length, this.y - this.height, this.z + this.width), new Vector(0, 0, -this.width * 2));
		
		return zoneLines;
	}
	
	/**
	 * Returns the upper and lower lines of the BoundingBox square
	 * @return
	 */
	public ZoneLine[] getSquareLines() {
		ZoneLine[] zoneLines = new ZoneLine[8];
		
		zoneLines[0] = new ZoneLine(new Vector(this.x - this.length, 0, this.z - this.width), new Vector(this.length * 2, 0, 0));
		zoneLines[1] = new ZoneLine(new Vector(this.x - this.length, 0, this.z - this.width), new Vector(0, 0, this.width * 2));
		
		zoneLines[2] = new ZoneLine(new Vector(this.x - this.length, 0, this.z + this.width), new Vector(this.length * 2, 0, 0));
		zoneLines[3] = new ZoneLine(new Vector(this.x - this.length, 0, this.z + this.width), new Vector(0, 0, -this.width * 2));
		
		zoneLines[4] = new ZoneLine(new Vector(this.x + this.length, 0, this.z - this.width), new Vector(-this.length * 2, 0, 0));
		zoneLines[5] = new ZoneLine(new Vector(this.x + this.length, 0, this.z - this.width), new Vector(0, 0, this.width * 2));
		
		zoneLines[6] = new ZoneLine(new Vector(this.x + this.length, 0, this.z + this.width), new Vector(-this.length * 2, 0, 0));
		zoneLines[7] = new ZoneLine(new Vector(this.x + this.length, 0, this.z + this.width), new Vector(0, 0, -this.width * 2));
		
		return zoneLines;
	}
	
	public boolean contains(ZoneBoundingBox boundingBox) {
		double thisXLow = this.x - this.length;
		double thisXHigh = this.x + this.length;
		double thisYLow = this.y - this.height;
		double thisYHigh = this.y + this.height;
		double thisZLow = this.z - this.width;
		double thisZHigh = this.z + this.width;
		
		double themXLow = boundingBox.x - boundingBox.length;
		double themXHigh = boundingBox.x + boundingBox.length;
		double themYLow = boundingBox.y - boundingBox.height;
		double themYHigh = boundingBox.y + boundingBox.height;
		double themZLow = boundingBox.z - boundingBox.width;
		double themZHigh = boundingBox.z + boundingBox.width;
		
		if (Zone.isBetween(themXLow, thisXLow, thisXHigh) && Zone.isBetween(themXHigh, thisXLow, thisXHigh)) {
			if (Zone.isBetween(themZLow, thisZLow, thisZHigh) && Zone.isBetween(themZHigh, thisZLow, thisZHigh)) {
				if (Zone.isBetween(themYLow, thisYLow, thisYHigh) && Zone.isBetween(themYHigh, thisYLow, thisYHigh)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean intersect(ZoneBoundingBox boundingBox) {
		double thisXLow = this.x - this.length;
		double thisXHigh = this.x + this.length;
		double thisYLow = this.y - this.height;
		double thisYHigh = this.y + this.height;
		double thisZLow = this.z - this.width;
		double thisZHigh = this.z + this.width;
		
		double themXLow = boundingBox.x - boundingBox.length;
		double themXHigh = boundingBox.x + boundingBox.length;
		double themYLow = boundingBox.y - boundingBox.height;
		double themYHigh = boundingBox.y + boundingBox.height;
		double themZLow = boundingBox.z - boundingBox.width;
		double themZHigh = boundingBox.z + boundingBox.width;
		
        if (themXLow > thisXHigh) return false;
        if (themYLow > thisYHigh) return false;
        if (themZLow > thisZHigh) return false;
        if (themXHigh < thisXLow) return false;
        if (themYHigh < thisYLow) return false;
        if (themZHigh < thisZLow) return false;
        
        return true;
	}
	
	public boolean intersect(Vector vector) {
		return vector.isInAABB(this.getMinimum(), this.getMaximum());
	}
}