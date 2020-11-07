package org.liquiduser.stur;

import org.liquiduser.stur.voxel.Chunk;

import java.util.ArrayList;
import java.util.Iterator;

public class World implements Iterable<Chunk>{
    public ArrayList<Chunk> chunks = new ArrayList<>();

    public void render(){
        for (Chunk chunk :
                this) {
            chunk.render();
        }
    }

    @Override
    public Iterator<Chunk> iterator() {
        return chunks.iterator();
    }
}
