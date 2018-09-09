package net.dertod2.ZonesLib.Util;

import org.bukkit.Chunk;
import org.bukkit.World;

public class ChunkPosition {
    private World world;

    private int chunkX;
    private int chunkZ;

    public ChunkPosition(World world, int chunkX, int chunkZ) {
        this.world = world;

        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public World getWorld() {
        return this.world;
    }

    public int getX() {
        return this.chunkX;
    }

    public int getZ() {
        return this.chunkZ;
    }

    public Chunk getChunk(boolean force) {
        if (force) {
            return this.world.getChunkAt(this.chunkX, this.chunkZ);
        } else {
            return this.world.getChunkAt(this.chunkX, this.chunkZ);
        }
    }
}