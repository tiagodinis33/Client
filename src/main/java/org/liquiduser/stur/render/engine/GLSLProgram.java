package org.liquiduser.stur.render.engine;

import static org.lwjgl.opengl.GL33.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import org.liquiduser.stur.engine.Material;
import org.liquiduser.stur.engine.Resource;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.liquiduser.stur.ioutils.IOUtils;

public class GLSLProgram extends Resource {
    private final int programId;

    private int vertexShaderId;
    private String location;
    private int fragmentShaderId;

    public GLSLProgram(String location) throws Exception {
        super();
        this.location = location;
        programId = glCreateProgram();
        if (programId == 0) {
            throw new Exception("Could not create Shader");
        }
    }

    public void createVertexShader(String shaderCode) throws Exception {
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) throws Exception {
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }

    protected int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new Exception("Error creating shader. Type: " + shaderType);
        }

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
        }

        glAttachShader(programId, shaderId);

        return shaderId;
    }

    public void link() throws Exception {
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

        if (vertexShaderId != 0) {
            glDetachShader(programId, vertexShaderId);
        }
        if (fragmentShaderId != 0) {
            glDetachShader(programId, fragmentShaderId);
        }

        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    @Override
    public void create() {
        String vertCode;
        String fragCode;
        try {
            JsonObject shaderJson = JsonParser
                    .parseReader(new BufferedReader(new InputStreamReader(
                            getClass().getResourceAsStream("/assets/shaders/" + location + "/shader.json"))))
                    .getAsJsonObject();
            vertCode = IOUtils.readBufferedReader(new BufferedReader(new InputStreamReader(GLSLProgram.class
                    .getResourceAsStream("/assets/glsl/" + shaderJson.get("vertex").getAsString() + ".glsl"))));
            fragCode = IOUtils.readBufferedReader(new BufferedReader(new InputStreamReader(GLSLProgram.class
                    .getResourceAsStream("/assets/glsl/" + shaderJson.get("frag").getAsString() + ".glsl"))));

            createFragmentShader(fragCode);
            createVertexShader(vertCode);
            link();
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            e.printStackTrace();
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void cleanup() {
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
        }
    }
    public int getUniformLocation(String name) throws IllegalArgumentException{
        int result = glGetUniformLocation(programId, name);
        if(result == -1){
            throw new IllegalArgumentException("Esse uniform não existe!!");
        }
        return result;
    }
	public void setUniform(String location, Matrix4f matrix) throws IllegalArgumentException {
        int loc = getUniformLocation(location);
        
        glUniformMatrix4fv(loc, true, matrixToArray(matrix));
    }
    public void setUniform(String location, Vector4f vector) throws IllegalArgumentException{
        int loc = getUniformLocation(location);
        glUniform4f(loc, vector.x, vector.y, vector.z, vector.w);
    }
    private static float[] matrixToArray(Matrix4f m)
    {
        float[] result = new float[16];
        result[0] = m.get(0,0);
        result[1] = m.get(1,0);
        result[2] = m.get(2,0);
        result[3] = m.get(3,0);
        result[4] = m.get(0,1);
        result[5] = m.get(1,1);
        result[6] = m.get(2,1);
        result[7] = m.get(3,1);
        result[8] = m.get(0,2);
        result[9] = m.get(1,2);
        result[10] = m.get(2,2);
        result[11] = m.get(3,2);
        result[12] = m.get(0,3);
        result[13] = m.get(1,3);
        result[14] = m.get(2,3);
        result[15] = m.get(3,3);
        
        return result;
    }

	public void setUniform(String location, Material material) throws IllegalArgumentException {
        setUniform(location + ".color", material.getColor());
        if(material.getTexture() != null){
            setUniform(location + ".tex.slot", material.getTexture().getSlot());
            setUniform(location + ".tex.enabled", 1);
        }
	}

    public void setUniform(String location, int i) {
        int loc = getUniformLocation(location);
        glUniform1i(loc, i);
    }
}
