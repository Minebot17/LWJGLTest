package ru.minebot.lwjgltest.render;

import ru.minebot.lwjgltest.utils.Shaders;
import ru.minebot.lwjgltest.utils.Utils;

import java.util.HashMap;

import static org.lwjgl.opengl.GL40.*;

public class MaterialCubemap extends Material {

    public MaterialCubemap() {
        super(Shaders.cubemap);
    }

    @Override
    public void initialize(HashMap<String, String> textures, boolean[] sRGB){
        if (textures == null)
            return;

        int i = 0;
        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureId);

        for (String name : textures.keySet()) {
            Utils.DecodedImage image = Utils.loadPNG(textures.get(name));
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, sRGB[i] ? GL_SRGB : GL_RGB, image.width, image.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image.buffer);
            i++;
        }

        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
        this.textures.put(textures.keySet().iterator().next(), textureId);
    }
}
