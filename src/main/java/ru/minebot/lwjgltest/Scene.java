package ru.minebot.lwjgltest;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import com.hackoeur.jglm.Vec3;
import ru.minebot.lwjgltest.objects.*;
import ru.minebot.lwjgltest.render.*;
import ru.minebot.lwjgltest.utils.MeshRenders;
import ru.minebot.lwjgltest.utils.SceneMatrices;
import ru.minebot.lwjgltest.utils.Shaders;
import ru.minebot.lwjgltest.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL40.*;

public class Scene {
    public static Scene singleton;
    private Window window;
    private CameraController controller;
    private int logicUpdateTime = 50;
    private long logicTicksCount = 0;
    private boolean isInitialized = false;

    private List<SceneObject> objects = new ArrayList<>();
    private List<LightSource> lightObjects = new ArrayList<>();
    private SceneMatrices matrices;
    private Framebuffer postFramebuffer;
    private FramebufferMSAA msaaFramebuffer;
    private MaterialCubemap cubemapMaterial = new MaterialCubemap();

    public Scene(Window window){
        singleton = this;
        this.window = window;
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
        cubemapMaterial.initialize(new HashMap<String, String>(){{
                    put("skybox", "textures/sky/sky_neg_x.bmp");
                    put("skybox0", "textures/sky/sky_pos_x.bmp");
                    put("skybox1", "textures/sky/sky_pos_y.bmp");
                    put("skybox2", "textures/sky/sky_neg_y.bmp");
                    put("skybox3", "textures/sky/sky_pos_z.bmp");
                    put("skybox4", "textures/sky/sky_neg_z.bmp");
        }}, new boolean[]{ true, true, true, true, true, true });

        addObject(new CameraController());
        addObject(new DirectionalLight(new Vec3(4, 4, 0), new Vec3(0, 0, -(float)Math.PI / 4f * 3f), 5, new Vec3(0, 1, 0)));
        addObject(new StandartMeshObject(MeshRenders.spaceShipRender,
                "textures/spaceShip/spaceShipAlbedo.bmp",
                "textures/spaceShip/spaceShipNormals.bmp",
                "textures/spaceShip/spaceShipSpecular.bmp"
        ));

        isInitialized = true;
    }

    public void renderTick(){
        Mat4 projection = Matrices.perspective((float)Math.toRadians(90), 4f/3f, 0.1f, 100f);
        Mat4 view = Matrices.lookAt(controller.getPosition(), controller.getBasis().getForward(), controller.getBasis().getUp());
        matrices = new SceneMatrices(projection, view);

        lightObjects.forEach(LightSource::renderTick);
        msaaFramebuffer.bind(true);
        for (SceneObject object : objects) {
            Mat4 model = object.getModelMatrix();
            Mat4 mvp = projection.multiply(view.multiply(model));
            matrices.setModel(model);
            matrices.setMvp(mvp);
            object.renderTick();
        }

        renderCubemap();
        glBindFramebuffer(GL_READ_FRAMEBUFFER, msaaFramebuffer.getFramebufferId());
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, postFramebuffer.getFramebufferId());
        glBlitFramebuffer(0, 0, window.getWidth(), window.getHeight(), 0, 0, window.getWidth(), window.getHeight(), GL_COLOR_BUFFER_BIT, GL_NEAREST);
        msaaFramebuffer.unbind();
        renderPost();
    }

    private void renderCubemap(){
        Mat4 skyView = Utils.toMat4(Utils.toMat3(matrices.getView()));
        Mat4 skyMvp = matrices.getProjection().multiply(skyView);
        glDepthMask(false);
        cubemapMaterial.bindAll(new HashMap<String, Object>(){{ put("mvp", skyMvp); }});
        MeshRenders.cubemapRender.render();
        glDepthMask(true);
    }

    private void renderPost(){
        glDisable(GL_DEPTH_TEST);
        glUseProgram(Shaders.post.getProgrammeId());

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, postFramebuffer.getTextureId());
        Shaders.post.setUniform("rendered_texture", 0);
        Shaders.post.setUniform("time", glfwGetTime());
        Shaders.post.setUniform("gamma", 1.0f/2.2f);
        MeshRenders.postQuadRender.render();

        glEnable(GL_DEPTH_TEST);
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

    public List<LightSource> getLightObjects() {
        return lightObjects;
    }
}
