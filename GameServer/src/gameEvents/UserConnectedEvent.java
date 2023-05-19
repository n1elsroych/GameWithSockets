package gameEvents;

import java.util.EventObject;

public class UserConnectedEvent extends EventObject {
    int sessionId;
    
    public UserConnectedEvent(Object source, int sessionId) {
        super(source);
        this.sessionId = sessionId;
    }

    public int getSessionId() {
        return sessionId;
    }
}
