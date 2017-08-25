package model;

/**
 * Result instances contains the datas collected from result table records.
 */
public class Result {
    int id;
    String firstName;
    String lastName;
    int result;
    String startTime;
    String endTime;

    /**
     * Result class constructor
     * @param id int
     * @param firstName String
     * @param lastName String
     * @param result int
     * @param startTime String
     * @param endTime String
     */
    public Result(int id, String firstName, String lastName, int result, String startTime, String endTime) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.result = result;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
