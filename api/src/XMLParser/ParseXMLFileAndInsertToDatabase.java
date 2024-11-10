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

        // Get the project base directory
        String baseDir = new File(System.getProperty("user.dir")).getParentFile().getAbsolutePath();

        // Construct relative paths from the base directory
//        **************************TEST CASES**************************
//        String actorFilePath = baseDir + "/stanford-movies/actors_test.xml";
//        String movieFilePath = baseDir + "/stanford-movies/movies_test.xml";
//        String castFilePath = baseDir + "/stanford-movies/cast_test.xml";

        String actorFilePath = baseDir + "/stanford-movies/actors63.xml";
        String movieFilePath = baseDir + "/stanford-movies/mains243.xml";
        String castFilePath = baseDir + "/stanford-movies/casts124.xml";

        // Initialize and run the parsers with constructed paths
        ActorParser actorParser = new ActorParser(loginUser, loginPasswd, loginUrl, errorFile);
        MovieParser movieParser = new MovieParser(loginUser, loginPasswd, loginUrl, errorFile);
        CastParser castParser = new CastParser(loginUser, loginPasswd, loginUrl, errorFile);

        actorParser.parse(actorFilePath);
        movieParser.parse(movieFilePath);
        castParser.parse(castFilePath);

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