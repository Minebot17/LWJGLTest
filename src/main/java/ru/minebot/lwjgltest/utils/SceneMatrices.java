package ru.minebot.lwjgltest.utils;

import com.hackoeur.jglm.Mat4;

public class SceneMatrices {
    private Mat4 projection;
    private Mat4 view;

    // model and mvp for current rendered SceneObject
    private Mat4 model;
    private Mat4 mvp;

    public SceneMatrices(Mat4 projection, Mat4 view) {
        this.projection = projection;
        this.view = view;
    }

    public Mat4 getProjection() {
        return projection;
    }

    public Mat4 getView() {
        return view;
    }

    public Mat4 getModel() {
        return model;
    }

    public Mat4 getMvp() {
        return mvp;
    }

    public void setModel(Mat4 model) {
        this.model = model;
    }

    public void setMvp(Mat4 mvp) {
        this.mvp = mvp;
    }
}
