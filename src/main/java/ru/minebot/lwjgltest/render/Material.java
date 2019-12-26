package ru.minebot.lwjgltest.render;

import static org.lwjgl.opengl.GL40.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Material {

    protected Shader shader;
    protected HashMap<String, Integer> textures = new HashMap<>(); // Uniform names -> textures id

    public Material(Shader shader) {
        this.shader = shader;
    }

    // Uniform name -> texture path
    public void initialization(HashMap<String, String> textures){
        if (textures == null)
            return;

        for (String name : textures.keySet()) {
            String path = textures.get(name);
            int width, height;
            byte[] data = new byte[0];
            try {
                data = Files.readAllBytes(Paths.get(path));
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (data[0] != 'B' || data[1] != 'M')
                System.err.println("Некорректный BMP-файл\n");

            width = ByteBuffer.wrap(data, 0x12, 4).getInt();
            height = ByteBuffer.wrap(data, 0x16, 4).getInt();

            int textureID = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, textureID);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_BGR, GL_UNSIGNED_BYTE, ByteBuffer.wrap(data));
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            glGenerateMipmap(GL_TEXTURE_2D);

            this.textures.put(name, textureID);
        }
    }

    // Uniform name -> uniform data, pre-render
    public void bind(HashMap<String, Object> data) {
        glUseProgram(shader.getProgrammeId());
        bindTextures();
        bindData(data);
    }

    public void unbind(){
        glUseProgram(0);
    }

    protected void bindTextures(){
        int i = 0;
        for (String name : textures.keySet()){
            glActiveTexture(GL_TEXTURE0 + i);
            glBindTexture(GL_TEXTURE_2D, textures.get(name));
            shader.setUniform(name, i);
            i++;
        }
    }

    // Uniform name -> uniform data, pre-render
    protected void bindData(HashMap<String, Object> data) {
        for (String name : data.keySet())
            shader.setUniform(name, data.get(name));
    }
}
