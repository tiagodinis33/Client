package org.liquiduser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joml.Vector3f;
import org.liquiduser.net.client.Client;
import org.liquiduser.stur.voxel.World;
import org.liquiduser.stur.engine.Resource;
import org.liquiduser.stur.entity.EntityRenderer;
import org.liquiduser.stur.entity.Player;
import org.liquiduser.stur.gui.GuiScreen;
import org.liquiduser.stur.gui.InGameMenu;
import org.liquiduser.stur.ioutils.Input;
import org.liquiduser.stur.lighning.Light;
import org.liquiduser.stur.render.engine.Camera;
import org.liquiduser.stur.voxel.Chunk;
import org.liquiduser.stur.voxel.tiles.Tile;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL33;

import java.io.IOException;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

final class FPScounter {
    private static long startTime;
    private static long endTime;
    private static long frameTimes = 0;
    private static short framesCounter = 0;
    private static short frames = 0;

    /**
     * Start counting the fps
     **/
    public static void StartCounter() {
        //get the current time
        startTime = System.currentTimeMillis();
    }

    public static short getFrames() {
        return frames;
    }

    public static void ProcessCounter() {
        endTime = System.currentTimeMillis();
        frameTimes = frameTimes + endTime - startTime;
        ++framesCounter;
        if (frameTimes >= 1000) {
            frames = framesCounter;
            framesCounter = 0;
            frameTimes = 0;
        }
    }
}

final public class Stur extends Thread {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Stur engine;
    Client client;
    Integer id = null;
    Player player;
    World world;
    GuiScreen guiScreen;
    Light light = new Light(new Vector3f(0, -0.5f, 1), new Vector3f(1), 1f);
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

    public boolean isFullscreen() {
        return fullscreen;
    }

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

    @Override
    public void run() {

        startGame();
        postStart();
        while (!glfwWindowShouldClose(getWindow())) {
            runGameLoop();

        }
        cleanup();
    }
    boolean wereLines;
    private void update() {
        glfwPollEvents();
        glfwSwapBuffers(window);

        if (guiScreen != null) {
            guiScreen.update();
        }
        player.raytraceBlock(world);
        if (Input.isButtonPressed(0))
            if (player.getSelectedBlock() != null)
                player.breakBlock();
        if(Input.isKeyPressed(GLFW_KEY_F6)){
            wereLines = !wereLines;
            if(wereLines){

                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
            }else{

                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
            }
        }
        if(Input.isButtonPressed(GLFW_MOUSE_BUTTON_RIGHT))
            player.placeBlock(Tile.grass);

        FPScounter.ProcessCounter();

        Input.update();

    }

    private void runGameLoop() {
        FPScounter.StartCounter();
        //GL11.glClearColor(0.0f, 1.0f, 1.0f, 0.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        if (width != 0 && height != 0) {
            render();
        }
        update();
    }

    private void render() {
        renderWorld();
        renderEntities();
        renderGui();
    }

    private void renderEntities() {
        EntityRenderer.renderBlockOverlay(player);
    }

    private void renderGui() {
        if (guiScreen != null) {
            guiScreen.render();
        }
    }

    private void renderWorld() {
        world.render();
    }

    public void cleanup() {
        for (Resource resource : Resource.resources) {
            resource.cleanup();
        }
        try {
            client.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        glfwTerminate();
    }

    public void reverseArrayList(List<Float> alist) {
        for (int i = 0; i < alist.size() / 2; i++) {
            Float temp = alist.get(i);
            alist.set(i, alist.get(alist.size() - i - 1));
            alist.set(alist.size() - i - 1, temp);
        }
    }

    public int getFps() {
        return FPScounter.getFrames();
    }

    private void postStart() {
        world = new World();
        Chunk chunk1 = new Chunk(0, 0, 0);
        chunk1.create();
        Chunk chunk2 = new Chunk(2, 0, 2);
        chunk2.create();
        world.chunks.add(chunk1);
        world.chunks.add(chunk2);
        displayGuiScreen(new InGameMenu());
        player = new Player();
        Camera.active = player.getCamera();

        try {
            client = new Client("localhost:8080");
            id = client.readObject();
            System.out.println(id);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void displayGuiScreen(GuiScreen gui) {
        gui.initGui();
        guiScreen = gui;
    }

    private void startGame() {
        if (!glfwInit()) {
            System.out.println("Não foi possivel iniciar o GLFW!!");
        }
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, 1);
        GLFWVidMode screen = glfwGetVideoMode(glfwGetPrimaryMonitor());
        assert screen != null;
        setWindow(glfwCreateWindow(screen.width(), screen.height(), title, 0, 0));
        assert window != 0;
        glfwShowWindow(getWindow());
        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        Input.create();


        glfwSetKeyCallback(window, new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {

                if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS)
                    glfwSetWindowShouldClose(window, true);
            }
        });
        glfwSetWindowSizeCallback(window, (window, width, height) -> {

            this.width = width;
            this.height = height;
            GL33.glViewport(0, 0, width, height);
            if (guiScreen != null) {
                guiScreen.initGui();
            }
        });
        GL.createCapabilities();
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(int width) {
        glfwSetWindowSize(window, width, (int) height);

        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(int height) {
        glfwSetWindowSize(window, (int) width, height);
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


}
