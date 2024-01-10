package tech.mania.core.features.event;

import tech.mania.core.types.event.EventArgument;
import tech.mania.core.types.event.EventListener;

public class Render2DEvent extends EventArgument {
  private final float TICK;
  
  public Render2DEvent(float TICK) {
     this.TICK = TICK;
  }
  
  public final float getTick() {
/* 18 */     return this.TICK;
  }

  @Override
  public void call(EventListener listener) {
    listener.onRender2D(this);
  }
}