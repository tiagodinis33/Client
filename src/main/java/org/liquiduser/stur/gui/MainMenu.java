package org.liquiduser.stur.gui;

import org.liquiduser.stur.engine.Texture;
import org.liquiduser.stur.ioutils.Input;
import org.liquiduser.stur.render.engine.Camera;
import org.liquiduser.stur.render.text.Font;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.io.IOException;

public class MainMenu implements GuiScreen {

    Texture t;
    {
        try {
            t = new Texture("misc/test");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    boolean m = false;
    Font font= Font.MINECRAFT;
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
                    , 0, 0, Color.WHITE);
            }
    }

    @Override
    public void update() {
        if(Input.isKeyDown(GLFW.GLFW_KEY_F3) && !m){
            renderText = !renderText;
        }
        m = Input.isKeyDown(GLFW.GLFW_KEY_F3);
    }

    @Override
    public void initGui() {

    }
}
