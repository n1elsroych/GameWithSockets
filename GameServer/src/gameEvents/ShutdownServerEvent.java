package gameEvents;

import java.util.EventObject;

public class ShutdownServerEvent extends EventObject{
    boolean shutdown;

    public ShutdownServerEvent(Object source, boolean shutdown) {
        super(source);
        this.shutdown = shutdown;
    }

    public boolean isShutdown() {
        return shutdown;
    }
}
