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

import static org.lwjgl.opengl.GL40.*;

public class DirectionalLight extends LightSource {

    protected FramebufferShadow shadowFramebuffer = new FramebufferShadow(Scene.singleton.getWindow(), 1024);
    protected Material shadowMaterial = new Material(Shaders.shadowMap);

    public DirectionalLight(float lightPower, Vec3 lightColor) {
        super(lightPower, lightColor);
    }

    @Override
    public void initialize() {
        shadowFramebuffer.initialize();
        shadowMaterial.initialization(null, null);
    }

    @Override
    public void renderTick() {
        VecBasis basis = new VecBasis(rotation);
        Mat4 shadowProjection = Matrices.ortho(-10, 10, -10, 10, -5, 8);
        Mat4 shadowView = Matrices.lookAt(position, basis.getForward(), basis.getUp());
        Mat4 shadowMvp = shadowProjection.multiply(shadowView.multiply(Scene.singleton.getMatrices().getModel()));

        shadowFramebuffer.bind(true);
        shadowMaterial.bind(new HashMap<String, Object>(){{ put("shadow_mvp", shadowMvp); }});
        Scene.singleton.getObjects().forEach(o -> {
            if (o instanceof MeshObject)
                ((MeshObject) o).getMeshRender().renderVertices();
        });
        shadowMaterial.unbind();
        shadowFramebuffer.unbind();
    }

    @Override
    public void logicTick() {

    }
}
