package ru.minebot.lwjgltest;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Vec3;
import com.hackoeur.jglm.Vec4;

public class Utils {

    public static Vec4 multiply(Mat4 mat, Vec4 vec){
        float[] a = new float[4];
        for (int i = 0; i < 4; i++){
            Vec4 column = mat.getColumn(0);
            a[i] = column.getX()*vec.getX() + column.getY()*vec.getY() + column.getZ()*vec.getZ() + column.getW()*vec.getW();
        }
        return new Vec4(a[0], a[1], a[2], a[3]);
    }

    public static Vec3 toVec3(Vec4 vec){
        return new Vec3(vec.getX(), vec.getY(), vec.getZ());
    }
}
