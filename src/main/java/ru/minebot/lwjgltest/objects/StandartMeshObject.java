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
import java.util.List;

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
        SceneMatrices matrices = Scene.singleton.getMatrices();
        Mat3 mat3 = Utils.toMat3(matrices.getModel().multiply(matrices.getView()));
        List<LightSource> lights = Scene.singleton.getLightObjects();

        Vec3[] positions = new Vec3[lights.size()];
        Mat4[] depthBiasMVP = new Mat4[lights.size()];
        Vec3[] colors = new Vec3[lights.size()];
        float[] powers = new float[lights.size()];

        for (int i = 0; i < lights.size(); i++) {
            positions[i] = lights.get(i).getPosition();
            depthBiasMVP[i] = LightSource.biasMatrix.multiply(lights.get(i).getShadowMVP());
            colors[i] = lights.get(i).getLightColor();
            powers[i] = lights.get(i).getLightPower();
        }

        material.bindAll(new HashMap<String, Object>(){{
            put("mvp", matrices.getMvp());
            put("model", matrices.getModel());
            put("view", matrices.getView());
            put("mv3x3", mat3);
            put("lightPosition_worldspace", positions);
            put("depthBiasMVP", depthBiasMVP);
            put("lightColor", colors);
            put("lightPower", powers);
            put("time", glfwGetTime());
            put("lightCount", lights.size());
        }});
        for (int i = 0; i < lights.size(); i++) {
            glActiveTexture(GL_TEXTURE3 + i);
            glBindTexture(GL_TEXTURE_2D, lights.get(i).getShadowTexture());
            material.getShader().setUniform("shadowSampler[" + i + "]", 3 + i);
        }
        meshRender.render();
    }

    @Override
    public void logicTick() {
        //rotation = rotation.add(new Vec3(0, 0.01f, 0));
    }
}
