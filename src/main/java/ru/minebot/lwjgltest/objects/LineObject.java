package ru.minebot.lwjgltest.objects;

import static org.lwjgl.opengl.GL40.*;

import com.hackoeur.jglm.Vec3;
import ru.minebot.lwjgltest.Scene;
import ru.minebot.lwjgltest.render.Shader;

import java.util.HashMap;

public class LineObject extends PointsObject {

    public LineObject(Vec3[] positions, Vec3[] colors, Shader shader){
        super(positions, colors, shader);
    }

    @Override
    public void renderTick() {
        material.bindAll(new HashMap<String, Object>(){{
            put("mvp", Scene.singleton.getMatrices().getMvp());
        }});

        glBindVertexArray(vaoId);
        enableBuffer(0, positionBufferId, 3);
        enableBuffer(1, colorBufferId, 3);
        glDrawArrays(GL_LINE_STRIP, 0, positions.length);
        for (int i = 0; i < 2; i++)
            glDisableVertexAttribArray(i);
    }
}
