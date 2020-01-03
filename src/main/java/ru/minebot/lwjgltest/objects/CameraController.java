package ru.minebot.lwjgltest.objects;

import com.hackoeur.jglm.Vec3;
import org.lwjgl.system.MemoryStack;
import ru.minebot.lwjgltest.Scene;
import ru.minebot.lwjgltest.Window;
import ru.minebot.lwjgltest.utils.Vec2Basis;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;

import static org.lwjgl.system.MemoryStack.stackPush;

public class CameraController extends SceneObject {

    protected long windowId;
    protected final float rotateSpeed = 0.00001f;
    protected final float flySpeed = 0.5f;
    protected Vec2Basis basis;

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
        DoubleBuffer dxBuf = stack.mallocDouble(1);
        DoubleBuffer dyBuf = stack.mallocDouble(1);

        glfwGetCursorPos(windowId, dxBuf, dyBuf);
        glfwSetCursorPos(windowId, window.getWidth()/2.0f, window.getHeight()/2.0f);
        float dx = (float)dxBuf.get() - Scene.singleton.getWindow().getWidth() / 2.0f;
        float dy = (float)dyBuf.get() - Scene.singleton.getWindow().getHeight() / 2.0f;

        rotation = rotation.add(new Vec3( 0, 0, -dy * Scene.singleton.getLogicUpdateTime() * rotateSpeed));
        rotation = rotation.add(new Vec3(0, -dx * Scene.singleton.getLogicUpdateTime() * rotateSpeed, 0));
        basis = new Vec2Basis(rotation.getY(), rotation.getZ());

        if (glfwGetKey(windowId, GLFW_KEY_W) == GLFW_PRESS)
            position = position.add(basis.getForward().multiply(flySpeed));
        if (glfwGetKey(windowId, GLFW_KEY_S) == GLFW_PRESS)
            position = position.add(basis.getForward().getNegated().multiply(flySpeed));
        if (glfwGetKey(windowId, GLFW_KEY_A) == GLFW_PRESS)
            position = position.add(basis.getLeft().multiply(flySpeed));
        if (glfwGetKey(windowId, GLFW_KEY_D) == GLFW_PRESS)
            position = position.add(basis.getLeft().getNegated().multiply(flySpeed));
        if (glfwGetKey(windowId, GLFW_KEY_C) == GLFW_PRESS)
            position = position.add(basis.getUp().getNegated().multiply(flySpeed));
        if (glfwGetKey(windowId, GLFW_KEY_SPACE) == GLFW_PRESS)
            position = position.add(basis.getUp().multiply(flySpeed));
    }

    public Vec2Basis getBasis() {
        return basis;
    }
}
