package ru.minebot.lwjgltest.utils;

import ru.minebot.lwjgltest.render.MeshRender;

import java.util.ArrayList;
import java.util.List;

public class MeshRenders {

    public static List<MeshRender> meshRenders = new ArrayList<>();
    public static MeshRender spaceShipRender;

    public void initialization(){
        spaceShipRender = new MeshRender(Meshes.spaceShip);

        meshRenders.forEach(MeshRender::initialize);
    }
}
