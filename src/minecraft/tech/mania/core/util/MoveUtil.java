package tech.mania.core.util;

import net.minecraft.util.MovementInput;
import net.minecraft.util.Vec3;
import tech.mania.MCHook;

public class MoveUtil implements MCHook {

    public static Vec3 getDir(double dist) {
        double rad = Math.toRadians(mc.player.rotationYaw + 90.0f);
        MovementInput input = mc.player.movementInput;
        return new Vec3(
                (input.moveForward * 0.45 * Math.cos(rad)
                        + input.moveStrafe * 0.45 * Math.sin(rad)) * dist,
                0,
                (input.moveForward * 0.45 * Math.sin(rad)
                        - input.moveStrafe * 0.45 * Math.cos(rad)) * dist
        );
    }
}
