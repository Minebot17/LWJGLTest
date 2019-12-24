package ru.minebot.lwjgltest;

import com.hackoeur.jglm.Vec3;

import java.util.List;

public class Mesh {

    protected String path;
    protected List<Vec3> vertices;
    protected List<Float> U;
    protected List<Float> V;
    protected List<Vec3> normal;
    protected List<Vec3> tangents;
    protected List<Vec3> bitangents;

    public Mesh(String path) {
        this.path = path;
    }

    public Mesh(List<Vec3> vertices) {
        this.vertices = vertices;
    }

    public void initialize(){
        if (path != null){

        }
    }


}
