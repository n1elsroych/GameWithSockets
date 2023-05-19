package tools;

public class Protocol {
    
    private static String getEspecificData(String type, String data){
        int i = data.indexOf(type) + type.length();
        int f = data.indexOf(";", i);
        return data.substring(i, f);
    }
    
    public static String parseLoginData(int sessionId, String username, String password){
        return "<login><session-id>"+sessionId+";<username>"+username+";<password>"+password+";";
    }
    
    public static String parseRegisterData(int sessionId, String username, String password){
        return "<register><session-id>"+sessionId+";<username>"+username+";<password>"+password+";";
    }
    
    public static boolean isSessionId(String data){
        return data.contains("<session-id>");
    }
    
    public static int getSessionId(String data){
        return Integer.parseInt(getEspecificData("<session-id>", data));
    }
    
    public static boolean isLoginSuccess(String data){
        return data.contains("<login-success>");
    }
    
    public static String getUsername(String data){
        return getEspecificData("<username>", data);
    }
    
    public static boolean isLoginError(String data){
        return data.contains("<login-error>");
    }
    
    public static String getLoginError(String data){
        return getEspecificData("<login-error>", data);
    }
    
    public static boolean isRegisterSuccess(String data){
        return data.contains("<register-success>");
    }
    
    public static boolean isRegisterError(String data){
        return data.contains("<register-error>");
    }
    
    public static String getRegisterError(String data){
        return getEspecificData("<register-error>", data);
    }
    
    public static String Coords(int sessionId){
        return "<coords><session-id>"+sessionId+";";
    }
    
    public static String parseCoordData(int row, int col){
        return "<row>"+row+";<col>"+col+";";
    }
    
    public static String readyToPlay(int sessionId){
        return "<ready><session-id>"+sessionId+";";
    }
    
    public static boolean isGameReady(String data){
        return data.contains("<game-ready>");
    }
    
    public static String BeginGameControlRequest(int sessionId){
        return "<game-control><session-id>"+sessionId+";";
    }
    
    public static boolean isGameControl(String data){
        return data.contains("<game-control>");
    }
    
    public static String BeginGameRequest(int sessionId){
        return "<begin-game><session-id>"+sessionId+";";
    }
    
    public static boolean isBeginGameControlTaken(String data){
        return data.contains("<game-control-taken>");
    }
    
    public static boolean isGameInProgressError(String data){
        return data.contains("<game-in-progress-error>");
    }
    
    public static String getGameInProgressError(String data){
        return getEspecificData("<game-in-progress-error>", data);
    }
    
    public static boolean isBeginGameError(String data){
        return data.contains("<begin-game-error>");
    }
    
    public static String getBeginGameError(String data){
        return getEspecificData("<begin-game-error>", data);
    }
    
    public static boolean isLoginErrorGameInProgress(String data){
        return data.contains("<login-error-game-in-progress>");
    }
    
    public static String getLoginErrorGameInProgress(String data){
        return getEspecificData("<login-error-game-in-progress>", data);
    }
    
    public static boolean isRegisterErrorGameInProgress(String data){
        return data.contains("<register-error-game-in-progress>");
    }
    
    public static String getRegisterErrorGameInProgress(String data){
        return getEspecificData("<register-error-game-in-progress>", data);
    }
    
    public static boolean isTurn(String data){
        return data.contains("<turn>");
    }
    
    public static String parseAttack(int sessionId, int row, int col){
        return "<attack><session-id>"+sessionId+";<row>"+row+";<col>"+col+";";
    }
    
    public static boolean isDamageReceived(String data){
        return data.contains("<damage>");
    }
    
    public static int getRow(String data){
        return Integer.parseInt(getEspecificData("<row>", data));
    }
    
    public static int getCol(String data){
        return Integer.parseInt(getEspecificData("<col>", data));
    }
    
    public static boolean isPlayerDefeated(String data){
        return data.contains("<defeated>");
    }
    
    public static boolean isPlayerVictory(String data){
        return data.contains("<victory>");
    }
    
    public static boolean isTakedownsMessage(String data){
        return data.contains("<takedown>");
    }
    
    public static String getTakedownsMessage(String data){
        return getEspecificData("<takedown>", data);
    }
    
    public static boolean isGameFinished(String data){
        return data.contains("<game-finished>");
    }
}
