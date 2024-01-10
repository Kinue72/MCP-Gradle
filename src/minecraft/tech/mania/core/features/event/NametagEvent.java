package tech.mania.core.features.event;

import net.minecraft.entity.EntityLivingBase;
import tech.mania.core.types.event.EventArgument;
import tech.mania.core.types.event.EventListener;

public class NametagEvent extends EventArgument {
  private final EntityLivingBase ENTITY;
  
  public NametagEvent(EntityLivingBase ENTITY) {
/* 12 */     this.ENTITY = ENTITY;
  }
  
  public final EntityLivingBase EntityLivingBase() {
/* 16 */     return this.ENTITY;
  }

  @Override
  public void call(EventListener listener) {
    listener.onNametag(this);
  }
}