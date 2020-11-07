package org.liquiduser.stur.gui;

import org.liquiduser.stur.engine.Texture;
import org.liquiduser.stur.ioutils.Input;
import org.liquiduser.stur.render.engine.Camera;
import org.liquiduser.stur.render.text.Font;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

public class InGameMenu implements GuiScreen {

    private double oldMouseX;
    private double oldMouseY;
    private float vel= 0.2f;
    private float mouseSensitivity = 0.2f;


    Font font= Font.ARIAL;
    boolean renderText = true;
    @Override
    public void render() {
        if (renderText) {
            font.drawText("X: " + Camera.active.getPosition().x
                    + "  Y: " + Camera.active.getPosition().y
                    + "   Z: " + Camera.active.getPosition().z
                    + "\nRotation X: "+Camera.active.getRotation().x
                    + "   Rotation Y: "+Camera.active.getRotation().y
                    + "   Rotation Z: "+Camera.active.getRotation().z
                    + "\nFPS: " + getEngine().getFps()
                    , 0, 0, Color.WHITE);

            }
        GuiRenderer.renderTexture((int)(getEngine().getWidth()/2)-25/2,(int)(getEngine().getHeight()/2)-25/2,25,25, 0,0,"gui/mira");

    }
    @Override
    public void update() {

        float v = Input.isKeyDown(GLFW_KEY_LEFT_CONTROL)? vel * 2 : vel;
        if(Input.isKeyPressed(GLFW_KEY_F3)){
            renderText = !renderText;
        }
        if (Input.isKeyPressed(GLFW_KEY_F) ) {
            Input.setMouseLocked(!Input.isMouseLocked());
        }
        if (Input.isKeyPressed(GLFW_KEY_F11) ) {
            getEngine().setFullscreen(!getEngine().isFullscreen());
        }
        float x = (float) Math.sin(Math.toRadians(Camera.active.getRotation().y)) * v;
        float z = (float) Math.cos(Math.toRadians(Camera.active.getRotation().y)) * v;

        double newMouseX = (float) getMouseX();
        double newMouseY = (float) getMouseY();

        if (Input.isKeyDown(GLFW_KEY_W)) {
            Camera.active.getPosition().x -= x;
            Camera.active.getPosition().z -= z;
        }
        if (Input.isKeyDown(GLFW_KEY_S)) {
            Camera.active.getPosition().x -= -x;
            Camera.active.getPosition().z -= -z;
        }
        if (Input.isKeyDown(GLFW_KEY_A)) {
            Camera.active.getPosition().x -= z;
            Camera.active.getPosition().z -= -x;
        }
        if (Input.isKeyDown(GLFW_KEY_D) ) {
            Camera.active.getPosition().x -= -z;
            Camera.active.getPosition().z -= x;
        }
        if (Input.isKeyDown(GLFW_KEY_SPACE)) {
            Camera.active.getPosition().y -= -vel;
        }
        if (Input.isKeyDown(GLFW_KEY_LEFT_SHIFT)
                || Input.isKeyDown(GLFW_KEY_RIGHT_SHIFT)) {
            Camera.active.getPosition().y -= vel;
        }

        float dx = (float) (newMouseX - oldMouseX);
        float dy = (float) (newMouseY - oldMouseY);

        if (Input.isMouseLocked()) {
            Camera.active.getRotation().x = Math.max(-90f,
                    Math.min(90f, Camera.active.getRotation().x + (-dy * mouseSensitivity)));
            Camera.active.getRotation().y = Camera.active.getRotation().y + (-dx * mouseSensitivity);

        }
        oldMouseX = newMouseX;
        oldMouseY = newMouseY;
    }

    @Override
    public void initGui() {

    }
}
