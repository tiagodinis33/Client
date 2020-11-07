package org.liquiduser.stur.engine;

import org.liquiduser.stur.render.engine.GLSLProgram;
import org.liquiduser.stur.render.engine.RawModel;
import org.liquiduser.stur.render.internal.VBO;


public class Model extends RawModel {
    public Model(RawModel mRawModel) {
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
    public Model(VBO index, GLSLProgram program)  {
        super(index, program);
    }
    public boolean isActive = true;

}