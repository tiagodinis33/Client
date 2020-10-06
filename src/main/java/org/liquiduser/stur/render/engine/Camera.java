package org.liquiduser.stur.render.engine;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    public static Camera active = new Camera(new Vector3f(),new Vector3f());
	private Vector3f position;
    private Vector3f rotation;
    
    public Camera(Vector3f pos, Vector3f rot) {
        position = pos;
        rotation = rot;
    }
    public Vector3f getPosition() {
        return position;
    }
    public Vector3f getRotation() {
        return rotation;
    }
    private Runnable onUpdate = new Runnable() {
    	public void run() {}
    };
    /**
     * 
     * Define o metodo de update
     * Esse metodo é executado cada vez que {@link Renderer#render} é executado. Obs: NAO SÃO TODOS OS OBJETOS CAMERA, APENAS O QUE ESTIVER EM {@link Camera#active} SERÁ CONSIDERADO
     * Mais tarde será obsoleto e será substituido por scripts JavaScript
     * 
     * 
     * @param onUpdate O Novo metodo
     **/
    public void setOnUpdate(Runnable onUpdate) {
		this.onUpdate = onUpdate;
	}
    public Runnable getOnUpdate() {
    	return onUpdate;
    }
    public static Matrix4f getMatrix() {
        Matrix4f matrixRot;
        Matrix4f matrixRotX = new Matrix4f().rotateX((float) Math.toRadians(-active.rotation.x));
        Matrix4f matrixRotY = new Matrix4f().rotateY((float) Math.toRadians(-active.rotation.y));
        Matrix4f matrixRotZ = new Matrix4f().rotateZ((float) Math.toRadians(-active.rotation.z));
        matrixRot = matrixRotX.mul(matrixRotY).mul(matrixRotZ);
		return matrixRot.mul(new Matrix4f().translate(new Vector3f(-active.position.x,-active.position.y,-active.position.z)));
	}
    
}