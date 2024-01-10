package tech.mania.core.types.event;


import tech.mania.core.features.event.*;

public interface EventListener {

    default void onAttack(AttackEvent event){}
    default void onClickTick(ClickTickEvent event){}
    default void onGetPacket(GetPacketEvent event) {}
    default void onInput(InputEvent event) {}
    default void onKeyPress(KeyPressEvent event) {}
    default void onMove(MoveEvent event) {}
    default void onNametag(NametagEvent event) {}
    default void onPostUpdate(PostUpdateEvent event) {}
    default void onPreUpdate(PreUpdateEvent event) {}
    default void onRender2D(Render2DEvent event) {}
    default void onPreRender3D(PreRender3DEvent event) {}
    default void onPostRender3D(PostRender3DEvent event) {}
    default void onRotation(RotationEvent event) {}
    default void onSendPacket(SendPacketEvent event) {}
    default void onSlowdown(SlowdownEvent event) {}
    default void onSpoofItem(SpoofItemEvent event) {}
}
