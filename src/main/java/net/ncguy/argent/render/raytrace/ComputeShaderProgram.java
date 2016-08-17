/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package net.ncguy.argent.render.raytrace;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.*;
import net.ncguy.argent.utils.AppUtils;
import org.lwjgl.opengl.GL43;

import java.lang.StringBuilder;
import java.nio.*;

import static com.badlogic.gdx.Gdx.gl20;
import static com.badlogic.gdx.Gdx.gl30;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL43.GL_COMPUTE_WORK_GROUP_SIZE;

public class ComputeShaderProgram implements Disposable {
    /** default name for position attributes **/
    public static final String POSITION_ATTRIBUTE = "a_position";
    /** default name for normal attributes **/
    public static final String NORMAL_ATTRIBUTE = "a_normal";
    /** default name for color attributes **/
    public static final String COLOR_ATTRIBUTE = "a_color";
    /** default name for texcoords attributes, append texture unit number **/
    public static final String TEXCOORD_ATTRIBUTE = "a_texCoord";
    /** default name for tangent attribute **/
    public static final String TANGENT_ATTRIBUTE = "a_tangent";
    /** default name for binormal attribute **/
    public static final String BINORMAL_ATTRIBUTE = "a_binormal";

    /** flag indicating whether attributes & uniforms must be present at all times **/
    public static boolean pedantic = true;

    /** code that is always added to the vertex shader code, typically used to inject a #version line. Note that this is added
     * as-is, you should include a newline (`\n`) if needed. */
    public static String prependVertexCode = "";

    /** code that is always added to every fragment shader code, typically used to inject a #version line. Note that this is added
     * as-is, you should include a newline (`\n`) if needed. */
    public static String prependFragmentCode = "";

    /** the list of currently available shaders **/
    private final static ObjectMap<Application, Array<ComputeShaderProgram>> shaders = new ObjectMap<>();

    /** the log **/
    private String log = "";

    /** whether this program compiled successfully **/
    private boolean isCompiled;

    /** uniform lookup **/
    private final ObjectIntMap<String> uniforms = new ObjectIntMap<String>();

    /** uniform types **/
    private final ObjectIntMap<String> uniformTypes = new ObjectIntMap<String>();

    /** uniform sizes **/
    private final ObjectIntMap<String> uniformSizes = new ObjectIntMap<String>();

    /** uniform names **/
    private String[] uniformNames;

    /** attribute lookup **/
    private final ObjectIntMap<String> attributes = new ObjectIntMap<String>();

    /** attribute types **/
    private final ObjectIntMap<String> attributeTypes = new ObjectIntMap<String>();

    /** attribute sizes **/
    private final ObjectIntMap<String> attributeSizes = new ObjectIntMap<String>();

    /** attribute names **/
    private String[] attributeNames;

    /** program handle **/
    private int program;

    /** vertex shader handle **/
    private int vertexShaderHandle;

    /** fragment shader handle **/
    private int fragmentShaderHandle;

    /** matrix float buffer **/
    private final FloatBuffer matrix;

    /** vertex shader source **/
    private final String vertexShaderSource;

    /** fragment shader source **/
    private final String fragmentShaderSource;

    /** whether this shader was invalidated **/
    private boolean invalidated;

    /** reference count **/
    private int refCount = 0;

    /** Constructs a new ShaderProgram and immediately compiles it.
     *
     * @param vertexShader the vertex shader
     * @param fragmentShader the fragment shader */

    public ComputeShaderProgram (String vertexShader, String fragmentShader) {
        if (vertexShader == null) throw new IllegalArgumentException("vertex shader must not be null");
        if (fragmentShader == null) throw new IllegalArgumentException("fragment shader must not be null");

        if (prependVertexCode != null && prependVertexCode.length() > 0)
            vertexShader = prependVertexCode + vertexShader;
        if (prependFragmentCode != null && prependFragmentCode.length() > 0)
            fragmentShader = prependFragmentCode + fragmentShader;

        this.vertexShaderSource = vertexShader;
        this.fragmentShaderSource = fragmentShader;
        this.matrix = BufferUtils.newFloatBuffer(16);

        compileShaders(vertexShader, fragmentShader);
        if (isCompiled()) {
            fetchAttributes();
            fetchUniforms();
            addManagedShader(Gdx.app, this);
        }
    }

    public ComputeShaderProgram (FileHandle vertexShader, FileHandle fragmentShader) {
        this(vertexShader.readString(), fragmentShader.readString());
    }

    /** Loads and compiles the shaders, creates a new program and links the shaders.
     *
     * @param vertexShader
     * @param fragmentShader */
    private void compileShaders (String vertexShader, String fragmentShader) {
        vertexShaderHandle = loadShader(GL20.GL_VERTEX_SHADER, vertexShader);
        fragmentShaderHandle = loadShader(GL20.GL_FRAGMENT_SHADER, fragmentShader);

        if (vertexShaderHandle == -1 || fragmentShaderHandle == -1) {
            isCompiled = false;
            return;
        }

        program = linkProgram(createProgram());
        if (program == -1) {
            isCompiled = false;
            return;
        }

        isCompiled = true;
    }

    private int loadShader (int type, String source) {
        GL20 gl = gl20;
        IntBuffer intbuf = BufferUtils.newIntBuffer(1);

        int shader = gl.glCreateShader(type);
        if (shader == 0) return -1;

        gl.glShaderSource(shader, source);
        gl.glCompileShader(shader);
        gl.glGetShaderiv(shader, GL20.GL_COMPILE_STATUS, intbuf);

        int compiled = intbuf.get(0);
        if (compiled == 0) {
// gl.glGetShaderiv(shader, GL20.GL_INFO_LOG_LENGTH, intbuf);
// int infoLogLength = intbuf.get(0);
// if (infoLogLength > 1) {
            String infoLog = gl.glGetShaderInfoLog(shader);
            log += infoLog;
// }
            return -1;
        }

        return shader;
    }

    protected int createProgram () {
        GL20 gl = gl20;
        int program = gl.glCreateProgram();
        return program != 0 ? program : -1;
    }

    private int linkProgram (int program) {
        GL20 gl = gl20;
        if (program == -1) return -1;

        gl.glAttachShader(program, vertexShaderHandle);
        gl.glAttachShader(program, fragmentShaderHandle);
        gl.glLinkProgram(program);

        ByteBuffer tmp = ByteBuffer.allocateDirect(4);
        tmp.order(ByteOrder.nativeOrder());
        IntBuffer intbuf = tmp.asIntBuffer();

        gl.glGetProgramiv(program, GL20.GL_LINK_STATUS, intbuf);
        int linked = intbuf.get(0);
        if (linked == 0) {
// Gdx.gl20.glGetProgramiv(program, GL20.GL_INFO_LOG_LENGTH, intbuf);
// int infoLogLength = intbuf.get(0);
// if (infoLogLength > 1) {
            log = gl20.glGetProgramInfoLog(program);
// }
            return -1;
        }

        return program;
    }

    final static IntBuffer intbuf = BufferUtils.newIntBuffer(1);

    /** @return the log info for the shader compilation and program linking stage. The shader needs to be bound for this method to
     *         have an effect. */
    public String getLog () {
        if (isCompiled) {
// Gdx.gl20.glGetProgramiv(program, GL20.GL_INFO_LOG_LENGTH, intbuf);
// int infoLogLength = intbuf.get(0);
// if (infoLogLength > 1) {
            log = gl20.glGetProgramInfoLog(program);
// }
            return log;
        } else {
            return log;
        }
    }

    /** @return whether this ShaderProgram compiled successfully. */
    public boolean isCompiled () {
        return isCompiled;
    }

    private int fetchAttributeLocation (String name) {
        GL20 gl = gl20;
        // -2 == not yet cached
        // -1 == cached but not found
        int location;
        if ((location = attributes.get(name, -2)) == -2) {
            location = gl.glGetAttribLocation(program, name);
            attributes.put(name, location);
        }
        return location;
    }

    private int fetchUniformLocation (String name) {
        return fetchUniformLocation(name, pedantic);
    }

    public int fetchUniformLocation (String name, boolean pedantic) {
        GL20 gl = gl20;
        // -2 == not yet cached
        // -1 == cached but not found
        int location;
        if ((location = uniforms.get(name, -2)) == -2) {
            location = gl.glGetUniformLocation(program, name);
            if (location == -1 && pedantic) throw new IllegalArgumentException("no uniform with name '" + name + "' in shader");
            uniforms.put(name, location);
        }
        return location;
    }

    public void setUniformi (String name, int value) {
        GL20 gl = gl20;
        checkManaged();
        int location = fetchUniformLocation(name);
        gl.glUniform1i(location, value);
    }

    public void setUniformi (int location, int value) {
        GL20 gl = gl20;
        checkManaged();
        gl.glUniform1i(location, value);
    }

    public void setUniformi (String name, int value1, int value2) {
        GL20 gl = gl20;
        checkManaged();
        int location = fetchUniformLocation(name);
        gl.glUniform2i(location, value1, value2);
    }

    public void setUniformi (int location, int value1, int value2) {
        GL20 gl = gl20;
        checkManaged();
        gl.glUniform2i(location, value1, value2);
    }

    public void setUniformi (String name, int value1, int value2, int value3) {
        GL20 gl = gl20;
        checkManaged();
        int location = fetchUniformLocation(name);
        gl.glUniform3i(location, value1, value2, value3);
    }

    public void setUniformi (int location, int value1, int value2, int value3) {
        GL20 gl = gl20;
        checkManaged();
        gl.glUniform3i(location, value1, value2, value3);
    }

    public void setUniformi (String name, int value1, int value2, int value3, int value4) {
        GL20 gl = gl20;
        checkManaged();
        int location = fetchUniformLocation(name);
        gl.glUniform4i(location, value1, value2, value3, value4);
    }

    public void setUniformi (int location, int value1, int value2, int value3, int value4) {
        GL20 gl = gl20;
        checkManaged();
        gl.glUniform4i(location, value1, value2, value3, value4);
    }

    public void setUniformf (String name, float value) {
        GL20 gl = gl20;
        checkManaged();
        int location = fetchUniformLocation(name);
        gl.glUniform1f(location, value);
    }

    public void setUniformf (int location, float value) {
        GL20 gl = gl20;
        checkManaged();
        gl.glUniform1f(location, value);
    }

    public void setUniformf (String name, float value1, float value2) {
        GL20 gl = gl20;
        checkManaged();
        int location = fetchUniformLocation(name);
        gl.glUniform2f(location, value1, value2);
    }

    public void setUniformf (int location, float value1, float value2) {
        GL20 gl = gl20;
        checkManaged();
        gl.glUniform2f(location, value1, value2);
    }

    public void setUniformf (String name, float value1, float value2, float value3) {
        GL20 gl = gl20;
        checkManaged();
        int location = fetchUniformLocation(name);
        gl.glUniform3f(location, value1, value2, value3);
    }

    public void setUniformf (int location, float value1, float value2, float value3) {
        GL20 gl = gl20;
        checkManaged();
        gl.glUniform3f(location, value1, value2, value3);
    }

    public void setUniformf (String name, float value1, float value2, float value3, float value4) {
        GL20 gl = gl20;
        checkManaged();
        int location = fetchUniformLocation(name);
        gl.glUniform4f(location, value1, value2, value3, value4);
    }

    public void setUniformf (int location, float value1, float value2, float value3, float value4) {
        GL20 gl = gl20;
        checkManaged();
        gl.glUniform4f(location, value1, value2, value3, value4);
    }

    public void setUniform1fv (String name, float[] values, int offset, int length) {
        GL20 gl = gl20;
        checkManaged();
        int location = fetchUniformLocation(name);
        gl.glUniform1fv(location, length, values, offset);
    }

    public void setUniform1fv (int location, float[] values, int offset, int length) {
        GL20 gl = gl20;
        checkManaged();
        gl.glUniform1fv(location, length, values, offset);
    }

    public void setUniform2fv (String name, float[] values, int offset, int length) {
        GL20 gl = gl20;
        checkManaged();
        int location = fetchUniformLocation(name);
        gl.glUniform2fv(location, length / 2, values, offset);
    }

    public void setUniform2fv (int location, float[] values, int offset, int length) {
        GL20 gl = gl20;
        checkManaged();
        gl.glUniform2fv(location, length / 2, values, offset);
    }

    public void setUniform3fv (String name, float[] values, int offset, int length) {
        GL20 gl = gl20;
        checkManaged();
        int location = fetchUniformLocation(name);
        gl.glUniform3fv(location, length / 3, values, offset);
    }

    public void setUniform3fv (int location, float[] values, int offset, int length) {
        GL20 gl = gl20;
        checkManaged();
        gl.glUniform3fv(location, length / 3, values, offset);
    }

    public void setUniform4fv (String name, float[] values, int offset, int length) {
        GL20 gl = gl20;
        checkManaged();
        int location = fetchUniformLocation(name);
        gl.glUniform4fv(location, length / 4, values, offset);
    }

    public void setUniform4fv (int location, float[] values, int offset, int length) {
        GL20 gl = gl20;
        checkManaged();
        gl.glUniform4fv(location, length / 4, values, offset);
    }

    public void setUniformMatrix (String name, Matrix4 matrix) {
        setUniformMatrix(name, matrix, false);
    }

    public void setUniformMatrix (String name, Matrix4 matrix, boolean transpose) {
        setUniformMatrix(fetchUniformLocation(name), matrix, transpose);
    }

    public void setUniformMatrix (int location, Matrix4 matrix) {
        setUniformMatrix(location, matrix, false);
    }

    public void setUniformMatrix (int location, Matrix4 matrix, boolean transpose) {
        GL20 gl = gl20;
        checkManaged();
        gl.glUniformMatrix4fv(location, 1, transpose, matrix.val, 0);
    }

    public void setUniformMatrix (String name, Matrix3 matrix) {
        setUniformMatrix(name, matrix, false);
    }

    public void setUniformMatrix (String name, Matrix3 matrix, boolean transpose) {
        setUniformMatrix(fetchUniformLocation(name), matrix, transpose);
    }

    public void setUniformMatrix (int location, Matrix3 matrix) {
        setUniformMatrix(location, matrix, false);
    }

    public void setUniformMatrix (int location, Matrix3 matrix, boolean transpose) {
        GL20 gl = gl20;
        checkManaged();
        gl.glUniformMatrix3fv(location, 1, transpose, matrix.val, 0);
    }

    public void setUniformMatrix3fv (String name, FloatBuffer buffer, int count, boolean transpose) {
        GL20 gl = gl20;
        checkManaged();
        buffer.position(0);
        int location = fetchUniformLocation(name);
        gl.glUniformMatrix3fv(location, count, transpose, buffer);
    }

    public void setUniformMatrix4fv (String name, FloatBuffer buffer, int count, boolean transpose) {
        GL20 gl = gl20;
        checkManaged();
        buffer.position(0);
        int location = fetchUniformLocation(name);
        gl.glUniformMatrix4fv(location, count, transpose, buffer);
    }

    public void setUniformMatrix4fv (int location, float[] values, int offset, int length) {
        GL20 gl = gl20;
        checkManaged();
        gl.glUniformMatrix4fv(location, length / 16, false, values, offset);
    }

    public void setUniformMatrix4fv (String name, float[] values, int offset, int length) {
        setUniformMatrix4fv(fetchUniformLocation(name), values, offset, length);
    }

    public void setUniformf (String name, Vector2 values) {
        setUniformf(name, values.x, values.y);
    }

    public void setUniformf (int location, Vector2 values) {
        setUniformf(location, values.x, values.y);
    }

    public void setUniformf (String name, Vector3 values) {
        setUniformf(name, values.x, values.y, values.z);
    }

    public void setUniformf (int location, Vector3 values) {
        setUniformf(location, values.x, values.y, values.z);
    }

    public void setUniformf (String name, Color values) {
        setUniformf(name, values.r, values.g, values.b, values.a);
    }

    public void setUniformf (int location, Color values) {
        setUniformf(location, values.r, values.g, values.b, values.a);
    }

    public void setVertexAttribute (String name, int size, int type, boolean normalize, int stride, Buffer buffer) {
        GL20 gl = gl20;
        checkManaged();
        int location = fetchAttributeLocation(name);
        if (location == -1) return;
        gl.glVertexAttribPointer(location, size, type, normalize, stride, buffer);
    }

    public void setVertexAttribute (int location, int size, int type, boolean normalize, int stride, Buffer buffer) {
        GL20 gl = gl20;
        checkManaged();
        gl.glVertexAttribPointer(location, size, type, normalize, stride, buffer);
    }

    public void setVertexAttribute (String name, int size, int type, boolean normalize, int stride, int offset) {
        GL20 gl = gl20;
        checkManaged();
        int location = fetchAttributeLocation(name);
        if (location == -1) return;
        gl.glVertexAttribPointer(location, size, type, normalize, stride, offset);
    }

    public void setVertexAttribute (int location, int size, int type, boolean normalize, int stride, int offset) {
        GL20 gl = gl20;
        checkManaged();
        gl.glVertexAttribPointer(location, size, type, normalize, stride, offset);
    }

    public void begin () {
        GL20 gl = gl20;
        checkManaged();
        gl.glUseProgram(program);
    }

    public void end () {
        GL20 gl = gl20;
        gl.glUseProgram(0);
    }

    public void dispose () {
        GL20 gl = gl20;
        gl.glUseProgram(0);
        gl.glDeleteShader(vertexShaderHandle);
        gl.glDeleteShader(fragmentShaderHandle);
        gl.glDeleteProgram(program);
        if (shaders.get(Gdx.app) != null) shaders.get(Gdx.app).removeValue(this, true);
    }

    public void disableVertexAttribute (String name) {
        GL20 gl = gl20;
        checkManaged();
        int location = fetchAttributeLocation(name);
        if (location == -1) return;
        gl.glDisableVertexAttribArray(location);
    }

    public void disableVertexAttribute (int location) {
        GL20 gl = gl20;
        checkManaged();
        gl.glDisableVertexAttribArray(location);
    }

    public void enableVertexAttribute (String name) {
        GL20 gl = gl20;
        checkManaged();
        int location = fetchAttributeLocation(name);
        if (location == -1) return;
        gl.glEnableVertexAttribArray(location);
    }

    public void enableVertexAttribute (int location) {
        GL20 gl = gl20;
        checkManaged();
        gl.glEnableVertexAttribArray(location);
    }

    private void checkManaged () {
        if (invalidated) {
            compileShaders(vertexShaderSource, fragmentShaderSource);
            invalidated = false;
        }
    }

    private void addManagedShader (Application app, ComputeShaderProgram shaderProgram) {
        Array<ComputeShaderProgram> managedResources = shaders.get(app);
        if (managedResources == null) managedResources = new Array<ComputeShaderProgram>();
        managedResources.add(shaderProgram);
        shaders.put(app, managedResources);
    }

    public static void invalidateAllShaderPrograms (Application app) {
        if (gl20 == null) return;

        Array<ComputeShaderProgram> shaderArray = shaders.get(app);
        if (shaderArray == null) return;

        for (int i = 0; i < shaderArray.size; i++) {
            shaderArray.get(i).invalidated = true;
            shaderArray.get(i).checkManaged();
        }
    }

    public static void clearAllShaderPrograms (Application app) {
        shaders.remove(app);
    }

    public static String getManagedStatus () {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        builder.append("Managed shaders/app: { ");
        for (Application app : shaders.keys()) {
            builder.append(shaders.get(app).size);
            builder.append(" ");
        }
        builder.append("}");
        return builder.toString();
    }

    public static int getNumManagedShaderPrograms () {
        return shaders.get(Gdx.app).size;
    }

    public void setAttributef (String name, float value1, float value2, float value3, float value4) {
        GL20 gl = gl20;
        int location = fetchAttributeLocation(name);
        gl.glVertexAttrib4f(location, value1, value2, value3, value4);
    }

    IntBuffer params = BufferUtils.newIntBuffer(1);
    IntBuffer type = BufferUtils.newIntBuffer(1);

    private void fetchUniforms () {
        params.clear();
        gl20.glGetProgramiv(program, GL20.GL_ACTIVE_UNIFORMS, params);
        int numUniforms = params.get(0);

        uniformNames = new String[numUniforms];

        for (int i = 0; i < numUniforms; i++) {
            params.clear();
            params.put(0, 1);
            type.clear();
            String name = gl20.glGetActiveUniform(program, i, params, type);
            int location = gl20.glGetUniformLocation(program, name);
            uniforms.put(name, location);
            uniformTypes.put(name, type.get(0));
            uniformSizes.put(name, params.get(0));
            uniformNames[i] = name;
        }
    }

    private void fetchAttributes () {
        params.clear();
        gl20.glGetProgramiv(program, GL20.GL_ACTIVE_ATTRIBUTES, params);
        int numAttributes = params.get(0);

        attributeNames = new String[numAttributes];

        for (int i = 0; i < numAttributes; i++) {
            params.clear();
            params.put(0, 1);
            type.clear();
            String name = gl20.glGetActiveAttrib(program, i, params, type);
            int location = gl20.glGetAttribLocation(program, name);
            attributes.put(name, location);
            attributeTypes.put(name, type.get(0));
            attributeSizes.put(name, params.get(0));
            attributeNames[i] = name;
        }
    }

    public boolean hasAttribute (String name) {
        return attributes.containsKey(name);
    }

    public int getAttributeType (String name) {
        return attributeTypes.get(name, 0);
    }

    public int getAttributeLocation (String name) {
        return attributes.get(name, -1);
    }

    public int getAttributeSize (String name) {
        return attributeSizes.get(name, 0);
    }

    public boolean hasUniform (String name) {
        return uniforms.containsKey(name);
    }

    public int getUniformType (String name) {
        return uniformTypes.get(name, 0);
    }

    public int getUniformLocation (String name) {
        return uniforms.get(name, -1);
    }

    public int getUniformSize (String name) {
        return uniformSizes.get(name, 0);
    }

    public String[] getAttributes () {
        return attributeNames;
    }

    public String[] getUniforms () {
        return uniformNames;
    }

    public String getVertexShaderSource () {
        return vertexShaderSource;
    }

    public String getFragmentShaderSource () {
        return fragmentShaderSource;
    }

    // COMPUTE STUFF

    private int createShader(String src, int type) {
        int shader = glCreateShader(type);
        glShaderSource(shader, src);
        glCompileShader(shader);
        int compiled = glGetShaderi(shader, GL_COMPILE_STATUS);
        String shaderLog = glGetShaderInfoLog(shader);
        if(shaderLog.trim().length() > 0)
            System.out.println(shaderLog);
        if(compiled == 0)
            throw new AssertionError("Could not compile shader");
        return shader;
    }

    private int computeProgram;
    private String computeSrc;

    private int workGroupSizeX, workGroupSizeY;
    private int eyeUniform;
    private int ray00Uniform;
    private int ray10Uniform;
    private int ray01Uniform;
    private int ray11Uniform;

    private Vector3 eyeRay = new Vector3();

    private int createComputeProgram() {
        int program = gl30.glCreateProgram();
        int shader = createShader("assets/render/raytrace.compute", GL43.GL_COMPUTE_SHADER);
        gl30.glAttachShader(program, shader);
        gl30.glLinkProgram(program);
        int linked = glGetProgrami(program, GL20.GL_LINK_STATUS);
        String programLog = glGetProgramInfoLog(program);
        if (programLog.trim().length() > 0)
            System.out.println(programLog);
        if (linked == 0)
            throw new AssertionError("Could not link program");
        return program;
    }

    public void initComputeProgram() {
        glUseProgram(computeProgram);
        IntBuffer workGroupSize = BufferUtils.newIntBuffer(3);
        org.lwjgl.opengl.GL20.glGetProgramiv(computeProgram, GL_COMPUTE_WORK_GROUP_SIZE, workGroupSize);
        workGroupSizeX = workGroupSize.get(0);
        workGroupSizeY = workGroupSize.get(1);
        eyeUniform = glGetUniformLocation(computeProgram, "eye");
        ray00Uniform = glGetUniformLocation(computeProgram, "ray00");
        ray01Uniform = glGetUniformLocation(computeProgram, "ray01");
        ray10Uniform = glGetUniformLocation(computeProgram, "ray10");
        ray11Uniform = glGetUniformLocation(computeProgram, "ray11");
        glUseProgram(0);
    }

    public void trace(Camera camera) {
        glUseProgram(computeProgram);
        glUniform3f(eyeUniform, camera.position.x, camera.position.y, camera.position.z);
        AppUtils.Graphics.getEyeRay(camera, -1, -1, eyeRay);
        glUniform3f(ray00Uniform, eyeRay.x, eyeRay.y, eyeRay.z);
        AppUtils.Graphics.getEyeRay(camera, -1,  1, eyeRay);
        glUniform3f(ray01Uniform, eyeRay.x, eyeRay.y, eyeRay.z);
        AppUtils.Graphics.getEyeRay(camera,  1, -1, eyeRay);
        glUniform3f(ray10Uniform, eyeRay.x, eyeRay.y, eyeRay.z);
        AppUtils.Graphics.getEyeRay(camera,  1,  1, eyeRay);
        glUniform3f(ray11Uniform, eyeRay.x, eyeRay.y, eyeRay.z);
    }

}
