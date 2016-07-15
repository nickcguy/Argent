package net.ncguy.argent.render.shader.program;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;
import org.lwjgl.opengl.GL32;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/**
 * Created by Guy on 12/07/2016.
 */
public class GeometryShaderProgram extends ShaderProgram {

    public GeometryShaderProgram(FileHandle vertexShader, FileHandle geometryShader, FileHandle fragmentShader) {
        this(vertexShader.readString(), geometryShader.readString(), fragmentShader.readString());
    }

    public GeometryShaderProgram(String vertexShader, String geometryShader, String fragmentShader) {
        super(vertexShader, fragmentShader);
        this.geometryShaderSource = geometryShader;
        try {
            compileShaders(vertexShader, geometryShader, fragmentShader);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(isCompiled()) {
            try {
                fetchAttributes();
                fetchUniforms();
                addManagedShader();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void addManagedShader() throws Exception {
        addManagedShader(Gdx.app, this);
    }
    protected void addManagedShader(Application app) throws Exception {
        addManagedShader(app, this);
    }
    protected void addManagedShader(Application app, ShaderProgram shader) throws Exception {
        ObjectMap<Application, Array<ShaderProgram>> shaders = $getValue__shaders();
        Array<ShaderProgram> managedResources = shaders.get(app);
        if (managedResources == null) managedResources = new Array<>();
        managedResources.add(shader);
        shaders.put(app, managedResources);
    }

    protected String geometryShaderSource;
    protected int geometryShaderHandle;

    protected int loadShader(int type, String source) throws Exception {
        IntBuffer intbuf = BufferUtils.newIntBuffer(1);
        int shader = Gdx.gl.glCreateShader(type);
        if(shader == 0) return -1;
        Gdx.gl.glShaderSource(shader, source);
        Gdx.gl.glCompileShader(shader);
        Gdx.gl.glGetShaderiv(shader, GL20.GL_COMPILE_STATUS, intbuf);

        int compiled = intbuf.get(0);
        if(compiled == 0) {
            $setValue_log($getValue__log() + Gdx.gl.glGetShaderInfoLog(shader));
            return -1;
        }
        return shader;
    }

    protected void compileShaders(String vertexShader, String geometryShader, String fragmentShader) throws Exception {
        if(!isCompiled()) return;
        $setValue_vertexShaderHandle(loadShader(GL20.GL_VERTEX_SHADER, vertexShader));
        geometryShaderHandle = loadShader(GL32.GL_GEOMETRY_SHADER, geometryShader);
        $setValue_fragmentShaderHandle(loadShader(GL20.GL_FRAGMENT_SHADER, fragmentShader));

        if(geometryShaderHandle == -1) {
            $setValue_isCompiled(false);
            return;
        }
        int programId = linkProgram(createProgram());
        if(programId == -1) {
            $setValue_isCompiled(false);
            return;
        }
        $setValue_program(programId);
        $setValue_isCompiled(true);
    }

    protected int linkProgram (int program) throws Exception {
        GL20 gl = Gdx.gl20;
        if (program == -1) return -1;

        gl.glAttachShader(program, $getValue__vertexShaderHandle());
        gl.glAttachShader(program, geometryShaderHandle);
        gl.glAttachShader(program, $getValue__fragmentShaderHandle());
        gl.glLinkProgram(program);

        ByteBuffer tmp = ByteBuffer.allocateDirect(4);
        tmp.order(ByteOrder.nativeOrder());
        IntBuffer intbuf = tmp.asIntBuffer();

        gl.glGetProgramiv(program, GL20.GL_LINK_STATUS, intbuf);
        int linked = intbuf.get(0);
        if (linked == 0) {
            $setValue_log(Gdx.gl20.glGetProgramInfoLog(program));
            return -1;
        }

        return program;
    }

    private void fetchAttributes () throws Exception {
        IntBuffer params = $getValue__params();
        IntBuffer type = $getValue__type();
        int program = $getValue__program();
        ObjectIntMap<String> attributes = $getValue__attributes();
        ObjectIntMap<String> attributeTypes = $getValue__attributeTypes();
        ObjectIntMap<String> attributeSizes = $getValue__attributeSizes();

        params.clear();
        Gdx.gl20.glGetProgramiv(program, GL20.GL_ACTIVE_ATTRIBUTES, params);
        int numAttributes = params.get(0);

        String[] attributeNames = new String[numAttributes];

        for (int i = 0; i < numAttributes; i++) {
            params.clear();
            params.put(0, 1);
            type.clear();
            String name = Gdx.gl20.glGetActiveAttrib(program, i, params, type);
            int location = Gdx.gl20.glGetAttribLocation(program, name);
            attributes.put(name, location);
            attributeTypes.put(name, type.get(0));
            attributeSizes.put(name, params.get(0));
            attributeNames[i] = name;
        }

        $setValue_attributeNames(attributeNames);
    }

    private void fetchUniforms () throws Exception {
        IntBuffer params = $getValue__params();
        IntBuffer type = $getValue__type();
        int program = $getValue__program();
        ObjectIntMap<String> uniforms = $getValue__uniforms();
        ObjectIntMap<String> uniformTypes = $getValue__uniformTypes();
        ObjectIntMap<String> uniformSizes = $getValue__uniformSizes();

        params.clear();
        Gdx.gl20.glGetProgramiv(program, GL20.GL_ACTIVE_UNIFORMS, params);
        int numUniforms = params.get(0);

        String[] uniformNames = new String[numUniforms];

        for (int i = 0; i < numUniforms; i++) {
            params.clear();
            params.put(0, 1);
            type.clear();
            String name = Gdx.gl20.glGetActiveUniform(program, i, params, type);
            int location = Gdx.gl20.glGetUniformLocation(program, name);
            uniforms.put(name, location);
            uniformTypes.put(name, type.get(0));
            uniformSizes.put(name, params.get(0));
            uniformNames[i] = name;
        }
        $setValue_uniformNames(uniformNames);
    }

// AUTOGENERATED

    private java.lang.reflect.Field $gen_POSITION_ATTRIBUTE;
    public java.lang.String $getValue__POSITION_ATTRIBUTE() throws Exception {
        if($gen_POSITION_ATTRIBUTE == null) {
            $gen_POSITION_ATTRIBUTE = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("POSITION_ATTRIBUTE");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_POSITION_ATTRIBUTE);
        }
        return (java.lang.String)$gen_POSITION_ATTRIBUTE.get(this);
    }
    public void $setValue_POSITION_ATTRIBUTE(java.lang.String var) throws Exception {
        if($gen_POSITION_ATTRIBUTE == null) {
            $gen_POSITION_ATTRIBUTE = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("POSITION_ATTRIBUTE");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_POSITION_ATTRIBUTE);
        }
        $gen_POSITION_ATTRIBUTE.set(this, (java.lang.String)var);
    }

    private java.lang.reflect.Field $gen_NORMAL_ATTRIBUTE;
    public java.lang.String $getValue__NORMAL_ATTRIBUTE() throws Exception {
        if($gen_NORMAL_ATTRIBUTE == null) {
            $gen_NORMAL_ATTRIBUTE = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("NORMAL_ATTRIBUTE");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_NORMAL_ATTRIBUTE);
        }
        return (java.lang.String)$gen_NORMAL_ATTRIBUTE.get(this);
    }
    public void $setValue_NORMAL_ATTRIBUTE(java.lang.String var) throws Exception {
        if($gen_NORMAL_ATTRIBUTE == null) {
            $gen_NORMAL_ATTRIBUTE = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("NORMAL_ATTRIBUTE");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_NORMAL_ATTRIBUTE);
        }
        $gen_NORMAL_ATTRIBUTE.set(this, (java.lang.String)var);
    }

    private java.lang.reflect.Field $gen_COLOR_ATTRIBUTE;
    public java.lang.String $getValue__COLOR_ATTRIBUTE() throws Exception {
        if($gen_COLOR_ATTRIBUTE == null) {
            $gen_COLOR_ATTRIBUTE = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("COLOR_ATTRIBUTE");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_COLOR_ATTRIBUTE);
        }
        return (java.lang.String)$gen_COLOR_ATTRIBUTE.get(this);
    }
    public void $setValue_COLOR_ATTRIBUTE(java.lang.String var) throws Exception {
        if($gen_COLOR_ATTRIBUTE == null) {
            $gen_COLOR_ATTRIBUTE = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("COLOR_ATTRIBUTE");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_COLOR_ATTRIBUTE);
        }
        $gen_COLOR_ATTRIBUTE.set(this, (java.lang.String)var);
    }

    private java.lang.reflect.Field $gen_TEXCOORD_ATTRIBUTE;
    public java.lang.String $getValue__TEXCOORD_ATTRIBUTE() throws Exception {
        if($gen_TEXCOORD_ATTRIBUTE == null) {
            $gen_TEXCOORD_ATTRIBUTE = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("TEXCOORD_ATTRIBUTE");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_TEXCOORD_ATTRIBUTE);
        }
        return (java.lang.String)$gen_TEXCOORD_ATTRIBUTE.get(this);
    }
    public void $setValue_TEXCOORD_ATTRIBUTE(java.lang.String var) throws Exception {
        if($gen_TEXCOORD_ATTRIBUTE == null) {
            $gen_TEXCOORD_ATTRIBUTE = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("TEXCOORD_ATTRIBUTE");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_TEXCOORD_ATTRIBUTE);
        }
        $gen_TEXCOORD_ATTRIBUTE.set(this, (java.lang.String)var);
    }

    private java.lang.reflect.Field $gen_TANGENT_ATTRIBUTE;
    public java.lang.String $getValue__TANGENT_ATTRIBUTE() throws Exception {
        if($gen_TANGENT_ATTRIBUTE == null) {
            $gen_TANGENT_ATTRIBUTE = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("TANGENT_ATTRIBUTE");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_TANGENT_ATTRIBUTE);
        }
        return (java.lang.String)$gen_TANGENT_ATTRIBUTE.get(this);
    }
    public void $setValue_TANGENT_ATTRIBUTE(java.lang.String var) throws Exception {
        if($gen_TANGENT_ATTRIBUTE == null) {
            $gen_TANGENT_ATTRIBUTE = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("TANGENT_ATTRIBUTE");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_TANGENT_ATTRIBUTE);
        }
        $gen_TANGENT_ATTRIBUTE.set(this, (java.lang.String)var);
    }

    private java.lang.reflect.Field $gen_BINORMAL_ATTRIBUTE;
    public java.lang.String $getValue__BINORMAL_ATTRIBUTE() throws Exception {
        if($gen_BINORMAL_ATTRIBUTE == null) {
            $gen_BINORMAL_ATTRIBUTE = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("BINORMAL_ATTRIBUTE");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_BINORMAL_ATTRIBUTE);
        }
        return (java.lang.String)$gen_BINORMAL_ATTRIBUTE.get(this);
    }
    public void $setValue_BINORMAL_ATTRIBUTE(java.lang.String var) throws Exception {
        if($gen_BINORMAL_ATTRIBUTE == null) {
            $gen_BINORMAL_ATTRIBUTE = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("BINORMAL_ATTRIBUTE");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_BINORMAL_ATTRIBUTE);
        }
        $gen_BINORMAL_ATTRIBUTE.set(this, (java.lang.String)var);
    }

    private java.lang.reflect.Field $gen_pedantic;
    public boolean $getValue__pedantic() throws Exception {
        if($gen_pedantic == null) {
            $gen_pedantic = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("pedantic");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_pedantic);
        }
        return (boolean)$gen_pedantic.get(this);
    }
    public void $setValue_pedantic(boolean var) throws Exception {
        if($gen_pedantic == null) {
            $gen_pedantic = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("pedantic");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_pedantic);
        }
        $gen_pedantic.set(this, (boolean)var);
    }

    private java.lang.reflect.Field $gen_prependVertexCode;
    public java.lang.String $getValue__prependVertexCode() throws Exception {
        if($gen_prependVertexCode == null) {
            $gen_prependVertexCode = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("prependVertexCode");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_prependVertexCode);
        }
        return (java.lang.String)$gen_prependVertexCode.get(this);
    }
    public void $setValue_prependVertexCode(java.lang.String var) throws Exception {
        if($gen_prependVertexCode == null) {
            $gen_prependVertexCode = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("prependVertexCode");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_prependVertexCode);
        }
        $gen_prependVertexCode.set(this, (java.lang.String)var);
    }

    private java.lang.reflect.Field $gen_prependFragmentCode;
    public java.lang.String $getValue__prependFragmentCode() throws Exception {
        if($gen_prependFragmentCode == null) {
            $gen_prependFragmentCode = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("prependFragmentCode");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_prependFragmentCode);
        }
        return (java.lang.String)$gen_prependFragmentCode.get(this);
    }
    public void $setValue_prependFragmentCode(java.lang.String var) throws Exception {
        if($gen_prependFragmentCode == null) {
            $gen_prependFragmentCode = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("prependFragmentCode");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_prependFragmentCode);
        }
        $gen_prependFragmentCode.set(this, (java.lang.String)var);
    }

    private java.lang.reflect.Field $gen_shaders;
    public com.badlogic.gdx.utils.ObjectMap $getValue__shaders() throws Exception {
        if($gen_shaders == null) {
            $gen_shaders = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("shaders");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_shaders);
        }
        return (com.badlogic.gdx.utils.ObjectMap)$gen_shaders.get(this);
    }
    public void $setValue_shaders(com.badlogic.gdx.utils.ObjectMap var) throws Exception {
        if($gen_shaders == null) {
            $gen_shaders = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("shaders");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_shaders);
        }
        $gen_shaders.set(this, (com.badlogic.gdx.utils.ObjectMap)var);
    }

    private java.lang.reflect.Field $gen_log;
    public java.lang.String $getValue__log() throws Exception {
        if($gen_log == null) {
            $gen_log = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("log");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_log);
        }
        return (java.lang.String)$gen_log.get(this);
    }
    public void $setValue_log(java.lang.String var) throws Exception {
        if($gen_log == null) {
            $gen_log = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("log");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_log);
        }
        $gen_log.set(this, (java.lang.String)var);
    }

    private java.lang.reflect.Field $gen_isCompiled;
    public boolean $getValue__isCompiled() throws Exception {
        if($gen_isCompiled == null) {
            $gen_isCompiled = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("isCompiled");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_isCompiled);
        }
        return (boolean)$gen_isCompiled.get(this);
    }
    public void $setValue_isCompiled(boolean var) throws Exception {
        if($gen_isCompiled == null) {
            $gen_isCompiled = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("isCompiled");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_isCompiled);
        }
        $gen_isCompiled.set(this, (boolean)var);
    }

    private java.lang.reflect.Field $gen_uniforms;
    public com.badlogic.gdx.utils.ObjectIntMap $getValue__uniforms() throws Exception {
        if($gen_uniforms == null) {
            $gen_uniforms = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("uniforms");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_uniforms);
        }
        return (com.badlogic.gdx.utils.ObjectIntMap)$gen_uniforms.get(this);
    }
    public void $setValue_uniforms(com.badlogic.gdx.utils.ObjectIntMap var) throws Exception {
        if($gen_uniforms == null) {
            $gen_uniforms = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("uniforms");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_uniforms);
        }
        $gen_uniforms.set(this, (com.badlogic.gdx.utils.ObjectIntMap)var);
    }

    private java.lang.reflect.Field $gen_uniformTypes;
    public com.badlogic.gdx.utils.ObjectIntMap $getValue__uniformTypes() throws Exception {
        if($gen_uniformTypes == null) {
            $gen_uniformTypes = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("uniformTypes");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_uniformTypes);
        }
        return (com.badlogic.gdx.utils.ObjectIntMap)$gen_uniformTypes.get(this);
    }
    public void $setValue_uniformTypes(com.badlogic.gdx.utils.ObjectIntMap var) throws Exception {
        if($gen_uniformTypes == null) {
            $gen_uniformTypes = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("uniformTypes");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_uniformTypes);
        }
        $gen_uniformTypes.set(this, (com.badlogic.gdx.utils.ObjectIntMap)var);
    }

    private java.lang.reflect.Field $gen_uniformSizes;
    public com.badlogic.gdx.utils.ObjectIntMap $getValue__uniformSizes() throws Exception {
        if($gen_uniformSizes == null) {
            $gen_uniformSizes = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("uniformSizes");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_uniformSizes);
        }
        return (com.badlogic.gdx.utils.ObjectIntMap)$gen_uniformSizes.get(this);
    }
    public void $setValue_uniformSizes(com.badlogic.gdx.utils.ObjectIntMap var) throws Exception {
        if($gen_uniformSizes == null) {
            $gen_uniformSizes = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("uniformSizes");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_uniformSizes);
        }
        $gen_uniformSizes.set(this, (com.badlogic.gdx.utils.ObjectIntMap)var);
    }

    private java.lang.reflect.Field $gen_uniformNames;
    public java.lang.String[] $getValue__uniformNames() throws Exception {
        if($gen_uniformNames == null) {
            $gen_uniformNames = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("uniformNames");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_uniformNames);
        }
        return (java.lang.String[])$gen_uniformNames.get(this);
    }
    public void $setValue_uniformNames(java.lang.String[] var) throws Exception {
        if($gen_uniformNames == null) {
            $gen_uniformNames = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("uniformNames");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_uniformNames);
        }
        $gen_uniformNames.set(this, (java.lang.String[])var);
    }

    private java.lang.reflect.Field $gen_attributes;
    public com.badlogic.gdx.utils.ObjectIntMap $getValue__attributes() throws Exception {
        if($gen_attributes == null) {
            $gen_attributes = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("attributes");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_attributes);
        }
        return (com.badlogic.gdx.utils.ObjectIntMap)$gen_attributes.get(this);
    }
    public void $setValue_attributes(com.badlogic.gdx.utils.ObjectIntMap var) throws Exception {
        if($gen_attributes == null) {
            $gen_attributes = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("attributes");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_attributes);
        }
        $gen_attributes.set(this, (com.badlogic.gdx.utils.ObjectIntMap)var);
    }

    private java.lang.reflect.Field $gen_attributeTypes;
    public com.badlogic.gdx.utils.ObjectIntMap $getValue__attributeTypes() throws Exception {
        if($gen_attributeTypes == null) {
            $gen_attributeTypes = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("attributeTypes");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_attributeTypes);
        }
        return (com.badlogic.gdx.utils.ObjectIntMap)$gen_attributeTypes.get(this);
    }
    public void $setValue_attributeTypes(com.badlogic.gdx.utils.ObjectIntMap var) throws Exception {
        if($gen_attributeTypes == null) {
            $gen_attributeTypes = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("attributeTypes");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_attributeTypes);
        }
        $gen_attributeTypes.set(this, (com.badlogic.gdx.utils.ObjectIntMap)var);
    }

    private java.lang.reflect.Field $gen_attributeSizes;
    public com.badlogic.gdx.utils.ObjectIntMap $getValue__attributeSizes() throws Exception {
        if($gen_attributeSizes == null) {
            $gen_attributeSizes = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("attributeSizes");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_attributeSizes);
        }
        return (com.badlogic.gdx.utils.ObjectIntMap)$gen_attributeSizes.get(this);
    }
    public void $setValue_attributeSizes(com.badlogic.gdx.utils.ObjectIntMap var) throws Exception {
        if($gen_attributeSizes == null) {
            $gen_attributeSizes = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("attributeSizes");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_attributeSizes);
        }
        $gen_attributeSizes.set(this, (com.badlogic.gdx.utils.ObjectIntMap)var);
    }

    private java.lang.reflect.Field $gen_attributeNames;
    public java.lang.String[] $getValue__attributeNames() throws Exception {
        if($gen_attributeNames == null) {
            $gen_attributeNames = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("attributeNames");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_attributeNames);
        }
        return (java.lang.String[])$gen_attributeNames.get(this);
    }
    public void $setValue_attributeNames(java.lang.String[] var) throws Exception {
        if($gen_attributeNames == null) {
            $gen_attributeNames = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("attributeNames");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_attributeNames);
        }
        $gen_attributeNames.set(this, (java.lang.String[])var);
    }

    private java.lang.reflect.Field $gen_program;
    public int $getValue__program() throws Exception {
        if($gen_program == null) {
            $gen_program = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("program");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_program);
        }
        return (int)$gen_program.get(this);
    }
    public void $setValue_program(int var) throws Exception {
        if($gen_program == null) {
            $gen_program = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("program");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_program);
        }
        $gen_program.set(this, (int)var);
    }

    private java.lang.reflect.Field $gen_vertexShaderHandle;
    public int $getValue__vertexShaderHandle() throws Exception {
        if($gen_vertexShaderHandle == null) {
            $gen_vertexShaderHandle = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("vertexShaderHandle");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_vertexShaderHandle);
        }
        return (int)$gen_vertexShaderHandle.get(this);
    }
    public void $setValue_vertexShaderHandle(int var) throws Exception {
        if($gen_vertexShaderHandle == null) {
            $gen_vertexShaderHandle = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("vertexShaderHandle");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_vertexShaderHandle);
        }
        $gen_vertexShaderHandle.set(this, (int)var);
    }

    private java.lang.reflect.Field $gen_fragmentShaderHandle;
    public int $getValue__fragmentShaderHandle() throws Exception {
        if($gen_fragmentShaderHandle == null) {
            $gen_fragmentShaderHandle = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("fragmentShaderHandle");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_fragmentShaderHandle);
        }
        return (int)$gen_fragmentShaderHandle.get(this);
    }
    public void $setValue_fragmentShaderHandle(int var) throws Exception {
        if($gen_fragmentShaderHandle == null) {
            $gen_fragmentShaderHandle = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("fragmentShaderHandle");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_fragmentShaderHandle);
        }
        $gen_fragmentShaderHandle.set(this, (int)var);
    }

    private java.lang.reflect.Field $gen_matrix;
    public java.nio.FloatBuffer $getValue__matrix() throws Exception {
        if($gen_matrix == null) {
            $gen_matrix = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("matrix");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_matrix);
        }
        return (java.nio.FloatBuffer)$gen_matrix.get(this);
    }
    public void $setValue_matrix(java.nio.FloatBuffer var) throws Exception {
        if($gen_matrix == null) {
            $gen_matrix = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("matrix");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_matrix);
        }
        $gen_matrix.set(this, (java.nio.FloatBuffer)var);
    }

    private java.lang.reflect.Field $gen_vertexShaderSource;
    public java.lang.String $getValue__vertexShaderSource() throws Exception {
        if($gen_vertexShaderSource == null) {
            $gen_vertexShaderSource = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("vertexShaderSource");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_vertexShaderSource);
        }
        return (java.lang.String)$gen_vertexShaderSource.get(this);
    }
    public void $setValue_vertexShaderSource(java.lang.String var) throws Exception {
        if($gen_vertexShaderSource == null) {
            $gen_vertexShaderSource = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("vertexShaderSource");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_vertexShaderSource);
        }
        $gen_vertexShaderSource.set(this, (java.lang.String)var);
    }

    private java.lang.reflect.Field $gen_fragmentShaderSource;
    public java.lang.String $getValue__fragmentShaderSource() throws Exception {
        if($gen_fragmentShaderSource == null) {
            $gen_fragmentShaderSource = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("fragmentShaderSource");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_fragmentShaderSource);
        }
        return (java.lang.String)$gen_fragmentShaderSource.get(this);
    }
    public void $setValue_fragmentShaderSource(java.lang.String var) throws Exception {
        if($gen_fragmentShaderSource == null) {
            $gen_fragmentShaderSource = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("fragmentShaderSource");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_fragmentShaderSource);
        }
        $gen_fragmentShaderSource.set(this, (java.lang.String)var);
    }

    private java.lang.reflect.Field $gen_invalidated;
    public boolean $getValue__invalidated() throws Exception {
        if($gen_invalidated == null) {
            $gen_invalidated = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("invalidated");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_invalidated);
        }
        return (boolean)$gen_invalidated.get(this);
    }
    public void $setValue_invalidated(boolean var) throws Exception {
        if($gen_invalidated == null) {
            $gen_invalidated = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("invalidated");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_invalidated);
        }
        $gen_invalidated.set(this, (boolean)var);
    }

    private java.lang.reflect.Field $gen_refCount;
    public int $getValue__refCount() throws Exception {
        if($gen_refCount == null) {
            $gen_refCount = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("refCount");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_refCount);
        }
        return (int)$gen_refCount.get(this);
    }
    public void $setValue_refCount(int var) throws Exception {
        if($gen_refCount == null) {
            $gen_refCount = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("refCount");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_refCount);
        }
        $gen_refCount.set(this, (int)var);
    }

    private java.lang.reflect.Field $gen_intbuf;
    public java.nio.IntBuffer $getValue__intbuf() throws Exception {
        if($gen_intbuf == null) {
            $gen_intbuf = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("intbuf");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_intbuf);
        }
        return (java.nio.IntBuffer)$gen_intbuf.get(this);
    }
    public void $setValue_intbuf(java.nio.IntBuffer var) throws Exception {
        if($gen_intbuf == null) {
            $gen_intbuf = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("intbuf");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_intbuf);
        }
        $gen_intbuf.set(this, (java.nio.IntBuffer)var);
    }

    private java.lang.reflect.Field $gen_params;
    public java.nio.IntBuffer $getValue__params() throws Exception {
        if($gen_params == null) {
            $gen_params = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("params");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_params);
        }
        return (java.nio.IntBuffer)$gen_params.get(this);
    }
    public void $setValue_params(java.nio.IntBuffer var) throws Exception {
        if($gen_params == null) {
            $gen_params = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("params");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_params);
        }
        $gen_params.set(this, (java.nio.IntBuffer)var);
    }

    private java.lang.reflect.Field $gen_type;
    public java.nio.IntBuffer $getValue__type() throws Exception {
        if($gen_type == null) {
            $gen_type = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("type");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_type);
        }
        return (java.nio.IntBuffer)$gen_type.get(this);
    }
    public void $setValue_type(java.nio.IntBuffer var) throws Exception {
        if($gen_type == null) {
            $gen_type = com.badlogic.gdx.graphics.glutils.ShaderProgram.class.getDeclaredField("type");
            net.ncguy.argent.utils.ReflectionUtils.setCompletelyAccessible($gen_type);
        }
        $gen_type.set(this, (java.nio.IntBuffer)var);
    }




}
