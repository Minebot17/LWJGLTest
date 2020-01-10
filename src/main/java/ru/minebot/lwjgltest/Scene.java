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

    private long lastTime;
    private long deltaTime;

    private List<SceneObject> objects = new ArrayList<>();
    private List<LightSource> lightObjects = new ArrayList<>();
    private SceneMatrices matrices;
    private Framebuffer postFramebuffer;
    private FramebufferMSAA msaaFramebuffer;
    private MaterialCubemap cubemapMaterial = new MaterialCubemap();
    private Material testTextureMaterial = new Material(Shaders.testTexture);

    public Scene(Window window){
        singleton = this;
        this.window = window;
        postFramebuffer = new Framebuffer(window);
        msaaFramebuffer = new FramebufferMSAA(window, 4);
    }

    public void addObject(SceneObject object){
        object.initialize();
        if (object instanceof LightSource)
            lightObjects.add((LightSource) object);
        else
            objects.add(object);
    }

    public void start(){
        if (!isInitialized)
            initialize();

        lastTime = System.nanoTime();
        logicTick();

        while (!glfwWindowShouldClose(window.getId())) {
            renderTick();
            glfwSwapBuffers(window.getId());
            glfwPollEvents();
        }
    }

    // initialize post process, quad vao, msaa FB
    public void initialize(){
        glPointSize(25);
        glLineWidth(5);
        msaaFramebuffer.initialize();
        postFramebuffer.initialize();
        cubemapMaterial.initialize(new HashMap<String, String>(){{
                    put("skybox", "textures/sky/sky_pos_y.png");
                    put("skybox0", "textures/sky/sky_pos_x.png");
                    put("skybox1", "textures/sky/sky_neg_x.png");
                    put("skybox2", "textures/sky/sky_pos_z.png");
                    put("skybox3", "textures/sky/sky_neg_y.png");
                    put("skybox4", "textures/sky/sky_neg_z.png");
        }}, new boolean[]{ true, true, true, true, true, true });
        testTextureMaterial.initialize(new HashMap<String, String>(){{put("texture", "textures/test.png");}}, new boolean[]{true});

        addObject(new CameraController());
        //addObject(new DirectionalLight(new Vec3(3, 2, 0), new Vec3(-3, -2, 0).getUnitVector(), 2, new Vec3(1, 0, 0)));
        DirectionalLight light0 = new DirectionalLight(new Vec3(-3, 2, 0), new Vec3(3, -2, 0).getUnitVector(), 3, new Vec3(1, 0, 0));
        addObject(light0);
        addObject(new LightDebugObject(light0));
        DirectionalLight light1 = new DirectionalLight(new Vec3(3, 2, 0), new Vec3(-3, -2, 0).getUnitVector(), 3, new Vec3(0, 1, 0));
        addObject(light1);
        addObject(new LightDebugObject(light1));

        addObject(new StandartMeshObject(new Vec3(0, 0, 0), new Vec3(0, 0, 0), new Vec3(1, 1, 1),
                MeshRenders.spaceShipRender,
                "textures/spaceShip/spaceShipAlbedo.png",
                "textures/spaceShip/spaceShipNormals.png",
                "textures/spaceShip/spaceShipSpecular.png"
        ));
        //addObject(new TestMeshObject(new Vec3(), new Vec3(), new Vec3(1, 1, 1), MeshRenders.cubemapRender, new Material(Shaders.test)));

        isInitialized = true;
    }

    public void renderTick(){
        glCullFace(GL_FRONT_AND_BACK);
        glDepthFunc(GL_LEQUAL);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        deltaTime = System.nanoTime() - lastTime;
        lastTime = System.nanoTime();
        if (deltaTime > 1000 * logicUpdateTime){
            deltaTime = 0;
            logicTick();
            logicTicksCount++;
        }

        Mat4 projection = Matrices.perspective(90f, 4f/3f, 0.1f, 100f);
        Mat4 view = Matrices.lookAt(controller.getPosition(), controller.getPosition().add(controller.getBasis().getForward()), controller.getBasis().getUp());
        matrices = new SceneMatrices(projection, view);

        lightObjects.forEach(LightSource::renderTick);
        msaaFramebuffer.bind(true);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(0, 0.5f, 0, 1);
        renderCubemap();

        for (SceneObject object : objects) {
            updateModelMatrix(object.getModelMatrix());
            object.renderTick();
        }

        glBindFramebuffer(GL_READ_FRAMEBUFFER, msaaFramebuffer.getFramebufferId());
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, postFramebuffer.getFramebufferId());
        glBlitFramebuffer(0, 0, window.getWidth(), window.getHeight(), 0, 0, window.getWidth(), window.getHeight(), GL_COLOR_BUFFER_BIT, GL_NEAREST);
        msaaFramebuffer.unbind();
        renderPost();
    }

    public void updateModelMatrix(Mat4 model){
        Mat4 mvp = matrices.getProjection().multiply(matrices.getView().multiply(model));
        matrices.setModel(model);
        matrices.setMvp(mvp);
    }

    private void renderCubemap(){
        Mat4 skyView = Utils.toMat4(Utils.toMat3(matrices.getView()));
        Mat4 skyMvp = matrices.getProjection().multiply(skyView);
        cubemapMaterial.bindAll(new HashMap<String, Object>(){{ put("mvp", skyMvp); }});
        glDisable(GL_DEPTH_TEST);
        MeshRenders.cubemapRender.render();
        glEnable(GL_DEPTH_TEST);
    }

    private void renderPost(){
        glDisable(GL_DEPTH_TEST);
        glUseProgram(Shaders.post.getProgrammeId());

        LightSource light = Scene.singleton.getLightObjects().get(0);
        glActiveTexture(GL_TEXTURE10);
        glBindTexture(GL_TEXTURE_2D, light.getShadowTexture());
        Shaders.post.setUniform("shadow_texture", 10);

        List<Vec3> positions = new ArrayList<>();
        for (int i = 0; i < lightObjects.size(); i++)
            positions.add(Utils.multiply(matrices.getProjection().multiply(matrices.getView().multiply(lightObjects.get(i).getModelMatrix())), new Vec3(0, 0, 0)));

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, postFramebuffer.getTextureId());
        Shaders.post.setUniform("rendered_texture", 0);
        Shaders.post.setUniform("time", glfwGetTime());
        Shaders.post.setUniform("gamma", 1.0f/2.2f);
        //Shaders.post.setUniform("lightPositions_cameraspace", (Vec3[]) positions.toArray(new Vec3[0]));
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
