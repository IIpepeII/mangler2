package controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * This class responsible for every I/O and other service operations for the server.
 */
public class Controller {

    /**
     * This method responsible for always available current time.
     * @return String current time in format yyyy/MM/dd HH:mm:ss
     */
    public static String getCurrentTime() {
        return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
    }

    /**
     * This method responsible for deleting files by the given paths.
     * @param filePath to file
     */
    public static void deleteFile(String filePath) {

        try {
            //Example file
            File file = new File(filePath);

            if (file.delete()) {
                System.out.println(file.getName() + " is deleted!");
            } else {
                System.out.println("Delete operation is failed.");
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}

