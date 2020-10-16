package org.liquiduser.stur.engine;

import org.lwjgl.system.MemoryUtil;

import java.awt.image.*;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import static org.lwjgl.opengl.GL33.*;

public class Texture extends Resource {
    private final ByteBuffer buffer;
    private final BufferedImage image;
    int id;
    final int width;
    final int height;
    int slot;
    public Texture() throws IOException {
        this("misc/default");
    }

    @Override
    public void create() {
        super.create();
        create(MipMap.LINEAR);

    }
    @Override
    public void cleanup(){
        super.cleanup();
        glDeleteTextures(id);

    }
    public Texture(BufferedImage image) {
        this.image = image;
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
        buffer = ByteBuffer.allocateDirect(image.getWidth() * image.getHeight() * 4);
        width = image.getWidth();
        height = image.getHeight();
        for (int h = 0; h < image.getHeight(); h++) {
            for (int w = 0; w < image.getWidth(); w++) {
                int pixel = pixels[h * image.getWidth() + w];

                buffer.put((byte) ((pixel >> 16) & 0xFF));
                buffer.put((byte) ((pixel >> 8) & 0xFF));
                buffer.put((byte) (pixel & 0xFF));
                buffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }

        buffer.flip();
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getSlot() {
        return slot;
    }
    public void setSlot(int slot) {
        unbind();
        this.slot = slot;
        bind();
    }
    public Texture(String path) throws IOException {
        this(ImageIO.read(Texture.class.getResourceAsStream("/assets/textures/" + path + ".png")));
    }

    public static Texture get(String loc, MipMap mipMap, int slot) throws IOException {
        return new Texture(loc).create(mipMap,slot);
    }

    public static Texture get(String loc, MipMap mipMap) throws IOException {
        return new Texture(loc).create(mipMap);
    }

    public Texture create(Texture.MipMap MM, int slot){
        this.slot = slot;
        create(MM);
        return this;
    }
    public void bind()
    {
        glActiveTexture(GL_TEXTURE0 + slot);
        glBindTexture(GL_TEXTURE_2D, id);
    }
    public void unbind()
    {
        glActiveTexture(GL_TEXTURE0 + slot);
        glBindTexture(GL_TEXTURE_2D, 0);
        
    }
    public Texture create(Texture.MipMap MM){
        if( id != 0){
            throw new IllegalStateException("Textura ja foi criada, não é possivel criar outra vez");
        }
        id= glGenTextures();
        glBindTexture(GL_TEXTURE_2D,id);
        if(MM.getId().equals("nearest")){
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_NEAREST);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        }else if(MM.getId().equals("linear")){
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        }
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(),
                 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glGenerateMipmap(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D,0);
        return this;
    }


    public enum MipMap{
        LINEAR("linear"),NEAREST("nearest");


        private final String id;

        public String getId() {
            return id;
        }
        MipMap(String id){
            this.id = id;
        }
    }
}