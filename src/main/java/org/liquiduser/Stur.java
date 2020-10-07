package org.liquiduser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joml.Vector3f;
import org.liquiduser.stur.engine.Model;
import org.liquiduser.stur.engine.Resource;
import org.liquiduser.stur.engine.Texture;
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
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

final public class Stur extends Thread {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Stur engine;
    private final float mouseSensitivity = 0.2f;
    private final float vel = 0.05f;
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
    private long window;

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
    }

    private void runGameLoop() {
        //GL11.glClearColor(0.0f, 1.0f, 1.0f, 1.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        if (width != 0 && height != 0)
            renderer.render();
    }

    public void cleanup() {
        for (Resource resource : Resource.resources) {
            resource.cleanup();
        }
        glfwTerminate();
    }

    private void postStart() {


        ArrayList<Model> models = new ArrayList<>();
        VBO index = new VBO();
        index.create();
        index.addValue(0);
        index.addValue(1);
        index.addValue(2);
        index.addValue(2);
        index.addValue(3);
        index.addValue(0);

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
        PositionVBO vbo = new PositionVBO(0, 3);
        model.create();
        GL33.glBindVertexArray(model.getVaoID());
        vbo.create();
        vbo.addVert3f(-0.5f, 0.5f, 0);
        vbo.addVert3f(-0.5f, -0.5f, 0);
        vbo.addVert3f(0.5f, -0.5f, 0);
        vbo.addVert3f(0.5f, 0.5f, 0);
        vbo.update();
        GL33.glBindVertexArray(0);
        model.addVBO(vbo);

        // 2nd triangle for test
        VBO index1 = new VBO();
        index1.create();
        index1.addValue(0);
        index1.addValue(1);
        index1.addValue(2);

        index1.update();

        Model model1 = new Model(index1, shader);
        PositionVBO vbo1 = new PositionVBO(0, 3);
        VBO tex1 = new PositionVBO(1, 2);
        VBO tex2 = new PositionVBO(1, 2);
        model1.create();
        GL33.glBindVertexArray(model1.getVaoID());
        vbo1.create();
        vbo1.addVert3f(0.5f, -0.5f, 0);
        vbo1.addVert3f(0, 0.5f, 0);
        vbo1.addVert3f(-0.5f, -0.5f, 0);
        vbo1.update();
        tex1.create();
        tex1.addVert2f(0, 0);
        tex1.addVert2f(1, 0);
        tex1.addVert2f(1, 1);

        tex1.addVert2f(0, 1);

        tex1.update();
        tex2.create();
        tex2.addVert2f(0, 1);
        tex2.addVert2f(0, 0);
        tex2.addVert2f(1, 0);
        tex2.update();
        GL33.glBindVertexArray(0);
        model1.getPosition().x = 0.5f;
        model1.getPosition().y = 0.5f;

        model1.getPosition().z = -1;

        model1.addVBO(vbo1);
        model.addVBO(tex1);
        model1.addVBO(tex2);
        model.getMaterial().getColor().x = 1.0f;
        model.getMaterial().getColor().y = 0.0f;
        model.getMaterial().getColor().z = 1.0f;
        model.getMaterial().getColor().w = 1.0f;
        model.getRotation().x = -90;
        model.getScale().mul(2);

        try {
            model.getMaterial().setTexture(new Texture().create(Texture.MipMap.NEAREST, 0));
            model1.getMaterial().setTexture(new Texture("misc/test").create(Texture.MipMap.LINEAR, 1));

        } catch (IOException e) {

            e.printStackTrace();
        }
        VBO normals = new VBO(2, 3);
        normals.create();
        model1.addVBO(normals);
        models.add(model1);
        models.add(model);

        VBO normals1 = new VBO(2,3);
        normals1.create();
        model.addVBO(normals1);
        model.setOnUpdate(() ->{

            normals1.getArray().addAll(0, VectorMath.floatsFromVectorList(VectorMath.calculateVertexNormals(
                    VectorMath.vectorListFromFloats(model.getModelVBO().getArray()),
                    VectorMath.asIntegerList(model.getIndex().getArray()))));
            normals1.update();

        });
        model1.setOnUpdate(() ->{
            normals.getArray().addAll(0, VectorMath.floatsFromVectorList(VectorMath.calculateVertexNormals(
                    VectorMath.vectorListFromFloats(model1.getModelVBO().getArray()),
                    VectorMath.asIntegerList(model1.getIndex().getArray()))));

            normals.update();
        });
        renderer = new Renderer(models);
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
            float dx = newMouseX - oldMouseX;
            float dy = newMouseY - oldMouseY;

            if (isMouseLocked()) {
                Camera.active.getRotation().x = Math.max(-90f,
                        Math.min(90f, Camera.active.getRotation().x + (-dy * mouseSensitivity)));
                Camera.active.getRotation().y = Camera.active.getRotation().y + (-dx * mouseSensitivity);

            }
            oldMouseX = newMouseX;
            oldMouseY = newMouseY;
        });
    }
    public void setFullscreen(boolean fullscreen) {
        GLFWVidMode screen = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (fullscreen) {
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
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        GLFWVidMode screen = glfwGetVideoMode(glfwGetPrimaryMonitor());
        setWindow(glfwCreateWindow(screen.width(), screen.height(), title, 0, 0));
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
