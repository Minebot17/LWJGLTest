package ru.minebot.lwjgltest.utils;

import ru.minebot.lwjgltest.render.Shader;

import java.util.ArrayList;
import java.util.List;

public class Shaders {

    public static List<Shader> shaders = new ArrayList<>();
    public static Shader shadowMap;
    public static Shader cubemap;
    public static Shader post;
    public static Shader standart;

    public static void initialize(){
        shadowMap = new Shader("shaders/shadowMap.vert", "shaders/shadowMap.frag", null);
        cubemap = new Shader("shaders/cubemap.vert", "shaders/cubemap.frag", null);
        post = new Shader("shaders/post.vert", "shaders/post.frag", null);
        standart = new Shader("shaders/standart.vert", "shaders/standart.frag", "shaders/standart.geom");

        shaders.forEach(Shader::initialization);
    }
}
