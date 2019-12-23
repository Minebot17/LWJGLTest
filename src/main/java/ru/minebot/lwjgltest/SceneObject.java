package ru.minebot.lwjgltest;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Vec3;

public abstract class SceneObject {
    protected Vec3 position;
    protected Vec3 rotation;
    protected Vec3 scale;

    public abstract void initialize();
    public abstract void renderTick();
    public abstract void logicTick();

    public Mat4 getModelMatrix() {
        return Utils.scale(rotation).multiply(Utils.translate(position)).multiply(Utils.scale(scale));
    }
}
