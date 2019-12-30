package ru.minebot.lwjgltest.objects;

import com.hackoeur.jglm.Mat3;
import com.hackoeur.jglm.Mat4;
import static org.lwjgl.opengl.GL40.*;

import com.hackoeur.jglm.Vec3;
import ru.minebot.lwjgltest.Scene;
import ru.minebot.lwjgltest.render.Material;
import ru.minebot.lwjgltest.render.MeshRender;
import ru.minebot.lwjgltest.utils.SceneMatrices;
import ru.minebot.lwjgltest.utils.Shaders;
import ru.minebot.lwjgltest.utils.Utils;

import java.util.HashMap;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class StandartMeshObject extends MeshObject {

    public StandartMeshObject(Vec3 position, Vec3 rotation, Vec3 scale, MeshRender meshRender, String albedoPath, String normalPath, String specularPath) {
        super(position, rotation, scale, meshRender, new Material(Shaders.standart), new HashMap<String, String>(){{
            put("textureSampler", albedoPath);
            put("normalSampler", normalPath);
            put("specularSampler", specularPath);
        }}, new boolean[]{ true, true, true });
    }

    @Override
    public void renderTick() {
        LightSource light = Scene.singleton.getLightObjects().get(0);
        glActiveTexture(GL_TEXTURE10);
        glBindTexture(GL_TEXTURE_2D, light.getShadowTexture());
        material.getShader().setUniform("shadowSampler", 10);

        SceneMatrices matrices = Scene.singleton.getMatrices();
        Mat3 mat3 = Utils.toMat3(matrices.getModel().multiply(matrices.getView()));
        Mat4 depthBiasMVP = LightSource.biasMatrix.multiply(light.getShadowMVP());

        material.bindAll(new HashMap<String, Object>(){{
            put("mvp", matrices.getMvp());
            put("model", matrices.getModel());
            put("view", matrices.getView());
            put("mv3x3", mat3);
            put("lightDirection_worldspace", light.getPosition());
            put("depthBiasMVP", depthBiasMVP);
            put("lightColor", light.getLightColor());
            put("lightPower", light.getLightColor());
            put("time", glfwGetTime());
        }});
        meshRender.render();
    }

    @Override
    public void logicTick() {

    }
}
