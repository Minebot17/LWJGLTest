package ru.minebot.lwjgltest.render;

import com.hackoeur.jglm.Mat3;
import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Vec3;
import com.hackoeur.jglm.Vec4;
import com.sun.istack.internal.Nullable;
import ru.minebot.lwjgltest.utils.Shaders;
import ru.minebot.lwjgltest.utils.Utils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL40.*;

public class Shader {
    protected String vertexPath;
    protected String fragmentPath;
    protected String geometryPath;
    protected int programmeId = -1;

    public Shader(String vertexPath, String fragmentPath, @Nullable String geometryPath) {
        this.vertexPath = vertexPath;
        this.fragmentPath = fragmentPath;
        this.geometryPath = geometryPath;
        Shaders.shaders.add(this);
    }

    public void initialization(){
        int vertexId = glCreateShader(GL_VERTEX_SHADER);
        int fragmentId = glCreateShader(GL_FRAGMENT_SHADER);
        int geometryId = geometryPath != null ? glCreateShader(GL_GEOMETRY_SHADER) : -1;

        compile(vertexId, vertexPath);
        compile(fragmentId, fragmentPath);
        if (geometryId != -1)
            compile(geometryId, geometryPath);

        programmeId = glCreateProgram();
        glAttachShader(programmeId, vertexId);
        glAttachShader(programmeId, fragmentId);
        if (geometryId != -1)
            glAttachShader(programmeId, geometryId);
        glLinkProgram(programmeId);

        if (glGetProgrami(programmeId, GL_LINK_STATUS) != 1)
            System.err.println(glGetProgramInfoLog(programmeId));

        glDeleteShader(vertexId);
        glDeleteShader(fragmentId);
        if (geometryId != -1)
            glDeleteShader(geometryId);
    }

    private void compile(int id, String path){
        glShaderSource(id, Utils.concatenate(Utils.readResource(path)));
        glCompileShader(id);

        if (glGetShaderi(id, GL_COMPILE_STATUS) != 1)
            System.err.println(glGetShaderInfoLog(id));
    }

    public <T> void setUniform(String name, T value){
        int location = glGetUniformLocation(programmeId, name);
        if (value instanceof Integer)
            glUniform1i(location, (Integer) value);
        else if (value instanceof Float)
            glUniform1f(location, (Float) value);
        else if (value instanceof Double)
            glUniform1d(location, (Double) value);
        else if (value instanceof Vec3) {
            Vec3 vec = (Vec3)value;
            glUniform3f(location, vec.getX(), vec.getY(), vec.getZ());
        }
        else if (value instanceof Vec4) {
            Vec4 vec = (Vec4)value;
            glUniform4f(location, vec.getX(), vec.getY(), vec.getZ(), vec.getW());
        }
        else if (value instanceof Mat3)
            glUniformMatrix3fv(location, false, ((Mat3)value).getBuffer().array());
        else if (value instanceof Mat4)
            glUniformMatrix4fv(location, false, ((Mat4)value).getBuffer().array());
        else if (value instanceof int[])
            glUniform1iv(location, (int[])value);
        else if (value instanceof float[])
            glUniform1fv(location, (float[])value);
        else if (value instanceof double[])
            glUniform1dv(location, (double[])value);
    }

    public int getProgrammeId() {
        return programmeId;
    }
}
