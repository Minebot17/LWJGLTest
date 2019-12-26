package ru.minebot.lwjgltest;

import org.lwjgl.glfw.GLFWErrorCallback;
import ru.minebot.lwjgltest.render.MeshRender;
import ru.minebot.lwjgltest.utils.MeshRenders;
import ru.minebot.lwjgltest.utils.Meshes;
import ru.minebot.lwjgltest.utils.Shaders;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.glClearColor;

public class Main {

    public static void main(String[] args){
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        Window window = new Window(1600, 1200, "Hello");
        createCapabilities();
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

        // load resources
        Meshes.initialize();
        Shaders.initialize();
        MeshRenders.initialize();

        // start scene
        Scene scene = new Scene(window);
        scene.start();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(scene.getWindow().getId());
        glfwDestroyWindow(scene.getWindow().getId());

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
}
