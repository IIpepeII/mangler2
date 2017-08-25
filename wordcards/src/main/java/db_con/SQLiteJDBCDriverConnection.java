package db_con;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.sqlite.JDBC;

/**
 * This class responsible for the connection and
 * creation(if not exists) to database.
 */
public class SQLiteJDBCDriverConnection {

    /**
     * Connect to a wordcard SQLite database
     * @return Connection object
     */
    public static Connection connect() {
        Connection conn = null;
        try {
            // db parameters
            String url = "jdbc:sqlite:database/wordcards.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);

            System.out.println("Connection to SQLite has been established. Path: database/wordcards.db");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }
}