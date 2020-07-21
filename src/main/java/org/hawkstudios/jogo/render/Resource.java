package org.hawkstudios.jogo.render;

import java.util.ArrayList;

public class Resource {
    public static ArrayList<Resource> resources = new ArrayList<Resource>();
    protected Resource(){
        resources.add(this);
    }
    public void cleanup(){}
    public void create(){}
    public void update(){}
}