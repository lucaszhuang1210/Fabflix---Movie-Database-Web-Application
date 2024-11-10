package XMLParser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ParseXMLFileAndInsertToDatabase {

    public static void main(String[] args) {
        // Start the timer
        long startTime = System.currentTimeMillis();

        String loginUser = "mytestuser";
        String loginPasswd = "My6$Password";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";  // Replace with your actual JDBC URL
        String errorFile = "../logs/inconsistency_entries.txt";

        // Ensure the error log file and directory are set up, and clear its contents
        setupErrorLogFile(errorFile);

        // Initialize and run the parsers
        ActorParser actorParser = new ActorParser(loginUser, loginPasswd, loginUrl, errorFile);
        MovieParser movieParser = new MovieParser(loginUser, loginPasswd, loginUrl, errorFile);
        CastParser castParser = new CastParser(loginUser, loginPasswd, loginUrl, errorFile);

        actorParser.parse("/Users/lucaszhuang1210gmail.com/Documents/UCI/CS122B/2024-fall-cs-122b-microhard/stanford-movies/actors_test.xml");
        movieParser.parse("/Users/lucaszhuang1210gmail.com/Documents/UCI/CS122B/2024-fall-cs-122b-microhard/stanford-movies/movies_test.xml");
        castParser.parse("/Users/lucaszhuang1210gmail.com/Documents/UCI/CS122B/2024-fall-cs-122b-microhard/stanford-movies/cast_test.xml");

        // Calculate and print the elapsed time
        long endTime = System.currentTimeMillis();
        System.out.println("Total time taken: " + (endTime - startTime)/1000 + " seconds");
    }

    private static void setupErrorLogFile(String errorFile) {
        // Create the logs directory if it does not exist
        File logsDir = new File("../logs");
        if (!logsDir.exists()) {
            logsDir.mkdirs();
            System.out.println("Created logs directory at: " + logsDir.getAbsolutePath());
        }

        // Create the error log file if it does not exist and clear its contents
        File errorLogFile = new File(errorFile);
        try (FileWriter writer = new FileWriter(errorLogFile, false)) { // 'false' will clear the file
            if (!errorLogFile.exists()) {
                errorLogFile.createNewFile();
                System.out.println("Created error log file at: " + errorLogFile.getAbsolutePath());
            } else {
                System.out.println("Cleared error log file at: " + errorLogFile.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Failed to create or clear error log file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}