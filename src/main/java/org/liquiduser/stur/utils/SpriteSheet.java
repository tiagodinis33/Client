package org.liquiduser.stur.utils;

import org.liquiduser.stur.engine.Texture;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class SpriteSheet extends Texture {
    private int size;


    public SpriteSheet(String path, int size) throws IOException {
        super(path);
        this.size = size;

    }
    {
        if (getHeight() != getWidth()){
            throw new IllegalArgumentException("Illegal spritesheet size.");
        }
    }
    public SpriteSheet(BufferedImage i, int size){
        super(i);
        this.size = size;

    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
