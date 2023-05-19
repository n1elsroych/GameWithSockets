package app;

public class SeaMap {
    private int table[][];
    private int shipsAlive;

    public SeaMap(int rowNum, int colNum) {
        table = new int[rowNum][colNum];
        shipsAlive = 0;
    }
    
    public void addShip(int row, int col){
        table[row][col] = 1;
        shipsAlive++;
    }
    
    public void removeShip(int row, int col){
        table[row][col] = 0;
        shipsAlive--;
    }
    
    public void setCoords(int[][] coords){
        int r;
        int c;
        for (int[] coord : coords) {
            r = coord[0];
            c = coord[1];
            table[r][c] = 1;
            shipsAlive++;
        }
    }
    
    public boolean hasShipAt(int row, int col){
        return table[row][col] == 1;
    }
    
    public boolean hasShipsAlive(){
        return shipsAlive > 0;
    }
    
    public void showCoords(){
        for (int[] table1 : table) {
            for (int j = 0; j < table1.length; j++) {
                System.out.print(table1[j] + "\t");
            }
            System.out.println();
        }
    }
}
