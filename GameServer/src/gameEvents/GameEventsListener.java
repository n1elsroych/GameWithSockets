package gameEvents;

import java.util.EventListener;

public interface GameEventsListener extends EventListener{
    
    public void onUserConnected(UserConnectedEvent evt);
    
    public void onMessageReceived(DataPackageReceivedEvent evt);
    
    public void onShutdownServer(ShutdownServerEvent evt);
    
    public void onUserDisconnected(UserDisconnectedEvent evt);
}
