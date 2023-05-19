package formEvents;

import java.util.EventObject;

public class ClientConnectionEvent extends EventObject{
    boolean isConnected;
    
    public ClientConnectionEvent(Object source, boolean isConnected) {
        super(source);
        this.isConnected = isConnected;
    }

    public boolean isIsConnected() {
        return isConnected;
    }
}
