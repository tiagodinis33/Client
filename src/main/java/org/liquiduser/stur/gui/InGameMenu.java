package org.liquiduser.stur.gui;

import org.liquiduser.stur.engine.Texture;
import org.liquiduser.stur.ioutils.Input;
import org.liquiduser.stur.render.engine.Camera;
import org.liquiduser.stur.render.text.Font;
import org.liquiduser.stur.voxel.tiles.Tile;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

public class InGameMenu implements GuiScreen {



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
                    , 0, 0, Color.yellow);

            }
        GuiRenderer.renderTexture((int)(getEngine().getWidth()/2)-25/2,(int)(getEngine().getHeight()/2)-25/2,25,25, 0,0,"gui/mira");
        GuiRenderer.renderTexture((int)getEngine().getWidth()/2-(360/2),(int)getEngine().getHeight()-40,360,40,0,0,"gui/hotbar");
        GuiRenderer.renderTexture(((int)getEngine().getWidth()/2-(360/2))+(40*getEngine().thePlayer.getSelectedSlot()),(int)getEngine().getHeight()-40,40,40,0,40,"gui/hotbar");
        for (int i = 0; i<9; i++){
            Tile selectedTile = getEngine().thePlayer.getInventory().get(i);
            if(selectedTile.getId() == 0) continue;
            GuiRenderer.renderTexture(((int)getEngine().getWidth()/2-(360/2))+(40*i)+5,(int)getEngine().getHeight()-35, 16,16,30,30, selectedTile.getTexCoordsfront().x*16,selectedTile.getTexCoordsfront().y*16,"blocks/spritesheet");

        }

    }
    @Override
    public void update() {
        if(Input.isKeyPressed(GLFW_KEY_F3)){
            renderText = !renderText;
        }

    }

    @Override
    public void initGui() {

    }
}
