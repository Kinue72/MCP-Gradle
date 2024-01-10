package tech.mania.core.features.event;

import tech.mania.core.types.event.EventArgument;
import tech.mania.core.types.event.EventListener;

public class PreRender3DEvent extends EventArgument {
  private final float TICK;
  
  public PreRender3DEvent(float TICK) {
/* 11 */     this.TICK = TICK;
  }
  
  public final float getTick() {
/* 15 */     return this.TICK;
  }

  @Override
  public void call(EventListener listener) {
    listener.onPreRender3D(this);
  }
}