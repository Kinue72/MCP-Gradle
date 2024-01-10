package tech.mania.core.features.module.combat;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;
import tech.mania.core.features.event.ClickTickEvent;
import tech.mania.core.features.event.PostUpdateEvent;
import tech.mania.core.features.event.RotationEvent;
import tech.mania.core.features.setting.BooleanSetting;
import tech.mania.core.features.setting.DoubleSetting;
import tech.mania.core.features.setting.ModeSetting;
import tech.mania.core.types.module.Module;
import tech.mania.core.types.module.ModuleCategory;
import tech.mania.core.util.AlgebraUtil;
import tech.mania.core.util.PlayerUtil;
import tech.mania.core.util.RandomUtil;
import tech.mania.core.util.legit.LegitCPSTimer;
import tech.mania.core.util.legit.LegitEntityRotation;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class KillAura extends Module {

    private static final Comparator<EntityLivingBase> DISTANCE_COMP = Comparator.comparingDouble(
            e -> mc.player.getPositionEyes(1f).squareDistanceTo(AlgebraUtil.nearest(e.getEntityBoundingBox(), mc.player.getPositionEyes(1f)))
    ), HEALTH_COMP = Comparator.comparingDouble(
            e -> e.getHealth()
    );

    private final BooleanSetting animals = BooleanSetting.build()
            .name("Animals")
            .value(true)
            .end();
    private final BooleanSetting monsters = BooleanSetting.build()
            .name("Monsters")
            .value(true)
            .end();
    private final BooleanSetting players = BooleanSetting.build()
            .name("Players")
            .value(true)
            .end();
    private final BooleanSetting throughWalls = BooleanSetting
            .build()
            .name("Through Walls")
            .end();

    private final ModeSetting teams = ModeSetting.build()
            .name("Teams")
            .option(
                    "None",
                    "Normal"
                    )
            .end();
    private final ModeSetting sort = ModeSetting.build()
            .name("Sort")
            .visibility(players::getValue)
            .option(
                    "Distance",
                    "Health",
                    "Low Armor",
                    "High Armor",
                    "Fov"
            ).onSetting(v -> {
                switch (v) {
                    case "Distance":
                        comparator = DISTANCE_COMP;
                        break;
                    case "Health":
                        comparator = HEALTH_COMP;
                        break;
                }
            }).end();

    private final DoubleSetting attackRange = DoubleSetting
            .build()
            .name("Attack Range")
            .value(3.0)
            .range(0.0, 8.0)
            .unit("Blocks")
            .onSetting(v -> attackDistSq = v * v)
            .end();
    private final DoubleSetting loadRange = DoubleSetting
            .build()
            .name("Load Range")
            .value(4.0)
            .range(0.0, 8.0)
            .unit("Blocks")
            .onSetting(v -> loadDistSq = v * v)
            .end();
    private final DoubleSetting hitBox = DoubleSetting
            .build()
            .name("HitBox")
            .value(100.0)
            .range(0.0, 500.0)
            .unit("%")
            .end();
    private final DoubleSetting minCPS = DoubleSetting
            .build()
            .name("Min CPS")
            .value(10.0)
            .range(0.0, 20.0)
            .unit("CPS")
            .end();
    private final DoubleSetting maxCPS = DoubleSetting
            .build()
            .name("Max CPS")
            .value(12.0)
            .range(0.0, 20.0)
            .unit("CPS")
            .end();

    private Comparator<EntityLivingBase> comparator = DISTANCE_COMP;

    private double attackDistSq = 9, loadDistSq = 16;

    private final LegitEntityRotation ROT = new LegitEntityRotation();

    private static EntityLivingBase target = null;

    public static EntityLivingBase getTarget() {
        return target;
    }

    private final LegitCPSTimer timer = new LegitCPSTimer();

    public KillAura() {
        super("KillAura", "Attacks entities around you", ModuleCategory.Combat);
        this.keyCode = Keyboard.KEY_R;
        this.getSettings().addAll(Arrays.asList(
                this.animals,
                this.monsters,
                this.players,
                this.teams,
                this.sort,

                this.attackRange,
                this.loadRange,

                this.throughWalls,
                this.hitBox,

                this.minCPS,
                this.maxCPS
        ));
    }

    @Override
    public void onRotation(RotationEvent event) {
        target = null;
        final Vec3 eye = mc.player.getPositionEyes(1f);
        final List<EntityLivingBase> targetEntry = mc.world.loadedEntityList.stream()
                .filter(e -> e instanceof EntityLivingBase && e != mc.player)
                .map(e -> (EntityLivingBase) e)
                .filter(e -> {
                    if (e instanceof EntityAnimal) return this.animals.getValue();
                    if (e instanceof EntityMob) return this.monsters.getValue();
                    if (e instanceof EntityPlayer) {
                        if (this.players.getValue()) {
                            switch (this.teams.getValue()) {
                                case "None": return true;
                                case "Normal": return mc.player.getTeam().isSameTeam(e.getTeam());
                                default: return false;
                            }
                        }
                        return false;
                    }
                    return false;
                })
                .filter(e -> eye.squareDistanceTo(AlgebraUtil.nearest(e.getEntityBoundingBox(), eye)) < loadDistSq)
                .sorted(this.comparator)
                .collect(Collectors.toList());

        if (targetEntry.isEmpty()) return;

        target = targetEntry.get(0);

        ROT.setEntity(target);
        final float[] r = ROT.calcRotation();
        event.yaw = r[0];
        event.pitch = r[1];
        super.onRotation(event);
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
//        System.out.println(mc.player.getX());
//        System.out.println(PlayerUtil.predictPositions(mc.player, 1));
    }

    @Override
    public void onClickTick(ClickTickEvent event) {
        if (target == null) return;
        if (RandomUtil.percent(75)) {
            mc.clickMouse();
            mc.ingameGUI.getChatGUI().addToSentMessages((String.format("Health: %f", target.getHealth())));
            //timer.reset(RandomUtil.nextDouble(minCPS.getValue(), maxCPS.getValue()));
        }
        super.onClickTick(event);
    }
}
