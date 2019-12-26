package ru.minebot.lwjgltest.render;

import ru.minebot.lwjgltest.Window;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;

public class Framebuffer {
    protected Window window;
    protected int framebufferId = -1;
    protected int textureId = -1;
    protected int depthId = -1;

    public Framebuffer(Window window){
        this.window = window;
    }

    public void initialize(){
        framebufferId = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, framebufferId);

        textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, window.getWidth(), window.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        depthId = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, depthId);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, window.getWidth(), window.getHeight());

        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthId);
        glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, textureId, 0);

        glDrawBuffer(GL_COLOR_ATTACHMENT0);
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            System.err.println("Framebuffer isn't ok");
    }

    public void bind(boolean clear){
        glBindFramebuffer(GL_FRAMEBUFFER, framebufferId);
        glViewport(0, 0, window.getWidth(), window.getHeight());
        if (clear){
            glClearColor(0.0f, 0.0f, 0.25f, 0.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        }
    }

    public void unbind(){
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, window.getWidth(), window.getHeight());
    }

    public int getFramebufferId() {
        return framebufferId;
    }

    public int getTextureId() {
        return textureId;
    }
}
