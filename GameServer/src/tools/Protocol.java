package tools;

public class Protocol {
    
    private static String getEspecificData(String type, String dataMessage){
        int i = dataMessage.indexOf(type) + type.length();
        int f = dataMessage.indexOf(";", i);
        return dataMessage.substring(i, f);
    }
    
    public static boolean isLoginAction(String data){
        return data.contains("<login>");
    }
    
    public static String loginSuccess(String username){
        return "<login-success><username>"+username+";";
    }
    
    public static String loginFailed(){
        return "<login-error>La contraseña o el nombre de usuario son incorrectos;";
    }
    
    public static String registerSuccess(String username){
        return "<register-success><username>"+username+";";
    }
    
    public static String registerFailed(){
        return "<register-error>El nombre de usuario ya esta en uso;";
    }
    
    public static boolean isRegisterAction(String data){
        return data.contains("<register>");
    }
    
    public static boolean isMessageAction(String data){
        return data.contains("<message>");
    }
    
    public static String parseSessionIdData(int sessionId){
        return "<session-id>"+sessionId+";";
    }
    
    public static int getSessionId(String data){
        return Integer.parseInt(getEspecificData("<session-id>", data));
    }
    
    public static String getUsername(String data){
        return getEspecificData("<username>", data);
    }
    
    public static String getPassword(String data){
        return getEspecificData("<password>", data);
    }
    
    public static boolean isCoordsConfig(String data){
        return data.contains("<coords>");
    }
    
    public static int[][] getCoordsConfig(String data){
        int [][] coords = new int[4][2];
        int c = 0;
        while (data.contains("<col>")){
            int row = Integer.parseInt(getEspecificData("<row>", data));
            int col = Integer.parseInt(getEspecificData("<col>", data));
            coords[c][0] = row;
            coords[c][1] = col;
            c++;
            data = data.substring(data.indexOf("<col>") + "<col>".length());
        }
        return coords;
    }

    public static boolean isPlayerReady(String data) {
        return data.contains("<ready>");
    }
    
    public static String gameReady(){
        return "<game-ready>";
    }
    
    public static boolean isBeginGameControlRequest(String data){
         return data.contains("<game-control>");
    }
    
    public static boolean isBeginGameRequest(String data){
        return data.contains("<begin-game>");
    }
    
    public static String BeginGameControl(){
        return "<game-control>";
    }
    
    public static String BeginGameControlTaken(){
        return "<game-control-taken>";
    }
    
    public static String GameInProgressError(){
        return "<game-in-progress-error>Una partida esta en progreso. Intenta más tarde;";
    }
    
    public static String BeginGameError(){
        return "<begin-game-error>No hay suficientes jugadores para empezar la partida;";
    }
    
    public static String loginErrorGameInProgress(){
        return "<login-error-game-in-progress>Una partida esta en progreso. Intenta iniciar sesión más tarde;";
    }
    
    public static String registerErrorGameInProgress(){
        return "<register-error-game-in-progress>Una partida esta en progreso. Intenta registrarte más tarde;";
    }
    
    public static String Turn(){
        return "<turn>";
    }
    
    public static boolean isAttack(String data){
        return data.contains("<attack>");
    }
    
    public static int getRow(String data){
        return Integer.parseInt(getEspecificData("<row>", data));
    }
    
    public static int getCol(String data){
        return Integer.parseInt(getEspecificData("<col>", data));
    }
    
    public static String parseDamageReceived(int row, int col){
        return "<damage><row>"+row+";<col>"+col+";";
    }
    
    public static String PlayerDefeated(){
        return "<defeated>";
    }
    
    public static String PlayerVictory(){
        return "<victory>";
    }
    
    public static String takedownsMessage(int count){
        return "<takedown>Has derribado "+count+" barcos;";
    }
    
    public static String gameFinished(){
        return "<game-finished>";
    }
}
