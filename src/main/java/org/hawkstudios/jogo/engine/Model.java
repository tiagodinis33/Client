package org.hawkstudios.jogo.engine;

import org.hawkstudios.jogo.render.engine.GLSLProgram;
import org.hawkstudios.jogo.render.engine.RawModel;
import org.hawkstudios.jogo.render.engine.VBO;
import org.joml.Vector3f;

public class Model extends RawModel {
    public Model(RawModel mRawModel) throws IllegalArgumentException{
        super(mRawModel.getIndex(), mRawModel.getProgram());
        for(VBO vbo : mRawModel.getBuffers())
            super.addVBO(vbo);
        super.setPosition(mRawModel.getPosition());
        super.setRotation(mRawModel.getPosition());
        super.setScale(mRawModel.getScale());
    }
    private Material material = new Material();
    public Material getMaterial() {
        return material;
    }
    public void setMaterial(Material material) {
        this.material = material;
    }
    public Model(VBO index, GLSLProgram program) throws IllegalArgumentException {
        super(index, program);
    }
    
}