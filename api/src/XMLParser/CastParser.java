package XMLParser;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class CastParser implements XMLParser {
    private Connection conn;
    private PreparedStatement starPstmt, starsInMoviesPstmt, checkStarPstmt, checkMovieByTitlePstmt;
    private BufferedWriter errorWriter;
    private String tempVal;
    private String movieTitle, actorName;
    private Set<String> uniqueCasts;
    private Integer lastGeneratedStarID = null;

    public CastParser(String loginUser, String loginPasswd, String loginUrl, String errorFile) {
        try {
            conn = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            conn.setAutoCommit(false);

            starPstmt = conn.prepareStatement("INSERT INTO stars (id, name, birthYear) VALUES (?, ?, NULL)");
            starsInMoviesPstmt = conn.prepareStatement("INSERT INTO stars_in_movies (starId, movieId) VALUES (?, ?)");
            checkStarPstmt = conn.prepareStatement("SELECT id FROM stars WHERE name = ? LIMIT 1");
            checkMovieByTitlePstmt = conn.prepareStatement("SELECT id FROM movies WHERE title = ? LIMIT 1");

            errorWriter = new BufferedWriter(new FileWriter(errorFile, true));
            uniqueCasts = new HashSet<>();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void parse(String xmlFilePath) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            InputStreamReader reader = new InputStreamReader(new FileInputStream(xmlFilePath), "ISO-8859-1");
            saxParser.parse(new InputSource(reader), new DefaultHandler() {
                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) {
                    tempVal = "";
                }

                @Override
                public void characters(char[] ch, int start, int length) {
                    tempVal = new String(ch, start, length).trim();
                }

                @Override
                public void endElement(String uri, String localName, String qName) throws SAXException {
                    switch (qName.toLowerCase()) {
                        case "t":
                            movieTitle = tempVal.isEmpty() ? null : tempVal;
                            break;
                        case "a":
                            actorName = tempVal.isEmpty() ? null : tempVal;
                            break;
                        case "m":
                            processCastEntry();
                            movieTitle = null;
                            actorName = null;
                            break;
                    }
                }
            });

            conn.commit();
            System.out.println("All cast data committed to the database.");

        } catch (Exception e) {
            handleParseError(e);
        } finally {
            closeResources();
        }
    }

    private void processCastEntry() {
        try {
            if (movieTitle == null || actorName == null) {
                logError("Inconsistent cast entry - Missing movie or actor data for title: " + movieTitle + ", actorName: " + actorName);
                return;
            }

            String movieID = getMovieIdByTitle(movieTitle);
            if (movieID == null) {
                logError("Inconsistent cast entry - Movie title not found in database: " + movieTitle);
                return;
            }

            String starID = getOrCreateStarID(actorName);
            if (starID != null) {
                addStarToMovie(starID, movieID);
            }
        } catch (SQLException e) {
            logError("Error processing cast entry: " + e.getMessage());
        }
    }

    private String getMovieIdByTitle(String title) throws SQLException {
        checkMovieByTitlePstmt.setString(1, title);
        ResultSet rs = checkMovieByTitlePstmt.executeQuery();
        String movieID = rs.next() ? rs.getString("id") : null;
        rs.close();
        return movieID;
    }

    private String getOrCreateStarID(String actorName) throws SQLException {
        checkStarPstmt.setString(1, actorName);
        ResultSet rs = checkStarPstmt.executeQuery();
        if (rs.next()) {
            String starID = rs.getString("id");
            rs.close();
            return starID;
        } else {
            rs.close();
            return addStarToDatabase(actorName);
        }
    }

    private String addStarToDatabase(String actorName) throws SQLException {
        String newStarID = generateNewStarID();
        starPstmt.setString(1, newStarID);
        starPstmt.setString(2, actorName);
        starPstmt.executeUpdate();
//        System.out.println("Inserted new star - ID: " + newStarID + ", Name: " + actorName);
        return newStarID;
    }

    private void addStarToMovie(String starID, String movieID) throws SQLException {
        starsInMoviesPstmt.setString(1, starID);
        starsInMoviesPstmt.setString(2, movieID);
        starsInMoviesPstmt.executeUpdate();
//        System.out.println("Linked star ID: " + starID + " with movie ID: " + movieID);
    }

    private String generateNewStarID() {
        if (lastGeneratedStarID == null) {
            try (PreparedStatement stmt = conn.prepareStatement("SELECT MAX(id) AS max_id FROM stars");
                 ResultSet rs = stmt.executeQuery()) {
                lastGeneratedStarID = rs.next() ? Integer.parseInt(rs.getString("max_id").substring(2)) : 0;
            } catch (SQLException e) {
                logError("Error generating new star ID: " + e.getMessage());
            }
        }
        return "nm" + String.format("%07d", ++lastGeneratedStarID);
    }

    private void logError(String message) {
        try {
            errorWriter.write(message);
            errorWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleParseError(Exception e) {
        logError("Error parsing cast file: " + e.getMessage());
        e.printStackTrace();
        try {
            conn.rollback();
            System.out.println("Transaction rolled back due to an error.");
        } catch (SQLException rollbackEx) {
            logError("Error during rollback: " + rollbackEx.getMessage());
            rollbackEx.printStackTrace();
        }
    }

    private void closeResources() {
        try {
            if (starPstmt != null) starPstmt.close();
            if (starsInMoviesPstmt != null) starsInMoviesPstmt.close();
            if (checkStarPstmt != null) checkStarPstmt.close();
            if (checkMovieByTitlePstmt != null) checkMovieByTitlePstmt.close();
            if (conn != null) conn.close();
            if (errorWriter != null) errorWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}