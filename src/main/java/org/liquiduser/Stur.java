package org.liquiduser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.liquiduser.stur.engine.Model;
import org.liquiduser.stur.engine.Resource;
import org.liquiduser.stur.engine.Texture;
import org.liquiduser.stur.gui.GuiScreen;
import org.liquiduser.stur.gui.MainMenu;
import org.liquiduser.stur.ioutils.Input;
import org.liquiduser.stur.lighning.Light;
import org.liquiduser.stur.math.VectorMath;
import org.liquiduser.stur.render.engine.Camera;
import org.liquiduser.stur.render.engine.GLSLProgram;
import org.liquiduser.stur.render.engine.PositionVBO;
import org.liquiduser.stur.render.engine.Renderer;
import org.liquiduser.stur.render.internal.VBO;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL33;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

final public class Stur extends Thread {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Stur engine;
    private final float mouseSensitivity = 0.2f;
    private final float vel = 0.1f;
    Renderer renderer;
    double cursorX;
    double cursorY;
    private float oldMouseY;
    private float oldMouseX;
    private float newMouseY;
    private float newMouseX;
    private boolean mouseLocked;
    private boolean fullscreen = false;
    private float width;
    private float height;
    private String title;
    private Long window;

    public Stur(int width, int height, String title) {
        this.title = title;
        this.height = height;
        this.width = width;
        Stur.engine = this;
    }

    public static Gson GSON() {
        return GSON;
    }

    public static Stur getEngine() {
        return engine == null ? new Stur(0, 0, "") : engine;
    }

    @Override
    public void run() {

        startGame();
        postStart();
        while (!glfwWindowShouldClose(getWindow())) {
            runGameLoop();
            update();
        }
        cleanup();
    }

    private void update() {
        glfwPollEvents();
        glfwSwapBuffers(window);
        for (Model model : renderer.models) {
            model.getOnUpdate().run();
        }
        if(guiScreen != null){
            guiScreen.update();
        }
        Input.update();
        Camera.active.getOnUpdate().run();
    }

    private void runGameLoop() {

        GL11.glClearColor(0.0f, 1.0f, 1.0f, 0.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        if (width != 0 && height != 0) {
            render();
        }
    }
    private void render(){
        renderWorld();
        renderGui();
    }
    private void renderGui(){
        if(guiScreen != null){
            guiScreen.render();
        }
    }
    private void renderWorld(){
        renderer.setProjection(new Matrix4f().perspective(70, Stur.getEngine().getWidth() / Stur.getEngine().getHeight(),
                0.1f, 1000f));
        renderer.render();
    }

    public void cleanup() {
        for (Resource resource : Resource.resources) {
            resource.cleanup();
        }
        glfwTerminate();
    }
    public void reverseArrayList(List<Float> alist)
    {
        for (int i = 0; i < alist.size() / 2; i++) {
            Float temp = alist.get(i);
            alist.set(i, alist.get(alist.size() - i - 1));
            alist.set(alist.size() - i - 1, temp);
        }
    }


    private void postStart() {
        displayGuiScreen(new MainMenu());
        ArrayList<Model> models = new ArrayList<>();
        VBO index = new VBO();
        index.create();
        index.set( 0, 1, 3, 3, 1, 2,
                1, 5, 2, 2, 5, 6,
                5, 4, 6, 6, 4, 7,
                4, 0, 7, 7, 0, 3,
                3, 2, 7, 7, 2, 6,
                4, 5, 0, 0, 5, 1);
        reverseArrayList(index.getArray());
        index.update();
        GLSLProgram shader = null;
        try {
            shader = new GLSLProgram("lightning");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert shader != null;
        shader.create();
        Model model = new Model(index, shader);
        PositionVBO vbo = new PositionVBO();
        model.create();
        GL33.glBindVertexArray(model.getVaoID());
        vbo.set(new Vector3f(-1, -1, -1),
                new Vector3f(1, -1, -1),
                new Vector3f(1, 1, -1),
                new Vector3f(-1, 1, -1),
                new Vector3f(-1, -1, 1),
                new Vector3f(1, -1, 1),
                new Vector3f(1, 1, 1),
                new Vector3f(-1, 1, 1));
        vbo.create();
        VBO normals = new VBO(2,3);

        model.addVBO(vbo);
        model.getMaterial().getColor().x = 1.0f;
        model.getMaterial().getColor().y = 0.0f;
        model.getMaterial().getColor().z = 1.0f;
        model.getMaterial().getColor().w = 1.0f;
        model.getRotation().x = -90;
        VBO textureCoords = new VBO(1,2);

        models.add(model);
        model.addVBO(textureCoords);
        model.addVBO(normals);
        textureCoords.set(0,0,0,1,1,0,1,1,0,0,0,1,1,0,1,1);
        normals.set(new Vector3f(-2, -2, -2),
                new Vector3f(2, -2, -2),
                new Vector3f(2, 2, -2),
                new Vector3f(-2, 2, -2),
                new Vector3f(-2, -2, 2),
                new Vector3f(2, -2, 2),
                new Vector3f(2, 2, 2),
                new Vector3f(-2, 2, 2));
        normals.update();
        Model lightModel = null;
        try {
            lightModel = new Model(index, GLSLProgram.get("simple"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert lightModel != null;
        lightModel.create();
        lightModel.getMaterial().setColor(new Vector4f(1));
        lightModel.addVBO(vbo);
        lightModel.getMaterial().getColor().set(1);
        models.add(lightModel);
        renderer = new Renderer(models);


        Model finalLightModel = lightModel;
        Camera.active.setOnUpdate(() -> {
           float x = (float) Math.sin(Math.toRadians(Camera.active.getRotation().y)) * vel;
            float z = (float) Math.cos(Math.toRadians(Camera.active.getRotation().y)) * vel;

            newMouseX = (float) cursorX;
            newMouseY = (float) cursorY;

            if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
                Camera.active.getPosition().x -= x;
                Camera.active.getPosition().z -= z;
            }
            if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
                Camera.active.getPosition().x -= -x;
                Camera.active.getPosition().z -= -z;
            }
            if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
                Camera.active.getPosition().x -= z;
                Camera.active.getPosition().z -= -x;
            }
            if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
                Camera.active.getPosition().x -= -z;
                Camera.active.getPosition().z -= x;
            }
            if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) {
                Camera.active.getPosition().y -= -vel;
            }
            if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS
                    || glfwGetKey(window, GLFW_KEY_RIGHT_SHIFT) == GLFW_PRESS) {
                Camera.active.getPosition().y -= vel;
            }
            if(glfwGetKey(window,GLFW_KEY_J) == GLFW_PRESS){
                model.getPosition().z+=vel;
            }
            if(glfwGetKey(window,GLFW_KEY_U) == GLFW_PRESS){
                model.getPosition().z-=vel;
            }
            if(glfwGetKey(window,GLFW_KEY_H) == GLFW_PRESS){
                model.getPosition().x-=vel;
            }
            if(glfwGetKey(window,GLFW_KEY_K) == GLFW_PRESS){
                model.getPosition().x+=vel;
            }
            if(glfwGetKey(window,GLFW_KEY_G) == GLFW_PRESS){
                model.getPosition().y-=vel;
            }
            if(glfwGetKey(window,GLFW_KEY_T) == GLFW_PRESS){
                model.getPosition().y+=vel;
            }

            float dx = newMouseX - oldMouseX;
            float dy = newMouseY - oldMouseY;

            if (isMouseLocked()) {
                Camera.active.getRotation().x = Math.max(-90f,
                        Math.min(90f, Camera.active.getRotation().x + (-dy * mouseSensitivity)));
                Camera.active.getRotation().y = Camera.active.getRotation().y + (-dx * mouseSensitivity);

            }
            oldMouseX = newMouseX;
            oldMouseY = newMouseY;

            finalLightModel.setPosition(light.getPos());


        });
        try {
            model.getMaterial().setTexture(Texture.get("misc/default", Texture.MipMap.LINEAR));
        } catch (IOException e) {
            e.printStackTrace();
        }
        renderer.getLights().add(light);
        renderer.models.add(lightModel);
        lightModel.getScale().mul(0.2f);
    }
    GuiScreen guiScreen;
    public void displayGuiScreen(GuiScreen gui){
        gui.initGui();
        guiScreen = gui;
    }
    Light light = new Light(new Vector3f(0,-0.5f,1),new Vector3f(1), 1f);
    public void setFullscreen(boolean fullscreen) {
        GLFWVidMode screen = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (fullscreen) {
            assert screen != null;
            glfwSetWindowMonitor(window, glfwGetPrimaryMonitor(), 0, 0, screen.width(), screen.height(), screen.refreshRate());
        } else {
            glfwSetWindowMonitor(window, 0, 0, 30, (int) width, (int) height, -1);


        }
        this.fullscreen = fullscreen;
    }

    private void startGame() {
        if (!glfwInit()) {
            System.out.println("Não foi possivel iniciar o GLFW!!");
        }
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);

        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, 1);
        GLFWVidMode screen = glfwGetVideoMode(glfwGetPrimaryMonitor());
        setWindow(glfwCreateWindow(screen.width(), screen.height(), title, 0, 0));
        assert window != 0;
        glfwShowWindow(getWindow());
        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwSetWindowSizeLimits(window, 800, 600, -1, -1);


        glfwSetCursorPosCallback(window, new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                cursorX = xpos;
                cursorY = ypos;
            }
        });
        glfwSetKeyCallback(window, new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == GLFW_KEY_F && action == GLFW_PRESS) {
                    setMouseLocked(!isMouseLocked());
                }
                if (key == GLFW_KEY_F11 && action == GLFW_PRESS) {
                    setFullscreen(!fullscreen);
                }
                if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS)
                    glfwSetWindowShouldClose(window, true);
            }
        });
        glfwSetWindowSizeCallback(window, new GLFWWindowSizeCallback() {

            @Override
            public void invoke(long window, int width, int height) {
                setWidth(width);
                setHeight(height);
                GL33.glViewport(0, 0, width, height);
            }

        });
        GL.createCapabilities();
    }

    public float getWidth() {
        return width;
    }
    public void setWidth(int width) {


        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(int height) {

        this.height = height;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        if (window != 0)
            glfwSetWindowTitle(window, title);

    }

    public long getWindow() {
        return window;
    }

    private void setWindow(long window) {
        this.window = window;
    }

    public boolean isMouseLocked() {
        return mouseLocked;
    }

    public void setMouseLocked(boolean mouseLocked) {
        glfwSetInputMode(window, GLFW_CURSOR, mouseLocked ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);
        this.mouseLocked = mouseLocked;
    }

}
