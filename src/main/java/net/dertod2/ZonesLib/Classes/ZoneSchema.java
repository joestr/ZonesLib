package net.dertod2.ZonesLib.Classes;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.util.Vector;

import net.dertod2.DatabaseHandler.Table.Column;
import net.dertod2.DatabaseHandler.Table.Column.ColumnType;
import net.dertod2.DatabaseHandler.Table.Column.DataType;
import net.dertod2.DatabaseHandler.Table.Column.EntryType;
import net.dertod2.DatabaseHandler.Table.TableEntry;
import net.dertod2.DatabaseHandler.Table.TableInfo;

@TableInfo(tableName = "zones")
public class ZoneSchema extends TableEntry {
    @Column(columnName = "id", dataType = DataType.Integer, columnType = ColumnType.Primary, order = 1)
    public int id;

    @Column(columnName = "creator", dataType = DataType.String, order = 2)
    public String creator;
    @Column(columnName = "created", dataType = DataType.Timestamp, order = 3)
    public Timestamp created;

    @Column(columnName = "world", dataType = DataType.String, order = 4)
    public String world;
    @Column(columnName = "x", dataType = DataType.Double, order = 5)
    public double centerX;
    @Column(columnName = "y", dataType = DataType.Double, order = 6)
    public double centerY;
    @Column(columnName = "z", dataType = DataType.Double, order = 7)
    public double centerZ;

    @Column(columnName = "type_id", dataType = DataType.Integer, order = 8)
    public int type;

    @Column(columnName = "fullheight", dataType = DataType.Boolean, order = 9)
    public boolean fullHeight;
    @Column(columnName = "height", dataType = DataType.Double, order = 10)
    public double height;

    @Column(columnName = "length", dataType = DataType.Double, order = 11)
    public double length;
    @Column(columnName = "width", dataType = DataType.Double, order = 12)
    public double width;

    @Column(columnName = "radius", dataType = DataType.Double, order = 13)
    public double radius;

    @Column(columnName = "polys_x", dataType = DataType.Double, entryType = EntryType.List, order = 14)
    public List<Double> polysX;
    @Column(columnName = "polys_z", dataType = DataType.Double, entryType = EntryType.List, order = 15)
    public List<Double> polysZ;

    @Column(columnName = "deleted", dataType = DataType.Boolean, order = 16)
    public boolean deleted;

    public ZoneSchema() {
    }

    public ZoneSchema getInstance() {
        return new ZoneSchema();
    }

    public static ZoneSchema toSchema(Zone zone) {
        ZoneSchema zoneSchema = new ZoneSchema();

        zoneSchema.id = zone.zoneId;

        zoneSchema.creator = zone.creator.toString();
        zoneSchema.created = zone.getCreated();

        zoneSchema.world = zone.world;
        zoneSchema.centerX = zone instanceof OriginZone ? ((OriginZone) zone).getX() : 0;
        zoneSchema.centerY = zone instanceof OriginZone ? ((OriginZone) zone).getY()
                : zone instanceof PolygonZone ? ((PolygonZone) zone).getY() : 0;
        zoneSchema.centerZ = zone instanceof OriginZone ? ((OriginZone) zone).getZ() : 0;

        zoneSchema.type = zone.getType().getId();

        zoneSchema.fullHeight = zone instanceof HeightZone ? ((HeightZone) zone).isFullHeight() : false;
        zoneSchema.height = zone instanceof HeightZone ? ((HeightZone) zone).getPlainHeight() : 0;

        zoneSchema.length = zone instanceof CuboidZone ? ((CuboidZone) zone).length : 0;
        zoneSchema.width = zone instanceof CuboidZone ? ((CuboidZone) zone).width : 0;

        zoneSchema.radius = zone instanceof RoundedZone ? ((RoundedZone) zone).getRadius() : 0;

        zoneSchema.polysX = zone instanceof PolygonZone ? ((PolygonZone) zone).getPolysX() : new ArrayList<Double>();
        zoneSchema.polysZ = zone instanceof PolygonZone ? ((PolygonZone) zone).getPolysZ() : new ArrayList<Double>();

        zoneSchema.deleted = zone.isDeleted();

        zoneSchema.isLoadedEntry = true;
        return zoneSchema;
    }

    public Zone toZone() {
        if (this.id <= 0)
            return null;

        ZoneType zoneType = ZoneType.byId(this.type);
        Vector position = new Vector(this.centerX, this.centerY, this.centerZ);

        switch (zoneType) {
        case CUBOID:
            return new CuboidZone(this.id, UUID.fromString(this.creator), this.created, this.deleted, this.world,
                    position, this.fullHeight, this.height, this.width, this.length);
        case CYLINDER:
            return new CylinderZone(this.id, UUID.fromString(this.creator), this.created, this.deleted, this.world,
                    position, this.fullHeight, this.height, this.radius);
        case SPHERE:
            return new SphereZone(this.id, UUID.fromString(this.creator), this.created, this.deleted, this.world,
                    position, this.radius);
        case POLYGON:
            return new PolygonZone(this.id, UUID.fromString(this.creator), this.created, this.deleted, this.world,
                    this.fullHeight, this.centerY, this.height, this.polysX, this.polysZ);
        }

        return null;
    }
}