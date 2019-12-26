package ru.minebot.lwjgltest.objects;

import ru.minebot.lwjgltest.render.Material;
import ru.minebot.lwjgltest.render.MeshRender;

import java.util.HashMap;

public abstract class MeshObject extends SceneObject {

    protected MeshRender meshRender;
    protected Material material;
    protected HashMap<String, String> textures;
    protected boolean[] sRGB;

    public MeshObject(MeshRender meshRender, Material material, HashMap<String, String> textures, boolean[] sRGB) {
        this.meshRender = meshRender;
        this.material = material;
    }

    @Override
    public void initialize() {
        material.initialize(textures, sRGB);
    }

    public abstract void renderTick();
    public abstract void logicTick();

    public MeshRender getMeshRender() {
        return meshRender;
    }
}
