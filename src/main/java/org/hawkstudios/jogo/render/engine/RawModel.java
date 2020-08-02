package org.hawkstudios.jogo.render.engine;

import org.hawkstudios.jogo.render.internal.VAO;
import org.hawkstudios.jogo.render.internal.VBO;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class RawModel extends VAO {
    protected RawModel(){}
    public RawModel(VBO index, GLSLProgram program) throws IllegalArgumentException{
        super();
        if(index == null || program == null){
            throw new NullPointerException("O index e shader não podem ser null!");
        }
        if(!index.isIndex()){
            throw new IllegalArgumentException("O VBO passado não é um index!!");
        }
        this.program = program;
        this.index = index;
    }
    private VBO index;
    private GLSLProgram program;
    private Vector3f position = new Vector3f();
    private Vector3f rotation = new Vector3f();
    private Vector3f scale = new Vector3f(1);
    public void setPosition(Vector3f position) {
        this.position = position;
    }
    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }
    public void setScale(Vector3f scale) {
        this.scale = scale;
    }
    public Vector3f getPosition() {
        return position;
    }
    public Vector3f getRotation() {
        return rotation;
    }
    public Vector3f getScale() {
        return scale;
    }
    public Matrix4f getTransformationMatrix(){
        return new Matrix4f()
        .translate(position)
        .rotateYXZ(new Vector3f((float)Math.toRadians(rotation.x),(float)Math.toRadians(rotation.y),(float)Math.toRadians(rotation.z)))
        .scale(scale);
    }

    public GLSLProgram getProgram() {
        return program;
    }
    public VBO getIndex() {
        return index;
    }
    @Override
    public void create() {
        super.create();
        index.create();
    }
	public VBO getModelVBO() {
        for(VBO vbo: getBuffers()){
            if(vbo instanceof PositionVBO){
                return vbo;
            }
        }
		return null;
	}
    
}