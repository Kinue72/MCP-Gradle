package tech.mania.core.util;

import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import tech.mania.MCHook;

public class RotationUtil implements MCHook {

    public static float virtualYaw, virtualPitch, virtualPrevYaw, virtualPrevPitch;

    public static float smoothRot(final float current, final float goal, final float speed) {
        return current + MathHelper.clamp_float(
                MathHelper.wrapDegrees(goal - current),
                -speed,
                speed
        );
    }

    public static float[] rotation(double x, double y, double z, double ax, double ay, double az) {
        final double diffX = x - ax, diffY = y - ay, diffZ = z - az;
        final float yaw = (float) (Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F),
                pitch = (float) (-Math.toDegrees(Math.atan2(diffY, Math.hypot(diffX, diffZ))));
        return new float[] { yaw, pitch };
    }

    public static float[] rotation(Vec3 a, Vec3 b) {
        return rotation(a.xCoord, a.yCoord, a.zCoord, b.xCoord, b.yCoord, b.zCoord);
    }

    /**
     * Returns the smallest angle difference possible with a specific sensitivity ("gcd")
     */
    public static float getFixedAngleDelta() {
//        float z = (float) (mc.gameSettings.getMouseSensitivity().getValue() * 0.6f + 0.2f);
        float z = (float) (0.1 * 0.6f + 0.2f);
        return (z * z * z * 1.2f);
    }

    /**
     * Returns angle that is legitimately accomplishable with player's current sensitivity
     */
    public static float getFixedSensitivityAngle(float targetAngle, float startAngle) {
        float gcd = getFixedAngleDelta();
        return startAngle + (int) ((targetAngle - startAngle) / gcd) * gcd;
    }
}
