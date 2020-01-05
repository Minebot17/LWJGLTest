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
        /*Thread logic = new Thread(() -> {
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
        logic.start();*/

        while (!glfwWindowShouldClose(window.getId())) {
            renderTick();
            glfwSwapBuffers(window.getId());
            glfwPollEvents();
        }
        //logic.stop();
    }

    // initialize post process, quad vao, msaa FB
    public void initialize(){
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
        addObject(new DirectionalLight(new Vec3(3, 2, 0), new Vec3(-3, -2, 0).getUnitVector(), 2, new Vec3(1, 1, 1)));
        //addObject(new DirectionalLight(new Vec3(3, 2, 0), new Vec3(-3, -2, 0).getUnitVector(), 2, new Vec3(1, 1, 1)));
        addObject(new StandartMeshObject(new Vec3(0, 0, 0), new Vec3(0, 0, 0), new Vec3(1, 1, 1),
                MeshRenders.spaceShipRender,
                "textures/spaceShip/spaceShipAlbedo.png",
                "textures/spaceShip/spaceShipNormals.png",
                "textures/spaceShip/spaceShipSpecular.png"
        ));
        //addObject(new TestMeshObject(new Vec3(), new Vec3(), new Vec3(1, 1, 1), MeshRenders.cubemapRender, new Material(Shaders.test)));

        isInitialized = true;

        va = glGenVertexArrays();
        glBindVertexArray(va);
        buf = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, buf);
        glBufferData(GL_ARRAY_BUFFER, vert, GL_STATIC_DRAW);
        bufUv = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, bufUv);
        glBufferData(GL_ARRAY_BUFFER, uvs, GL_STATIC_DRAW);
    }

    public int va;
    public int buf;
    public int bufUv;
    public float[] vert = new float[] {
            -1.0f, -0.5f, 0.0f,
            1.0f, -0.5f, 0.0f,
            -1.0f,  1.0f, 0.0f,
            -1.0f,  1.0f, 0.0f,
            1.0f, -0.5f, 0.0f,
            1.0f,  1.0f, 0.0f,
    };
    public float[] uvs = new float[]{
            0, 1,
            1, 1,
            0, 0,
            0, 0,
            1, 1,
            1, 0
    };

    public void renderTick(){
        glCullFace(GL_FRONT_AND_BACK);
        glDepthFunc(GL_LEQUAL);
        //glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
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
            Mat4 model = object.getModelMatrix();
            Mat4 mvp = projection.multiply(view.multiply(model));
            matrices.setModel(model);
            matrices.setMvp(mvp);
            object.renderTick();

            /*
            Vec4[] res = new Vec4[6];
            for (int i = 0; i < 6; i++)
                res[i] = Utils.multiply(mvp, new Vec4(vert[i*3], vert[i*3 + 1], vert[i*3 + 2], 1));
            return res;
             */
        }

        /*glBindVertexArray(va);
        //glUseProgram(Shaders.test.getProgrammeId());
        //Shaders.test.setUniform("mvp", projection.multiply(view));
        testTextureMaterial.bindAll(new HashMap<String, Object>(){{put("mvp", projection.multiply(view));}});
        glDisable(GL_DEPTH_TEST);
        glEnableVertexAttribArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, buf);
        glVertexAttribPointer(
                0,                  // Àòðèáóò 0. Ïîäðîáíåå îá ýòîì áóäåò ðàññêàçàíî â ÷àñòè, ïîñâÿùåííîé øåéäåðàì.
                3,                  // Ðàçìåð
                GL_FLOAT,           // Òèï
                false,           // Óêàçûâàåò, ÷òî çíà÷åíèÿ íå íîðìàëèçîâàíû
                0,                  // Øàã
                0            // Ñìåùåíèå ìàññèâà â áóôåðå
        );
        glEnableVertexAttribArray(1);
        glBindBuffer(GL_ARRAY_BUFFER, bufUv);
        glVertexAttribPointer(
                1,                  // Àòðèáóò 0. Ïîäðîáíåå îá ýòîì áóäåò ðàññêàçàíî â ÷àñòè, ïîñâÿùåííîé øåéäåðàì.
                2,                  // Ðàçìåð
                GL_FLOAT,           // Òèï
                false,           // Óêàçûâàåò, ÷òî çíà÷åíèÿ íå íîðìàëèçîâàíû
                0,                  // Øàã
                0            // Ñìåùåíèå ìàññèâà â áóôåðå
        );
        glDrawArrays(GL_TRIANGLES, 0, 6);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glEnable(GL_DEPTH_TEST);*/

        //glPolygonMode( GL_FRONT_AND_BACK, GL_FILL);

        glBindFramebuffer(GL_READ_FRAMEBUFFER, msaaFramebuffer.getFramebufferId());
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, postFramebuffer.getFramebufferId());
        glBlitFramebuffer(0, 0, window.getWidth(), window.getHeight(), 0, 0, window.getWidth(), window.getHeight(), GL_COLOR_BUFFER_BIT, GL_NEAREST);
        msaaFramebuffer.unbind();
        renderPost();
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
