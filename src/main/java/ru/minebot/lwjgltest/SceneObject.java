package ru.minebot.lwjgltest;

import com.hackoeur.jglm.Vec3;

public abstract class SceneObject {
    protected Vec3 position;
    protected Vec3 rotation;
    protected Vec3 scale;

    public abstract void initialize();
    public abstract void tickRender();
    public abstract void tickLogic();
}
