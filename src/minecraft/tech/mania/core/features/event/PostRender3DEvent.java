package tech.mania.core.features.event;

import tech.mania.core.types.event.EventArgument;
import tech.mania.core.types.event.EventListener;

public class PostRender3DEvent extends EventArgument {

    private final float tick;

    public PostRender3DEvent(final float tick) {
        this.tick = tick;
    }

    public float getTick() {
        return this.tick;
    }

    @Override
    public void call(EventListener listener) {
        listener.onPostRender3D(this);
    }
}
