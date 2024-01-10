package tech.mania.core.features.event;

import tech.mania.core.types.event.EventArgument;
import tech.mania.core.types.event.EventListener;

public class MoveEvent extends EventArgument {

  public double x, y, z;

  public MoveEvent(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  @Override
  public void call(EventListener listener) {
    listener.onMove(this);
  }
}