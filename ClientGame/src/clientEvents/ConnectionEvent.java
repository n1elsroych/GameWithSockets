package clientEvents;

import java.net.Socket;
import java.util.EventObject;

public class ConnectionEvent extends EventObject{
    private Socket socket;

    public ConnectionEvent(Object source, Socket socket) {
        super(source);
        this.socket = socket;
    }
    
    public Socket getSocket(){
        return socket;
    }
}
