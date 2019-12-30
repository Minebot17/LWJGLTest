package ru.minebot.lwjgltest.objects;

import com.hackoeur.jglm.Vec3;
import ru.minebot.lwjgltest.Scene;
import ru.minebot.lwjgltest.render.Material;
import ru.minebot.lwjgltest.render.MeshRender;

import java.util.HashMap;

public class TestMeshObject extends MeshObject {

    public TestMeshObject(Vec3 position, Vec3 rotation, Vec3 scale, MeshRender meshRender, Material material) {
        super(position, rotation, scale, meshRender, material, null, null);
    }

    @Override
    public void renderTick() {
        material.bindAll(new HashMap<String, Object>(){{ put("mvp", Scene.singleton.getMatrices().getMvp()); }});
        meshRender.render();
    }

    @Override
    public void logicTick() {

    }
}
