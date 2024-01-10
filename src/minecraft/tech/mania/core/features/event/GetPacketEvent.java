package tech.mania.core.features.event;

import net.minecraft.network.Packet;
import tech.mania.core.types.event.EventArgument;
import tech.mania.core.types.event.EventListener;

public class GetPacketEvent extends EventArgument
{
  private final Packet<?> PACKET;
  
  public GetPacketEvent(Packet<?> PACKET) {
/* 13 */     this.PACKET = PACKET;
  }
  
  public final Packet<?> getPacket() {
/* 17 */     return this.PACKET;
  }

  @Override
  public void call(EventListener listener) {
    listener.onGetPacket(this);
  }
}