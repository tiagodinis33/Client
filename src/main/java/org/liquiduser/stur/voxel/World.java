package org.liquiduser.stur.voxel;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.liquiduser.stur.engine.Model;
import org.liquiduser.stur.render.engine.Renderer;
import org.liquiduser.stur.vendor.FastNoiseLite;
import org.liquiduser.stur.voxel.tiles.Tile;
import org.ode4j.ode.DWorld;
import org.ode4j.ode.OdeHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class World implements Iterable<Chunk> {
    private final HashMap<Integer, HashMap<Integer, Chunk>> chunks = new HashMap<>();
    DWorld odeWorld = OdeHelper.createWorld();

    public Chunk add(Chunk chunk) {
        chunks.put((int) chunk.getDivPos().x, chunks.getOrDefault((int) chunk.getDivPos().x, new HashMap<>()));
        chunks.get((int) chunk.getDivPos().x).put((int) chunk.getDivPos().z, chunk);
        return chunk;
    }

    public Chunk getChunkByTilePos(int x, int z) throws ChunkNotFoundException {
        Vector2f chunkPos = new Vector2f(x / Chunk.CHUNKSIZE, z / Chunk.CHUNKSIZE);
        return getChunkAt(chunkPos);
    }

    public void setTile(int x, int y, int z, byte tile) throws ChunkNotFoundException {
        Chunk chunk = getChunkByTilePos(x, z);
        Vector2f chunkPos = new Vector2f(chunk.getDivPos().x, chunk.getDivPos().z);
        Vector3f blockPosInChunk = new Vector3f(x - (chunkPos.x * Chunk.CHUNKSIZE), y, z - (chunkPos.y * Chunk.CHUNKSIZE));
        if(x <0){
            blockPosInChunk.x = (Chunk.CHUNKSIZE-1) - Math.abs(blockPosInChunk.x);
        }
        if(z <0){
            blockPosInChunk.z = (Chunk.CHUNKSIZE-1) - Math.abs(blockPosInChunk.z);
        }
        try {
            chunk.setTile((int) Math.abs(blockPosInChunk.x), (int) Math.abs(blockPosInChunk.y), (int) Math.abs(blockPosInChunk.z), tile);

        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ChunkNotFoundException((int) chunkPos.x, (int) chunkPos.y);
        }
    }

    public void generateTerrain(int seed) {
        FastNoiseLite noise = new FastNoiseLite(seed);
        noise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        for (Chunk chunk: this) {
                for (int x = 0; x < Chunk.CHUNKSIZE; x++)
                    for (int y = 0; y < Chunk.CHUNKHEIGHTLIMIT; y++)
                        for (int z = 0; z < Chunk.CHUNKSIZE; z++)
                            chunk.tiles[x][y][z] = (byte) 0;


        }
        int xnSize = 0;
        for (Map.Entry<Integer, HashMap<Integer, Chunk>> entry : chunks.entrySet()) {
            if (entry.getKey() < 0) {
                xnSize++;
            }
        }
        for (int x = -(xnSize * Chunk.CHUNKSIZE); x < (chunks.size()*Chunk.CHUNKSIZE) - (xnSize*Chunk.CHUNKSIZE); x++) {
            int znSize = 0;
            for (Map.Entry<Integer, Chunk> entry : chunks.get(x / 16).entrySet()) {
                if (entry.getKey() < 0) {
                    znSize++;
                }
            }
            for (int z = -(znSize*Chunk.CHUNKSIZE); z < (chunks.get(x/16).size()*Chunk.CHUNKSIZE) - (znSize*Chunk.CHUNKSIZE); z++) {
                float noiseValue = ((noise.GetNoise(x, z) + 1)/2) * (Chunk.CHUNKHEIGHTLIMIT / 10f);
                try {
                    setTile(x, Math.round(noiseValue), z, Tile.grass.getId());
                } catch (ChunkNotFoundException e) {
                    e.printStackTrace();
                }
                for (int y = 0; y < Chunk.CHUNKHEIGHTLIMIT; y++) {
                    if (y < Math.round(noiseValue)) {
                        try {
                            setTile(x, y, z, Tile.dirt.getId());
                        } catch (ChunkNotFoundException ignored) {

                        }
                    }
                    if (y + 4 < Math.round(noiseValue)) {
                        try {
                            setTile(x, y, z, Tile.stone.getId());
                        } catch (ChunkNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }
        for (Chunk chunk: this) {
            chunk.rebuild();

        }

    }

    public void render() {
        ArrayList<Model> model = new ArrayList<>();
        var renderer = new Renderer(model);
        renderer.useIndex = false;
        for (Chunk chunk :
                this) {

            chunk.checkInView(renderer);
            if (chunk.isActive)
                model.add(chunk.chunkModel);
        }

        renderer.render();
    }

    public Chunk getChunkAt(int x, int z) throws ChunkNotFoundException {
        try {
            return chunks.get(x).get(z);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ChunkNotFoundException(x, z);
        }
    }

    public Chunk getChunkAt(Vector3f vec) throws ChunkNotFoundException {
        try {
            return chunks.get((int) vec.x).get((int) vec.z);
        } catch (NullPointerException e) {
            throw new ChunkNotFoundException((int) vec.x, (int) vec.z);
        }
    }

    public HashMap<Integer, HashMap<Integer, Chunk>> getChunks() {
        return chunks;
    }

    public Chunk getChunkAt(Vector2f vec) throws ChunkNotFoundException {
        try {
            return chunks.get((int) vec.x).get((int) vec.y);
        } catch (NullPointerException e) {
            throw new ChunkNotFoundException((int) vec.x, (int) vec.y);
        }
    }

    @Override

    public Iterator<Chunk> iterator() {
        ArrayList<Chunk> chunksArray = new ArrayList<>();
        for (Map.Entry<Integer, HashMap<Integer, Chunk>> chunk : chunks.entrySet()) {
            chunksArray.addAll(chunk.getValue().values());
        }

        return chunksArray.iterator();
    }

    public Tile getTile(int x, int y, int z) throws ChunkNotFoundException {
        Chunk chunk = getChunkByTilePos(x, z);
        Vector2f chunkPos = new Vector2f(chunk.getDivPos().x, chunk.getDivPos().z);
        Vector3f blockPosInChunk = new Vector3f(x - (chunkPos.x * Chunk.CHUNKSIZE), y, z - (chunkPos.y * Chunk.CHUNKSIZE));
        if(x <0){
            blockPosInChunk.x = (Chunk.CHUNKSIZE-1) - Math.abs(blockPosInChunk.x);
        }
        if(z <0){
            blockPosInChunk.z = (Chunk.CHUNKSIZE-1) - Math.abs(blockPosInChunk.z);
        }

        return Tile.getTileById(chunk.getTiles()[(int) Math.abs(blockPosInChunk.x)][(int)Math.abs(blockPosInChunk.y)][(int)Math.abs(blockPosInChunk.z)]);

    }

    public static class ChunkNotFoundException extends Exception {
        public ChunkNotFoundException(int x, int z) {
            super("At X: " + x + " Z: " + z);
        }

    }
}
