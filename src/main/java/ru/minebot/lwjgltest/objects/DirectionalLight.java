package ru.minebot.lwjgltest.objects;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import com.hackoeur.jglm.Vec3;
import ru.minebot.lwjgltest.Scene;
import ru.minebot.lwjgltest.render.FramebufferShadow;
import ru.minebot.lwjgltest.render.Material;
import ru.minebot.lwjgltest.utils.Shaders;
import ru.minebot.lwjgltest.utils.VecBasis;

import java.util.HashMap;
import java.util.List;

public class DirectionalLight extends LightSource {

    protected FramebufferShadow shadowFramebuffer = new FramebufferShadow(Scene.singleton.getWindow(), 1024);
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
        VecBasis basis = new VecBasis(rotation);
        Mat4 shadowProjection = Matrices.ortho(-10, 10, -10, 10, -5, 8);
        Mat4 shadowView = Matrices.lookAt(position, basis.getForward(), basis.getUp());
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
