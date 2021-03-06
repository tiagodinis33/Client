package org.liquiduser.stur.render.engine;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    public static Camera active = new Camera(new Vector3f(),new Vector3f());
	private final Vector3f position;
    private final Vector3f rotation;
    
    public Camera(Vector3f pos, Vector3f rot) {
        position = pos;
        rotation = rot;
    }
    public Camera(){
        position = new Vector3f();
        rotation = new Vector3f();
    }
    public Vector3f getPosition() {
        return position;
    }
    public Vector3f getRotation() {
        return rotation;
    }

    public static Matrix4f getMatrixS() {
        Matrix4f matrixRot;
        Matrix4f matrixRotX = new Matrix4f().rotateX((float) Math.toRadians(-active.rotation.x));
        Matrix4f matrixRotY = new Matrix4f().rotateY((float) Math.toRadians(-active.rotation.y));
        Matrix4f matrixRotZ = new Matrix4f().rotateZ((float) Math.toRadians(-active.rotation.z));
        matrixRot = matrixRotX.mul(matrixRotY).mul(matrixRotZ);
		return matrixRot.mul(new Matrix4f().translate(new Vector3f(-active.position.x,-active.position.y,-active.position.z)));
	}public Matrix4f getMatrix() {
        Matrix4f matrixRot;
        Matrix4f matrixRotX = new Matrix4f().rotateX((float) Math.toRadians(-rotation.x));
        Matrix4f matrixRotY = new Matrix4f().rotateY((float) Math.toRadians(-rotation.y));
        Matrix4f matrixRotZ = new Matrix4f().rotateZ((float) Math.toRadians(-rotation.z));
        matrixRot = matrixRotX.mul(matrixRotY).mul(matrixRotZ);
		return matrixRot.mul(new Matrix4f().translate(new Vector3f(-position.x,-position.y,-position.z)));
	}
    
}