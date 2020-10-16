package org.liquiduser.stur.ioutils;

import org.liquiduser.Stur;

import java.util.ArrayList;
import static org.lwjgl.glfw.GLFW.*;
public class Input {
    static ArrayList<Boolean> lastButtons = new ArrayList<>();
    static ArrayList<Boolean> lastKeys = new ArrayList<>();
    static {
        for(int i = 0; i< GLFW_KEY_LAST; i++){
            lastKeys.add(false);
        }
        for (int i = 0; i < GLFW_MOUSE_BUTTON_LAST; i++) {
            lastButtons.add(false);
        }



    }
    public static void update(){
        for(int i = 0; i< GLFW_KEY_LAST; i++){
            lastKeys.set(i, isKeyDown(i));
        }
        for (int i = 0; i < GLFW_MOUSE_BUTTON_LAST; i++) {
            lastButtons.set(i, isButtonDown(i));
        }
    }
    private static long getWindow(){
        return Stur.getEngine().getWindow();
    }
    public static boolean isKeyDown(int k){
        return glfwGetKey(getWindow(), k) == GLFW_PRESS;
    }
    public static boolean isKeyPressed(int k){
        return isKeyDown(k) && !lastKeys.get(k);
    }
    public static boolean isButtonPressed(int b){
        return isButtonDown(b) && !lastButtons.get(b);
    }
    public static boolean isButtonDown(int b){
        return glfwGetMouseButton(getWindow(), b) == GLFW_PRESS;
    }
}
