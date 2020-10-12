package org.liquiduser.stur.render.internal;

import org.joml.Vector3f;
import org.liquiduser.stur.engine.Resource;
import org.liquiduser.stur.math.VectorMath;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.opengl.GL33.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VBO extends Resource {
    private int id;
    private int slot;
    private int vectorSize;
    private boolean isIndex;
    

    public boolean isIndex() {
        return isIndex;
    }

    private List<Float> array;

    public int getId() {
        return id;
    }

    public VBO(int slot, int vectorSize) {
        array = new ArrayList<>();
        this.slot = slot;
        this.vectorSize = vectorSize;
    }
    //Create a index
    public VBO() {
        array = new ArrayList<>();
        isIndex = true;
    }

    @Override
    public void create() {
        setId(glGenBuffers(), false);
        update();

    }
    public int getVectorSize() {
        return vectorSize;
    }
    /**
     * Adiciona um vector com 3 floats ao VBO
     * @implNote As alterações apenas são aplicadas usando o metodo {@link #update()}
     * @param x posição no x
     * @param y posição no y
     * @param z posição no z
     * @throws IllegalArgumentException caso esse VBO seja um Index
     */
    public void addVert3f(float x, float y, float z) {
        if (!isIndex()) {
            addValue(x);
            addValue(y);
            addValue(z);
        } else {
            throw new IllegalArgumentException("Não é possivel adicionar vertices em um Index!!");
        }
    }
    /**
     * Adiciona um vector com 2 floats ao VBO
     * @implNote As alterações apenas são aplicadas usando o metodo {@link #update()}
     * @param x posição no x
     * @param y posição no y
     * @throws UnsupportedOperationException caso esse VBO seja um Index
     */
    public void addVert2f(float x, float y) {
        if (!isIndex()) {
            addValue(x);
            addValue(y);
        } else {
            throw new UnsupportedOperationException("Não é possivel adicionar vertices em um Index!!");
        }
    }
    public void addValue(float x){
        array.add(x);
    }

    @Override
    public void cleanup() {
        if (getId() != 0)
            glDeleteBuffers(getId());
    }

    private void setId(int id, boolean cleanup) {
        if (cleanup) {
            cleanup();
        }
        this.id = id;
    }

    public int getBindedVao() {
        int[] bindingBuffer = new int[1];
        glGetIntegerv(GL_VERTEX_ARRAY_BINDING, bindingBuffer);

        return bindingBuffer[0];
    }
    /**
     * Atualiza o VBO no contexto do OpenGL <br/><br/>
     * 
     * Esse metodo sincroniza a array atual com o VBO dentro da GPU.
     */
    @Override
    public void update() {
        if (isIndex) {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
            
            int[] bufferData = new int[array.size()];
            for (int x = 0; x < array.size(); x++) {
                bufferData[x] = (int) array.get(x).floatValue();
            }
            IntBuffer data = MemoryUtil.memAllocInt(array.size()).put(bufferData);
            data.flip();
            glBufferData( GL_ELEMENT_ARRAY_BUFFER , data, GL_DYNAMIC_DRAW);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        } else {
            glBindBuffer(GL_ARRAY_BUFFER, id);
            float[] bufferData = new float[array.size()];
            for (int x = 0; x < array.size(); x++) {
                bufferData[x] = array.get(x);
            }
            FloatBuffer data = MemoryUtil.memAllocFloat(array.size()).put(bufferData);
            data.flip();
            glBufferData( GL_ARRAY_BUFFER , data, GL_DYNAMIC_DRAW);
            setupShaderPointer(false);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
        }
    }

    public List<Float> getArray() {
        return array;
    }

    public int getSlot() {
        return isIndex ? null : slot;
    }

    public void setupShaderPointer(boolean bind) {
        if (bind)
            glBindBuffer(isIndex ? GL_ELEMENT_ARRAY_BUFFER : GL_ARRAY_BUFFER, id);
        if (!isIndex)
            glVertexAttribPointer(slot, vectorSize, GL_FLOAT, false, 0, 0);
        if (bind)
            glBindBuffer(isIndex ? GL_ELEMENT_ARRAY_BUFFER : GL_ARRAY_BUFFER, 0);
    }

    public void setSlot(int slot) {
        if (!isIndex) {
            setupShaderPointer(true);
            this.slot = slot;
        }
    }

    public void set(Vector3f... vectors) {
        array = VectorMath.floatsFromVectorList(Arrays.asList(vectors));
        update();
    }
    public void set(Float... f){
        array = Arrays.asList(f);
        update();
    }
    public void set(Integer... i){
        array.clear();
        for (int ii = 0; ii < i.length ; ii++){
            array.add(ii,i[ii].floatValue());
        }
        update();
    }
}
