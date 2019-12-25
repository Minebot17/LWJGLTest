package ru.minebot.lwjgltest.utils;

import ru.minebot.lwjgltest.render.Mesh;

import java.util.ArrayList;
import java.util.List;

public class Meshes {

    public static List<Mesh> meshes = new ArrayList<>();
    public static Mesh spaceShip;
    public static Mesh postQuad;

    public static void initialize(){
        spaceShip = new Mesh("models/spaceShip.obj");
        postQuad = new Mesh("models/postQuad.obj");

        meshes.forEach(Mesh::initialize);
    }
}
