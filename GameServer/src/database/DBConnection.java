package database;

import app.User;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.Base64;
import java.util.Map;
import java.util.Properties;

public class DBConnection {
    Connection conn;

    public DBConnection(){
        try {
            Class.forName("org.postgresql.Driver");
            String PATH = "src/database/persistence.properties";
            Properties properties = new Properties();
            try {
                properties.load(new FileReader(PATH));
                String URL = "jdbc:postgresql://"+properties.getProperty("DB_HOST")+":"+properties.getProperty("DB_PORT")+"/"+properties.getProperty("DB_DATABASE");
                conn = DriverManager.getConnection(URL, properties.getProperty("DB_USERNAME"), properties.getProperty("DB_PASSWORD"));
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void loadUsersFromDBTo(Map<String, User> users){
        String sqlQuery = "SELECT * FROM users";
        Statement stmt;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlQuery);
            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                //byte[] passwordBytes = rs.getBytes("password");
                //String password = Base64.getEncoder().encodeToString(passwordBytes);
                String password = rs.getString("password");
                User user = new User(id, username, password);
                users.put(username, user);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void saveUsersOnDBFrom(Map<String, User> users) throws SQLException{
        String tempTable = "temp_users";
        String createTempTableQuery = "CREATE TEMPORARY TABLE "+tempTable+
                                        " (username VARCHAR(16), password VARCHAR(16))";
        Statement statement = conn.createStatement();
        statement.execute(createTempTableQuery);

        String insertIntoTempTableQuery = "INSERT INTO " + tempTable + " (username, password) VALUES (?, ?)";
        PreparedStatement insertStatement = conn.prepareStatement(insertIntoTempTableQuery);

        for (User user : users.values()){
            insertStatement.setString(1, user.getUsername());
            insertStatement.setString(2, user.getPassword());
            insertStatement.executeUpdate();
        }

        String mergeTablesQuery = "INSERT INTO users (username, password)" +
                                " SELECT t.username, t.password FROM " + tempTable + " t " +
                                " ON CONFLICT (username) DO UPDATE SET password = EXCLUDED.password;";
        statement.execute(mergeTablesQuery);

        String deleteNotPresentQuery = "DELETE FROM users WHERE NOT EXISTS (SELECT 1 FROM " + tempTable + " WHERE users.username = " + tempTable + ".username);";
        statement.execute(deleteNotPresentQuery);

        // Eliminar la tabla temporal
        String delTempTableQuery = "DROP TABLE " + tempTable;
        statement.execute(delTempTableQuery);
    }
}
