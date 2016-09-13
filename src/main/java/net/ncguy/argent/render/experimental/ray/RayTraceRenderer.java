package net.ncguy.argent.render.experimental.ray;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import net.ncguy.argent.entity.WorldEntity;
import net.ncguy.argent.utils.AppUtils;
import net.ncguy.argent.utils.MultiTargetFrameBuffer;
import net.ncguy.argent.utils.ReflectionUtils;
import net.ncguy.argent.utils.ScreenshotUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL42;
import org.lwjgl.opengl.GL43;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Created by Guy on 11/09/2016.
 */
public class RayTraceRenderer {

    ShaderProgram quadProgram;
    SpriteBatch batch;
    int computeShader = 0;
    int computeProgram = 0;
    MultiTargetFrameBuffer fbo;
    int tex;
    int vao;

    int workGroupSizeX, workGroupSizeY;

    int eyeUniform;
    int ray00Uniform;
    int ray01Uniform;
    int ray10Uniform;
    int ray11Uniform;

    PerspectiveCamera worldCamera;
    OrthographicCamera screenCamera;

    public RayTraceRenderer(PerspectiveCamera worldCamera, OrthographicCamera screenCamera) {
        this.worldCamera = worldCamera;
        this.screenCamera = screenCamera;
        initialize();
    }

    public void initialize() {
        this.quadProgram = initializeRaster();
        this.computeShader = initializeCompute();
        this.computeProgram = initializeComputeProgram();
        this.fbo = initializeFBO();
        this.tex = createFramebufferTexture();
        this.batch = initializeSpriteBatch();
        this.vao = quadFullScreenVao();
        initQuadProgram();
        initComputeProgram();
    }

    private int quadFullScreenVao() {
        int vao = glGenVertexArrays();
        int vbo = glGenBuffers();
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        ByteBuffer bb = BufferUtils.createByteBuffer(2 * 6);
        bb.put((byte) -1).put((byte) -1);
        bb.put((byte) 1).put((byte) -1);
        bb.put((byte) 1).put((byte) 1);
        bb.put((byte) 1).put((byte) 1);
        bb.put((byte) -1).put((byte) 1);
        bb.put((byte) -1).put((byte) -1);
        bb.flip();
        glBufferData(GL_ARRAY_BUFFER, bb, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_BYTE, false, 0, 0L);
        glBindVertexArray(0);
        return vao;
    }

    private int createFramebufferTexture() {
        int tex = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, tex);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        ByteBuffer black = null;
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, GL_RGBA, GL_FLOAT, black);
        glBindTexture(GL_TEXTURE_2D, 0);
        return tex;
    }

    private ShaderProgram initializeRaster() {
        return AppUtils.Shader.compileShader(Gdx.files.internal("assets/render/raytrace.vert"), Gdx.files.internal("assets/render/raytrace.frag"));
    }

    private void initQuadProgram() {
        int program = ReflectionUtils.getShaderProgramHandle(quadProgram);
        glUseProgram(program);
        int texUniform = glGetUniformLocation(program, "tex");
        glUniform1i(texUniform, 1);
        glUseProgram(0);
    }

    private int initializeCompute() {
        int shader = 0;
        shader = Gdx.gl.glCreateShader(GL43.GL_COMPUTE_SHADER);
        if(shader == 0) return 0;
        Gdx.gl.glShaderSource(shader, Gdx.files.internal("assets/render/raytrace.compute").readString());
        Gdx.gl.glCompileShader(shader);
        return shader;
    }

    private int initializeComputeProgram() {
        int program = Gdx.gl.glCreateProgram();
        Gdx.gl.glAttachShader(program, this.computeShader);
        Gdx.gl.glLinkProgram(program);

        int linked = glGetProgrami(program, GL_LINK_STATUS);
        String programLog = glGetProgramInfoLog(program);
        if (programLog.trim().length() > 0) {
            System.err.println(programLog);
        }
        if (linked == 0) {
            throw new AssertionError("Could not link program");
        }
        return program;
    }

    private void initComputeProgram() {
        glUseProgram(this.computeProgram);
        IntBuffer workGroupSize = BufferUtils.createIntBuffer(3);
        GL20.glGetProgramiv(this.computeProgram, GL43.GL_COMPUTE_WORK_GROUP_SIZE, workGroupSize);
        workGroupSizeX = workGroupSize.get(0);
        workGroupSizeY = workGroupSize.get(1);
        eyeUniform = glGetUniformLocation(this.computeProgram, "eye");
        ray00Uniform = glGetUniformLocation(this.computeProgram, "ray00");
        ray01Uniform = glGetUniformLocation(this.computeProgram, "ray01");
        ray10Uniform = glGetUniformLocation(this.computeProgram, "ray10");
        ray11Uniform = glGetUniformLocation(this.computeProgram, "ray11");
        glUseProgram(0);
    }

    private void trace() {
        glUseProgram(this.computeProgram);
        bindVector3(eyeUniform, worldCamera.position);
        bindVector3(ray00Uniform, worldCamera.getPickRay(-1, -1).direction);
        bindVector3(ray01Uniform, worldCamera.getPickRay(-1,  1).direction);
        bindVector3(ray10Uniform, worldCamera.getPickRay( 1, -1).direction);
        bindVector3(ray11Uniform, worldCamera.getPickRay( 1,  1).direction);

        GL42.glBindImageTexture(0, tex, 0, false, 0, GL15.GL_WRITE_ONLY, GL_RGBA32F);

        int workSizeX = nextPowerOfTwo(Gdx.graphics.getWidth());
        int workSizeY = nextPowerOfTwo(Gdx.graphics.getHeight());

        GL43.glDispatchCompute(workSizeX / workGroupSizeX, workSizeY / workGroupSizeY, 1);

        if(Gdx.input.isKeyJustPressed(Input.Keys.F2))
            ScreenshotUtils.saveScreenshot(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), "Test");

        GL42.glBindImageTexture(0, 0, 0, false, 0, GL15.GL_READ_WRITE, GL_RGBA32F);
        GL42.glMemoryBarrier(GL42.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
        glUseProgram(0);

        renderQuad();
    }

    private void bindVector3(int uniform, Vector3 vec) {
        glUniform3f(uniform, vec.x, vec.y, vec.z);
    }

    private MultiTargetFrameBuffer initializeFBO() {
        return MultiTargetFrameBuffer.create(MultiTargetFrameBuffer.Format.RGBA32F, 1, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true, false);
    }

    private SpriteBatch initializeSpriteBatch() {
        return new SpriteBatch(1024, this.quadProgram);
    }

    private void renderCompute(List<WorldEntity> entityList) {
        trace();
//        GL42.glBindImageTexture();
    }

    private void renderQuad() {
        glUseProgram(ReflectionUtils.getShaderProgramHandle(quadProgram));
        glBindVertexArray(vao);
        glBindTexture(GL_TEXTURE_2D, tex);
        glDrawArrays(GL_TRIANGLES, 0, 6);
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindVertexArray(0);
        glUseProgram(0);
//        batch.setProjectionMatrix(screenCamera.combined);
//        batch.begin();
//        batch.draw(tex, -Gdx.graphics.getWidth()/2, -Gdx.graphics.getHeight()/2, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        batch.end();
    }

    public void render(List<WorldEntity> entityList) {
        renderCompute(entityList);
    }

    static int nextPowerOfTwo(int x) {
        x--;
        x |= x >> 1;
        x |= x >> 2;
        x |= x >> 4;
        x |= x >> 8;
        x |= x >> 16;
        x++;
        return x;
    }

}
