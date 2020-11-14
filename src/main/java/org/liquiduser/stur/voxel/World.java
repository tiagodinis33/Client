package org.liquiduser.stur.voxel;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.liquiduser.stur.engine.Model;
import org.liquiduser.stur.render.engine.Renderer;
import org.liquiduser.stur.vendor.FastNoiseLite;
import org.liquiduser.stur.voxel.tiles.Tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class World implements Iterable<Chunk> {
    private Chunk[][] chunks = new Chunk[0][0];

    public Chunk add(Chunk chunk) {
        if (chunk.getDivPos().x >= chunks.length) {
            chunks = Arrays.copyOf(chunks, (int) chunk.getDivPos().x + 1);
            chunks[(int) chunk.getDivPos().x] = new Chunk[0];
        }
        if (chunk.getDivPos().z >= chunks[(int) chunk.getDivPos().x].length)
            chunks[(int) chunk.getDivPos().x] = Arrays.copyOf(chunks[(int) chunk.getDivPos().x], (int) chunk.getDivPos().z + 1);
        chunks[(int) chunk.getDivPos().x][(int) chunk.getDivPos().z] = chunk;
        return chunk;
    }

    public Chunk getChunkByTilePos(int x, int z) throws ChunkNotFoundException {
        Vector2f chunkPos = new Vector2f(x / 16, z / 16);
        return getChunkAt(chunkPos);
    }

    public void setTile(int x, int y, int z, byte tile) throws ChunkNotFoundException {
        Vector2f chunkPos = new Vector2f(x / 16, z / 16);
        Chunk chunk = getChunkAt(chunkPos);
        Vector3f blockPosInChunk = new Vector3f(x - (chunkPos.x * 16), y, z - (chunkPos.y * 16));
        try {
            chunk.setTile((int) blockPosInChunk.x, (int) blockPosInChunk.y, (int) blockPosInChunk.z, tile);

        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ChunkNotFoundException((int) chunkPos.x, (int) chunkPos.y);
        }
    }

    public void generateTerrain(int seed) {
        FastNoiseLite noise = new FastNoiseLite();
        noise.SetSeed(seed);
        noise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        for (int i=0; i< chunks.length; i++){
            for (int j= 0; j< chunks[i].length; j++){
                for(int x = 0; x< Chunk.CHUNKSIZE;x++ ){
                    for(int y = 0; y< Chunk.CHUNKHEIGHTLIMIT;y++ ){
                        for(int z = 0; z< Chunk.CHUNKSIZE;z++ ){
                            chunks[i][j].tiles[x][y][z] = (byte)0;
                        }
                    }
                }
            }
        }

        for (int x = 0; x < chunks.length * Chunk.CHUNKSIZE; x++) {
            for (int z = 0; z < chunks[x/16].length * Chunk.CHUNKSIZE; z++) {

                float noiseValue =  (noise.GetNoise(x,z)+1) * (Chunk.CHUNKHEIGHTLIMIT/10f);
                        try {
                            setTile(x, Math.round(noiseValue), z, Tile.grass.getId());
                        } catch (ChunkNotFoundException ignored) {
                        }
                        for (int y = 0; y<Chunk.CHUNKHEIGHTLIMIT;y++){
                            if(y < Math.round(noiseValue)){
                                try {
                                    setTile(x,y,z,Tile.dirt.getId());
                                } catch (ChunkNotFoundException ignored) {

                                }
                            }
                            if(y+4 < Math.round(noiseValue)){
                                try {
                                    setTile(x,y,z,Tile.stone.getId());
                                } catch (ChunkNotFoundException ignored) {

                                }
                            }
                        }


            }
        }
        for (int i=0; i< chunks.length; i++){
            for (int j= 0; j< chunks[i].length; j++){
                chunks[i][j].rebuild();
            }
        }

    }

    public void render() {
        ArrayList<Model> model = new ArrayList<>();
        var renderer = new Renderer(model);
        renderer.useIndex = false;
        for (Chunk chunk :
                this) {

            chunk.checkInView(renderer);
	    if(chunk.isActive)
            model.add(chunk.chunkModel);
        }

        renderer.render();
    }

    public Chunk getChunkAt(int x, int z) throws ChunkNotFoundException {
        try {
            return chunks[x][z];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ChunkNotFoundException(x, z);
        }
    }

    public Chunk getChunkAt(Vector3f vec) throws ChunkNotFoundException {
        try {
            return chunks[(int) vec.x][(int) vec.z];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ChunkNotFoundException((int) vec.x, (int) vec.z);
        }
    }

    public Chunk[][] getChunks() {
        return chunks;
    }

    public Chunk getChunkAt(Vector2f vec) throws ChunkNotFoundException {
        try {
            return chunks[(int) vec.x][(int) vec.y];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ChunkNotFoundException((int) vec.x, (int) vec.y);
        }
    }

    @Override

    public Iterator<Chunk> iterator() {
        ArrayList<Chunk> chunksArray = new ArrayList<>();
        for (Chunk[] chunk : chunks) {
            chunksArray.addAll(Arrays.asList(chunk));
        }

        return chunksArray.iterator();
    }

    public static class ChunkNotFoundException extends Exception {
        public ChunkNotFoundException(int x, int z) {
            super("At X: " + x + " Z: " + z);
        }

    }
}
