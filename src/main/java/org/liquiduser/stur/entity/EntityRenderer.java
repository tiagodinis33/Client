package org.liquiduser.stur.entity;

import org.joml.Vector3f;
import org.liquiduser.stur.engine.Model;
import org.liquiduser.stur.math.Shape;
import org.liquiduser.stur.render.engine.GLSLProgram;
import org.liquiduser.stur.render.engine.PositionVBO;
import org.liquiduser.stur.render.engine.Renderer;
import org.liquiduser.stur.voxel.Chunk;
import org.liquiduser.stur.voxel.World;
import org.liquiduser.stur.voxel.tiles.Tile;

public class EntityRenderer {
    static Model blockOverlay;
    public static void renderBlockOverlay(Player player){
        if(blockOverlay == null){
            try {
                blockOverlay = new Model(null, GLSLProgram.get("simple"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            blockOverlay.create();
            blockOverlay.addVBO(new PositionVBO());
            blockOverlay.getModelVBO().set(
                    Shape.createCubeShape(0,0,0,1.005f,true,true,true,true,true,true)
            );
            blockOverlay.getMaterial().getColor().set(0,1,1,0.3f);
        }

        if (player.selected != null) {
            blockOverlay.setPosition(new Vector3f((player.selected.x + player.selectedChunk.getPos().x)-0.0025f,player.selected.y-0.0025f,(player.selected.z + player.selectedChunk.getPos().z)-0.0025f));
            Tile tile = null;
            try {
                tile = player.world.getTile(
                        (int) (player.selected.x+player.selectedChunk.getPos().x),
                        player.selected.y,
                        (int) (player.selected.z + player.selectedChunk.getPos().z)
                );
            } catch (World.ChunkNotFoundException | Chunk.TileNotFoundException e) {
                e.printStackTrace();
                return;
            }
            blockOverlay.getPosition().add(tile.getBoundingBox().minX,tile.getBoundingBox().minY,tile.getBoundingBox().minZ);
            blockOverlay.getScale().set(tile.getBoundingBox().maxX-tile.getBoundingBox().minX,tile.getBoundingBox().maxY-tile.getBoundingBox().minY,tile.getBoundingBox().maxZ-tile.getBoundingBox().minZ);

            var render = new Renderer(blockOverlay);
            render.useIndex = false;
            render.render();

        }
    }
    static Model playerHand;
    public static void renderFirstPersonHand(Player player){
        if (playerHand == null) {
            try {
                playerHand = new Model(null, GLSLProgram.get("simple"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
