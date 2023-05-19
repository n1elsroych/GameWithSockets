package app;

import java.util.Base64;

public class User {
    private int id;
    private String username;
    private String password;
    private SeaMap seaMap;
    private boolean isReady;
    
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        isReady = false;
    }

    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
        isReady = false;
    }
    
    public void createSeaMap(int rowNum, int colNum){
        seaMap = new SeaMap(rowNum, colNum);
    }
    
    public void putShip(int row, int col){
        seaMap.addShip(row, col);
    }
    
    public void removeShip(int row, int col){
        seaMap.removeShip(row, col);
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public byte[] getPasswordBytes(){
        return Base64.getDecoder().decode(password);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SeaMap getSeaMap() {
        return seaMap;
    }

    public void setSeaMap(SeaMap seaMap) {
        this.seaMap = seaMap;
    }
    
    public void setReady(boolean ready){
        isReady = ready;
    }
    
    public boolean getReady(){
        return isReady;
    }
    
    public void showMap(){
        seaMap.showCoords();
    }
}
