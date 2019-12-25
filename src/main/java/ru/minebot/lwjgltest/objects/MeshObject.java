package ru.minebot.lwjgltest.objects;

import ru.minebot.lwjgltest.render.MeshRender;
import ru.minebot.lwjgltest.render.Shader;

public class MeshObject extends SceneObject {

    protected MeshRender meshRender;

    public MeshObject(MeshRender mesh){
        this.meshRender = meshRender;
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
