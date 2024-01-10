package tech.mania.core.features.module.movement;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;
import tech.mania.core.features.event.*;
import tech.mania.core.features.setting.BooleanSetting;
import tech.mania.core.features.setting.DoubleSetting;
import tech.mania.core.features.setting.ModeSetting;
import tech.mania.core.types.module.Module;
import tech.mania.core.types.module.ModuleCategory;
import tech.mania.core.util.*;

import java.util.*;

public class Scaffold extends Module {

    private static final EnumFacing[] invert = {
            EnumFacing.UP,
            EnumFacing.DOWN,
            EnumFacing.SOUTH,
            EnumFacing.NORTH,
            EnumFacing.EAST,
            EnumFacing.WEST
    };

    private static final BlockPos[][][] addons;

    static {
        addons = new BlockPos[7][2][7];
        for (int x = -3; x <= 3; x++) {
            for (int y = -1; y <= 0; y++) {
                for (int z = -3; z <= 3; z++) {
                    addons[x + 3][y + 1][z + 3] = new BlockPos(x, y, z);
                }
            }
        }
    }

    private final DoubleSetting placeRange = DoubleSetting.build()
            .range(0, 6)
            .value(3)
            .name("Place Range")
            .increment(0.1)
            .onSetting(v -> placeRangeSq = v * v)
            .unit("Blocks")
            .end();

    private final ModeSetting mode = ModeSetting.build()
            .name("Mode")
            .option(
                    "No AC",
                    "Ray cast",
                    "God bridge"
            )
            .value("God bridge")
            .end();

    private final BooleanSetting sideRotation = BooleanSetting.build()
            .name("Side rotation")
            .value(false)
            .end();

    private final BooleanSetting sameY = BooleanSetting.build()
            .name("Same Y")
            .value(false)
            .end();

    private double placeRangeSq = 9;

    private BlockData data, dataForStrafing;
    private int sneakTick, placeCount;
    private long lastClicked;
    boolean diagonal;
    private Vec3 bestPosition;
    private int airTick, startPosY;

    public Scaffold() {
        super("Scaffold", "Place block at your feet", ModuleCategory.Movement);
        getSettings().addAll(Arrays.asList(
                        this.mode,
                        this.placeRange,
                        this.sideRotation,
                        this.sameY
                )
        );
        keyCode = Keyboard.KEY_C;
    }

    @Override
    protected void onEnable() {
        dataForStrafing = null;
        sneakTick = 0;
        data = null;
        bestPosition = null;
        airTick = 0;
        super.onEnable();
    }

    @Override
    public void onPreUpdate(PreUpdateEvent event) {
        if (mc.world.getBlockState(new BlockPos(mc.player).add(0, -1, 0)).getBlock() == Blocks.air) airTick++;
        else airTick = 0;
        if (mc.player.onGround) {
            boolean z = airTick > 2 && !isGood(mc.objectMouseOver, data);
            if (!diagonal) {
                if (z) {
                    placeCount = 0;
                    //mc.player.jump();
                    //shouldSneak = true;
                }
            } else {
                if (z) {
                    placeCount = 0;
                    mc.player.jump();
                    //shouldSneak = true;
                }
            }
        }
    }

    @Override
    public void onRotation(RotationEvent event) {
        data = null;
        //if (mc.player.isOnGround()) return;
        data = getBlockData();
        if (data != null) {
            dataForStrafing = data;
        }
        float[] std = stdRotation();
        std[0] = RotationUtil.smoothRot(mc.player.rotationYaw, std[0], RandomUtil.nextFloat(0, 10));
        std[1] = RotationUtil.smoothRot(mc.player.rotationPitch,  std[1], RandomUtil.nextFloat(5, 15));
        std[1] += (float) (Math.sin(MathHelper.wrapDegrees(mc.player.rotationYaw - std[0]) / 5) * 5);
        if (!mc.player.onGround) {
            sneakTick = 0;
            mc.gameSettings.keyBindSneak.setPressed(false);
            if (data != null) {
                std = unqRotation();
            } else {
                std = new float[]{
                        mc.player.rotationYaw,
                        mc.player.rotationPitch
                };
            }
        }
        event.yaw = std[0];
        event.pitch = std[1];

        if (Math.abs(event.yaw - mc.player.rotationYaw) > 1) {
            sneakTick = 40;
            mc.gameSettings.keyBindSneak.setPressed(true);
        } else {
            sneakTick--;
            if (sneakTick < 0) {
                mc.gameSettings.keyBindSneak.setPressed(false);
            }
        }
        super.onRotation(event);
    }

    private float[] unqRotation() {
        // get best yaw
        if (false) {
            Vec3 eye = mc.player.getPositionEyes(1f);
            AxisAlignedBB bb = data.toAxisAlignedBB();
            float bestYaw = 0f;
            float bestDist = Float.MAX_VALUE;
            for (double x = bb.minX; x <= bb.maxX; x += 0.1) {
                for (double z = bb.minZ; z <= bb.maxZ; z += 0.1) {
                    float currentYaw = RotationUtil.rotation(new Vec3(x, eye.yCoord, z), eye)[0];
                    float currentDist = Math.abs(MathHelper.wrapDegrees(currentYaw - mc.player.rotationYaw));
                    if (currentDist > bestDist) continue;
                    bestYaw = currentYaw;
                    bestDist = currentDist;
                }
            }
            float[] best = {bestYaw, 0f}, temp = {bestYaw, 0f};
            bestDist = Float.MAX_VALUE;
            for (float p = 65; p <= 85; p += 0.1f) {
                temp[1] = p;
                float currentDist = Math.abs(mc.player.rotationPitch - best[1]);
                if (currentDist > bestDist) continue;
                if (!isGood(RayCastUtil.rayCast(temp, 3, 0), data)) continue;
                best[1] = p;
                System.out.println("!!!");
            }
            if (best[1] == 0f) best[1] = mc.player.rotationPitch;
            return best;
        }
        AxisAlignedBB box = data.toAxisAlignedBB();
        float[] best = null;
        Vec3 eye = mc.player.getPositionEyes(1f);
        double bestDist = Double.MAX_VALUE;
        for (double x = box.minX; x <= box.maxX; x += 0.1) {
            for (double y = box.minY; y <= box.maxY; y += 0.1) {
                for (double z = box.minZ; z <= box.maxZ; z += 0.1) {
                    float[] currentRot = RotationUtil.rotation(new Vec3(x, y, z), eye);
                    if (!isGood(RayCastUtil.rayCast(currentRot, placeRange.getValue(), 1f), data)) {
                        continue;
                    }
                    double currentDist = Math.hypot(
                            MathHelper.wrapDegrees(mc.player.rotationYaw - currentRot[0]),
                            MathHelper.wrapDegrees(mc.player.rotationPitch - currentRot[1])
                    );
                    if (bestDist > currentDist) {
                        bestDist = currentDist;
                        best = currentRot;
                    }
                }
            }
        }
        return best == null ? RotationUtil.rotation(box.getCenter(), eye)  : best;
    }

    private long lastStrafeSwitch, strafeSwitchDelay;

    @Override
    public void onInput(InputEvent event) {
        event.moveFix = true;
        if (true) return;
        if (!this.mode.getValue().equalsIgnoreCase("God bridge")) return;
        if (dataForStrafing == null || mc.player.isSneaking()) return;
        boolean deltaX = dataForStrafing == null ? Math.abs(mc.player.posX) % 1 > 0.5 : dataForStrafing.getPos().getX() + 0.5 - mc.player.posX > 0;
        boolean deltaZ = dataForStrafing == null ? Math.abs(mc.player.posZ) % 1 > 0.5 : dataForStrafing.getPos().getZ() + 0.5 - mc.player.posZ > 0;;
        boolean z = Math.abs(dataForStrafing.getPos().getX() + 0.5 - mc.player.posX) > 1;
        boolean x = Math.abs(dataForStrafing.getPos().getZ() + 0.5 - mc.player.posZ) > 1;
        System.out.println(Math.abs(dataForStrafing.getPos().getX() + 0.5 - mc.player.posX));
        if (mc.player.onGround) {
            if (!diagonal) {
                if (System.currentTimeMillis() - lastStrafeSwitch > strafeSwitchDelay || z || x) {
                    switch (EnumFacing.fromAngle(mc.player.rotationYaw).toString()) {
                        case "south":
                            lastSideways = deltaX ? -1 : 1f;
                            break;
                        case "north":
                            lastSideways = deltaX ? 1 : -1f;
                            break;
                        case "east":
                            lastSideways = deltaZ ? 1 : -1f;
                            break;
                        case "west":
                            lastSideways = deltaZ ? -1 : 1f;
                            break;
                    }
                    strafeSwitchDelay = 10000;
                    lastStrafeSwitch = System.currentTimeMillis();
                }
            }
        }
        event.getInput().moveStrafe = lastSideways;
        super.onInput(event);
    }

    @Override
    public void onClickTick(ClickTickEvent event) {
        if (sneakTick > 0) return;
        if (System.currentTimeMillis() - lastClicked < 25) return;
        boolean sneakPacket = false;
        if (isGood(mc.objectMouseOver, data)) {
            //if (sneakPacket) mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
            //mc.inGameHud.getChatHud().addMessage(Text.literal("Clicked"));
            //if (sneakPacket) mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
            mc.rightClickMouse();
            lastClicked = System.currentTimeMillis();
            return;
        }
        if (true) {
            return;
        }

        for (float delta = 0; delta <= 1f; delta += 0.01f) {
            MovingObjectPosition result = mc.player.rayTrace(3, delta);
            if (isGood(result, data)) {
                //System.out.println("Found " + delta);
//                if (sneakPacket) mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
//                mc.interactionManager.interactBlock(
//                        mc.player,
//                        Hand.MAIN_HAND,
//                        (BlockHitResult) result
//                );
//                mc.player.swingHand(Hand.MAIN_HAND);
//                mc.inGameHud.getChatHud().addMessage(Text.literal("Clicked"));
//                if (sneakPacket) mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
//                lastClicked = System.currentTimeMillis();
                break;
            }
        }
        super.onClickTick(event);
    }

    @Override
    public void onSendPacket(SendPacketEvent event) {
        if (event.getPacket() instanceof C0APacketAnimation) {
            placeCount++;
        }
        super.onSendPacket(event);
    }

    private boolean isGood(MovingObjectPosition result, BlockData data) {
        if (result == null || data == null) return false;
        if (result.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
            return false;
        }
        //return mc.world.getBlockState(block.getBlockPos().offset(block.getSide())).isReplaceable();
        return result.getBlockPos().offset(result.sideHit).toString().equalsIgnoreCase(data.getPos().offset(data.getDirection()).toString());
    }

    private float lastSideways = 0f, lastForward = 0f, lastVirtualYaw;

    private float[] stdRotation() {
        final float na = 79.9f;
        if (data == null) {
            return new float[] {
                    mc.player.rotationYaw,
                    na
                    //mc.player.fallDistance <= 0 || mc.player.isOnGround() ? 75.95f : 82f
            };
        }
        GameSettings options = mc.gameSettings;
        //if (options.forwardKey.isPressed() || options.backKey.isPressed() || options.rightKey.isPressed() || options.leftKey.isPressed()) {
        lastSideways = options.keyBindForward.isPressed() == options.keyBindBack.isPressed() ? 0.0F : (options.keyBindForward.isPressed() ? 1.0F : -1.0F);
        lastForward = options.keyBindLeft.isPressed() == options.keyBindRight.isPressed() ? 0.0F : (options.keyBindLeft.isPressed() ? 1.0F : -1.0F);
        lastVirtualYaw = RotationUtil.virtualYaw + 180;
        //}
        float stdYaw = Math.round(lastVirtualYaw / 45) * 45;
        //stdYaw += (float) Math.toDegrees(Math.atan2(lastSideways, lastForward));
        float stdPitch = na;
        boolean deltaX = data == null ? Math.abs(mc.player.posX) % 1 > 0.5 : data.getPos().getX() + 0.5 - mc.player.posX > 0;
        boolean deltaZ = data == null ? Math.abs(mc.player.posZ) % 1 > 0.5 : data.getPos().getZ() + 0.5 - mc.player.posZ > 0;;
        if (false) {
            if (Math.abs(stdYaw % 90) < 1) {
                float add = 0f;
                switch (EnumFacing.fromAngle(stdYaw).toString()) {
                    case "south":
                        add += deltaX ? -45 : 45;
                        break;
                    case "north":
                        add += deltaX ? 45 : -45;
                        break;
                    case "east":
                        add += deltaZ ? 45 : -45;
                        break;
                    case "west":
                        add += deltaZ ? -45 : 45;
                        break;
                }
                stdYaw += add;
                //stdPitch = 75.95f;
                stdPitch = 80;
                diagonal = false;
            } else {
                bestPosition = null;
                diagonal = true;
                stdPitch = 78.5f;
            }
        }
        if (this.mode.getValue().equalsIgnoreCase("Ray cast")) {
            System.out.println("A");
            float[] stdRot = new float[] {
                    stdYaw,
                    0
            };
            float bestP = stdPitch, bestDist = Float.MAX_VALUE;
            for (float p = 72; p < 90; p += 0.1f) {
                stdRot[1] = p;
                float currentDist = Math.abs(p - stdPitch);
                //if (bestDist < currentDist) continue;
                MovingObjectPosition current = RayCastUtil.rayCast(stdRot, 3, 1f);
                if (!isGood(current, data)) continue;
                bestP = p;
                bestDist = currentDist;
            }
            stdPitch = bestP;
        }
        //stdYaw += (float) Math.toDegrees(Math.atan2(lastSideways, lastForward));
        float[] za = new float[] {
                stdYaw,
                //80.2f
                stdPitch
        };
        return za;
    }

    private BlockData getBlockData() {
        BlockPos blockPos = new BlockPos(mc.player).add(0, -1, 0);
        if (mc.world.getBlockState(blockPos).getBlock() != Blocks.air) {
            return null;
        }
        Vec3 eye = mc.player.getPositionEyes(1f);
        List<BlockData> dataEntry = new ArrayList<>();
        for (BlockPos[][] xBP : addons) {
            for (BlockPos[] yBP : xBP) {
                for (BlockPos zBP : yBP) {
                    BlockPos offsetPos = blockPos.add(zBP);
                    if (mc.world.getBlockState(offsetPos).getBlock() != Blocks.air) continue;;
                    for (EnumFacing facing : EnumFacing.values()) {
                        if (mc.world.getBlockState(offsetPos.offset(facing)).getBlock() == Blocks.air) continue;
                        dataEntry.add(new BlockData(offsetPos.offset(facing), invert[facing.ordinal()]));
                    }
                }
            }
        }
        return dataEntry.stream()
                .filter(d -> mc.player.getDistanceSq(d.getPos().offset(d.getDirection()).toCenterPos()) < placeRangeSq)
                .min(Comparator.comparingDouble(d -> eye.squareDistanceTo(d.getPos().offset(d.getDirection()).toCenterPos())))
                .orElse(null);
    }

}
