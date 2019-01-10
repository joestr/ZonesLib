package net.dertod2.ZonesLib.Classes;

public enum ZoneType {
    /**
     * A cuboid is a convex polyhedron bounded by six quadrilateral faces
     */
    CUBOID(1, "Cuboid", CuboidZone.class, 2, 2),
    /**
     * A cylinder is defined as any ruled surface spanned by a one-parameter family
     * of parallel lines
     */
    CYLINDER(2, "Cylinder", CylinderZone.class, 2, 2),
    /**
     * A sphere is a perfectly round geometrical and circular object in
     * three-dimensional space that resembles the shape of a completely round ball.
     */
    SPHERE(3, "Sphere", SphereZone.class, 2, 2),

    /**
     * TODO
     */
    POLYGON(4, "Polygon", PolygonZone.class, 3, -1);

    private int typeId;
    private String typeName;
    private Class<? extends Zone> typeClass;

    private int minimumPoints;
    private int maximumPoints;

    private static ZoneType[] values = ZoneType.values();

    private ZoneType(int typeId, String typeName, Class<? extends Zone> typeClass, int minimumPoins,
            int maximumPoints) {
        this.typeId = typeId;
        this.typeName = typeName;
        this.typeClass = typeClass;

        this.minimumPoints = minimumPoins;
        this.maximumPoints = maximumPoints;
    }

    public int getId() {
        return this.typeId;
    }

    public String getName() {
        return this.typeName;
    }

    public Class<? extends Zone> getTypeClass() {
        return this.typeClass;
    }

    public int getMinimum() {
        return this.minimumPoints;
    }

    public int getMaximum() {
        return this.maximumPoints;
    }

    public static ZoneType byId(int typeId) {
        for (ZoneType zoneType : values) {
            if (zoneType.typeId == typeId) {
                return zoneType;
            }
        }

        return null;
    }

    public static ZoneType byName(String typeName) {
        for (ZoneType zoneType : values) {
            if (zoneType.typeName.equalsIgnoreCase(typeName)) {
                return zoneType;
            }
        }

        return null;
    }

    public static ZoneType byClass(Class<? extends Zone> typeClass) {
        for (ZoneType zoneType : values) {
            if (zoneType.typeClass != null && zoneType.typeClass.isAssignableFrom(typeClass)) {
                return zoneType;
            }
        }

        return null;
    }
}