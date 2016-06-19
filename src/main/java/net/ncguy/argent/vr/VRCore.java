package net.ncguy.argent.vr;

import com.badlogic.gdx.utils.Disposable;
import net.ncguy.argent.exception.ArgentInitialisationException;
import org.lwjgl.PointerBuffer;
import org.lwjgl.ovr.*;

import java.nio.ByteBuffer;

import static org.lwjgl.ovr.OVR.*;
import static org.lwjgl.ovr.OVRUtil.ovr_Detect;
import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memFree;

/**
 * Created by Guy on 17/06/2016.
 */
public class VRCore implements Disposable {

    public static long ovrSessionID;

    public VRCore() throws ArgentInitialisationException {
        try {
            OVRDetectResult detect = OVRDetectResult.calloc();
            ovr_Detect(0, detect);
            System.out.println("OVRDetectResult.IsOculusHMDConnected: "+detect.IsOculusHMDConnected());
            System.out.println("OVRDetectResult.IsOculusServiceRunning: "+detect.IsOculusServiceRunning());
        }catch (Exception e) {
            throw new ArgentInitialisationException(e);
        }

        try {
            OVRInitParams initParams = OVRInitParams.calloc();
            initParams = initParams.Flags(ovrInit_Debug);
            System.out.println("ovr_Initialisation: "+ovr_Initialize(initParams));
        }catch (Exception e) {
            throw new ArgentInitialisationException(e);
        }

        System.out.println("ovr_GetVersionString: "+ovr_GetVersionString());

        try {
            OVRGraphicsLuid luid = OVRGraphicsLuid.calloc();
            OVRHmdDesc desc = OVRHmdDesc.malloc();
            PointerBuffer hmd_p = memAllocPointer(1);
            System.out.println("ovr_Create: "+(ovrSessionID = ovr_Create(hmd_p, luid)));
            long hmd = hmd_p.get(0);
            memFree(hmd_p);
            ovr_GetHmdDesc(hmd, desc);
            System.out.printf("ovr_GetHmdDesc: %s, %s, %s\n", desc.ManufacturerString(), desc.ProductNameString(), desc.SerialNumberString());
        }catch (Exception e) {
            throw new ArgentInitialisationException(e);
        }
    }

    public void drawFrame() {
        OVRViewScaleDesc desc = OVRViewScaleDesc.calloc();
        OVRLayerEyeFov eyeFov = OVRLayerEyeFov.calloc();
        OVRLayerQuad quad = OVRLayerQuad.calloc();

        OVRLayerHeader[] layers = new OVRLayerHeader[]{
                eyeFov.Header(),
                quad.Header()
        };

        ByteBuffer buffer = ByteBuffer.allocate(layers.length);
//        for(OVRLayerHeader layer : layers) {
//            ByteBuffer b = ByteBuffer.allocate(16384);
//            layer.set(b);
//            buffer.put(b);
//        }

        drawFrame(desc, buffer, layers.length);

        quad.free();
        eyeFov.free();
        desc.free();
    }

    public void drawFrame(OVRViewScaleDesc viewScaleDesc, ByteBuffer layerPtrs, int layerCount) {
        ovr_SubmitFrame(ovrSessionID, 0, viewScaleDesc, layerPtrs, layerCount);
    }

    @Override
    public void dispose() {
        ovr_Shutdown();

    }
}
