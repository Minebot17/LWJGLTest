package ru.minebot.lwjgltest.render;

import ru.minebot.lwjgltest.utils.Shaders;
import ru.minebot.lwjgltest.utils.Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

import static org.lwjgl.opengl.GL40.*;

public class MaterialCubemap extends Material {

    public MaterialCubemap() {
        super(Shaders.cubemap);
    }

    @Override
    public void initialization(HashMap<String, String> textures, boolean[] sRGB){
        if (textures == null)
            return;

        int i = 0;
        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureId);

        for (String name : textures.keySet()) {
            String path = textures.get(name);
            BufferedImage image = Utils.loadImage(path);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                String[] splitted = path.split("\\.");
                ImageIO.write(image, splitted[splitted.length - 1], out);
            } catch (IOException e) {
                out = null;
                e.printStackTrace();
            }

            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, sRGB[i] ? GL_SRGB : GL_RGB, image.getWidth(), image.getHeight(), 0, GL_BGR, GL_UNSIGNED_BYTE, ByteBuffer.wrap(out.toByteArray()));
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
