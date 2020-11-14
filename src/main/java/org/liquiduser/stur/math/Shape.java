package org.liquiduser.stur.math;

import org.joml.Vector2i;
import org.joml.Vector4f;
import org.liquiduser.stur.voxel.tiles.Tile;

import java.util.ArrayList;
import java.util.Arrays;


public class Shape {


    public static Float[] createCubeShape(float x,
                                          float y,
                                          float z,
                                          float size,
                                          boolean front,
                                          boolean back,
                                          boolean right ,
                                          boolean left,
                                          boolean top,
                                          boolean bottom){
        ArrayList<Float> verts = new ArrayList<>();
        if(front){

            verts.addAll(Arrays.asList(
                    x+size, y+size,z,
                    x+size,y,      z,
                    x,     y,      z,

                    x+size, y+size,z,
                    x,y,z,
                    x, y+size,z));

        }

        if(left){
            verts.addAll(Arrays.asList(
                    x,     y,      z,
                    x,     y+size, z+size,
                    x,     y+size, z,
                    x,     y,      z,
                    x,     y,      z+size,
                    x,     y+size, z+size));
        }
        if(bottom){
            verts.addAll(Arrays.asList(
                    x+size,y,       z+size,
                    x,     y,       z+size,
                    x,     y,      z,
                    x+size,y,       z+size,
                    x,     y,      z,
                    x+size,y,      z));
        }
        if(right){
            verts.addAll(Arrays.asList(
                    x+size,     y+size, z+size,
                    x+size,     y,      z+size,
                    x+size,     y,      z,
                    x+size,     y+size, z,
                    x+size,     y+size, z+size,
                    x+size,     y,      z));
        }
        if(top){
            verts.addAll(Arrays.asList(x+size,y+size,      z,
                    x,     y+size,      z,
                    x+size,y+size,       z+size,
                    x,     y+size,      z,
                    x,     y+size,       z+size,
                    x+size,y+size,       z+size
                    ));
        }
        if(back){
            verts.addAll(Arrays.asList(
                    x+size, y+size, z+size,
                    x, y+size,      z+size,
                    x+size,y,       z+size,
                    x,      y+size, z+size,
                    x,     y,       z+size,
                    x+size,y,       z+size
            ));
        }

        return ArrayUtils.toArrayf(verts);
    }


    public static Float[] getTexCoords(byte b, int size, int imageSize,
                                       boolean front,
                                       boolean back,
                                       boolean right ,
                                       boolean left,
                                       boolean top,
                                       boolean bottom) {
        Vector2i coordsfront = Tile.getTileById(b).getTexCoordsfront();
        Vector2i coordsback = Tile.getTileById(b).getTexCoordsback();
        Vector2i coordstop = Tile.getTileById(b).getTexCoordstop();
        Vector2i coordsbottom = Tile.getTileById(b).getTexCoordsbottom();
        Vector2i coordsright = Tile.getTileById(b).getTexCoordsright();
        Vector2i coordsleft = Tile.getTileById(b).getTexCoordsleft();
        float xtop = (float)(coordstop.x * size)/(float)imageSize;
        float ytop = (float)(coordstop.y * size)/(float)imageSize;
        float xRighttop = (float)(coordstop.x * size+size)/(float)imageSize;
        float  yBottomtop = (float)(coordstop.y * size+size)/(float)imageSize;

        float xbottom = (float)(coordsbottom.x * size)/(float)imageSize;
        float ybottom = (float)(coordsbottom.y * size)/(float)imageSize;
        float xRightbottom = (float)(coordsbottom.x * size+size)/(float)imageSize;
        float  yBottombottom = (float)(coordsbottom.y * size+size)/(float)imageSize;

        float xright = (float)(coordsright.x * size)/(float)imageSize;
        float yright = (float)(coordsright.y * size)/(float)imageSize;
        float xRightright = (float)(coordsright.x * size+size)/(float)imageSize;
        float  yBottomright = (float)(coordsright.y * size+size)/(float)imageSize;

        float xleft = (float)(coordsleft.x * size)/(float)imageSize;
        float yleft = (float)(coordsleft.y * size)/(float)imageSize;
        float xRightleft = (float)(coordsleft.x * size+size)/(float)imageSize;
        float  yBottomleft = (float)(coordsleft.y * size+size)/(float)imageSize;

        float xfront = (float)(coordsfront.x * size)/(float)imageSize;
        float yfront = (float)(coordsfront.y * size)/(float)imageSize;
        float xRightfront = (float)(coordsfront.x * size+size)/(float)imageSize;
        float  yBottomfront = (float)(coordsfront.y * size+size)/(float)imageSize;

        float xback = (float)(coordsback.x * size)/(float)imageSize;
        float yback = (float)(coordsback.y * size)/(float)imageSize;
        float xRightback = (float)(coordsback.x * size+size)/(float)imageSize;
        float  yBottomback = (float)(coordsback.y * size+size)/(float)imageSize;
        ArrayList<Float> coords = new ArrayList<>();
        if(front){
            coords.addAll(Arrays.asList(
                    xfront, yfront,
                    xfront,yBottomfront,
                    xRightfront,     yBottomfront,
                    xfront, yfront,
                    xRightfront,yBottomfront,
                    xRightfront, yfront));
        }
        if(left){
            coords.addAll(Arrays.asList(
                    xleft,     yBottomleft,
                    xRightleft,     yleft,
                    xleft,     yleft,

                    xleft,yBottomleft,
                    xRightleft,yBottomleft,
                    xRightleft, yleft));
        }
        if(bottom){
            coords.addAll(Arrays.asList(
                    xRightbottom,yBottombottom,
                    xRightbottom,     ybottom,
                    xbottom,     ybottom,
                    xRightbottom,yBottombottom,
                    xbottom,     ybottom,
                    xbottom,yBottombottom));
        }
        if(right){
            coords.addAll(Arrays.asList(
                    xright, yright,
                    xright,yBottomright,
                    xRightright,yBottomright,
                    xRightright,     yright,
                    xright,     yright,
                    xRightright,     yBottomright));
        }
        if(top){
            coords.addAll(Arrays.asList(
                    xtop,yBottomtop,
                    xtop,     ytop,
                    xRighttop,yBottomtop,
                    xtop,     ytop,
                    xRighttop,     ytop,
                    xRighttop,yBottomtop));
        }
        if(back){
            coords.addAll(Arrays.asList(
                    xRightfront, yfront,
                    xfront, yfront,
                    xRightfront,yBottomfront,
                    xfront, yfront,
                    xfront,yBottomfront,
                    xRightfront,     yBottomfront));
        }
        return ArrayUtils.toArrayf(coords);
    }
}
