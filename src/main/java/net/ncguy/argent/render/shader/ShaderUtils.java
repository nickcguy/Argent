package net.ncguy.argent.render.shader;

import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.ncguy.argent.utils.Reference.Defaults.Shaders.UNIFORMS;

/**
 * Created by Guy on 23/06/2016.
 */
public class ShaderUtils {

    public static Map<BaseShader.Uniform, BaseShader.Setter> detectUniforms(String vertexShaderCode, String fragmentShaderCode) {
        List<String> uniformLines = new ArrayList<>();
        String[] lines = null;
        // Vertex pass
        lines = vertexShaderCode.split("\n");
        for (String line : lines) {
            if(line.contains("uniform "))
                uniformLines.add(line);
        }
        // Fragment pass
        lines = fragmentShaderCode.split("\n");
        for(String line : lines) {
            if(line.contains("uniform "))
                uniformLines.add(line);
        }

        Map<BaseShader.Uniform, BaseShader.Setter> uniforms = new HashMap<>();

        uniformLines.forEach(line -> {
            String[] lineArr = null;
            if(line.startsWith("layout")) {
                lineArr = line.split("=")[1].split(" ");
            }else{
                lineArr = line.split("=")[0].split(" ");
            }
            Map.Entry<BaseShader.Uniform, BaseShader.Setter> entry = getUniformSet(lineArr[lineArr.length-1].replace(";", ""));
            if(entry != null)
                uniforms.put(entry.getKey(), entry.getValue());
        });
        return uniforms;
    }

    private static Map.Entry<BaseShader.Uniform, BaseShader.Setter> getUniformSet(String name) {
        for (Map.Entry<BaseShader.Uniform, BaseShader.Setter> entry : UNIFORMS().entrySet()) {
            if(entry.getKey().alias.equals(name))
                return entry;
        }
        return null;
    }

}
