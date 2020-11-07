package org.liquiduser.stur.gui;

import org.liquiduser.Stur;
import org.liquiduser.stur.ioutils.Input;

public interface GuiScreen {
    void render();
    void update();
    void initGui();
    default Stur getEngine(){
        return Stur.getEngine();
    }
    default int getMouseY(){
        return Input.getMouseY();
    }
    default int getMouseX(){
        return  Input.getMouseX();
    }
}
