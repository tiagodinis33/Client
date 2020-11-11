package org.liquiduser.stur.voxel;

import org.liquiduser.stur.engine.Model;
import org.liquiduser.stur.render.engine.Renderer;
import java.util.ArrayList;
import java.util.Iterator;

public class World implements Iterable<Chunk>{
    public ArrayList<Chunk> chunks = new ArrayList<>();

    public void render(){
        ArrayList<Model> model = new ArrayList<>();
        var renderer = new Renderer(model);
        renderer.useIndex = false;
        for (Chunk chunk :
                this) {
            model.add(chunk.chunkModel);
            chunk.checkInView(renderer);
        }

        renderer.render();
    }

    @Override
    public Iterator<Chunk> iterator() {
        return chunks.iterator();
    }
}
