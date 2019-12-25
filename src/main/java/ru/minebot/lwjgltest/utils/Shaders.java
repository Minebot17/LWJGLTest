package ru.minebot.lwjgltest.utils;

import ru.minebot.lwjgltest.render.Shader;

import java.util.ArrayList;
import java.util.List;

public class Shaders {

    public static List<Shader> shaders = new ArrayList<>();
    public static Shader shadowMap;

    public static void initialize(){
        shadowMap = new Shader("shaders/shadowMap.vert", "shaders/shadowMap.frag", null);

        shaders.forEach(Shader::initialization);
    }
}
