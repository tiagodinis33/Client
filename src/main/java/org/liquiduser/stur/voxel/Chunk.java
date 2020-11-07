package org.liquiduser.stur.voxel;

import org.joml.*;
import org.liquiduser.stur.engine.Model;
import org.liquiduser.stur.engine.Resource;
import org.liquiduser.stur.engine.Texture;
import org.liquiduser.stur.ioutils.Input;
import org.liquiduser.stur.render.engine.Camera;
import org.liquiduser.stur.render.engine.GLSLProgram;
import org.liquiduser.stur.render.engine.PositionVBO;
import org.liquiduser.stur.render.engine.Renderer;
import org.liquiduser.stur.render.internal.VBO;
import org.liquiduser.stur.math.MathUtils;
import org.liquiduser.stur.voxel.tiles.Tile;

import java.lang.Math;
import java.util.Arrays;

public class Chunk
extends Resource
{
    private static final int CHUNKSIZE = 16;
    private static GLSLProgram shader;

    static {
        try {
            shader = GLSLProgram.get("chunk");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    VBO verticesVBO;
    VBO colorsVBO;
    VBO texCoords;
    public static final int CHUNKHEIGHTLIMIT = 256;
    public Texture getTexture(){
        return chunkModel.getMaterial().getTexture();
    }
    @Override
    public void create() {
        super.create();
        sizeX = CHUNKSIZE;
        sizeY = CHUNKHEIGHTLIMIT;
        sizeZ = CHUNKSIZE;
        tiles = new byte[sizeX][sizeY][sizeZ];
        chunkModel = new Model(null, shader);
        chunkModel.create();
        texCoords = new VBO(1,2);
        verticesVBO = new PositionVBO();
        colorsVBO = new VBO(2,4);
        chunkModel.addVBO(verticesVBO);
        chunkModel.addVBO(texCoords);
        chunkModel.addVBO(colorsVBO);
        /*try {
            chunkModel.getMaterial().setTexture(new SpriteSheet("blocks/spritesheet",16));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                for (int z = 0; z < sizeZ; z++) {
                    if(y <=(pos.y + CHUNKSIZE)){
                            tiles[x][y][z] = 1;
                        if (tiles[x][y][z] != 0) {
                            //texCoords.getArray().addAll(Arrays.asList(MathUtils.getTexCoords(tiles[x][y][z], ((SpriteSheet) getTexture()).getSize(), getTexture().getHeight())));
                            colorsVBO.getArray().addAll(Arrays.asList(MathUtils.getCubeColors(tiles[x][y][z])));
                            verticesVBO.getArray().addAll(Arrays.asList(MathUtils.createCubeShape(x, y, z, 1)));
                        }
                    }
                }
            }
        }
        chunkModel.getPosition().set(pos);
        colorsVBO.update();
        verticesVBO.update();
    }

    @Override
    public void cleanup() {
        super.cleanup();
        chunkModel.cleanup();
    }

    boolean isActive;
    byte[][][] tiles;
    private int sizeX, sizeY,sizeZ;
    /** Model correspondente a este chunk*/
    Model chunkModel;
    Vector3f pos;
    public Chunk(float x, float y, float z){
        this(new Vector3f(x,y,z));
    }
    public Chunk(Vector3f v){
        pos = v.mul(16, new Vector3f());
    }
    public Vector3i getSize(){
        return new Vector3i(sizeX,sizeY,sizeZ);
    }
    public Vector3f getPos() {
        return pos;
    }

    public byte[][][] getTiles() {
        return tiles;
    }
    public void setTile(int x, int y, int z, byte tileId){

        getTiles()
                [x]
                [y]
                [z] = tileId;
    }
    public void setPos(Vector3f pos) {
        this.pos = pos;
    }

    static GLSLProgram simpleShader;

    static {
        try {
            simpleShader = GLSLProgram.get("simple");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Vector3i selected;
    public void render(){
        var renderer = new Renderer(chunkModel);
        renderer.useIndex = false;
        renderer.render();





    }
    public void rebuild(){
        verticesVBO.getArray().clear();
        colorsVBO.getArray().clear();
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                for (int z = 0; z < sizeZ; z++) {

                        if (tiles[x][y][z] != 0) {
                            //texCoords.getArray().addAll(Arrays.asList(MathUtils.getTexCoords(tiles[x][y][z], ((SpriteSheet) getTexture()).getSize(), getTexture().getHeight())));
                            colorsVBO.getArray().addAll(Arrays.asList(MathUtils.getCubeColors(tiles[x][y][z])));
                            verticesVBO.getArray().addAll(Arrays.asList(MathUtils.createCubeShape(x, y, z, 1)));
                        }

                }
            }
        }
        colorsVBO.update();
        verticesVBO.update();
    }
    private void checkTileInView(){

    }
    public enum BlockSide{
        UP,DOWN,LEFT,RIGHT,FRONT,BACK,NONE
    }

    private BlockSide getBlockSide(float closestDistance, AABBf box){
        return BlockSide.NONE;
    }
    private Camera getCamera(){
        return Camera.active;
    }

    static Model cube;
}
