package XMLParser;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class ActorParser implements XMLParser {
    private Connection conn;
    private PreparedStatement pstmt;
    private BufferedWriter errorWriter;
    private String tempVal;
    private String stagename, dob;
    private String lastGeneratedID = null;
    private Set<String> uniqueActors;  // Set to track unique actor names

    public ActorParser(String loginUser, String loginPasswd, String loginUrl, String errorFile) {
        try {
            // Set up database connection and prepared statement
            conn = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            conn.setAutoCommit(false);  // Turn off auto-commit
            pstmt = conn.prepareStatement("INSERT INTO stars (id, name, birthYear) VALUES (?, ?, ?)");
            errorWriter = new BufferedWriter(new FileWriter(errorFile, true)); // Append mode
            uniqueActors = new HashSet<>(); // Initialize the set for tracking duplicates
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void parse(String xmlFilePath) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            // Use InputStreamReader with ISO-8859-1 encoding
            InputStreamReader reader = new InputStreamReader(new FileInputStream(xmlFilePath), "ISO-8859-1");
            saxParser.parse(new InputSource(reader), new DefaultHandler() {
                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) {
                    tempVal = "";  // Reset the temporary value
                }

                @Override
                public void characters(char[] ch, int start, int length) {
                    tempVal = new String(ch, start, length).trim();  // Trim to remove any whitespace
                }

                @Override
                public void endElement(String uri, String localName, String qName) throws SAXException {
                    switch (qName.toLowerCase()) {
                        case "stagename":
                            stagename = tempVal.isEmpty() ? null : tempVal;
                            break;
                        case "dob":
                            dob = tempVal.matches("\\d{4}") ? tempVal : null;  // Check if dob is a valid 4-digit year
                            break;
                        case "actor":
                            if (stagename == null) {
                                logError("Missing name for actor with dob: " + dob);
                            } else if (!uniqueActors.contains(stagename)) {  // Check for duplicate
                                Integer birthYear = dob != null ? Integer.parseInt(dob) : null;
                                String id = generateNewID();  // Generate unique ID for actor
                                addStarToDatabase(id, stagename, birthYear);
                                uniqueActors.add(stagename);  // Add to the set to track as inserted
                            } else {
                                logError("Duplicate actor entry ignored: " + stagename);
                            }
                            // Reset values for the next actor
                            stagename = null;
                            dob = null;
                            break;
                    }
                }
            });

            conn.commit();  // Commit all changes after parsing is complete
//            System.out.println("All data committed to the database.");
//            System.out.println("Unique Actors Parsed:");
//            uniqueActors.forEach(System.out::println);
        } catch (Exception e) {
            logError("Error parsing actor file: " + e.getMessage());
            e.printStackTrace();
            try {
                conn.rollback();  // Rollback in case of an error
            } catch (SQLException rollbackEx) {
                logError("Error during rollback: " + rollbackEx.getMessage());
                rollbackEx.printStackTrace();
            }
        } finally {
            try {
                conn.setAutoCommit(true);  // Re-enable auto-commit
            } catch (SQLException e) {
                logError("Error re-enabling auto-commit: " + e.getMessage());
            }
            closeResources();
        }
    }

    public void addStarToDatabase(String id, String name, Integer birthYear) {
        try {
            pstmt.setString(1, id);
            pstmt.setString(2, name);
            if (birthYear != null) {
                pstmt.setInt(3, birthYear);
            } else {
                pstmt.setNull(3, java.sql.Types.INTEGER);
            }
//            System.out.println("Inserted Actor - ID: " + id + ", Name: " + name + ", Birth Year: " + (birthYear != null ? birthYear : "NULL"));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logError("Error inserting actor: " + name + " - " + e.getMessage());
        }
    }

    private String generateNewID() {
        if (lastGeneratedID == null) {
            try (PreparedStatement stmt = conn.prepareStatement("SELECT MAX(id) AS max_id FROM stars");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String maxId = rs.getString("max_id");
                    if (maxId != null && maxId.startsWith("nm")) {
                        int idNumber = Integer.parseInt(maxId.substring(2));  // Extract numeric part of the ID
                        lastGeneratedID = "nm" + String.format("%07d", idNumber); // Cache the max ID
                    }
                }
            } catch (SQLException e) {
                logError("Error generating new ID: " + e.getMessage());
            }
            if (lastGeneratedID == null) lastGeneratedID = "nm0000000"; // Default if no max ID
        }
        // Increment the cached ID
        int idNumber = Integer.parseInt(lastGeneratedID.substring(2)) + 1;
        lastGeneratedID = "nm" + String.format("%07d", idNumber);
        return lastGeneratedID;
    }

    private void logError(String message) {
        try {
            errorWriter.write(message);
            errorWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeResources() {
        try {
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
            if (errorWriter != null) errorWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}