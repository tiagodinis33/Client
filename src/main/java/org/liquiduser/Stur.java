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
import org.ode4j.ode.DWorld;
import org.ode4j.ode.OdeConfig;
import org.ode4j.ode.OdeHelper;
import org.ode4j.ode.internal.ErrorHandler;
import org.ode4j.ode.internal.ErrorHdl;
import org.ode4j.ode.internal.OdeInit;

import java.io.IOException;
import java.util.List;
import java.util.Random;

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
    public Player thePlayer;
    public World theWorld;
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
        thePlayer.raytraceBlock();
        if (Input.isButtonPressed(0))
            if (thePlayer.getSelectedBlock() != null)
                thePlayer.breakBlock();
        if(Input.isKeyPressed(GLFW_KEY_F6)){
            wereLines = !wereLines;
            if(wereLines){

                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
            }else{

                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
            }
        }

        if(Input.isButtonPressed(GLFW_MOUSE_BUTTON_RIGHT)) {
            Tile selectedTile = thePlayer.getInventory().get(thePlayer.getSelectedSlot());
            thePlayer.placeBlock(selectedTile);
        }

        for(int i = 0; i<9; i++){
            if(Input.isKeyPressed(49+i)){
                thePlayer.setSelectedSlot(i);
            }
        }
        float v = Input.isKeyDown(GLFW_KEY_LEFT_CONTROL)? vel * 2 : vel;

        if (Input.isKeyPressed(GLFW_KEY_F) ) {
            Input.setMouseLocked(!Input.isMouseLocked());
        }
        if (Input.isKeyPressed(GLFW_KEY_F11) ) {
            getEngine().setFullscreen(!getEngine().isFullscreen());
        }
        float x = (float) Math.sin(Math.toRadians(Camera.active.getRotation().y)) * v;
        float z = (float) Math.cos(Math.toRadians(Camera.active.getRotation().y)) * v;

        double newMouseX = (float) Input.getMouseX();
        double newMouseY = (float) Input.getMouseY();

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
        if(Input.isKeyPressed(GLFW_KEY_R)){
            theWorld.generateTerrain(new Random().nextInt(10000));

            thePlayer = new Player(theWorld);
            Camera.active = thePlayer.getCamera();

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
        thePlayer.setSelectedSlot((int) (thePlayer.getSelectedSlot()-Input.getScrollDeltaY()));
        FPScounter.ProcessCounter();

        Input.update();

    }

    private double oldMouseX;
    private double oldMouseY;
    private float vel= 0.2f;
    private float mouseSensitivity = 0.2f;
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
        EntityRenderer.renderBlockOverlay(thePlayer);
    }

    private void renderGui() {
        if (guiScreen != null) {
            guiScreen.render();
        }
    }

    private void renderWorld() {
        theWorld.render();
    }

    public void cleanup() {
        for (Resource resource : Resource.resources) {
            resource.cleanup();
        }
        glfwTerminate();
        try {
            client.getSocket().close();
        } catch (IOException| NullPointerException e) {
            e.printStackTrace();
        }

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
        OdeHelper.initODE();
        ErrorHdl.dSetErrorHandler((errnum, msg, ap) -> {
            System.out.println(errnum +": "+ msg);
        });
        theWorld = new World();

        int worldSize = 4;
        for(int x= 0; x < worldSize; x++)
            for(int z= 0; z < worldSize; z++){
                Chunk chunk = new Chunk(-x,-z);
                chunk.create();
                theWorld.add(chunk);
            }

        theWorld.generateTerrain(new Random().nextInt(10000));

        thePlayer = new Player(theWorld);
        Camera.active = thePlayer.getCamera();
        displayGuiScreen(new InGameMenu());
        try {
            client = new Client("localhost:8080");
            id = client.readObject();
            System.out.println(id);
        } catch (IOException | ClassNotFoundException e) {
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
