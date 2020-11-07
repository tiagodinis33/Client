package org.liquiduser.stur.math;

import org.joml.Vector2i;
import org.joml.Vector4f;
import org.liquiduser.stur.voxel.tiles.Tile;


public class MathUtils {
    public static Float[] createCubeShape(float x, float y, float z, float size){
        return new Float[]{

                x,y,z,                 //-1.0f,-1.0f,-1.0f,
                x,y, z+size,           //-1.0f,-1.0f, 1.0f,
                x, y+size, z+size,     //-1.0f, 1.0f, 1.0f,
                x+size, y+size,z,      // 1.0f, 1.0f,-1.0f,
                x,y,z,                 //-1.0f,-1.0f,-1.0f,
                x, y+size,z,           //-1.0f, 1.0f,-1.0f,
                x+size,y,       z+size,// 1.0f,-1.0f, 1.0f,
                x,     y,      z,      //-1.0f,-1.0f,-1.0f,
                x+size,y,      z,      // 1.0f,-1.0f,-1.0f,
                x+size, y+size,z,      // 1.0f, 1.0f,-1.0f,
                x+size,y,      z,      // 1.0f,-1.0f,-1.0f,
                x,     y,      z,      //-1.0f,-1.0f,-1.0f,
                x,     y,      z,      //-1.0f,-1.0f,-1.0f,
                x,     y+size,  z+size,//-1.0f, 1.0f, 1.0f,
                x,     y+size, z,      //-1.0f, 1.0f,-1.0f,
                x+size,y,       z+size,// 1.0f,-1.0f, 1.0f,
                x,     y,       z+size,//-1.0f,-1.0f, 1.0f,
                x,     y,      z,      //-1.0f,-1.0f,-1.0f,
                x,      y+size, z+size,//-1.0f, 1.0f, 1.0f,
                x,     y,       z+size,//-1.0f,-1.0f, 1.0f,
                x+size,y,       z+size,// 1.0f,-1.0f, 1.0f,
                x+size, y+size, z+size,// 1.0f, 1.0f, 1.0f,
                x+size,y,      z,      // 1.0f,-1.0f,-1.0f,
                x+size, y+size,z,      // 1.0f, 1.0f,-1.0f,
                x+size,y,       z,     // 1.0f,-1.0f,-1.0f,
                x+size, y+size, z+size,// 1.0f, 1.0f, 1.0f,
                x+size,y,       z+size,// 1.0f,-1.0f, 1.0f,
                x+size, y+size, z+size,// 1.0f, 1.0f, 1.0f,
                x+size, y+size,z,      // 1.0f, 1.0f,-1.0f,
                x, y+size,z,           //-1.0f, 1.0f,-1.0f,
                x+size, y+size, z+size,// 1.0f, 1.0f, 1.0f,
                x, y+size,z,           //-1.0f, 1.0f,-1.0f,
                x, y+size,      z+size,//-1.0f, 1.0f, 1.0f,
                x+size, y+size, z+size,// 1.0f, 1.0f, 1.0f,
                x, y+size,      z+size,//-1.0f, 1.0f, 1.0f,
                x+size,y,       z+size // 1.0f,-1.0f, 1.0f 


        };
    }

    public static Float[] getCubeColors(byte b) {
        Vector4f color = Tile.getTileById(b).getColor();
        return new Float[]{
            color.x,color.y,color.z,color.w,
            color.x,color.y,color.z,color.w,
            color.x,color.y,color.z,color.w,
            color.x,color.y,color.z,color.w,
            color.x,color.y,color.z,color.w,
            color.x,color.y,color.z,color.w,
                color.x,color.y,color.z,color.w,
            color.x,color.y,color.z,color.w,
            color.x,color.y,color.z,color.w,
            color.x,color.y,color.z,color.w,
            color.x,color.y,color.z,color.w,
            color.x,color.y,color.z,color.w,
                color.x,color.y,color.z,color.w,
            color.x,color.y,color.z,color.w,
            color.x,color.y,color.z,color.w,
            color.x,color.y,color.z,color.w,
            color.x,color.y,color.z,color.w,
            color.x,color.y,color.z,color.w,
                color.x,color.y,color.z,color.w,
            color.x,color.y,color.z,color.w,
            color.x,color.y,color.z,color.w,
            color.x,color.y,color.z,color.w,
            color.x,color.y,color.z,color.w,
            color.x,color.y,color.z,color.w,
                color.x,color.y,color.z,color.w,
            color.x,color.y,color.z,color.w,
            color.x,color.y,color.z,color.w,
            color.x,color.y,color.z,color.w,
            color.x,color.y,color.z,color.w,
            color.x,color.y,color.z,color.w,
                color.x,color.y,color.z,color.w,
            color.x,color.y,color.z,color.w,
            color.x,color.y,color.z,color.w,
            color.x,color.y,color.z,color.w,
            color.x,color.y,color.z,color.w,
            color.x,color.y,color.z,color.w,

        };
    }

    public static Float[] getTexCoords(byte b, int size, int imageSize) {
        Vector2i coords = Tile.getTileById(b).getTexCoords();
        float x = (float)(coords.x * size)/(float)imageSize;
        float y = 1.0f-(float)(coords.y * size)/(float)imageSize;
        float xRight = (float)(coords.x * size+16)/(float)imageSize;
        float yBottom = 1.0f-(float)(coords.y * size+16)/(float)imageSize;
        return new Float[]{
                x, y,
                x, yBottom,
                xRight, yBottom,
                xRight, y,
                xRight, yBottom,
                xRight, yBottom,

                x, y,
                x, yBottom,
                xRight, yBottom,
                xRight, y,
                xRight, yBottom,
                xRight, yBottom,

                x, y,
                x, yBottom,
                xRight, yBottom,
                xRight, y,
                xRight, yBottom,
                xRight, yBottom,

                x, y,
                x, yBottom,
                xRight, yBottom,
                xRight, y,
                xRight, yBottom,
                xRight, yBottom,

                x, y,
                x, yBottom,
                xRight, yBottom,
                xRight, y,
                xRight, yBottom,
                xRight, yBottom,

                x, y,
                x, yBottom,
                xRight, yBottom,
                xRight, y,
                xRight, yBottom,
                xRight, yBottom,

        };
    }
}
