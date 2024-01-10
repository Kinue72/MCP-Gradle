package net.minecraft.util;

import net.minecraft.client.settings.GameSettings;
import tech.mania.Mania;
import tech.mania.core.features.event.InputEvent;
import tech.mania.core.util.RotationUtil;

import static tech.mania.MCHook.mc;

public class MovementInputFromOptions extends MovementInput
{
    private final GameSettings gameSettings;

    public MovementInputFromOptions(GameSettings gameSettingsIn)
    {
        this.gameSettings = gameSettingsIn;
    }

    public void updatePlayerMoveState()
    {
        this.moveStrafe = 0.0F;
        this.moveForward = 0.0F;

        if (this.gameSettings.keyBindForward.isKeyDown())
        {
            ++this.moveForward;
        }

        if (this.gameSettings.keyBindBack.isKeyDown())
        {
            --this.moveForward;
        }

        if (this.gameSettings.keyBindLeft.isKeyDown())
        {
            ++this.moveStrafe;
        }

        if (this.gameSettings.keyBindRight.isKeyDown())
        {
            --this.moveStrafe;
        }

        this.jump = this.gameSettings.keyBindJump.isKeyDown();
        this.sneak = this.gameSettings.keyBindSneak.isKeyDown();

        if (this.sneak)
        {
            this.moveStrafe = (float)((double)this.moveStrafe * 0.3D);
            this.moveForward = (float)((double)this.moveForward * 0.3D);
        }

        final InputEvent event = new InputEvent(this);
        Mania.getEventManager().call(event);
        if (event.moveFix) {
            fixStrafe(event);
        }
    }

    private void fixStrafe(final InputEvent event) {
        final float diff = (RotationUtil.virtualYaw - mc.player.rotationYaw),
                f = (float) Math.sin(diff * ((float) Math.PI / 180F)),
                f1 = (float) Math.cos(diff * ((float) Math.PI / 180F));
        float multiplier = 1f;
        if (mc.player.isSneaking() || mc.player.isUsingItem()) multiplier = 10;
        float forward = (float) (Math.round((event.getInput().moveForward * (double) f1 + event.getInput().moveStrafe * (double) f) * multiplier)) / multiplier;
        float strafe = (float) (Math.round((event.getInput().moveStrafe * (double) f1 - event.getInput().moveForward * (double) f) * multiplier)) / multiplier;
        event.getInput().moveForward = forward;
        event.getInput().moveStrafe = strafe;
    }
}
