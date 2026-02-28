package banking.DBClient;

import java.sql.*;

public class DataBase {

    private final String url;

    private Connection cnn;

    public DataBase(String fileName) {
        this.url = "jdbc:sqlite:" + fileName;
    }

    public void init() throws SQLException {
        this.cnn = DriverManager.getConnection(url);
        createTable();
        //return cnn;
    }

    public void closeConnection() {
        if (cnn != null) {
            try {
                cnn.close();
                // System.out.println("Connection closed");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void createTable() {
        String sql = """
                    CREATE TABLE IF NOT EXISTS card (
                        id INTEGER PRIMARY KEY,
                        number TEXT,
                        pin TEXT,
                        balance INTEGER DEFAULT 0
                    );
                """;
        try (Statement stmt = cnn.createStatement()) {
            //System.out.println("My connection is really good!");
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return cnn;
    }
}



