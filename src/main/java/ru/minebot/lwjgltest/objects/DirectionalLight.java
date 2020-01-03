package ru.minebot.lwjgltest.objects;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import com.hackoeur.jglm.Vec3;
import ru.minebot.lwjgltest.Scene;
import ru.minebot.lwjgltest.render.FramebufferShadow;
import ru.minebot.lwjgltest.render.Material;
import ru.minebot.lwjgltest.utils.Shaders;
import ru.minebot.lwjgltest.utils.Vec2Basis;

import java.util.HashMap;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE10;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class DirectionalLight extends LightSource {

    protected FramebufferShadow shadowFramebuffer = new FramebufferShadow(Scene.singleton.getWindow(), 2048);
    protected Material shadowMaterial = new Material(Shaders.shadowMap);
    protected Mat4 shadowMvp;

    public DirectionalLight(Vec3 position, Vec3 lookVector, float lightPower, Vec3 lightColor) {
        super(position, lookVector, lightPower, lightColor);
    }

    @Override
    public void initialize() {
        shadowFramebuffer.initialize();
        shadowMaterial.initialize(null, null);
    }

    @Override
    public void renderTick() {
        Vec2Basis basis = new Vec2Basis(rotation.getY(), rotation.getZ());
        Mat4 shadowProjection = Matrices.ortho(-10, 10, -10, 10, -5, 8);
        Mat4 shadowView = Matrices.lookAt(position, position.add(basis.getForward()), basis.getUp());
        List<SceneObject> objects = Scene.singleton.getObjects();

        shadowFramebuffer.bind(true);
        shadowMaterial.bind();
        shadowMaterial.bindTextures();

        for (SceneObject object : objects) {
            if (!(object instanceof MeshObject))
                continue;

            shadowMvp = shadowProjection.multiply(shadowView.multiply(object.getModelMatrix()));
            shadowMaterial.bindData(new HashMap<String, Object>() {{
                put("shadow_mvp", shadowMvp);
            }});
            ((MeshObject) object).getMeshRender().renderVertices();
        }
        shadowMaterial.unbind();
        shadowFramebuffer.unbind();
    }

    @Override
    public void logicTick() {

    }

    @Override
    public int getShadowTexture() {
        return shadowFramebuffer.getTextureId();
    }

    @Override
    public Mat4 getShadowMVP() {
        return shadowMvp;
    }
}
