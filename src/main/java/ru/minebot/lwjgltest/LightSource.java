package ru.minebot.lwjgltest;

import com.hackoeur.jglm.Vec3;

public abstract class LightSource extends SceneObject {

    protected float lightPower;
    protected Vec3 lightColor;

    public LightSource(float lightPower, Vec3 lightColor) {
        this.lightPower = lightPower;
        this.lightColor = lightColor;
    }
}
