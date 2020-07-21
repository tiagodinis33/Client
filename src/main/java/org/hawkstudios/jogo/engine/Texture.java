package org.hawkstudios.jogo.engine;

import java.awt.image.*;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import static org.lwjgl.opengl.GL33.*;

public class Texture {
    private ByteBuffer buffer;
    private BufferedImage image;
    int id;
    int slot;
    public Texture() throws IOException {
        this("misc/default");
    }
    public Texture(BufferedImage image) {
        this.image = image;
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
        buffer = ByteBuffer.allocateDirect(image.getWidth() * image.getHeight() * 4);

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
        id= glGenTextures();
        glBindTexture(GL_TEXTURE_2D,id);
        if(MM.getId() == "nearest"){
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_NEAREST);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        }else if(MM.getId() == "linear"){
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        }
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(),
                 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        glGenerateMipmap(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D,0);
        return this;
    }


    public static enum MipMap{
        LINEAR("linear"),NEAREST("nearest");


        private String id; 

        public String getId() {
            return id;
        }
        private MipMap(String id){
            this.id = id;
        }
    }
}