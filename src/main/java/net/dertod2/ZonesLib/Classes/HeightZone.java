package net.dertod2.ZonesLib.Classes;

public interface HeightZone {

	/**
	 * Defines if this Zone goes from the bottom of the map to the top or over a defined height
	 */
	public boolean isFullHeight();
	
	/**
	 * Gets the Height of the Zone. When the Zone is an {@link HeightZone#isFullHeight()} Zone this will return 256.
	 */
	public double getHeight();
	
	/**
	 * Returns the saved height, which means the half-height double value
	 */
	public double getPlainHeight();
}