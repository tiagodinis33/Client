package org.liquiduser.stur.gui;

import org.liquiduser.stur.engine.Texture;
import org.liquiduser.stur.render.text.Font;

import java.awt.*;
import java.io.IOException;

public class MainMenu implements GuiScreen {

    Texture t;
    {
        try {
            t = new Texture("misc/default");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    Font font= new Font(70);
    @Override
    public void render() {

        font.drawText("Teste", 0,0, Color.WHITE);
    }

    @Override
    public void update() {

    }

    @Override
    public void initGui() {

    }
}
