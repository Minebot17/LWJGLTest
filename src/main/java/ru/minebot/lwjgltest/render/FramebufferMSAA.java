package ru.minebot.lwjgltest.render;

import static org.lwjgl.opengl.GL40.*;
import ru.minebot.lwjgltest.Window;

public class FramebufferMSAA extends Framebuffer {

    protected int samples;

    public FramebufferMSAA(Window window, int samples) {
        super(window);
        this.samples = samples;
    }

    @Override
    public void initialize(){
        framebufferId = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, framebufferId);

        textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, textureId);
        glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, samples, GL_RGB, window.getWidth(), window.getHeight(), true);
        glTexParameteri(GL_TEXTURE_2D_MULTISAMPLE, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D_MULTISAMPLE, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        depthId = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, depthId);
        glRenderbufferStorageMultisample(GL_RENDERBUFFER, samples, GL_DEPTH_COMPONENT, window.getWidth(), window.getHeight());

        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthId);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D_MULTISAMPLE, textureId, 0);

        glDrawBuffer(GL_COLOR_ATTACHMENT0);
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            System.err.println("MSAA framebuffer isn't ok");
    }
}
