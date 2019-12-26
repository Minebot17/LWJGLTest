package ru.minebot.lwjgltest.objects;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Vec3;

public abstract class LightSource extends SceneObject {

    protected float lightPower;
    protected Vec3 lightColor;
    public static final Mat4 biasMatrix = new Mat4(new float[] {
            0.5f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.5f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.5f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f
    });

    public LightSource(float lightPower, Vec3 lightColor) {
        this.lightPower = lightPower;
        this.lightColor = lightColor;
    }
}
