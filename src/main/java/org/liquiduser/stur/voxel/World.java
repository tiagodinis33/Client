package org.liquiduser.stur.voxel;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.liquiduser.Stur;
import org.liquiduser.stur.engine.Model;
import org.liquiduser.stur.entity.Player;
import org.liquiduser.stur.math.PhysicsUtils;
import org.liquiduser.stur.render.engine.Renderer;
import org.liquiduser.stur.vendor.FastNoiseLite;
import org.liquiduser.stur.voxel.tiles.Tile;
import org.ode4j.math.DMatrix3;
import org.ode4j.ode.DBody;
import org.ode4j.ode.DWorld;
import org.ode4j.ode.OdeHelper;
import org.ode4j.ode.internal.DxBox;
import org.ode4j.ode.internal.DxSpace;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class World implements Iterable<Chunk> {
    private final HashMap<Integer, HashMap<Integer, Chunk>> chunks = new HashMap<>();


    DxSpace chunksAABBs = (DxSpace) OdeHelper.createSimpleSpace();

    DWorld odeWorld = OdeHelper.createWorld();
    DBody bodyChunks = OdeHelper.createBody(odeWorld);
    public World(){
        odeWorld.setGravity(0,-9.807*1.5f,0);
        bodyChunks.enable();
        chunksAABBs.setBody(bodyChunks);
    }
    public Chunk add(Chunk chunk) {
        chunks.put((int) chunk.getDivPos().x, chunks.getOrDefault((int) chunk.getDivPos().x, new HashMap<>()));
        chunks.get((int) chunk.getDivPos().x).put((int) chunk.getDivPos().z, chunk);
        rebuildChunkAABBs();
        return chunk;
    }

    public Chunk getChunkByTilePos(int x, int z) throws ChunkNotFoundException {

        MathContext mc = new MathContext(1000, RoundingMode.DOWN);
        BigDecimal xx = new BigDecimal(Math.abs((float)x) / ((float)Chunk.CHUNKSIZE), mc);
        BigDecimal zz = new BigDecimal(Math.abs((float)z) / ((float)Chunk.CHUNKSIZE), mc);

        Vector2i chunkPos = new Vector2i(xx.intValue(), zz.intValue());
        System.out.println(chunkPos);
        Chunk chunk = getChunkAt(chunkPos.x,chunkPos.y);
        if(chunk == null) throw new ChunkNotFoundException(chunkPos.x,chunkPos.y);
        return chunk;
    }
    public void rebuildChunkAABBs(){
        chunksAABBs.cleanGeoms();
        for(Chunk chunk : this){

            for (int x = 0; x < Chunk.CHUNKSIZE; x++)
                for (int y = 0; y < 1; y++)
                    for (int z = 0; z < Chunk.CHUNKSIZE; z++){

                            DxBox tilebox = DxBox.dCreateBox(chunksAABBs, 0.5,0.5,0.5);

                    }
        }
    }

    public void generateTerrain(int seed) {
        FastNoiseLite noise = new FastNoiseLite(seed);
        noise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        for (Chunk chunk : this) {
            for (int x = 0; x < Chunk.CHUNKSIZE; x++)
                for (int y = 0; y < 1; y++)
                    for (int z = 0; z < Chunk.CHUNKSIZE; z++)
                        chunk.tiles[x][y][z] = (byte) 1;


        }

        for (Chunk chunk : this) {
            chunk.rebuild();

        }
        rebuildChunkAABBs();
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
        } catch (NullPointerException e) {
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
            return chunks
                    .get((int) vec.x)
                    .get((int) vec.y);
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

    public void setTile(int x, int y, int z, byte tile) throws ChunkNotFoundException, Chunk.TileNotFoundException {
        Chunk chunk = getChunkByTilePos(x , z);
        Vector2f chunkPos = new Vector2f(chunk.getPos().x, chunk.getPos().z);
        if(x < 0){
            chunkPos.x +=Math.abs(chunkPos.x/16);
        }
        if(z < 0){
            chunkPos.y += Math.abs(chunkPos.y/16);
        }
        Vector3f blockPosInChunk = new Vector3f(Math.abs(Math.abs(x) - Math.abs(chunkPos.x)) , y, Math.abs(Math.abs(z) - Math.abs(chunkPos.y)));

        chunk.setTile(
                 ((int) blockPosInChunk.x),
                (int) blockPosInChunk.y,
                ((int) blockPosInChunk.z),
                tile);
        chunk.rebuild();
        rebuildChunkAABBs();
    }

    public Tile getTile(int x, int y, int z) throws Chunk.TileNotFoundException, ChunkNotFoundException {
        Chunk chunk = getChunkByTilePos(x , z);
        Vector2f chunkPos = new Vector2f(chunk.getPos().x, chunk.getPos().z);
        if(x < 0){
            chunkPos.x +=Math.abs(chunkPos.x/16);
        }
        if(z < 0){
            chunkPos.y += Math.abs(chunkPos.y/16);
        }
        Vector3f blockPosInChunk = new Vector3f(Math.abs(Math.abs(x) - Math.abs(chunkPos.x)) , y, Math.abs(Math.abs(z) - Math.abs(chunkPos.y)));

        try{
            return Tile.getTileById(chunk.getTiles()
                    [ ((int) blockPosInChunk.x)]
                    [(int) blockPosInChunk.y]
                    [ ((int) blockPosInChunk.z)]);
        }catch (ArrayIndexOutOfBoundsException e){
            throw new Chunk.TileNotFoundException(
                     ((int) blockPosInChunk.x),
                    (int) blockPosInChunk.y,
                    ((int) blockPosInChunk.z)
            );
        }
    }
    private Stur getEngine(){
        return Stur.getEngine();
    }
    public void update(){
        odeWorld.step(1f/30f);
        bodyChunks.setPosition(0f,0f,0f);
        bodyChunks.setRotation(new DMatrix3().setIdentity());
    }
    public void addBody(Player player) {
        DBody body = OdeHelper.createBody(odeWorld);
        player.setBody(body);
        body.setDynamic();
        body.enable();
        DxSpace space = (DxSpace) OdeHelper.createSimpleSpace();
        DxBox boxGeom = DxBox.dCreateBox(space,player.getBoxSize().x, player.getBoxSize().y,player.getBoxSize().z);
        boxGeom.setPosition(PhysicsUtils.toODEVec3f(player.getCamera().getPosition()));
        space.setBody(body);


    }

    public static class ChunkNotFoundException extends Exception {
        public ChunkNotFoundException(int x, int z) {
            super("At X: " + x + " Z: " + z);
        }

    }
}
