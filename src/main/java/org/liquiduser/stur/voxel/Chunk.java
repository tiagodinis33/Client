package org.liquiduser.stur.voxel;

import org.joml.*;
import org.liquiduser.stur.engine.Model;
import org.liquiduser.stur.engine.Resource;
import org.liquiduser.stur.engine.Texture;
import org.liquiduser.stur.render.engine.Camera;
import org.liquiduser.stur.render.engine.GLSLProgram;
import org.liquiduser.stur.render.engine.PositionVBO;
import org.liquiduser.stur.render.engine.Renderer;
import org.liquiduser.stur.render.internal.VBO;
import org.liquiduser.stur.math.Shape;
import org.liquiduser.stur.utils.SpriteSheet;
import org.liquiduser.stur.voxel.tiles.Tile;

import java.io.IOException;
import java.util.Arrays;

public class Chunk
extends Resource
{
    private static final int CHUNKSIZE = 16;
    private static GLSLProgram shader;

    static {
        try {
            shader = GLSLProgram.get("simple");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    VBO verticesVBO;
    VBO texCoords;
    public static final int CHUNKHEIGHTLIMIT = 256;
    public Texture getTexture(){
        return chunkModel.getMaterial().getTexture();
    }
    @Override
    public void create() {
        super.create();

        boolean lDefault = true;
        sizeX = CHUNKSIZE;
        sizeY = CHUNKHEIGHTLIMIT;
        sizeZ = CHUNKSIZE;
        tiles = new byte[sizeX][sizeY][sizeZ];
        chunkModel = new Model(null, shader);
        chunkModel.create();
        texCoords = new VBO(1,2);
        verticesVBO = new PositionVBO();
        chunkModel.addVBO(verticesVBO);
        chunkModel.addVBO(texCoords);
        try {
            Texture txt = new SpriteSheet("blocks/spritesheet",16);
            txt.create(Texture.MipMap.NEAREST);
            chunkModel.getMaterial().setTexture(txt);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                for (int z = 0; z < sizeZ; z++) {
                    if(y <=(CHUNKSIZE)){
                        tiles[x][y][z] = 1;
                    }


                }
            }
        }
        rebuild();
        chunkModel.getPosition().set(pos);
        verticesVBO.update();
        texCoords.update();
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


    public void rebuild(){
        verticesVBO.getArray().clear();
        texCoords.getArray().clear();
        boolean lDefault = true;
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                for (int z = 0; z < sizeZ; z++) {
                    Tile tile = Tile.getTileById(tiles[x][y][z]);
                    if(!tile.isActive())
                    {
                        continue;
                    }

                    boolean lXNegative = lDefault;
                    if(x > 0)
                        lXNegative =  !Tile.getTileById(tiles[x-1][y][z]).isActive();

                    boolean lXPositive = lDefault;
                    if(x < CHUNKSIZE - 1)
                        lXPositive =  !Tile.getTileById(tiles[x+1][y][z]).isActive();
                    boolean lYNegative = lDefault;
                    if(y > 0)
                        lYNegative =  !Tile.getTileById(tiles[x][y-1][z]).isActive();
                    boolean lYPositive = lDefault;
                    if(y < CHUNKHEIGHTLIMIT - 1)
                        lYPositive = !Tile.getTileById(tiles[x][y+1][z]).isActive();

                    boolean lZNegative = lDefault;
                    if(z > 0)
                        lZNegative = !Tile.getTileById(tiles[x][y][z-1]).isActive();

                    boolean lZPositive = lDefault;
                    if(z < CHUNKSIZE - 1)
                        lZPositive =  !Tile.getTileById(tiles[x][y][z+1]).isActive();
                    texCoords.getArray().addAll(Arrays.asList(Shape.getTexCoords(tiles[x][y][z], ((SpriteSheet) getTexture()).getSize(), getTexture().getHeight(),lZNegative,lZPositive,lXPositive,lXNegative,lYPositive,lYNegative)));
                    verticesVBO.getArray().addAll(Arrays.asList(Shape.createCubeShape(x, y, z, 1,lZNegative,lZPositive,lXPositive,lXNegative,lYPositive,lYNegative)));





                }
            }
        }
        verticesVBO.update();
        texCoords.update();
    }
    public void checkInView(Renderer renderer){
        Matrix4f camera = Camera.getMatrixS();
        Matrix4f perspective = renderer.getProjection();
        Matrix4f frustrum =perspective.mul(camera,new Matrix4f());
        FrustumIntersection intersection = new FrustumIntersection(frustrum);
        isActive = intersection.testAab(pos,pos.add(CHUNKSIZE,CHUNKHEIGHTLIMIT,CHUNKSIZE, new Vector3f()));
        chunkModel.isActive = intersection.testAab(pos,pos.add(CHUNKSIZE,CHUNKHEIGHTLIMIT,CHUNKSIZE, new Vector3f()));

    }
    public enum BlockSide{
        TOP("top"), BOTTOM("bottom"),LEFT("left"),RIGHT("right"),FRONT("front"),BACK("back"),NONE("none");
        String s;
        BlockSide(String s){
            this.s = s;
        }

        @Override
        public String toString() {
            return s;
        }
    }

    private BlockSide getBlockSide(float closestDistance, AABBf box){
        return BlockSide.NONE;
    }
    private Camera getCamera(){
        return Camera.active;
    }

    static Model cube;
}
