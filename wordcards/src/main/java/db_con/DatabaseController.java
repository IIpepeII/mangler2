package db_con;

import controller.Controller;
import model.Result;
import model.WordCard;
import org.json.JSONException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class responsible for the queries. It uses prepared statement on every
 * queries againts SQL injection.
 */
public class DatabaseController {

    private PreparedStatement preparedStatement;
    private Connection dbConnection;

    /**
     * This constructor method make an instance of the class with the connection to the database.
     */
    public DatabaseController() {
            dbConnection = SQLiteJDBCDriverConnection.connect();
    }

    /**
     * Add new record to word_card table.
     * @param cardDetails List of String objects
     */
    public void addNewWordCard(List<String> cardDetails) {

        String insertIntoTable = "INSERT INTO word_card (pic_location, theme, hun, eng) VALUES (?,?,?,?);";

        try {
            // Adding record to DB
            preparedStatement = dbConnection.prepareStatement(insertIntoTable);
            preparedStatement.setString(1, cardDetails.get(0));
            preparedStatement.setString(2, cardDetails.get(1));
            preparedStatement.setString(3, cardDetails.get(2));
            preparedStatement.setString(4, cardDetails.get(3));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add new record to the result table.
     * @param firstName String
     * @param lastName String
     * @param result String
     * @param startTime String
     * @param endTime String
     */
    public void addNewResult(String firstName, String lastName, String result, String startTime, String endTime) {

        String insertIntoTable = "INSERT INTO result (first_name, last_name, result, start_time, end_time) VALUES (?,?,?,?,?);";

        try {
            // Adding record to DB
            preparedStatement = dbConnection.prepareStatement(insertIntoTable);
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, result);
            preparedStatement.setString(4, startTime);
            preparedStatement.setString(5, endTime);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method made Wordcard object from every records from the word_card table.
     * @return List of WordCard objects
     * @throws JSONException thrown by method
     */
    public List<WordCard> getAllWordCards() throws JSONException {

        String query = "SELECT * FROM word_card";
        return queryToPreparedStatementForWordcards(query);
    }

    /**This method made Result object from every records from the result table.
     * @return List of Result objects.
     * @throws JSONException thrown by the method
     */
    public List<Result> getAllResults() throws JSONException {

        String query = "SELECT * FROM result";
        return queryToPreparedStatementForResults(query);
    }

    /**
     * This method deletes a record from the result table by id.
     * @param id Integer
     */
    public void delResult(Integer id) {
        String query = "DELETE FROM result WHERE id= ?;";
        deleteRowById(id, query);
    }

    /**
     * This method first select a record from the word_card table by id, then delete the file
     * selected from the /Upload folder using the path from the pic_location column. In the
     * last step it deletes the record from the word_card table too.
     * @param id Integer
     */
    public void delWordCard(Integer id) {
        String selectFirst = "SELECT pic_location FROM word_card WHERE id=?;";
        try {
            preparedStatement = dbConnection.prepareStatement(selectFirst,
                    ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY,
                    ResultSet.CLOSE_CURSORS_AT_COMMIT);
            preparedStatement.setInt(1, id);
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()) {
                Controller.deleteFile(result.getString("pic_location"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String query = "DELETE FROM word_card WHERE id= ?;";
        deleteRowById(id, query);
    }

    /**
     * Generic method to execute delete or other updathe queries by id.
     * @param id Integer
     * @param query String
     */
    private void deleteRowById(Integer id, String query) {
        try {
            preparedStatement = dbConnection.prepareStatement(query,
                    ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY,
                    ResultSet.CLOSE_CURSORS_AT_COMMIT);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method make Result objects from a ResultSet.
     * @param query String
     * @return List of Result objects.
     */
    private List<Result> queryToPreparedStatementForResults(String query) {
        try {
            preparedStatement = dbConnection.prepareStatement(query,
                    ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY,
                    ResultSet.CLOSE_CURSORS_AT_COMMIT);
            ResultSet result = preparedStatement.executeQuery();
            return resultFactory(result);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method make Wordcard objects from a ResultSet.
     * @param query String
     * @return List of WordCard objects.
     */
    private List<WordCard> queryToPreparedStatementForWordcards(String query) {
        try {
            preparedStatement = dbConnection.prepareStatement(query,
                    ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY,
                    ResultSet.CLOSE_CURSORS_AT_COMMIT);
            ResultSet result = preparedStatement.executeQuery();
            return cardFactory(result);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method get 10 random records from word_card table then make WordCard objects from theme.
     * @return List of WordCard objects.
     */
    public List<WordCard> getMixedCards() {
        String query = "SELECT * FROM word_card\n" +
                "ORDER BY RANDOM()\n" +
                "LIMIT 10";
        return queryToPreparedStatementForWordcards(query);
    }

    /**
     * This method get 10 random records from word_card table then make WordCard objects from theme.
     * @param theme String
     * @return List of WordCard objects.
     */
    public List<WordCard> getCardsByTheme(String theme) {

        String query = "SELECT * FROM word_card WHERE theme = ? ORDER BY RANDOM() LIMIT 10";
        try {
            preparedStatement = dbConnection.prepareStatement(query,
                    ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY,
                    ResultSet.CLOSE_CURSORS_AT_COMMIT);
            preparedStatement.setString(1, theme);
            ResultSet result = preparedStatement.executeQuery();
            return cardFactory(result);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method make a List of WordCard objects from a ResultSet object.
     * @param result ResultSet
     * @return List of WordCard objects
     */
    private List<WordCard> cardFactory(ResultSet result) {
        List<WordCard> wordCardList = new ArrayList<>();
        try {
            while (result.next()) {
                WordCard wordCard = new WordCard(result.getInt("id"),
                        result.getString("pic_location"),
                        result.getString("theme"),
                        result.getString("eng"),
                        result.getString("hun"));
                wordCardList.add(wordCard);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wordCardList;
    }

    /**
     * This method make a List of Result objects from a ResultSet object.
     * @param result ResultSet
     * @return List of Result objects
     */
    private List<Result> resultFactory(ResultSet result) {
        List<Result> resultList = new ArrayList<>();
        try {
            while (result.next()) {
                Result newResult = new Result(result.getInt("id"),
                        result.getString("first_name"),
                        result.getString("last_name"),
                        result.getInt("result"),
                        result.getString("start_time"),
                        result.getString("end_time"));
                resultList.add(newResult);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    /**
     * This method creates word_card and result table if they don't exist.
     */
    public void createTablesIfNotExists(){

        String query = "CREATE TABLE IF NOT EXISTS word_card (\n" +
                "  id         INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "  pic_location VARCHAR(255),\n" +
                "  theme        VARCHAR(255),\n" +
                "  hun          VARCHAR(100),\n" +
                "  eng          VARCHAR(100)\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE IF NOT EXISTS result (\n" +
                "  id         INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "  first_name VARCHAR(255),\n" +
                "  last_name  VARCHAR(255),\n" +
                "  start_time VARCHAR(100),\n" +
                "  end_time   VARCHAR(100),\n" +
                "  result     INT\n" +
                ");";

        try {
            Statement stmnt = dbConnection.createStatement();
            stmnt.executeUpdate(query);
        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }

    }

}






