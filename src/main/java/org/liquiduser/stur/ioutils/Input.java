package org.liquiduser.stur.ioutils;

import org.liquiduser.Stur;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import java.util.ArrayList;
import static org.lwjgl.glfw.GLFW.*;
public class Input {
    static ArrayList<Boolean> lastButtons = new ArrayList<>();
    static ArrayList<Boolean> lastKeys = new ArrayList<>();
    private static double scrollY;
    private static double scrollX;
    public static void create(){
        for(int i = 0; i< GLFW_KEY_LAST; i++){
            lastKeys.add(false);
        }
        for (int i = 0; i < GLFW_MOUSE_BUTTON_LAST; i++) {
            lastButtons.add(false);
        }
        glfwSetScrollCallback(getWindow(), (window, xoffset, yoffset) ->{
            scrollX = xoffset;
            scrollY = yoffset;
        });
        glfwSetCursorPosCallback(getWindow(), new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                cursorX = xpos;
                cursorY = ypos;
            }
        });
    }
    static double cursorX;
    static double cursorY;

    static boolean mouseLocked;
    public static boolean isMouseLocked(){
        return mouseLocked;
    }
    public static void setMouseLocked(boolean mouseLocked) {
        glfwSetInputMode(getWindow(), GLFW_CURSOR, mouseLocked ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);
        Input.mouseLocked = mouseLocked;
    }
    public static double getScrollY(){
        return scrollY;
    }
    public static double getScrollX(){
        return scrollX;
    }

    public static void update(){
        for(int i = 0; i< GLFW_KEY_LAST; i++){
            lastKeys.set(i, isKeyDown(i));
        }
        for (int i = 0; i < GLFW_MOUSE_BUTTON_LAST; i++) {
            lastButtons.set(i, isButtonDown(i));
        }
    }
    public static int getMouseY(){
        return (int)cursorY;
    }
    public static int getMouseX(){
        return (int)cursorX;
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
