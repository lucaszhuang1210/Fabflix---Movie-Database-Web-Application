package XMLParser;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.sql.*;
import java.util.*;

public class MovieParser implements XMLParser {
    private Connection conn;
    private PreparedStatement moviePstmt, genrePstmt, genreLinkPstmt;
    private BufferedWriter errorWriter;
    private String tempVal;
    private String movieID, title, director;
    private Integer year;
    private Set<String> uniqueMovies;       // Set of existing movie IDs
    private Map<String, Integer> genreCache; // Map of genre names to IDs
    private List<Integer> currentMovieGenres; // Temporary list of genre IDs for the current movie
    private Integer lastGeneratedGenreID = null;

    public MovieParser(String loginUser, String loginPasswd, String loginUrl, String errorFile) {
        try {
            // Set up database connection and prepared statements
            conn = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            conn.setAutoCommit(false);  // Turn off auto-commit

            moviePstmt = conn.prepareStatement("INSERT INTO movies (id, title, year, director) VALUES (?, ?, ?, ?)");
            genrePstmt = conn.prepareStatement("INSERT INTO genres (id, name) VALUES (?, ?)");
            genreLinkPstmt = conn.prepareStatement("INSERT INTO genres_in_movies (movie_id, genre_id) VALUES (?, ?)");

            errorWriter = new BufferedWriter(new FileWriter(errorFile, true)); // Append mode

            // Initialize caches with existing database entries
            loadExistingMovies();
            loadExistingGenres();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadExistingMovies() {
        uniqueMovies = new HashSet<>();
        try (PreparedStatement stmt = conn.prepareStatement("SELECT id FROM movies");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                uniqueMovies.add(rs.getString("id"));
            }
            System.out.println("Loaded existing movies into cache.");
        } catch (SQLException e) {
            logError("Error loading existing movies: " + e.getMessage());
        }
    }

    private void loadExistingGenres() {
        genreCache = new HashMap<>();
        try (PreparedStatement stmt = conn.prepareStatement("SELECT id, name FROM genres");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                genreCache.put(rs.getString("name"), rs.getInt("id"));
            }
            // Determine the last genre ID used
            if (!genreCache.isEmpty()) {
                lastGeneratedGenreID = Collections.max(genreCache.values());
            } else {
                lastGeneratedGenreID = 0;
            }
            System.out.println("Loaded existing genres into cache.");
        } catch (SQLException e) {
            logError("Error loading existing genres: " + e.getMessage());
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
                    if ("film".equalsIgnoreCase(qName)) {
                        currentMovieGenres = new ArrayList<>();  // Reset genre list for each movie
                    }
                }

                @Override
                public void characters(char[] ch, int start, int length) {
                    tempVal = new String(ch, start, length).trim();
                }

                @Override
                public void endElement(String uri, String localName, String qName) throws SAXException {
                    switch (qName.toLowerCase()) {
                        case "fid":
                            movieID = tempVal.isEmpty() ? null : tempVal;
                            break;
                        case "t":
                            title = tempVal.isEmpty() ? null : tempVal;
                            break;
                        case "year":
                            year = tempVal.matches("\\d{4}") ? Integer.parseInt(tempVal) : null;
                            break;
                        case "dirname":
                            director = tempVal.isEmpty() ? null : tempVal;
                            break;
                        case "cat":  // Genre
                            addGenreToDatabase(tempVal);
                            break;
                        case "film":
                            if (movieID == null || title == null || year == null || director == null || currentMovieGenres.isEmpty()) {
                                logError("Incomplete movie entry: " + (movieID != null ? movieID : "unknown ") + (title != null ? title : "unknown ") + (year != null ? year : "unknown ") + (director != null ? director : "unknown ") + (currentMovieGenres.isEmpty() ? "Genre empty" : ""));
                            } else if (!uniqueMovies.contains(movieID)) {
                                addMovieToDatabase(movieID, title, year, director);
                                addGenresToMovie(movieID);  // Link genres to movie
                                uniqueMovies.add(movieID);
                            } else {
                                logError("Duplicate movie entry ignored: " + movieID);
                            }
                            // Reset values for the next movie
                            movieID = null;
                            title = null;
                            year = null;
                            director = null;
                            currentMovieGenres = null;
                            break;
                    }
                }
            });

            conn.commit();  // Commit all changes after parsing is complete
            System.out.println("All movie and genre data committed to the database.");

        } catch (Exception e) {
            logError("Error parsing movie file: " + e.getMessage());
            e.printStackTrace();
            try {
                conn.rollback();  // Rollback in case of an error
                System.out.println("Transaction rolled back due to an error.");
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


    private void addMovieToDatabase(String id, String title, Integer year, String director) {
        try {
            moviePstmt.setString(1, id);
            moviePstmt.setString(2, title);
            moviePstmt.setInt(3, year);
            moviePstmt.setString(4, director);
            System.out.println("Inserted Movie - ID: " + id + ", Title: " + title + ", Year: " + year + ", Director: " + director);
            moviePstmt.executeUpdate();
        } catch (SQLException e) {
            logError("Error inserting movie: " + title + " - " + e.getMessage());
        }
    }

    private void addGenreToDatabase(String genreName) {
        try {
            // Check if genre is already cached
            if (genreCache.containsKey(genreName)) {
                // Use cached genre ID
                currentMovieGenres.add(genreCache.get(genreName));
            } else {
                // Generate new genre ID and insert the genre into the database
                int newGenreID = generateNewGenreID();
                genrePstmt.setInt(1, newGenreID);
                genrePstmt.setString(2, genreName);
                genrePstmt.executeUpdate();
                System.out.println("Inserted Genre - ID: " + newGenreID + ", Name: " + genreName);

                // Cache the new genre ID and add it to the current movie's genre list
                genreCache.put(genreName, newGenreID);
                currentMovieGenres.add(newGenreID);
            }
        } catch (SQLException e) {
            logError("Error handling genre: " + genreName + " - " + e.getMessage());
        }
    }

    private void addGenresToMovie(String movieID) {
        try {
            for (Integer genreID : currentMovieGenres) {
                genreLinkPstmt.setString(1, movieID);
                genreLinkPstmt.setInt(2, genreID);
                genreLinkPstmt.executeUpdate();
            }
        } catch (SQLException e) {
            logError("Error linking genres to movie: " + movieID + " - " + e.getMessage());
        }
    }

    private Integer generateNewGenreID() {
        if (lastGeneratedGenreID == null) {
            try (PreparedStatement stmt = conn.prepareStatement("SELECT MAX(id) AS max_id FROM genres");
                 ResultSet rs = stmt.executeQuery()) {
                lastGeneratedGenreID = rs.next() ? rs.getInt("max_id") : 0;
            } catch (SQLException e) {
                logError("Error generating new genre ID: " + e.getMessage());
            }
        }
        return ++lastGeneratedGenreID;
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
            if (moviePstmt != null) moviePstmt.close();
            if (genrePstmt != null) genrePstmt.close();
            if (genreLinkPstmt != null) genreLinkPstmt.close();
            if (conn != null) conn.close();
            if (errorWriter != null) errorWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
    }
    }
}