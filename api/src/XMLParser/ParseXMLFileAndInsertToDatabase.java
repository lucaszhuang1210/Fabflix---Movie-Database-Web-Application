package XMLParser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ParseXMLFileAndInsertToDatabase extends DefaultHandler {

    public static void main(String[] args) {
        XMLParser movieParser = new MovieParser();
        XMLParser actorParser = new ActorParser();
        XMLParser castParser = new CastParser();

        movieParser.parse("movies.xml");
        actorParser.parse("actors.xml");
        castParser.parse("cast.xml");
    }

    
//    private Connection conn;
//    private PreparedStatement pstmt;
//    private BufferedWriter errorWriter;
//    private String tempVal;
//    private String title, stars, director, genre;
//
//    public void parseXML(String xmlFilePath) {
//        try {
//            // Step 1: Set up database connection and turn off auto-commit
//            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "username", "password");
//            conn.setAutoCommit(false);
//            pstmt = conn.prepareStatement("INSERT INTO movies (title, stars, director, genre) VALUES (?, ?, ?, ?)");
//
//            // Set up the error log file
//            errorWriter = new BufferedWriter(new FileWriter("inconsistency_entries.txt"));
//
//            // Step 2: Set up SAX parser
//            SAXParserFactory factory = SAXParserFactory.newInstance();
//            SAXParser saxParser = factory.newSAXParser();
//            saxParser.parse(xmlFilePath, this);
//
//            // Step 3: Commit the transaction after parsing is complete
//            conn.commit();
//            System.out.println("Movies added to the database successfully.");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            try {
//                if (conn != null) conn.rollback();
//            } catch (Exception rollbackEx) {
//                rollbackEx.printStackTrace();
//            }
//        } finally {
//            try {
//                if (pstmt != null) pstmt.close();
//                if (conn != null) conn.setAutoCommit(true);
//                if (errorWriter != null) errorWriter.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    // Override methods for SAX parsing
//    @Override
//    public void startElement(String uri, String localName, String qName, Attributes attributes) {
//        tempVal = "";  // Clear temp value for each new element
//    }
//
//    @Override
//    public void characters(char[] ch, int start, int length) {
//        tempVal = new String(ch, start, length);  // Store element content
//    }
//
//    @Override
//    public void endElement(String uri, String localName, String qName) throws SAXException {
//        try {
//            switch (qName.toLowerCase()) {
//                case "title":
//                    title = tempVal;
//                    break;
//                case "stars":
//                    stars = tempVal;
//                    break;
//                case "director":
//                    director = tempVal;
//                    break;
//                case "genre":
//                    genre = tempVal;
//                    break;
//                case "movie":
//                    try {
//                        // Insert movie data into the database
//                        pstmt.setString(1, title);
//                        pstmt.setString(2, stars);
//                        pstmt.setString(3, director);
//                        pstmt.setString(4, genre);
//                        pstmt.executeUpdate();
//                    } catch (SQLException e) {
//                        // Log any SQL errors (e.g., constraint violations) to the error log
//                        logError("Error inserting movie: " + title + " - " + e.getMessage());
//                    }
//                    break;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void logError(String message) {
//        try {
//            errorWriter.write(message);
//            errorWriter.newLine();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void main(String[] args) {
//        new MovieXMLToDatabase().parseXML("movies.xml");
//    }
}