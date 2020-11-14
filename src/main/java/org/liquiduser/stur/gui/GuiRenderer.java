package org.liquiduser.stur.gui;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.liquiduser.Stur;
import org.liquiduser.stur.engine.Model;
import org.liquiduser.stur.engine.Texture;
import org.liquiduser.stur.render.engine.GLSLProgram;
import org.liquiduser.stur.render.engine.PositionVBO;
import org.liquiduser.stur.render.engine.Renderer;
import org.liquiduser.stur.render.internal.VBO;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GuiRenderer {


    static VBO index = new VBO();
    static GLSLProgram shader;
    static{
        try {
            shader = GLSLProgram.get("simple");
        } catch (Exception e) {
            e.printStackTrace();
        }
        index.create();
        index.set(0,1,2,3,0,2);
    }

    public static void renderTexture(int x, int y, int width,int height,int u, int v, Texture t){
        Model m;
        try {
            if(!quadsCachedt.containsKey(t)){
                var mm = new Model(index,shader);
                quadsCachedt.put(t, mm);
                var vbo = new PositionVBO();
                vbo.create();

                mm.create();
                mm.addVBO(vbo);
                mm.addVBO(new VBO(1,2));
            }
            m = quadsCachedt.get(t);


        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if(!m.isCreated()){
            m.create();
        }
        if(m.getMaterial().getTexture() != t){
            m.getMaterial().setTexture(t);
        }
        float f = (float)1 /(float) t.getWidth();
        float f1 = (float)1 / (float)t.getHeight();
        var pos = m.getBuffers().get(0);
        m.getMaterial().setColor(new Vector4f(0,0,0,1));
        m.getBuffers().get(1).set(
                (u * f), (v * f1),
                ((u + width) * f), (v * f1),
                ((u + width) * f), ((v + height) * f1),
                (u * f), ((v + height) * f1)




        );

        pos.set(

                x, (y + height), 0,
                (x + width), (y + height), 0,
                (x + width), y, 0,
                x, y , 0
        );
        pos.update();
        m.getBuffers().get(1).update();
        var renderer = new Renderer(new ArrayList<>());
        renderer.models.add(m);
        renderer.useCameraMatrix = false;
        renderer.setProjection(new Matrix4f().ortho2D(0,Stur.getEngine().getWidth(),Stur.getEngine().getHeight(),0));
        renderer.setDepthTestingEnabled(false);
        renderer.setDrawMode(GL11.GL_TRIANGLES);
        renderer.render();
    }

    static HashMap<Texture, Model> quadsCachedt = new HashMap<>();
    static HashMap<String, Model> quadsCached = new HashMap<>();
    static HashMap<String, Texture> textureCached = new HashMap<>();
    public static void renderTexture(int x, int y, int width,int height,int u, int v, String location){
        Model m = null;
        Texture t = null;
        boolean flag = false;
        try {
            if(!quadsCached.containsKey(location)){
                var mm = new Model(index,shader);
                quadsCached.put(location, mm);
                var vbo = new PositionVBO();
                vbo.create();

                mm.create();
                mm.addVBO(vbo);
                mm.addVBO(new VBO(1,2));
            }
            m = quadsCached.get(location);

            if(!textureCached.containsKey(location)){
                textureCached.put(location, Texture.get(location, Texture.MipMap.NEAREST));
            }
            t = textureCached.get(location);

        }catch(IOException | IllegalArgumentException e){
            e.printStackTrace();

            flag = true;

        }
        if(flag) {
            t =Texture.DEFAULT_TEXTURE;

        }
        if(!m.isCreated()){
            m.create();
        }
        if(m.getMaterial().getTexture() != t){
            m.getMaterial().setTexture(t);
        }
        float f = (float)1 /(float) t.getWidth();
        float f1 = (float)1 / (float)t.getHeight();
        var pos = m.getBuffers().get(0);
        m.getMaterial().setColor(new Vector4f(0,0,0,1));
        List<Float> texCoordsVBO = Arrays.asList(
                (u * f), ((v + height) * f1),
                ((u + width) * f), ((v + height) * f1),
                ((u + width) * f), (v * f1),
                (u * f), (v * f1)
        );
        if(!m.getBuffers().get(1).getArray().equals(texCoordsVBO)){
            m.getBuffers().get(1).getArray().clear();
            m.getBuffers().get(1).getArray().addAll(texCoordsVBO);
            m.getBuffers().get(1).update();
        }
        List<Float> vertsVBO = Arrays.asList(
                (float)x, (float)(y + height), (float)0,
                (float)(x + width), (float)(y + height), (float)0,
                (float)(x + width),(float) y, (float)0,
                (float)x, (float)y ,(float) 0
        );
        if(!pos.getArray().equals(vertsVBO)){
            pos.getArray().clear();
            pos.getArray().addAll(vertsVBO);
            pos.update();
        }


        var renderer = new Renderer(new ArrayList<>());
        renderer.models.add(m);
        renderer.useCameraMatrix = false;
        renderer.setProjection(new Matrix4f().ortho2D(0,Stur.getEngine().getWidth(),Stur.getEngine().getHeight(),0));
        renderer.setDepthTestingEnabled(false);
        renderer.render();
    }
    public static void renderTexture(int x, int y, int width,int height, int widtht,int heightt,int u, int v, String location){
        Model m = null;
        Texture t = null;
        boolean flag = false;
        try {
            if(!quadsCached.containsKey(location)){
                var mm = new Model(index,shader);
                quadsCached.put(location, mm);
                var vbo = new PositionVBO();
                vbo.create();

                mm.create();
                mm.addVBO(vbo);
                mm.addVBO(new VBO(1,2));
            }
            m = quadsCached.get(location);

            if(!textureCached.containsKey(location)){
                textureCached.put(location, Texture.get(location, Texture.MipMap.NEAREST));
            }
            t = textureCached.get(location);

        }catch(IOException | IllegalArgumentException e){
            e.printStackTrace();

            flag = true;

        }
        if(flag) {
            t =Texture.DEFAULT_TEXTURE;

        }
        if(!m.isCreated()){
            m.create();
        }
        if(m.getMaterial().getTexture() != t){
            m.getMaterial().setTexture(t);
        }
        float f = (float)1 /(float) t.getWidth();
        float f1 = (float)1 / (float)t.getHeight();
        var pos = m.getBuffers().get(0);
        m.getMaterial().setColor(new Vector4f(0,0,0,1));
        m.getBuffers().get(1).set(
                (u * f), ((v + height) * f1),
                ((u + width) * f), ((v + height) * f1),
                ((u + width) * f), (v * f1),
                (u * f), (v * f1)




        );

        pos.set(

                x, (y + heightt), 0,
                (x + widtht), (y + heightt), 0,
                (x + widtht), y, 0,
                x, y , 0
        );
        pos.update();
        m.getBuffers().get(1).update();
        var renderer = new Renderer(new ArrayList<>());
        renderer.models.add(m);
        renderer.useCameraMatrix = false;
        renderer.setProjection(new Matrix4f().ortho2D(0,Stur.getEngine().getWidth(),Stur.getEngine().getHeight(),0));
        renderer.setDepthTestingEnabled(false);
        renderer.render();
    }
}
