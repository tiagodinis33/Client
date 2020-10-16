package org.liquiduser.stur.render.engine;

import static org.lwjgl.opengl.GL33.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joml.Vector3f;
import org.liquiduser.Stur;
import org.liquiduser.stur.engine.Model;
import org.liquiduser.stur.lighning.Light;
import org.liquiduser.stur.render.internal.VBO;
import org.joml.Matrix4f;

public class Renderer {
    public List<Model> models;

    public Renderer(List<Model> models) {
        this.models = models;

    }
    public Renderer(Model... models) {

        this.models = Arrays.asList(models);
    }
    Matrix4f projection = new Matrix4f().perspective(70, Stur.getEngine().getWidth() / Stur.getEngine().getHeight(),
            0.1f, 1000f);
    public Matrix4f getProjection() {
        return projection;
    }
    boolean depthTestingEnabled = true;

    public boolean isDepthTestingEnabled() {
        return depthTestingEnabled;
    }

    public void setDepthTestingEnabled(boolean depthTestingEnabled) {
        this.depthTestingEnabled = depthTestingEnabled;
    }

    public void setProjection(Matrix4f p){
        this.projection = p;
    }
    ArrayList<Light> lights = new ArrayList<>();

    public ArrayList<Light> getLights() {
        return lights;
    }

    public List<Model> getModels() {
        return models;
    }

    public void setLights(ArrayList<Light> lights) {
        this.lights = lights;
    }
    private int drawmode = GL_TRIANGLES;
    public void setDrawMode(int dm){
        drawmode = dm;
    }
    /**
     * Define os modelos a serem renderizados,
     * é muito util para dar impressão que entrou em outra cena
     * 
     * @param models Nova ArrayList de Modelos
     */
    public void setModels(List<Model> models) {
		this.models = models;
	}
    public boolean is2D;
    public void render() {

        for (Model model : models) {
            if(depthTestingEnabled)
                glEnable(GL_DEPTH_TEST);
            glDepthFunc(GL_LESS);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glBindVertexArray(model.getVaoID());
            for (VBO buffer : model.getBuffers()) {
                glEnableVertexAttribArray(buffer.getSlot());
            }

            model.getProgram().bind();
            if(model.getProgram().getLoc().contains("light")) {
                model.getProgram().setUniform("lightN", getLights().size());
                model.getProgram().setUniform("lightsPos", getLights());
                model.getProgram().setUniform("viewPos",Camera.active.getPosition());
            }
            model.getProgram().setUniform("material", model.getMaterial());
            if(model.getMaterial().getTexture() != null){
                model.getMaterial().getTexture().bind();
            }
            model.getProgram().setUniform("perspective", getProjection());

            model.getProgram().setUniform("camera", is2D?new Matrix4f():Camera.getMatrix());
            model.getProgram().setUniform("transform", model.getTransformationMatrix());
            
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, model.getIndex().getId());


            glDrawElements(drawmode, model.getIndex().getArray().size(),
                    GL_UNSIGNED_INT, 0);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
            if(model.getMaterial().getTexture() != null){
                model.getMaterial().getTexture().unbind();
            }
            model.getProgram().unbind();
            for (VBO buffer : model.getBuffers()) {
                glDisableVertexAttribArray(buffer.getSlot());
            }
            glBindVertexArray(0);
            glDisable(GL_DEPTH_TEST);
            glDisable(GL_BLEND);
        }
    }
}