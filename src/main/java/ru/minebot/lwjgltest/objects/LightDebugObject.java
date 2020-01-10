package ru.minebot.lwjgltest.objects;

import com.hackoeur.jglm.Vec3;
import ru.minebot.lwjgltest.Scene;
import ru.minebot.lwjgltest.utils.Shaders;

public class LightDebugObject extends SceneObject {

    protected LightSource light;
    protected PointsObject point;
    protected LineObject line;

    public LightDebugObject(LightSource light){
        this.light = light;
    }

    protected void update(){
        Vec3 color = light.getLightColor();
        point = new PointsObject(new Vec3[]{ light.getPosition() }, new Vec3[]{ color }, Shaders.lightPosition);
        line = new LineObject(new Vec3[]{ new Vec3(), new Vec3(1, 0, 0) }, new Vec3[]{ color, color }, Shaders.lightPosition);
        line.setPosition(light.getPosition());
        line.setRotation(light.getRotation());
    }

    @Override
    public void initialize() {
        update();
        point.initialize();
        line.initialize();
    }

    @Override
    public void renderTick() {
        point.renderTick();
        Scene.singleton.updateModelMatrix(line.getModelMatrix());
        line.renderTick();
    }

    @Override
    public void logicTick() {
        point.logicTick();
        line.logicTick();
    }
}
