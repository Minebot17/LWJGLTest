package ru.minebot.lwjgltest.utils;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import com.hackoeur.jglm.Vec3;
import com.hackoeur.jglm.Vec4;
import ru.minebot.lwjgltest.Main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Utils {

    public static Random rnd = new Random();

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

    public static Mat4 translate(Vec3 vec){
        return new Mat4(new float[] {
                1, 0, 0, vec.getX(),
                0, 1, 0, vec.getY(),
                0, 0, 1, vec.getZ(),
                0, 0, 0, 1
        });
    }

    public static Mat4 scale(Vec3 vec){
        return new Mat4(new float[] {
                vec.getX(), 0, 0, 0,
                0, vec.getY(), 0, 0,
                0, 0, vec.getZ(), 0,
                0, 0, 0, 1
        });
    }

    public static Mat4 rotation(Vec3 euler){
        return Matrices.rotate(euler.getZ(), new Vec3(0, 0, 1)).multiply(Matrices.rotate(euler.getX(), new Vec3(1, 0, 0)).multiply(Matrices.rotate(euler.getY(), new Vec3(0, 1, 0))));
    }

    public static Mat4 rotation(Vec3 axis, float angle){
        return rotation(quaternionToEuler(axis, angle));
    }

    public static Vec3 quaternionToEuler(Vec3 axis, float angle){
        double y = Math.asin(2f*(angle*axis.getX() - axis.getZ()*axis.getY()));
        double x, z;
        if (Math.abs(Math.abs(y) - Math.PI/2f) < 0.005f){
            x = Math.atan(axis.getZ()/angle);
            z = 0f;
        }
        else {
            x = Math.atan((angle*axis.getZ() + axis.getX()*axis.getY())/(1f - 2f*(axis.getZ()*axis.getZ() + axis.getX()*axis.getX())));
            z = Math.atan((2f*(angle*axis.getY() + axis.getZ()*axis.getX()))/(1f - 2f*(axis.getX()*axis.getX() + axis.getY()*axis.getY())));
        }

        return new Vec3((float)x, (float)y, (float)z);
    }

    public static InputStream getResource(String path){
        return Main.class.getClassLoader().getResourceAsStream(path);
    }

    public static List<String> readResource(String path){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getResource(path), StandardCharsets.UTF_8));
            List<String> result = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null)
                result.add(line);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String concatenate(List<String> list){
        StringBuilder builder = new StringBuilder();
        list.forEach(builder::append);
        return builder.toString();
    }

    public static float[] toFloatArray(List<Vec3> list){
        float[] result = new float[list.size() * 3];
        for (int i = 0; i < list.size(); i++){
            result[i*3] = list.get(i).getX();
            result[i*3 + 1] = list.get(i).getY();
            result[i*3 + 2] = list.get(i).getZ();
        }
        return result;
    }

    public static float[] toFloatArray(List<Float> U, List<Float> V){
        float[] result = new float[U.size()*2];
        for (int i = 0; i < U.size(); i++){
            result[i*2] = U.get(i);
            result[i*2 + 1] = V.get(i);
        }
        return result;
    }

    public static HashMap<String, Object> toHashMap(List<String> keys, List<Object> values){
        HashMap<String, Object> result = new HashMap<>();
        for (int i = 0; i < keys.size(); i++)
            result.put(keys.get(i), values.get(i));
        return result;
    }

    public static BufferedImage loadImage(String path){
        try {
            return ImageIO.read(getResource(path));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
