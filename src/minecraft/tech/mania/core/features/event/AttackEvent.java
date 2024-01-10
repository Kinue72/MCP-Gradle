package tech.mania.core.features.event;

import net.minecraft.entity.Entity;
import tech.mania.core.types.event.EventArgument;
import tech.mania.core.types.event.EventListener;

public class AttackEvent extends EventArgument {
  private final Entity TARGET;
  
  public AttackEvent(Entity TARGET) {
/* 12 */     this.TARGET = TARGET;
  }
  
  public final Entity getTarget() {
/* 16 */     return this.TARGET;
  }

  @Override
  public void call(EventListener listener) {
    listener.onAttack(this);
  }
}