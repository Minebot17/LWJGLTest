package ru.minebot.lwjgltest.render;

import com.hackoeur.jglm.Vec3;
import ru.minebot.lwjgltest.utils.Meshes;
import ru.minebot.lwjgltest.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Mesh {

    protected String path;
    public List<Vec3> vertices;
    public List<Float> U;
    public List<Float> V;
    public List<Vec3> normal;
    public List<Vec3> tangents;
    public List<Vec3> bitangents;

    public Mesh() { }

    public Mesh(String path) {
        this.path = path;
        Meshes.meshes.add(this);
    }

    public void initialize(){
        if (path != null && vertices == null){
            vertices = new ArrayList<>();
            U = new ArrayList<>();
            V = new ArrayList<>();
            normal = new ArrayList<>();
            tangents = new ArrayList<>();
            bitangents = new ArrayList<>();

            List<Vec3> v = new ArrayList<>();
            List<Float> vtX = new ArrayList<>();
            List<Float> vtY = new ArrayList<>();
            List<Vec3> vn = new ArrayList<>();

            Scanner sc = new Scanner(Utils.getResource(path));
            while (sc.hasNextLine()){
                String line = sc.nextLine();
                if (line.length() == 0)
                    continue;

                char first = line.charAt(0);
                if (first == 'v') {
                    char sec = line.charAt(1);
                    if (sec == ' ') {
                        String[] splitted = line.split(" ");
                        v.add(new Vec3(Float.parseFloat(splitted[1]), Float.parseFloat(splitted[2]), Float.parseFloat(splitted[3])));
                    }
                    else if (sec == 't'){
                        String[] splitted = line.split(" ");
                        vtX.add(Float.parseFloat(splitted[1]));
                        vtY.add(Float.parseFloat(splitted[2]));
                    }
                    else if (sec == 'n'){
                        String[] splitted = line.split(" ");
                        vn.add(new Vec3(Float.parseFloat(splitted[1]), Float.parseFloat(splitted[2]), Float.parseFloat(splitted[3])));
                    }
                }
                else if (first == 'f'){
                    String[] splitted = line.replace(' ', '/').split("/");
                    if (vn.size() != 0) {
                        int[] values = new int[9]; // v vt vn v vt vn v vt vn
                        for (int i = 0; i < 9; i++)
                            values[i] = Integer.parseInt(splitted[1 + i]);

                        vertices.add(v.get(values[0] - 1));
                        U.add(vtX.get(values[1] - 1));
                        V.add(1f - vtY.get(values[1] - 1));
                        normal.add(vn.get(values[2] - 1));

                        vertices.add(v.get(values[3] - 1));
                        U.add(vtX.get(values[4] - 1));
                        V.add(1f - vtY.get(values[4] - 1));
                        normal.add(vn.get(values[5] - 1));

                        vertices.add(v.get(values[6] - 1));
                        U.add(vtX.get(values[7] - 1));
                        V.add(1f - vtY.get(values[7] - 1));
                        normal.add(vn.get(values[8] - 1));
                    }
                    else {
                        int[] values = new int[6]; // v vt vn v vt vn v vt vn
                        for (int i = 0; i < 6; i++)
                            values[i] = Integer.parseInt(splitted[1 + i]);

                        vertices.add(v.get(values[0] - 1));
                        U.add(vtX.get(values[1] - 1));
                        V.add(1f - vtY.get(values[1] - 1));

                        vertices.add(v.get(values[2] - 1));
                        U.add(vtX.get(values[3] - 1));
                        V.add(1f - vtY.get(values[3] - 1));

                        vertices.add(v.get(values[4] - 1));
                        U.add(vtX.get(values[5] - 1));
                        V.add(1f - vtY.get(values[5] - 1));
                    }
                }
            }

            // build tangents and bitangents
            if (normal.size() != 0)
                for (int i = 0; i < vertices.size(); i += 3){
                    Vec3 deltaPos1 = vertices.get(i + 1).subtract(vertices.get(i));
                    Vec3 deltaPos2 = vertices.get(i + 2).subtract(vertices.get(i));
                    float deltaU1 = U.get(i + 1) - U.get(i);
                    float deltaU2 = U.get(i + 2) - U.get(i);
                    float deltaV1 = V.get(i + 1) - V.get(i);
                    float deltaV2 = V.get(i + 2) - V.get(i);

                    float r = 1.0f / (deltaU1 * deltaV2 - deltaV1 * deltaU2);
                    Vec3 tangent = deltaPos1.multiply(deltaV2).subtract(deltaPos2.multiply(deltaV1)).multiply(r);
                    Vec3 bitangent = deltaPos2.multiply(deltaU1).subtract(deltaPos1.multiply(deltaU2)).multiply(r);

                    for (int j = 0; j < 3; j++){
                        tangents.add(tangent);
                        bitangents.add(bitangent);
                    }
                }
        }
    }
}
