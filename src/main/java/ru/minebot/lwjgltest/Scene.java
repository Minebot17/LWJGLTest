package ru.minebot.lwjgltest;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class Scene {
    public static Scene singleton;
    private Window window;
    private CameraController controller;
    private int logicUpdateTime = 50;
    private long logicTicksCount = 0;

    private List<SceneObject> objects = new ArrayList<>();

    public Scene(){
        singleton = this;
        window = new Window(1600, 1200, "Hello");

        addObject(new CameraController());
    }

    public void addObject(SceneObject object){
        object.initialize();
        objects.add(object);
    }

    public void start(){
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

    public void renderTick(){
        Mat4 projection = Matrices.perspective((float)Math.toRadians(90), 4f/3f, 0.1f, 100f);
        Mat4 view = Matrices.lookAt(controller.position, controller.getForward(), controller.getUp());
        for (SceneObject object : objects) {
            Mat4 model = object.getModelMatrix();
            Mat4 mvp = projection.multiply(view.multiply(model));

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
}
