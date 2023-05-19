package app;

import java.util.ArrayList;

public class Game {
    private ArrayList<Integer> players;
    
    public Game(){
        players = new ArrayList<>();
    }
    
    public void addPlayer(int sessionId){
        players.add(sessionId);
    }
    
    public ArrayList<Integer> getPlayers(){
        return players;
    }
    
    public int playersCount(){
        return players.size();
    }
}
