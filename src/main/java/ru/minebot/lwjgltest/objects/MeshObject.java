package ru.minebot.lwjgltest.objects;

import com.hackoeur.jglm.Vec3;
import ru.minebot.lwjgltest.render.Material;
import ru.minebot.lwjgltest.render.MeshRender;

import java.util.HashMap;

public abstract class MeshObject extends SceneObject {

    protected MeshRender meshRender;
    protected Material material;
    protected HashMap<String, String> textures;
    protected boolean[] sRGB;

    public MeshObject(Vec3 position, Vec3 rotation, Vec3 scale, MeshRender meshRender, Material material, HashMap<String, String> textures, boolean[] sRGB) {
        super(position, rotation, scale);
        this.meshRender = meshRender;
        this.material = material;
        this.textures = textures;
        this.sRGB = sRGB;
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
