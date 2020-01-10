package ru.minebot.lwjgltest.utils;

import com.hackoeur.jglm.Vec3;
import ru.minebot.lwjgltest.render.Mesh;

import java.util.ArrayList;
import java.util.List;

public class Meshes {

    public static List<Mesh> meshes = new ArrayList<>();
    public static Mesh spaceShip;
    public static Mesh postQuad;
    public static Mesh cubemap;
    public static Mesh sphere;

    public static void initialize(){
        spaceShip = new Mesh("models/spaceShip.obj");
        postQuad = new Mesh("models/postQuad.obj");
        cubemap = new Mesh();
        initCubemap();
        sphere = new Mesh("models/sphere.obj");

        meshes.forEach(Mesh::initialize);
    }

    private static void initCubemap(){
        cubemap.vertices = new ArrayList<>();
        Vec3[] sky_one_vertices = {
                new Vec3(-1,-1,-1),
                new Vec3(1,-1,-1),
                new Vec3(1,1,-1),
                new Vec3(-1,1,-1),
                new Vec3(-1,-1,1),
                new Vec3(1,-1,1),
                new Vec3(1,1,1),
                new Vec3(-1,1,1)
        };
        int[] sky_indexes = { 2,3,0,0,1,2,2,1,6,1,5,6,4,3,7,4,0,3,4,7,6,6,5,4,3,2,6,7,3,6,5,1,0,4,5,0 };
        for (int sky_index : sky_indexes)
            cubemap.vertices.add(sky_one_vertices[sky_index]);
    }
}
