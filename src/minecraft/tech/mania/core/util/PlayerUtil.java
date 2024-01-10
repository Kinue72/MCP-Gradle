package tech.mania.core.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovementInput;
import net.minecraft.util.Vec3;
import tech.mania.MCHook;

import java.util.ArrayList;
import java.util.List;

public class PlayerUtil implements MCHook {

    /*
    public static List<Vec3> predictPositions(Entity entity, int tick) {
        List<Vec3> positions = new ArrayList<>();

        MovementInput input = mc.thePlayer.movementInput;
        Vec3 playerVelocity = AlgebraUtil.clone(mc.thePlayer.getVelocity());

        EntityPlayerSP player = new EntityPlayerSP(
                mc,
                mc.thePlayer,
                new NetHandlerPlayClient(
                        mc,
                        new ClientConnection(NetworkSide.CLIENTBOUND),
                        new ClientConnectionState(
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null
                        )
                ) {
                    @Override
                    public void sendPacket(Packet<?> packet) {
                        //super.sendPacket(packet);
                    }

                    @Override
                    public GameProfile getProfile() {
                        return mc.getNetworkHandler().getProfile();
                    }

                    @Override
                    public FeatureSet getEnabledFeatures() {
                        return mc.getNetworkHandler().getEnabledFeatures();
                    }
                },
                new StatHandler(),
                new ClientRecipeBook(),
                entity.isSneaking(),
                entity.isSprinting()
        ) {
            @Override
            public float getHealth() {
                return getMaxHealth();
            }

            @Override
            public void tickMovement() {
                fallDistance = 0;
                super.tickMovement();
            }

            @Override
            public void tick() {
                tickMovement();
            }

            @Override
            protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
                //super.fall(heightDifference, onGround, state, landedPosition);
            }

            @Override
            protected boolean isCamera() {
                return true;
            }

            @Override
            public void playSound(SoundEvent sound, float volume, float pitch) {
                //super.playSound(sound, volume, pitch);
            }

            @Override
            public void playSound(SoundEvent event, SoundCategory category, float volume, float pitch) {
                //super.playSound(event, category, volume, pitch);
            }
        };
        player.input = new Input() {
            @Override
            public void tick(boolean slowDown, float slowDownFactor) {
                movementForward = input.movementForward;
                movementSideways = input.movementSideways;

                pressingForward = input.pressingForward;
                pressingBack = input.pressingBack;

                pressingLeft = input.pressingLeft;
                pressingRight = input.pressingRight;

                jumping = input.jumping;
                sneaking = input.sneaking;

                if (slowDown) {
                    movementSideways *= slowDownFactor;
                    movementForward *= slowDownFactor;
                }
            }
        };

        player.init();
        player.copyPositionAndRotation(entity);
        player.copyFrom(entity);

        player.setOnGround(entity.isOnGround());

        for (int i = 0; i < tick; i++) {
            player.resetPosition();
            player.age++;
            player.tick();
            positions.add(player.getPos());
        }

        mc.thePlayer.setVelocity(playerVelocity);
        return positions;
    }

     */
}
