package ru.minebot.lwjgltest.objects;

import com.hackoeur.jglm.Vec3;
import ru.minebot.lwjgltest.Scene;
import ru.minebot.lwjgltest.render.Material;
import ru.minebot.lwjgltest.render.Shader;
import ru.minebot.lwjgltest.utils.Utils;

import java.util.Arrays;
import java.util.HashMap;

import static org.lwjgl.opengl.GL40.*;

public class PointsObject extends SceneObject {

    protected int vaoId = -1;
    protected int positionBufferId = -1;
    protected int colorBufferId = -1;
    protected Material material;

    protected Vec3[] positions;
    protected Vec3[] colors;

    public PointsObject(Vec3[] positions, Vec3[] colors, Shader shader){
        this.positions = positions;
        this.colors = colors;
        this.material = new Material(shader);
    }

    public void initialize(){
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        positionBufferId = loadBuffer(Utils.toFloatArray(Arrays.asList(positions)));
        colorBufferId = loadBuffer(Utils.toFloatArray(Arrays.asList(colors)));
    }

    protected int loadBuffer(float[] data){
        int id = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, id);
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        return id;
    }

    protected void enableBuffer(int index, int id, int size){
        glEnableVertexAttribArray(index);
        glBindBuffer(GL_ARRAY_BUFFER, id);
        glVertexAttribPointer(index, size, GL_FLOAT, false, 0, 0);
    }

    @Override
    public void renderTick() {
        material.bindAll(new HashMap<String, Object>(){{
            put("mvp", Scene.singleton.getMatrices().getMvp());
        }});

        glBindVertexArray(vaoId);
        enableBuffer(0, positionBufferId, 3);
        enableBuffer(1, colorBufferId, 3);
        glDrawArrays(GL_POINTS, 0, positions.length);
        for (int i = 0; i < 2; i++)
            glDisableVertexAttribArray(i);
    }

    @Override
    public void logicTick() {

    }
}
