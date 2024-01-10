package tech.mania.core.util;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class AlgebraUtil {

    public static Vec3 clone(Vec3 vec) {
        return new Vec3(
                vec.xCoord,
                vec.yCoord,
                vec.zCoord
        );
    }

    public static Vec3 nearest(AxisAlignedBB box, Vec3 vec) {
        return new Vec3(
                MathHelper.clamp_double(vec.xCoord, box.minX, box.maxX),
                MathHelper.clamp_double(vec.yCoord, box.minY, box.maxY),
                MathHelper.clamp_double(vec.zCoord, box.minZ, box.maxZ)
        );
    }
}
