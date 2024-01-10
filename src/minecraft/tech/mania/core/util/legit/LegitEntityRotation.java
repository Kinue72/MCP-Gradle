package tech.mania.core.util.legit;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import tech.mania.MCHook;
import tech.mania.core.util.AlgebraUtil;
import tech.mania.core.util.RandomUtil;
import tech.mania.core.util.RotationUtil;

public class LegitEntityRotation implements MCHook {

    private Entity entity;

    private float aYaw, aPitch;
    private long next;

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public float[] calcRotation() {
        Vec3 eye = mc.player.getPositionEyes(1f);
        AxisAlignedBB bb = entity.getEntityBoundingBox();
        Vec3 nearest = AlgebraUtil.nearest(bb, eye);
        final Vec3 eyeA = mc.player.getLook(1f).multiply(6);
        if (bb.intersects(eye, eye.add(eyeA))) {
            if (System.currentTimeMillis() > next) {
                final float[] center = RotationUtil.rotation(nearest, eye);
                next = System.currentTimeMillis() + RandomUtil.nextInt(50);
                aYaw = RandomUtil.nextFloat(0.3f) * MathHelper.wrapDegrees(
                        center[0] - mc.player.rotationYaw
                );
                aPitch = RandomUtil.nextFloat(0.3f) * MathHelper.wrapDegrees(
                        center[1] - mc.player.rotationPitch
                );
            }
            return new float[] {
                    mc.player.rotationYaw + aYaw * RandomUtil.nextFloat(3),
                    mc.player.rotationPitch + aPitch * RandomUtil.nextFloat(3)
            };
        }
        final float[] z = RotationUtil.rotation(nearest.addVector(
                RandomUtil.nextDouble(-0.1, 0.1),
                RandomUtil.nextDouble(-0.1, 0.1),
                RandomUtil.nextDouble(-0.1, 0.1)
        ), eye);
        z[0] = RotationUtil.smoothRot(mc.player.rotationYaw, z[0], RandomUtil.nextFloat(25, 30));
        z[1] = RotationUtil.smoothRot(mc.player.rotationPitch, z[1], RandomUtil.nextFloat(25, 30));
        z[1] += (float) (Math.sin(MathHelper.wrapDegrees(mc.player.rotationYaw - z[0]) / 5) * 5);
        return z;
    }
}
