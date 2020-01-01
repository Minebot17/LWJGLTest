package ru.minebot.lwjgltest.utils;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import com.hackoeur.jglm.Vec3;

public class Vec2Basis {
    private Vec3 left;
    private Vec3 forward;
    private Vec3 up;

    public Vec2Basis(Vec3 rotation) {
        Mat4 rotZ = Matrices.rotate(rotation.getZ(), new Vec3(0, 0, 1));
        Mat4 rotY = Matrices.rotate(rotation.getY(), new Vec3(0, 1, 0));
        left = Utils.multiply(rotY, Utils.multiply(rotZ, new Vec3(0, 0, -1)));
        forward = Utils.multiply(rotY, Utils.multiply(rotZ, new Vec3(1, 0, 0)));
        up = Utils.multiply(rotY, Utils.multiply(rotZ, new Vec3(0, 1, 0)));
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
