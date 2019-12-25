package ru.minebot.lwjgltest.objects;

import com.hackoeur.jglm.Vec3;
import static org.lwjgl.opengl.GL40.*;

public class DirectionalLight extends LightSource {

    protected int shadowFramebuffer;
    protected int shadowTexture;

    public DirectionalLight(float lightPower, Vec3 lightColor) {
        super(lightPower, lightColor);
    }

    @Override
    public void initialize() {
        shadowFramebuffer = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, shadowFramebuffer);

        shadowTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, shadowTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT24, 1024, 1024, 0, GL_DEPTH_COMPONENT, GL_FLAT, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, shadowTexture, 0);
        glDrawBuffer(GL_NONE);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            System.err.println("Framebuffer of " + getClass().getName() + " is not ok");
    }

    @Override
    public void renderTick() {
        // TODO
    }

    @Override
    public void logicTick() {

    }
}
