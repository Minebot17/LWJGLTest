package ru.minebot.lwjgltest.objects;

import ru.minebot.lwjgltest.render.Mesh;
import ru.minebot.lwjgltest.render.Shader;

public class MeshObject extends SceneObject {

    protected Mesh mesh;
    protected Shader shader;

    public MeshObject(Mesh mesh){
        this.mesh = mesh;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void renderTick() {

    }

    @Override
    public void logicTick() {

    }
}
