package tech.mania.core.features.event;

import tech.mania.core.types.event.EventArgument;
import tech.mania.core.types.event.EventListener;

public class SpoofItemEvent extends EventArgument {
  public int current;
  
  public SpoofItemEvent(int CURRENT) {
/* 11 */     this.current = CURRENT;
  }

  @Override
  public void call(EventListener listener) {
    listener.onSpoofItem(this);
  }
}