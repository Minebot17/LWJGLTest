package ru.minebot.lwjgltest.objects;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Vec3;
import ru.minebot.lwjgltest.utils.Utils;

public abstract class SceneObject {
    protected int sceneId;
    protected Vec3 position;
    protected Vec3 rotation;
    protected Vec3 scale;

    public SceneObject(){
        sceneId = Utils.rnd.nextInt();
    }

    public abstract void initialize();
    public abstract void renderTick();
    public abstract void logicTick();

    public Mat4 getModelMatrix() {
        return Utils.scale(rotation).multiply(Utils.translate(position)).multiply(Utils.scale(scale));
    }

    public int getSceneId() {
        return sceneId;
    }

    public Vec3 getPosition() {
        return position;
    }

    public Vec3 getRotation() {
        return rotation;
    }

    public Vec3 getScale() {
        return scale;
    }
}
