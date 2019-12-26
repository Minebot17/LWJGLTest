package ru.minebot.lwjgltest.utils;

import ru.minebot.lwjgltest.render.MeshRender;

import java.util.ArrayList;
import java.util.List;

public class MeshRenders {

    public static List<MeshRender> meshRenders = new ArrayList<>();
    public static MeshRender spaceShipRender;
    public static MeshRender postQuadRender;

    public static void initialize(){
        spaceShipRender = new MeshRender(Meshes.spaceShip);
        postQuadRender = new MeshRender(Meshes.postQuad);

        meshRenders.forEach(MeshRender::initialize);
    }
}
