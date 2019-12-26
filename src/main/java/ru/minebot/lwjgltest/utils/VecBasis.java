package ru.minebot.lwjgltest.utils;

import com.hackoeur.jglm.Matrices;
import com.hackoeur.jglm.Vec3;
import com.hackoeur.jglm.Vec4;

public class VecBasis {
    private Vec3 left;
    private Vec3 forward;
    private Vec3 up;

    public VecBasis(Vec3 rotation) {
        left = new Vec3((float)Math.cos(rotation.getX() - Math.PI/2.0f), 0, (float)Math.sin(rotation.getX() - Math.PI/2.0f));
        forward = Utils.toVec3(Utils.multiply(Matrices.rotate(rotation.getX(), new Vec3(0, 1, 0)), Utils.multiply(Matrices.rotate(rotation.getY(), new Vec3(0, 0, 1)), new Vec4(1, 0, 0, 0))));
        up = forward.cross(left);
    }

    public Vec3 getLeft() {
        return left;
    }

    public Vec3 getForward() {
        return forward;
    }

    public Vec3 getUp() {
        return up;
    }
}
