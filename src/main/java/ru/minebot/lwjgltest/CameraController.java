package ru.minebot.lwjgltest;

import com.hackoeur.jglm.Matrices;
import com.hackoeur.jglm.Vec3;
import com.hackoeur.jglm.Vec4;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;

import static org.lwjgl.system.MemoryStack.stackPush;

public class CameraController extends SceneObject {

    protected long windowId;
    protected final float rotateSpeed = 0.1f;
    protected final float flySpeed = 1.0f;

    protected Vec3 left;
    protected Vec3 forward;
    protected Vec3 up;

    @Override
    public void initialize() {
        Scene.singleton.setCameraController(this);
        windowId = Scene.singleton.getWindow().getId();
    }

    @Override
    public void renderTick() {

    }

    @Override
    public void logicTick() {
        Window window = Scene.singleton.getWindow();
        MemoryStack stack = stackPush();
        DoubleBuffer dx = stack.mallocDouble(1);
        DoubleBuffer dy = stack.mallocDouble(1);

        glfwGetCursorPos(windowId, dx, dy);
        glfwSetCursorPos(windowId, window.getWidth()/2.0f, window.getHeight()/2.0f);
        rotation = rotation.add(new Vec3(((float)dx.get() - Scene.singleton.getWindow().getWidth() / 2.0f) * Scene.singleton.getLogicUpdateTime() * rotateSpeed, 0, 0));
        rotation = rotation.add(new Vec3((-(float)dy.get() + Scene.singleton.getWindow().getHeight() / 2.0f) * Scene.singleton.getLogicUpdateTime() * rotateSpeed, 0, 0));

        left = new Vec3((float)Math.cos(rotation.getX() - Math.PI/2.0f), 0, (float)Math.sin(rotation.getX() - Math.PI/2.0f));
        forward = Utils.toVec3(Utils.multiply(Matrices.rotate(rotation.getX(), new Vec3(0, 1, 0)), Utils.multiply(Matrices.rotate(rotation.getY(), new Vec3(0, 0, 1)), new Vec4(1, 0, 0, 0))));
        up = forward.cross(left);

        if (glfwGetKey(windowId, GLFW_KEY_W) == GLFW_PRESS)
            position = position.add(forward.multiply(flySpeed));
        if (glfwGetKey(windowId, GLFW_KEY_S) == GLFW_PRESS)
            position = position.add(forward.getNegated().multiply(flySpeed));
        if (glfwGetKey(windowId, GLFW_KEY_A) == GLFW_PRESS)
            position = position.add(left.multiply(flySpeed));
        if (glfwGetKey(windowId, GLFW_KEY_D) == GLFW_PRESS)
            position = position.add(left.getNegated().multiply(flySpeed));
        if (glfwGetKey(windowId, GLFW_KEY_C) == GLFW_PRESS)
            position = position.add(up.getNegated().multiply(flySpeed));
        if (glfwGetKey(windowId, GLFW_KEY_SPACE) == GLFW_PRESS)
            position = position.add(up.multiply(flySpeed));
    }

    public Vec3 getLeft() {
        return left;
    }

    public Vec3 getForward() {
        return forward;
    }

    public Vec3 getUp() {
        return up;
    }
}
