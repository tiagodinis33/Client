package org.liquiduser.stur.math;

import org.joml.Vector3d;
import org.joml.Vector3f;
import org.ode4j.math.DVector3;

public class PhysicsUtils {
    public static DVector3 toODEVec3f(Vector3f vec){
        return new DVector3(vec.x,vec.y,vec.z);
    }
    public static Vector3d toJomlVec3d(DVector3 vec){
        return new Vector3d(vec.get0(),vec.get1(),vec.get2());
    }
}
