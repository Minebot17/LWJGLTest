package ru.minebot.lwjgltest.objects;

import com.hackoeur.jglm.Vec3;
import org.lwjgl.system.MemoryStack;
import ru.minebot.lwjgltest.Scene;
import ru.minebot.lwjgltest.Window;
import ru.minebot.lwjgltest.utils.VecBasis;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;

import static org.lwjgl.system.MemoryStack.stackPush;

public class CameraController extends SceneObject {

    protected long windowId;
    protected final float rotateSpeed = 0.1f;
    protected final float flySpeed = 1.0f;
    protected VecBasis basis;

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
        basis = new VecBasis(rotation);

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

    public VecBasis getBasis() {
        return basis;
    }
}
