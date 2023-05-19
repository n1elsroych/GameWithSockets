package gameEvents;

import java.util.EventObject;

public class UserDisconnectedEvent extends EventObject{
    int sessionId;
    
    public UserDisconnectedEvent(Object source, int sessionId) {
        super(source);
        this.sessionId = sessionId;
    }

    public int getSessionId() {
        return sessionId;
    }
}
