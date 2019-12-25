package ru.minebot.lwjgltest.render;

import static org.lwjgl.opengl.GL40.*;
import ru.minebot.lwjgltest.utils.MeshRenders;
import ru.minebot.lwjgltest.utils.Utils;

public class MeshRender {

    protected Mesh mesh;

    protected int vaoId;
    protected int vertexBufferId;
    protected int uvBufferId;
    protected int normalBufferId;
    protected int tangentBufferId;
    protected int bitangentBufferId;

    public MeshRender(Mesh mesh) {
        this.mesh = mesh;
        MeshRenders.meshRenders.add(this);
    }

    public void initialize(){
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        vertexBufferId = loadBuffer(Utils.toFloatArray(mesh.vertices));
        uvBufferId = loadBuffer(Utils.toFloatArray(mesh.U, mesh.V));
        normalBufferId = loadBuffer(Utils.toFloatArray(mesh.normal));
        tangentBufferId = loadBuffer(Utils.toFloatArray(mesh.tangents));
        bitangentBufferId = loadBuffer(Utils.toFloatArray(mesh.bitangents));
    }

    protected int loadBuffer(float[] data){
        int id = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, id);
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        return id;
    }

    public void render(){
        glBindVertexArray(vaoId);
        enableBuffer(0, vertexBufferId, 3);
        enableBuffer(1, uvBufferId, 2);
        enableBuffer(2, normalBufferId, 3);
        enableBuffer(3, tangentBufferId, 3);
        enableBuffer(4, bitangentBufferId, 3);
        glDrawArrays(GL_TRIANGLES, 0, mesh.vertices.size());
        for (int i = 0; i < 5; i++)
            glDisableVertexAttribArray(i);
    }

    protected void enableBuffer(int index, int id, int size){
        glEnableVertexAttribArray(index);
        glBindBuffer(GL_ARRAY_BUFFER, id);
        glVertexAttribPointer(index, size, GL_FLAT, false, 0, 0);
    }
}
