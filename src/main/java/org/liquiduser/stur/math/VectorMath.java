package org.liquiduser.stur.math;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VectorMath {
    public static Vector3f cross(Vector3f vec, Vector3f vec1){
        Vector3f result = new Vector3f();
        vec.cross(vec1, result);
        return result;
    }

    public static Float[] toArrayf(List<Float> list){
        Float[] array = new Float[list.size()];
        for (int x = 0; x < list.size(); x++) {
            array[x] = list.get(x);
        }
        return array;
    }
    public static Vector3f[] toArrayVec3f(List<Vector3f> list){
        Vector3f[] array = new Vector3f[list.size()];
        for (int x = 0; x < list.size(); x++) {
            array[x] = list.get(x);
        }
        return array;
    }
    public static int[] toArrayi(List<Integer> list){
        int[] array = new int[list.size()];
        for (int x = 0; x < list.size(); x++) {
            array[x] = list.get(x);
        }
        return array;
    }
    public static Vector3f normalize(Vector3f vec){
        Vector3f result = new Vector3f();
        vec.normalize(result);
        return result;
    }
    public static List<Vector3f> calculateVertexNormals(List<Vector3f> vertexPositions, List<Integer> triangleIndices){
        return Arrays.asList(calculateVertexNormals(toArrayVec3f(vertexPositions), toArrayi(triangleIndices)));
    }
    public static List<Vector3f> vectorListFromFloats(List<Float> floats){

        List<Vector3f> result = new ArrayList<>();
        for(int i = 0; i < floats.size(); i += 3){
            result.add(i/3, new Vector3f(floats.get(i),floats.get(i+1),floats.get(i+2)));
        }
        return result;
    }
    public static List<Float> floatsFromVectorList(List<Vector3f> vectors){
        List<Float> floats = new ArrayList<>();
        for(int i = 0; i < vectors.size(); i++){
            Vector3f vec = vectors.get(i);
            floats.add(i, vec.x);
            floats.add(i+1, vec.y);
            floats.add(i+2,vec.z);
        }
        return floats;
    }
    public static Vector3f[] calculateVertexNormals(Vector3f[] vertexPositions, int[] triangleIndices)
    {
        Vector3f[] result = new Vector3f[vertexPositions.length];

        // Zero-out our normal buffer to start from a clean slate.
        for(int vertex = 0; vertex < vertexPositions.length; vertex++)
            result[vertex] = new Vector3f();

        // For each face, compute the face normal, and accumulate it into each vertex.
        for(int index = 0; index < triangleIndices.length; index += 3) {
            int vertexA = triangleIndices[index];
            int vertexB = triangleIndices[index + 1];
            int vertexC = triangleIndices[index + 2];

            var edgeAB = vertexPositions[vertexB].sub(vertexPositions[vertexA], new Vector3f());
            var edgeAC = vertexPositions[vertexC].sub(vertexPositions[vertexA], new Vector3f());

            // The cross product is perpendicular to both input vectors (normal to the plane).
            // Flip the argument order if you need the opposite winding.
            var areaWeightedNormal = cross(edgeAB, edgeAC);

            // Don't normalize this vector just yet. Its magnitude is proportional to the
            // area of the triangle (times 2), so this helps ensure tiny/skinny triangles
            // don't have an outsized impact on the final normal per vertex.

            // Accumulate this cross product into each vertex normal slot.
            result[vertexA].add(areaWeightedNormal);
            result[vertexB].add(areaWeightedNormal);
            result[vertexC].add(areaWeightedNormal);
        }

        // Finally, normalize all the sums to get a unit-length, area-weighted average.
        for(int vertex = 0; vertex < vertexPositions.length; vertex++)
            result[vertex].normalize();
        return result;
    }
    public static List<Integer> asIntegerList(List<Float> list){
        List<Integer> result = new ArrayList<>();
        for(int i = 0; i < list.size(); i++){
            result.add(i, list.get(i).intValue());
        }
        return result;
    }
}