package tech.mania.core.features.event;

import net.minecraft.util.MovementInput;
import tech.mania.core.types.event.EventArgument;
import tech.mania.core.types.event.EventListener;

public class InputEvent extends EventArgument
{
  private final MovementInput INPUT;
  public boolean moveFix = false;
  
  public InputEvent(MovementInput input) {
    this.INPUT = input;
  }
  
  public MovementInput getInput() {
/* 20 */     return this.INPUT;
  }

  @Override
  public void call(EventListener listener) {
    listener.onInput(this);
  }
}