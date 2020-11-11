package org.liquiduser.stur.entity;

import org.joml.*;
import org.liquiduser.stur.voxel.World;
import org.liquiduser.stur.render.engine.Camera;
import org.liquiduser.stur.voxel.Chunk;
import org.liquiduser.stur.voxel.Chunk.BlockSide;
import org.liquiduser.stur.voxel.tiles.Tile;

public class Player {
    Vector3i selected;
    BlockSide side = BlockSide.NONE;
    Chunk selectedChunk;
    Camera camera;

    public Chunk getSelectedChunk() {
        return selectedChunk;
    }

    public Player(Camera camera){
        this.camera = camera;
    }
    public Player(){
        camera = new Camera();
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

    public void raytraceBlock(World world){
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
                            System.out.println(side);
                        }
                    }
                }
            }
        }





    }
    public void breakBlock(){
        selectedChunk.setTile(selected.x,selected.y,selected.z, (byte)0);
        selectedChunk.rebuild();

    }
    public void placeBlock(Tile tile){
        if(tile.getId() == 0) return;

        if(side != BlockSide.NONE && selected != null){
            try{
                switch (side){

                    case TOP:
                        selectedChunk.setTile(selected.x,selected.y+1,selected.z, tile.getId());
                        break;
                    case BOTTOM:
                        selectedChunk.setTile(selected.x,selected.y-1,selected.z, tile.getId());
                        break;
                    case LEFT:
                        selectedChunk.setTile(selected.x-1,selected.y,selected.z, tile.getId());
                        break;
                    case RIGHT:
                        selectedChunk.setTile(selected.x+1,selected.y,selected.z, tile.getId());
                        break;
                    case FRONT:
                        selectedChunk.setTile(selected.x,selected.y,selected.z-1, tile.getId());
                        break;
                    case BACK:
                        selectedChunk.setTile(selected.x,selected.y,selected.z+1, tile.getId());
                        break;
                }
            } catch (ArrayIndexOutOfBoundsException ignored){

            }

            selectedChunk.rebuild();
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
        expand(0.0001f,back);
        expand(0.0001f,front);
        expand(0.0001f,right);
        expand(0.0001f,left);
        expand(0.0001f,top);
        expand(0.0001f,bottom);
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
