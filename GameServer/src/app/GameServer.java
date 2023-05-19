package app;

import database.DBConnection;
import gameEvents.GameEventsListener;
import gameEvents.DataPackageReceivedEvent;
import gameEvents.ShutdownServerEvent;
import gameEvents.UserConnectedEvent;
import gameEvents.UserDisconnectedEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import threads.Server;
import tools.Protocol;

public class GameServer implements GameEventsListener{
    private Server server;
    private Map<String, User> users;
    DBConnection dbConnection;
    //private Map<Integer, Game> games;
    //private int gamesCount; //currentGame
    
    private boolean gameInProgress;
    private boolean gameControlTaken;
    private int turn;
    private ArrayList<Integer> usersPlaying; //sessionIds
    
    public GameServer(int port) throws IOException{
        server = new Server(port);
        users = new HashMap<>();
        //games = new HashMap<>();
        gameInProgress = false;
        gameControlTaken = false;
        //usersPlaying = new ArrayList<>();
        //gamesCount = 0;
        dbConnection = new DBConnection();
        dbConnection.loadUsersFromDBTo(users);
        show_users();
    }
    
    public void start() throws IOException{
        server.addEventsListener(this);
        server.start();
        
        ShutdownServer shutdownHandler = new ShutdownServer();
        shutdownHandler.addEventsListener(this);
        shutdownHandler.start();
    }

    private String getData(String type, String dataMessage){
        int i = dataMessage.indexOf(type) + type.length();
        int f = dataMessage.indexOf(";", i);
        return dataMessage.substring(i, f);
    }

    /**
     * 
    private void addToAGame(String data){
        Integer sessionId = Protocol.getSessionId(data);
        Game game = games.get(gamesCount);
        if (game != null){
            if (game.playersCount() < 4){
                game.addPlayer(sessionId);
                if (game.playersCount() == 4){
                    System.out.println("Mandar listos para jugar");
                    try {
                        server.sendMulticast(Protocol.gameReady(), game.getPlayers());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            else {   
                gamesCount++;
            }
        }
        Game newGame = new Game();
        games.put(gamesCount, newGame);
        newGame.addPlayer(sessionId);
    }
    */
    
    private void beginTheGame(String data){
        int sessionId = Protocol.getSessionId(data);
        if (gameInProgress){
            try {
                server.sendUnicast(sessionId, Protocol.GameInProgressError());
            } catch (IOException ex){
                ex.printStackTrace();
            }
            return;
        }
        if (usersPlaying.size() > 1){
            gameInProgress = true;
            try {
                server.sendMulticast(Protocol.gameReady(), usersPlaying);
                turn = 0;
                setTurn(usersPlaying.get(turn));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            try {
                server.sendUnicast(sessionId, Protocol.BeginGameError());
            } catch (IOException ex){
                ex.printStackTrace();
            } 
        }
    }
    
    private void setTurn(int sessionId){
        try {
            server.sendUnicast(sessionId, Protocol.Turn());
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }
    
    private void setGameControl(String data){  
        if (gameControlTaken){
            return;
        }
        int sessionId = Protocol.getSessionId(data);
        try {
            server.sendUnicast(sessionId, Protocol.BeginGameControl());
            gameControlTaken = true;
            usersPlaying = new ArrayList<>();
            usersPlaying.add(sessionId);
            server.sendBroadcast(Protocol.BeginGameControlTaken());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void setUserReady(String data){
        int sessionId = Protocol.getSessionId(data);
        if (gameInProgress){
            try {
                server.sendUnicast(sessionId, Protocol.GameInProgressError());
            } catch (IOException ex){
                ex.printStackTrace();
            }
            return;
        }
        users.get(String.valueOf(sessionId)).setReady(true); //revisar talvez no sea necesario ya que hay lista
        usersPlaying.add(sessionId);
    }
    
    private void login(String dataPackage){
        String sessionID = String.valueOf(Protocol.getSessionId(dataPackage));
        String username = Protocol.getUsername(dataPackage);
        String password = Protocol.getPassword(dataPackage);
        try {
            if (gameInProgress){
                server.sendUnicast(Integer.parseInt(sessionID), Protocol.loginErrorGameInProgress());
                return;
            }
            if (users.containsKey(username)){
                User user = users.get(username);
                if (user.getPassword().equals(password)){
                    users.put(sessionID, user);
                    users.remove(username);
                    users.get(sessionID).createSeaMap(10, 10); //hardcode
                    server.sendUnicast(Integer.parseInt(sessionID), Protocol.loginSuccess(username));
                    
                    if (gameControlTaken){
                        server.sendUnicast(Integer.parseInt(sessionID), Protocol.BeginGameControlTaken());
                    }
                        show_users();
                    return;
                }            
            }
            server.sendUnicast(Integer.parseInt(sessionID), Protocol.loginFailed());
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }
    
    private void register(String dataPackage){
        int sessionID = Protocol.getSessionId(dataPackage);
        String username = Protocol.getUsername(dataPackage);
        try {
            if (gameInProgress){
                server.sendUnicast(sessionID, Protocol.registerErrorGameInProgress());
                return;
            }
            if (users.containsKey(username)){
                server.sendUnicast(sessionID, Protocol.registerFailed());
                return;
            }
            String password = Protocol.getPassword(dataPackage);
            User user = new User(username, password);
            users.put(String.valueOf(sessionID), user);
            users.get(String.valueOf(sessionID)).createSeaMap(10, 10); //hardcode
                            show_users();
            server.sendUnicast(sessionID, Protocol.registerSuccess(username));
        } catch (IOException ex){
            ex.printStackTrace();
//            if (users.containsKey(String.valueOf(sessionID)))
//                users.remove(String.valueOf(sessionID));
        }
    }
    
    private void show_users(){
        for (Map.Entry<String, User> user : users.entrySet()){
            User userValue = user.getValue();
            System.out.print("users[ "+user.getKey()+" ]: ");
            System.out.println("username: "+userValue.getUsername()+", password: "+userValue.getPassword());
        }
    }
    
    private void sendMessage(String dataPackage){
        int sessionID = Integer.parseInt(getData("<origin>", dataPackage));
        String message = getData("<message>", dataPackage);
        message = "["+users.get(sessionID).getUsername()+"]: "+message;
        try {
            server.sendBroadcast(message);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void setMapCoords(String data){
        String sessionId = String.valueOf(Protocol.getSessionId(data));
        int [][] coords = Protocol.getCoordsConfig(data);
        users.get(sessionId).getSeaMap().setCoords(coords);
        //users.get(sessionId).showMap();
    }
 
    private void sendAttack(String data){
        int sessionIdOrigin = Protocol.getSessionId(data);
        int row = Protocol.getRow(data);
        int col = Protocol.getCol(data);
        int c = 0;
        int i = 0;
        for (int player : usersPlaying){
            if (player != sessionIdOrigin){
                User user = users.get(String.valueOf(player));
                if (user.getSeaMap().hasShipAt(row, col)){
                    c++;
                    user.getSeaMap().removeShip(row, col);
                    try {
                        server.sendUnicast(player, Protocol.parseDamageReceived(row, col));
                    } catch (IOException ex){
                        ex.printStackTrace();
                    }
                }
                    user.showMap();
                if (!user.getSeaMap().hasShipsAlive()){
                    usersPlaying.remove(i);
                    System.out.println("eliminando jugador "+player);
                    try {
                        server.sendUnicast(player, Protocol.PlayerDefeated());
                    } catch (IOException ex){
                        ex.printStackTrace();
                    }
                }
                if (usersPlaying.size() < 2){
                    try {
                        System.out.println("El ganador es "+sessionIdOrigin);
                        gameInProgress = false;
                        gameControlTaken = false;
                        //turn = 0;
                        usersPlaying = new ArrayList<>();
                        server.sendUnicast(sessionIdOrigin, Protocol.PlayerVictory());
                        server.sendBroadcast(Protocol.gameFinished());
                    } catch (IOException ex){
                        ex.printStackTrace();
                    }
                }
            }
            i++;
        }
        try {
            server.sendUnicast(sessionIdOrigin, Protocol.takedownsMessage(c));
        } catch (IOException ex){
            ex.printStackTrace();
        }
        
        turn = (turn + 1) % usersPlaying.size();
        setTurn(usersPlaying.get(turn));
    }
    
    @Override
    public void onMessageReceived(DataPackageReceivedEvent evt) {
        String dataPackage = evt.getMessage();
        if (Protocol.isLoginAction(dataPackage)){
            login(dataPackage);
            return;
        }
        if (Protocol.isRegisterAction(dataPackage)){
            register(dataPackage);
            return;
        }
        if (Protocol.isMessageAction(dataPackage)){
            sendMessage(dataPackage);
            return;
        }
        if (Protocol.isCoordsConfig(dataPackage)){
            setMapCoords(dataPackage);
            return;
        }
        if (Protocol.isPlayerReady(dataPackage)){
            setUserReady(dataPackage);
            return;
        }
        if (Protocol.isBeginGameControlRequest(dataPackage)){
            setGameControl(dataPackage);
            return;
        }
        if (Protocol.isBeginGameRequest(dataPackage)){
            beginTheGame(dataPackage);
            return;
        }
        if (Protocol.isAttack(dataPackage)){
            sendAttack(dataPackage);
        }
    }

    @Override
    public void onShutdownServer(ShutdownServerEvent evt) {
        try {
            dbConnection.saveUsersOnDBFrom(users);
            server.shutdown();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            dbConnection.saveUsersOnDBFrom(users);
            server.shutdown();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        //super.finalize();
    }

    @Override
    public void onUserConnected(UserConnectedEvent evt) {
        int sessionId = evt.getSessionId();
        try {
            server.sendUnicast(sessionId, Protocol.parseSessionIdData(sessionId));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onUserDisconnected(UserDisconnectedEvent evt) {
        String sessionId = String.valueOf(evt.getSessionId());
        User user = users.get(sessionId);
        String username = user.getUsername();
        users.put(username, user);
        users.remove(sessionId);
            show_users();
    }
}
