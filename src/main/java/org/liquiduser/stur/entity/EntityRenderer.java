package org.liquiduser.stur.entity;

import org.joml.Vector3f;
import org.joml.Vector3i;
import org.liquiduser.stur.engine.Model;
import org.liquiduser.stur.math.MathUtils;
import org.liquiduser.stur.render.engine.GLSLProgram;
import org.liquiduser.stur.render.engine.PositionVBO;
import org.liquiduser.stur.render.engine.Renderer;
import org.liquiduser.stur.voxel.Chunk;
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
                    MathUtils.createCubeShape(0,0,0,1.0f)
            );
            blockOverlay.getMaterial().getColor().set(0,1,1,0.3f);
        }

        if (player.selected != null) {
            blockOverlay.setPosition(new Vector3f(player.selected.x-0.0025f,player.selected.y-0.0025f,player.selected.z-0.0025f));
            Tile tile = Tile.getTileById(player.selectedChunk.getTiles()[player.selected.x][player.selected.y][player.selected.z]);
            blockOverlay.getPosition().add(tile.getBoundingBox().minX,tile.getBoundingBox().minY,tile.getBoundingBox().minZ);
            blockOverlay.getScale().set(tile.getBoundingBox().maxX-tile.getBoundingBox().minX,tile.getBoundingBox().maxY-tile.getBoundingBox().minY,tile.getBoundingBox().maxZ-tile.getBoundingBox().minZ);

            blockOverlay.getScale().mul(1.005f);
            blockOverlay.getPosition().add(player.selectedChunk.getPos());
            var render = new Renderer(blockOverlay);
            render.useIndex = false;
            render.render();

        }
    }
}
