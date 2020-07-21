package org.hawkstudios.jogo.render.engine;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.util.List;

import org.hawkstudios.jogo.Engine;
import org.hawkstudios.jogo.engine.Model;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL33;

public class Renderer {
    public List<Model> models;

    public Renderer(List<Model> models) {
        this.models = models;
    }

    Matrix4f getPerspective() {
        return new Matrix4f().perspective(70, Engine.getEngine().getWidth() / Engine.getEngine().getHeight(),
                0.1f, 1000f);
    }

    public void render() {
        for (Model model : models) {
            glEnable(GL_DEPTH_TEST);
            glDepthFunc(GL_LESS);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
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
            glDrawElements(GL33.GL_TRIANGLES, model.getIndex().getArray().size(),
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
        }
    }
}