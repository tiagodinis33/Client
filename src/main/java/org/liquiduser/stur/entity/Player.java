package org.liquiduser.stur.entity;

import org.joml.*;
import org.joml.Random;
import org.liquiduser.stur.voxel.World;
import org.liquiduser.stur.render.engine.Camera;
import org.liquiduser.stur.voxel.Chunk;
import org.liquiduser.stur.voxel.Chunk.BlockSide;
import org.liquiduser.stur.voxel.tiles.Tile;
import org.liquiduser.stur.voxel.tiles.TileAir;

import java.lang.Math;
import java.util.*;

public class Player {
    Vector3i selected;
    BlockSide side = BlockSide.NONE;
    Chunk selectedChunk;
    Camera camera;
    World world;
    public Chunk getSelectedChunk() {
        return selectedChunk;
    }

    public Player(Camera camera, World world){
        this.camera = camera;
        this.world = world;
    }
    public Player(World world){
        this.world = world;
        camera = new Camera(calculateSpawn(),new Vector3f());
    }

    private Vector3f calculateSpawn() {
        Random random = new Random();
        //positive spawn
        int xSize = 0;
        for(Map.Entry<Integer, HashMap<Integer, Chunk>> entry : world.getChunks().entrySet()){
            if(entry.getKey() >0){
                xSize++;
            }
        }
        int x = random.nextInt(Chunk.CHUNKSIZE*xSize);
        int zSize = 0;
        for(Map.Entry<Integer, Chunk> entry : world.getChunks().get(x/16).entrySet()){
            if(entry.getKey() >0){
                zSize++;
            }
        }
        int z = random.nextInt(Chunk.CHUNKSIZE*zSize);

        //negative spawn
        int xnSize = 0;
        for(Map.Entry<Integer, HashMap<Integer, Chunk>> entry : world.getChunks().entrySet()){
            if(entry.getKey() <0){
                xnSize++;
            }
        }
        int xn = random.nextInt(Chunk.CHUNKSIZE*xnSize);
        int znSize = 0;
        for(Map.Entry<Integer, Chunk> entry : world.getChunks().get(-(xn/16)).entrySet()){
            if(entry.getKey() <0){
                znSize++;
            }
        }
        int zn = random.nextInt(Chunk.CHUNKSIZE*znSize);
        boolean negative = new java.util.Random().nextBoolean() && xSize == 0 && zSize == 0;
        for(int y = 0; y < Chunk.CHUNKHEIGHTLIMIT; y++){
            try {
                if(
                        world.getTile(negative?-xn:x,y,negative?-zn:z) instanceof TileAir &&
                        world.getTile(negative?-xn:x,y+1,negative?-zn:z) instanceof TileAir
                ){
                    return new Vector3f( (negative?-xn:x)+0.5f,y+1.75f,(negative?-zn:z)+0.5f);
                }
            } catch (World.ChunkNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    float swingProgress = 0.0f;

    public void swingItem(){

    }
    public boolean isSwinging(){
        return swingProgress != 0.0f;
    }
    public void setSwingProgress(float swingProgress) {
        this.swingProgress = swingProgress;
    }

    public float getSwingProgress() {
        return swingProgress;
    }

    public void raytraceBlock(){
        Vector3f dir = camera.getMatrix().positiveZ(new Vector3f()).negate();
        side = BlockSide.NONE;
        float closestDistance = 5;
        Vector3f min = new Vector3f();
        Vector3f max = new Vector3f();
        selected = null;
        selectedChunk = null;
        for (Chunk chunk:
             world) {
            for (int x = 0; x < chunk.getSize().x; x++) {
                for (int y = 0; y < chunk.getSize().y; y++) {
                    for (int z = 0; z < chunk.getSize().z; z++) {
                        if(chunk.getTiles()[x][y][z] == 0) continue;
                        Tile tile = Tile.getTileById(chunk.getTiles()[x][y][z]);

                        AABBf aabb =tile.getBoundingBox();

                        aabb.translate(x,y,z);
                        aabb.translate(chunk.getPos());
                        Vector2f nearFar = new Vector2f();
                        Rayf ray = new Rayf(camera.getPosition(), dir);
                        if (Intersectionf.intersectRayAab(ray, aabb, nearFar) && nearFar.x <= closestDistance) {
                            closestDistance = nearFar.x;
                            selected = new Vector3i(x, y, z);
                            selectedChunk = chunk;
                            side = getBlockSide(closestDistance, aabb,ray);

                        }
                    }
                }
            }
        }





    }
    int selectedSlot = 0;

    public int getSelectedSlot() {
        return selectedSlot;
    }

    public void setSelectedSlot(int selectedSlot) {
        this.selectedSlot = selectedSlot;
        if(this.selectedSlot < 0) this.selectedSlot = 8;
        if(this.selectedSlot > 8) this.selectedSlot = 0;
    }
    List<Tile> inventory = Arrays.asList(
            Tile.grass,
            Tile.stone,
            Tile.dirt,
            Tile.air,
            Tile.air,
            Tile.air,
            Tile.air,
            Tile.air,
            Tile.air
    );

    public List<Tile> getInventory() {
        return inventory;
    }

    public void breakBlock(){
        selectedChunk.setTile(selected.x,selected.y,selected.z, (byte)0);
        selectedChunk.rebuild();

    }
    public void placeBlock(Tile tile){
        if(tile.getId() == 0) return;

        if(selected != null){
            Chunk neighborChunk = null;
            try{
                switch (side) {
                    case NONE:
                        break;
                    case TOP:
                        selectedChunk.setTile(selected.x, selected.y + 1, selected.z, tile.getId());

                        break;
                    case BOTTOM:
                        selectedChunk.setTile(selected.x, selected.y - 1, selected.z, tile.getId());
                        break;
                    case LEFT:

                        if (selected.x - 1 == -1 && (neighborChunk = world.getChunkAt(selectedChunk.getDivPos().sub(1, 0, 0))) != null) {
                            neighborChunk.setTile(15, selected.y, selected.z, tile.getId());
                        } else {
                            selectedChunk.setTile(selected.x - 1, selected.y, selected.z, tile.getId());
                        }

                        break;
                    case RIGHT:
                        if (selected.x + 1 == Chunk.CHUNKSIZE && (neighborChunk = world.getChunkAt(selectedChunk.getDivPos().add(1, 0, 0))) != null) {
                            neighborChunk.setTile(0, selected.y, selected.z, tile.getId());

                        } else {
                            selectedChunk.setTile(selected.x + 1, selected.y, selected.z, tile.getId());
                        }

                        break;
                    case FRONT:
                        if (selected.z - 1 == -1 && (neighborChunk = world.getChunkAt(selectedChunk.getDivPos().sub(0, 0, 1))) != null) {
                            neighborChunk.setTile(selected.x, selected.y, Chunk.CHUNKSIZE-1, tile.getId());
                        } else {
                            selectedChunk.setTile(selected.x, selected.y, selected.z - 1, tile.getId());
                        }
                        break;
                    case BACK:
                        if (selected.z + 1 == Chunk.CHUNKSIZE && (neighborChunk = world.getChunkAt(selectedChunk.getDivPos().add(0, 0, 1))) != null) {
                            neighborChunk.setTile(selected.x, selected.y, 0, tile.getId());
                        } else {
                            selectedChunk.setTile(selected.x, selected.y, selected.z + 1, tile.getId());
                        }
                        break;
                }
            }catch(World.ChunkNotFoundException ignored){
            }
            if (neighborChunk != null) {
                neighborChunk.rebuild();
            } else {
                selectedChunk.rebuild();
            }
        }
    }

    public BlockSide getSelectedSide() {
        return side;
    }
    private void expand(float e, AABBf aabb){
        aabb.minX*=1f-e;
        aabb.minY*=1f-e;
        aabb.minZ*=1f-e;
        aabb.maxX*=1f+e;
        aabb.maxY*=1f+e;
        aabb.maxZ*=1f+e;
    }
    private BlockSide getBlockSide(float closestDistance, AABBf aabb, Rayf ray) {
        AABBf back = new AABBf(aabb.minX,aabb.minY,aabb.maxZ,aabb.maxX,aabb.maxY,aabb.maxZ);
        AABBf front = new AABBf(aabb.minX,aabb.minY,aabb.minZ,aabb.maxX,aabb.maxY,aabb.minZ);
        AABBf right = new AABBf(aabb.maxX,aabb.minY,aabb.minZ,aabb.maxX,aabb.maxY,aabb.maxZ);
        AABBf left = new AABBf(aabb.minX,aabb.minY,aabb.minZ,aabb.minX,aabb.maxY,aabb.maxZ);
        AABBf top = new AABBf(aabb.minX,aabb.maxY,aabb.minZ,aabb.maxX,aabb.maxY,aabb.maxZ);
        AABBf bottom = new AABBf(aabb.minX,aabb.minY,aabb.minZ,aabb.maxX,aabb.minY,aabb.maxZ);
        expand(0.000001f,back);
        expand(0.000001f,front);
        expand(0.000001f,right);
        expand(0.000001f,left);
        expand(0.000001f,top);
        expand(0.000001f,bottom);
        Vector2f nearFar = new Vector2f();
        if(Intersectionf.intersectRayAab(ray, back, nearFar) && nearFar.x <= closestDistance)
            return BlockSide.BACK;
        nearFar = new Vector2f();
        if(Intersectionf.intersectRayAab(ray, front, nearFar) && nearFar.x <= closestDistance)
            return BlockSide.FRONT;
        nearFar = new Vector2f();
        if(Intersectionf.intersectRayAab(ray, right, nearFar) && nearFar.x <= closestDistance)
            return BlockSide.RIGHT;
        nearFar = new Vector2f();
        if(Intersectionf.intersectRayAab(ray, left, nearFar) && nearFar.x <= closestDistance)
            return BlockSide.LEFT;
        nearFar = new Vector2f();
        if(Intersectionf.intersectRayAab(ray, top, nearFar) && nearFar.x <= closestDistance)
            return BlockSide.TOP;
        nearFar = new Vector2f();
        if(Intersectionf.intersectRayAab(ray, bottom, nearFar) && nearFar.x <= closestDistance)
            return BlockSide.BOTTOM;

        return BlockSide.NONE;
    }

    public Camera getCamera() {
        return camera;
    }

    public Vector3i getSelectedBlock() {
        return selected;
    }
}
