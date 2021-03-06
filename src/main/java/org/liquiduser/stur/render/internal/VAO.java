package org.liquiduser.stur.render.internal;

import java.util.ArrayList;
import java.util.List;

import org.liquiduser.stur.engine.Resource;

import static org.lwjgl.opengl.GL33.*;
public class VAO extends Resource {

    private List<VBO> buffers = new ArrayList<VBO>();
    private int vaoID;
    public int getVaoID() {
        return vaoID;
    }
    public VAO() {
    }
    @Override
    public void create() {
        vaoID = glGenVertexArrays();
        for(VBO buffer : buffers){
            addVBO(buffer);
        }

    }
    @Override
    public void cleanup() {
        super.cleanup();
        for(VBO buffer : buffers){
            if(buffer.getId() == 0){
                continue;
            }
            buffer.cleanup();
        }
        if(vaoID != 0)
        glDeleteVertexArrays(vaoID);
    }
    public void removeVBO(int slot) throws IndexOutOfBoundsException{
        buffers.get(slot).cleanup();
        buffers.remove(slot);
    }
    public void addVBO(VBO vbo) throws IllegalStateException{
        if(vaoID == 0){
            throw new IllegalStateException("O ID não pode ser 0 ao adicionar um VBO!! Isto pode ser causado pela VAO ter sido limpo ou não foi criado!");
        }
        glBindVertexArray(vaoID);
        if(vbo.getId() != 0){
            vbo.update();
        } else{
            vbo.create();
        }
        if(buffers.size() > vbo.getSlot() + 1) {
        	if(buffers.get(vbo.getSlot()) != null) {
            	removeVBO(vbo.getSlot());
            }
        }
        if(!buffers.contains(vbo)) {
            buffers.add(vbo.getSlot(), vbo);
        }
        glBindVertexArray(0);
    }
    public List<VBO> getBuffers() {
        return buffers;
    }
    public boolean isCreated() {
        return getVaoID() != 0;
    }
}