package tech.mania.core.util;

import net.minecraft.util.MovingObjectPosition;
import tech.mania.MCHook;

public class RayCastUtil implements MCHook {

    public static MovingObjectPosition rayCast(float[] rot, double dist, float delta) {
        float prevYaw = mc.player.prevRotationYaw,
                prevPitch = mc.player.prevRotationPitch,
                yaw = mc.player.rotationYaw,
                pitch = mc.player.rotationPitch;
        mc.player.rotationYaw = (rot[0]);
        mc.player.rotationPitch = (rot[1]);
        mc.player.prevRotationYaw = yaw;
        mc.player.prevRotationPitch = pitch;
        MovingObjectPosition result = mc.player.rayTrace(dist, delta);
        mc.player.rotationYaw = (yaw);
        mc.player.rotationPitch = (pitch);
        mc.player.prevRotationPitch = prevPitch;
        mc.player.prevRotationYaw = prevYaw;
        return result;
    }
}
