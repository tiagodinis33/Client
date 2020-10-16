package org.liquiduser.stur.engine;

import java.util.ArrayList;

public class Resource {
    public static ArrayList<Resource> resources = new ArrayList<Resource>();
    protected Resource(){
        resources.add(this);
    }

    public static void cleanup(Resource res) {
        res.cleanup();
    }

    public void cleanup(){}
    public void create(){}
    public void update(){}
}