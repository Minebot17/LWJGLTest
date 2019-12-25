package ru.minebot.lwjgltest.render;

import static org.lwjgl.opengl.GL40.*;

import java.util.HashMap;

public abstract class Material {

    protected Shader shader;
    protected HashMap<String, Integer> textures = new HashMap<>(); // Uniform names -> textures id

    public Material(Shader shader) {
        this.shader = shader;
    }

    // Uniform name -> texture path
    public void initialization(HashMap<String, String> textures){
        // TODO load bmp
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
        }
    }

    // Uniform name -> uniform data, pre-render
    protected abstract void bindData(HashMap<String, Object> data);
}
