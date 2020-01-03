package ru.minebot.lwjgltest.objects;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Vec3;
import ru.minebot.lwjgltest.utils.Utils;

public abstract class LightSource extends SceneObject {

    protected float lightPower;
    protected Vec3 lightColor;
    public static final Mat4 biasMatrix = new Mat4(new float[] {
            0.5f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.5f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.5f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f
    });

    public LightSource(Vec3 position, Vec3 lookVector, float lightPower, Vec3 lightColor) {
        super(position, Utils.getEulerFromDirection(lookVector), new Vec3(1, 1, 1));
        this.lightPower = lightPower;
        this.lightColor = lightColor;
    }

    public abstract int getShadowTexture();
    public abstract Mat4 getShadowMVP();

    public float getLightPower() {
        return lightPower;
    }

    public Vec3 getLightColor() {
        return lightColor;
    }
}
