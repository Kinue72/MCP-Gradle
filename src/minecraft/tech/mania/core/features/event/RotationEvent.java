package tech.mania.core.features.event;

import tech.mania.core.types.event.EventArgument;
import tech.mania.core.types.event.EventListener;

public class RotationEvent extends EventArgument {
  public float yaw, pitch;
  public float yawSpeed, pitchSpeed;
  
  public RotationEvent(float yaw, float pitch) {
    this.yaw = yaw;
    this.pitch = pitch;
    this.yawSpeed = 180;
    this.pitchSpeed = 180;
  }

  @Override
  public void call(EventListener listener) {
    listener.onRotation(this);
  }
}