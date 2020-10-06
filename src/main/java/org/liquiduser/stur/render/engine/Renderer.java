package org.liquiduser.stur.render.engine;

import static org.lwjgl.opengl.GL33.*;

import java.util.List;

import org.liquiduser.Stur;
import org.liquiduser.stur.engine.Model;
import org.liquiduser.stur.render.internal.VBO;
import org.joml.Matrix4f;

public class Renderer {
    public List<Model> models;

    public Renderer(List<Model> models) {
        this.models = models;
    }

    Matrix4f getPerspective() {
        return new Matrix4f().perspective(70, Stur.getEngine().getWidth() / Stur.getEngine().getHeight(),
                0.1f, 1000f);
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
    public synchronized void render() {
        for (Model model : models) {
            glEnable(GL_DEPTH_TEST);
            glDepthFunc(GL_LESS);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glEnable(GL_CULL_FACE);
            glBindVertexArray(model.getVaoID());
            for (VBO buffer : model.getBuffers()) {
                glEnableVertexAttribArray(buffer.getSlot());
            }

            model.getProgram().bind();
            model.getProgram().setUniform("material", model.getMaterial());
            if(model.getMaterial().getTexture() != null){
                model.getMaterial().getTexture().bind();
            }
            model.getProgram().setUniform("perspective", getPerspective());
            model.getProgram().setUniform("camera", Camera.getMatrix());
            model.getProgram().setUniform("transform", model.getTransformationMatrix());
            
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, model.getIndex().getId());
            model.getOnUpdate().run();
            Camera.active.getOnUpdate().run();
            glDrawElements(GL_TRIANGLES, model.getIndex().getArray().size(),
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
            glDisable(GL_CULL_FACE);
        }
    }
}