package tech.mania.core.features.event;

import tech.mania.core.types.event.EventArgument;
import tech.mania.core.types.event.EventListener;

public class SlowdownEvent extends EventArgument {

  public boolean slowdown = true;

  @Override
  public void call(EventListener listener) {
    listener.onSlowdown(this);
  }
}