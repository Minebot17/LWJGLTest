package ru.minebot.lwjgltest;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import ru.minebot.lwjgltest.objects.CameraController;
import ru.minebot.lwjgltest.objects.SceneObject;
import ru.minebot.lwjgltest.render.Framebuffer;
import ru.minebot.lwjgltest.render.FramebufferMSAA;
import ru.minebot.lwjgltest.utils.SceneMatrices;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class Scene {
    public static Scene singleton;
    private Window window;
    private CameraController controller;
    private int logicUpdateTime = 50;
    private long logicTicksCount = 0;
    private boolean isInitialized = false;

    private List<SceneObject> objects = new ArrayList<>();
    private SceneMatrices matrices;
    private Framebuffer postFramebuffer;
    private FramebufferMSAA msaaFramebuffer;

    public Scene(){
        singleton = this;
        window = new Window(1600, 1200, "Hello");
        postFramebuffer = new Framebuffer(window);
        msaaFramebuffer = new FramebufferMSAA(window, 4);
    }

    public void addObject(SceneObject object){
        object.initialize();
        objects.add(object);
    }

    public void start(){
        if (!isInitialized)
            initialize();

        Thread logic = new Thread(() -> {
            while (true){
                try {
                    logicTicksCount++;
                    logicTick();
                    Thread.sleep(logicUpdateTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        });
        logic.start();

        while (!glfwWindowShouldClose(window.getId())) {
            renderTick();
            glfwSwapBuffers(window.getId());
            glfwPollEvents();
        }
        logic.stop();
    }

    // initialize post process, quad vao, msaa FB
    public void initialize(){
        msaaFramebuffer.initialize();
        postFramebuffer.initialize();

        addObject(new CameraController());

        isInitialized = true;
    }

    public void renderTick(){
        Mat4 projection = Matrices.perspective((float)Math.toRadians(90), 4f/3f, 0.1f, 100f);
        Mat4 view = Matrices.lookAt(controller.getPosition(), controller.getBasis().getForward(), controller.getBasis().getUp());
        matrices = new SceneMatrices(projection, view);
        for (SceneObject object : objects) {
            Mat4 model = object.getModelMatrix();
            Mat4 mvp = projection.multiply(view.multiply(model));
            matrices.setModel(model);
            matrices.setMvp(mvp);

            // TODO
        }
    }

    public void logicTick(){
        for (SceneObject object : objects)
            object.logicTick();
    }

    public Window getWindow(){
        return window;
    }

    public int getLogicUpdateTime(){
        return logicUpdateTime;
    }

    public long getLogicTicksCount(){
        return logicTicksCount;
    }

    public void setCameraController(CameraController controller){
        this.controller = controller;
    }

    public SceneMatrices getMatrices() {
        return matrices;
    }

    public List<SceneObject> getObjects() {
        return objects;
    }
}
