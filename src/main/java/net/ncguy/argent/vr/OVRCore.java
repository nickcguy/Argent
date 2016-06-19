package net.ncguy.argent.vr;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.oculusvr.capi.*;
import net.ncguy.argent.exception.ArgentInitialisationException;
import net.ncguy.argent.utils.ScreenshotFactory;
import org.lwjgl.util.vector.Matrix4f;

import static com.oculusvr.capi.OvrLibrary.ovrProjectionModifier.ovrProjection_ClipRangeOpenGL;

/**
 * Created by Guy on 17/06/2016.
 */
public class OVRCore implements Disposable {

    Hmd hmd;
    OvrVector3f position;
    OvrQuaternionf orientation;
    OvrMatrix4f projection;
    FovPort fovPort;
    HmdDesc desc;

    private final OvrVector3f[] eyeOffsets;
    protected final Posef[] poses;
    private final FovPort[] fovPorts;
    private final OvrSizei[] textureSizes;
    private final ViewScaleDesc viewScaleDesc = new ViewScaleDesc();
    private final Matrix4f[] projections = new Matrix4f[2];

    private TextureSwapChain swapChain = null;
    private MirrorTexture mirrorTexture = null;
    private LayerEyeFov eyeFovLayer = new LayerEyeFov();
    private FrameBuffer frameBuffer = null;

    public OVRCore() throws ArgentInitialisationException {

        Hmd.initialize();
        this.hmd = Hmd.create();
        this.desc = hmd.getDesc();

        this.eyeOffsets = OvrVector3f.buildPair();
        this.poses = Posef.buildPair();
        this.fovPorts = FovPort.buildPair();
        this.textureSizes = new OvrSizei[2];

        this.position = getTrackingState().HeadPose.Pose.Position;
        this.orientation = getTrackingState().HeadPose.Pose.Orientation;
        this.fovPort = new FovPort();
        this.projection = Hmd.getPerspectiveProjection(this.fovPort, 0.1f, 1000, 0);

        for (int eye = 0; eye < 2; ++eye) {
            fovPorts[eye] = this.desc.DefaultEyeFov[eye];
            OvrMatrix4f m = Hmd.getPerspectiveProjection(fovPorts[eye], 0.1f, 1000000f, ovrProjection_ClipRangeOpenGL);
            projections[eye] = RiftUtils.toMatrix4f(RiftUtils.toMatrix4(m));
            textureSizes[eye] = hmd.getFovTextureSize(eye, fovPorts[eye], 1.0f);
        }

        init();
    }

    public Matrix4 getPerspectiveProjection() {
        this.projection = Hmd.getPerspectiveProjection(this.fovPort, 0.1f, 1000, 0);
        return new Matrix4(this.projection.M);
    }

    public TrackingState getTrackingState() {
        return hmd.getTrackingState(0, false);
    }
    public Vector3 getPosition() {
        this.position = getTrackingState().HeadPose.Pose.Position;
        return new Vector3(position.x, position.y, position.z);
    }
    public Quaternion getOrientation() {
        this.orientation = getTrackingState().HeadPose.Pose.Orientation;
        return new Quaternion(orientation.x, orientation.y, orientation.z, orientation.w);
    }

    public void init() {
        TextureSwapChainDesc desc = new TextureSwapChainDesc();
        desc.Type = OvrLibrary.ovrTextureType.ovrTexture_2D;
        desc.ArraySize = 1;
        desc.Width = this.desc.Resolution.w;
        desc.Height = this.desc.Resolution.h;
        desc.MipLevels = 1;
        desc.Format = OvrLibrary.ovrTextureFormat.OVR_FORMAT_R8G8B8A8_UNORM_SRGB;
        desc.SampleCount = 1;
        desc.StaticImage = false;
        this.swapChain = this.hmd.createSwapTextureChain(desc);

        MirrorTextureDesc mirrorDesc = new MirrorTextureDesc();
        mirrorDesc.Format = OvrLibrary.ovrTextureFormat.OVR_FORMAT_R8G8B8A8_UNORM;
        mirrorDesc.Width = 1600;
        mirrorDesc.Height = 900;
        this.mirrorTexture = this.hmd.createMirrorTexture(mirrorDesc);

        this.eyeFovLayer.Header.Type = OvrLibrary.ovrLayerType.ovrLayerType_EyeFov;
        this.eyeFovLayer.ColorTexure[0] = swapChain;
        this.eyeFovLayer.Fov = fovPorts;
        this.eyeFovLayer.RenderPose = poses;
        for(int eye = 0; eye < 2; eye++) {
            this.eyeFovLayer.Viewport[eye].Size = textureSizes[eye];
        }
        this.eyeFovLayer.Viewport[1].Pos.x = this.eyeFovLayer.Viewport[0].Size.w;
        this.frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, desc.Width, desc.Height, true);

        for(int eye = 0; eye < 2; eye++) {
            EyeRenderDesc eyeRenderDesc = hmd.getRenderDesc(eye, fovPorts[eye]);
            this.eyeOffsets[eye].x = eyeRenderDesc.HmdToEyeViewOffset.x;
            this.eyeOffsets[eye].y = eyeRenderDesc.HmdToEyeViewOffset.y;
            this.eyeOffsets[eye].z = eyeRenderDesc.HmdToEyeViewOffset.z;
        }
        this.viewScaleDesc.HmdSpaceToWorldScaleInMeters = 1.0f;
    }

    int frameCount = 0;

    public void renderToHMD(Runnable render) {
        frameCount++;
        Posef[] eyePoses = hmd.getEyePoses(frameCount, eyeOffsets);
        this.frameBuffer.begin();
        Gdx.gl.glClearColor(0, 0, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        int texId = swapChain.getTextureId(swapChain.getCurrentIndex());

        Gdx.gl.glFramebufferTexture2D(Gdx.gl.GL_FRAMEBUFFER, Gdx.gl.GL_COLOR_ATTACHMENT0, Gdx.gl.GL_TEXTURE_2D, texId, 0);

        for(int eye = 0; eye < 2; eye++) {
            OvrRecti vp = eyeFovLayer.Viewport[eye];
            Gdx.gl.glScissor(vp.Pos.x, vp.Pos.y, vp.Size.w, vp.Size.h);
            Gdx.gl.glViewport(vp.Pos.x, vp.Pos.y, vp.Size.w, vp.Size.h);

            Posef pose = eyePoses[eye];

            poses[eye].Orientation = pose.Orientation;
            poses[eye].Position = pose.Position;

            render.run();
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.P))
            ScreenshotFactory.saveScreenshot(this.frameBuffer.getWidth(), this.frameBuffer.getHeight(), "HMD");

        Gdx.gl.glFramebufferTexture2D(Gdx.gl.GL_FRAMEBUFFER, Gdx.gl.GL_COLOR_ATTACHMENT0, Gdx.gl.GL_TEXTURE_2D, 0, 0);

        this.frameBuffer.end();
        this.frameBuffer.getColorBufferTexture().bind(texId);
        swapChain.commit();
        hmd.submitFrame(frameCount, eyeFovLayer);
    }

    @Override
    public void dispose() {
        hmd.destroy();
        hmd = null;
        Hmd.shutdown();
    }
}
