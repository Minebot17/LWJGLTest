package ru.minebot.lwjgltest.render;

import static org.lwjgl.opengl.GL40.*;
import ru.minebot.lwjgltest.utils.MeshRenders;
import ru.minebot.lwjgltest.utils.Utils;

public class MeshRender {

    protected Mesh mesh;

    protected int vaoId = -1;
    protected int vertexBufferId = -1;
    protected int uvBufferId = -1;
    protected int normalBufferId = -1;
    protected int tangentBufferId = -1;
    protected int bitangentBufferId = -1;

    public MeshRender(Mesh mesh) {
        this.mesh = mesh;
        MeshRenders.meshRenders.add(this);
    }

    public void initialize(){
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        vertexBufferId = loadBuffer(Utils.toFloatArray(mesh.vertices));
        if (mesh.U != null)
            uvBufferId = loadBuffer(Utils.toFloatArray(mesh.U, mesh.V));
        if (mesh.normal != null)
            normalBufferId = loadBuffer(Utils.toFloatArray(mesh.normal));
        if (mesh.tangents != null) {
            tangentBufferId = loadBuffer(Utils.toFloatArray(mesh.tangents));
            bitangentBufferId = loadBuffer(Utils.toFloatArray(mesh.bitangents));
        }
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
        if (uvBufferId != -1)
            enableBuffer(1, uvBufferId, 2);
        if (uvBufferId != -1)
            enableBuffer(2, normalBufferId, 3);
        if (tangentBufferId != -1) {
            enableBuffer(3, tangentBufferId, 3);
            enableBuffer(4, bitangentBufferId, 3);
        }
        glDrawArrays(GL_TRIANGLES, 0, mesh.vertices.size());
        for (int i = 0; i < 5; i++)
            glDisableVertexAttribArray(i);
    }

    public void renderVertices(){
        glBindVertexArray(vaoId);
        enableBuffer(0, vertexBufferId, 3);
        glDrawArrays(GL_TRIANGLES, 0, mesh.vertices.size());
        glDisableVertexAttribArray(0);
    }

    protected void enableBuffer(int index, int id, int size){
        glEnableVertexAttribArray(index);
        glBindBuffer(GL_ARRAY_BUFFER, id);
        glVertexAttribPointer(index, size, GL_FLOAT, false, 0, 0);
    }
}
