package ru.minebot.lwjgltest.render;

import ru.minebot.lwjgltest.utils.Utils;

import static org.lwjgl.opengl.GL40.*;

import java.util.HashMap;

public class Material {

    protected Shader shader;
    protected HashMap<String, Integer> textures = new HashMap<>(); // Uniform names -> textures id

    public Material(Shader shader) {
        this.shader = shader;
    }

    // Uniform name -> texture path
    public void initialize(HashMap<String, String> textures, boolean[] sRGB){
        if (textures == null)
            return;

        int i = 0;
        for (String name : textures.keySet()) {
            /*String path = textures.get(name);
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
            height = ByteBuffer.wrap(data, 0x16, 4).getInt();*/

            Utils.DecodedImage image = Utils.loadPNG(textures.get(name));
            int textureID = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, textureID);
            glTexImage2D(GL_TEXTURE_2D, 0, sRGB[i] ? GL_SRGB : GL_RGB, image.width, image.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image.buffer);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            glGenerateMipmap(GL_TEXTURE_2D);

            this.textures.put(name, textureID);
            i++;
        }
    }

    // Uniform name -> uniform data, pre-render
    public void bindAll(HashMap<String, Object> data) {
        glUseProgram(shader.getProgrammeId());
        bindTextures();
        bindData(data);
    }

    public void bind(){
        glUseProgram(shader.getProgrammeId());
    }

    public void unbind(){
        glUseProgram(0);
    }

    public void bindTextures(){
        int i = 0;
        for (String name : textures.keySet()){
            glActiveTexture(GL_TEXTURE0 + i);
            glBindTexture(GL_TEXTURE_2D, textures.get(name));
            shader.setUniform(name, i);
            i++;
        }
    }

    // Uniform name -> uniform data, pre-render
    public void bindData(HashMap<String, Object> data) {
        if (data != null)
            for (String name : data.keySet())
                shader.setUniform(name, data.get(name));
    }

    public Shader getShader() {
        return shader;
    }
}
