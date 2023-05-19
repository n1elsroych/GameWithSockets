package app;

import gameEvents.GameEventsListener;
import java.util.ArrayList;
import java.util.Scanner;
import gameEvents.ShutdownServerEvent;

public class ShutdownServer extends Thread{
    ArrayList<GameEventsListener> listeners;
    
    public ShutdownServer(){
        listeners = new ArrayList<>();
    }

    @Override
    public void run() {
        boolean shutdown = false;
        Scanner in = new Scanner(System.in);
        while (!shutdown){
            if (in.nextLine().equals("/shutdown")){
                shutdown = true;
                triggerShutdownServerEvent(shutdown);
            }
        }
        System.out.println("Entradas por consola finalizadas");
    }
    
    public void addEventsListener(GameEventsListener listener){
        listeners.add(listener);
    }
    
    public void removeMiEventoListener(GameEventsListener listener) {
        listeners.remove(listener);
    }

    private void triggerShutdownServerEvent(boolean shutdown){
        ShutdownServerEvent evt = new ShutdownServerEvent(this, shutdown);
        for (GameEventsListener listener : listeners) {
            listener.onShutdownServer(evt);
        }
    }
}
