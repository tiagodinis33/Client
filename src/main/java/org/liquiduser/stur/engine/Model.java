package org.hawkstudios.jogo.engine;

import org.hawkstudios.jogo.render.engine.GLSLProgram;
import org.hawkstudios.jogo.render.engine.RawModel;
import org.hawkstudios.jogo.render.internal.VBO;


public class Model extends RawModel {
    public Model(RawModel mRawModel) throws IllegalArgumentException{
        super(mRawModel.getIndex(), mRawModel.getProgram());
        for(VBO vbo : mRawModel.getBuffers())
            super.addVBO(vbo);
        super.setPosition(mRawModel.getPosition());
        super.setRotation(mRawModel.getPosition());
        super.setScale(mRawModel.getScale());
    }
    private Runnable onUpdate = new Runnable() {
    	public void run() {}
    };
    /**
     * 
     * Define o metodo de update
     * Esse metodo é executado cada vez que este modelo que é renderizado.
     * Mais tarde será obsoleto e será substituido por scripts JavaScript
     * 
     * @param onUpdate O Novo metodo
     **/
    public void setOnUpdate(Runnable onUpdate) {
		this.onUpdate = onUpdate;
	}
    public Runnable getOnUpdate() {
    	return onUpdate;
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