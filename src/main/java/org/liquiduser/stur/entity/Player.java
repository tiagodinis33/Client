package org.liquiduser.stur.entity;

import org.joml.*;
import org.liquiduser.stur.World;
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
    public void raytraceBlock(World world){
        Vector3f dir = Camera.getMatrix().positiveZ(new Vector3f()).negate();
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
                            side = getBlockSide(closestDistance, aabb);
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

                    case UP:
                        selectedChunk.setTile(selected.x,selected.y+1,selected.z, tile.getId());
                        break;
                    case DOWN:
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

            } finally{
                selectedChunk.rebuild();
            }

        }
    }

    private BlockSide getBlockSide(float closestDistance, AABBf aabb) {
        return null;
    }

    public Camera getCamera() {
        return camera;
    }

    public Vector3i getSelectedBlock() {
        return selected;
    }
}
