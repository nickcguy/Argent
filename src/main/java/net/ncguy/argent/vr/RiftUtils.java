package net.ncguy.argent.vr;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import com.oculusvr.capi.OvrMatrix4f;
import com.oculusvr.capi.OvrQuaternionf;
import com.oculusvr.capi.OvrVector3f;
import com.oculusvr.capi.Posef;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by Guy on 18/06/2016.
 */
public class RiftUtils {

    public static Vector3 toVector3(OvrVector3f v) {
        return new Vector3(v.x, v.y, v.z);
    }
    public static Vector3f toVector3f(Vector3 v) {
        return new Vector3f(v.x, v.y, v.z);
    }

    public static Quaternion toQuaternion(OvrQuaternionf q) {
        return new Quaternion(q.x, q.y, q.z, q.w);
    }

    public static Matrix4 toMatrix4(Posef p) {
        return new Matrix4().rotate(toQuaternion(p.Orientation)).mul(new Matrix4().translate(toVector3(p.Position)));
    }

    public static Matrix4 toMatrix4(OvrMatrix4f m) {
        if(m == null)
            return new Matrix4();
        return new Matrix4(m.M).tra();
    }

    // BEWARE, MONSTERS BELOW

    public static Matrix4f toMatrix4f(Matrix4 mat) {
        Matrix4f mat4f = new Matrix4f();
        mat4f.m00 = mat.val[Matrix4.M00];
        mat4f.m01 = mat.val[Matrix4.M01];
        mat4f.m02 = mat.val[Matrix4.M02];
        mat4f.m03 = mat.val[Matrix4.M03];
        mat4f.m10 = mat.val[Matrix4.M10];
        mat4f.m11 = mat.val[Matrix4.M11];
        mat4f.m12 = mat.val[Matrix4.M12];
        mat4f.m13 = mat.val[Matrix4.M13];
        mat4f.m20 = mat.val[Matrix4.M20];
        mat4f.m21 = mat.val[Matrix4.M21];
        mat4f.m22 = mat.val[Matrix4.M22];
        mat4f.m23 = mat.val[Matrix4.M23];
        mat4f.m30 = mat.val[Matrix4.M30];
        mat4f.m31 = mat.val[Matrix4.M31];
        mat4f.m32 = mat.val[Matrix4.M32];
        mat4f.m33 = mat.val[Matrix4.M33];
        return mat4f;
    }

}
