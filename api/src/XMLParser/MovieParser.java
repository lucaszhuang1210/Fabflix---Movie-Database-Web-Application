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
    private PreparedStatement moviePstmt, genrePstmt, genreLinkPstmt, ratingsPstmt;
    private BufferedWriter errorWriter;
    private String tempVal;
    private String movieID, title, director;
    private Integer year;
    private Set<String> uniqueMovies;
    private Map<String, Integer> genreCache;
    private List<Integer> currentMovieGenres;
    private Integer lastGeneratedGenreID;

    public MovieParser(String loginUser, String loginPasswd, String loginUrl, String errorFile) {
        try {
            conn = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            conn.setAutoCommit(false);

            initializePreparedStatements();
            errorWriter = new BufferedWriter(new FileWriter(errorFile, true));

            loadExistingMovies();
            loadExistingGenres();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializePreparedStatements() throws SQLException {
        moviePstmt = conn.prepareStatement("INSERT INTO movies (id, title, year, director) VALUES (?, ?, ?, ?)");
        genrePstmt = conn.prepareStatement("INSERT INTO genres (id, name) VALUES (?, ?)");
        genreLinkPstmt = conn.prepareStatement("INSERT INTO genres_in_movies (movieId, genreId) VALUES (?, ?)");
        ratingsPstmt = conn.prepareStatement("INSERT INTO ratings (movieId, rating, numVotes) VALUES (?, 0, 0)");
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
            lastGeneratedGenreID = genreCache.isEmpty() ? 0 : Collections.max(genreCache.values());
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
                        currentMovieGenres = new ArrayList<>();
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
                        case "dirn":
                            director = tempVal.isEmpty() ? null : tempVal;
                            break;
                        case "cat":
                            addGenreToDatabase(tempVal);
                            break;
                        case "film":
                            if (isValidMovie()) {
                                addMovieToDatabase(movieID, title, year, director);
                                addGenresToMovie(movieID);
                                uniqueMovies.add(movieID);
                            } else {
                                logError("Incomplete movie entry: Movie ID: " + (movieID != null ? movieID : "unknown") + ", Title: " + (title != null ? title : "unknown") + ", Year: " + (year != null ? year : "unknown") + ", Director: " + (director != null ? director : "unknown") + (currentMovieGenres.isEmpty() ? ", Genre empty" : ""));
                            }
                            resetMovieData();
                            break;
                        case "directorfilms":
                            director = null;
                    }
                }
            });

            conn.commit();
            System.out.println("All movie and genre data committed to the database.");
        } catch (Exception e) {
            handleParseError(e);
        } finally {
            closeResources();
        }
    }

    private boolean isValidMovie() {
//        USE THIS ONE IF WE REQUIRE MOVIE TO HAVE GENRE
//        return movieID != null && title != null && year != null && director != null && !currentMovieGenres.isEmpty();
        return movieID != null && title != null && year != null && director != null;

    }

    private void resetMovieData() {
        movieID = title = null;
        year = null;
        currentMovieGenres.clear();
    }

    private void handleParseError(Exception e) {
        logError("Error parsing movie file: " + e.getMessage());
        e.printStackTrace();
        try {
            conn.rollback();
            System.out.println("Transaction rolled back due to an error.");
        } catch (SQLException rollbackEx) {
            logError("Error during rollback: " + rollbackEx.getMessage());
            rollbackEx.printStackTrace();
        }
    }

    private void addMovieToDatabase(String id, String title, Integer year, String director) {
        try {
            moviePstmt.setString(1, id);
            moviePstmt.setString(2, title);
            moviePstmt.setInt(3, year);
            moviePstmt.setString(4, director);
            moviePstmt.executeUpdate();

            // Insert default rating entry for the new movie
            ratingsPstmt.setString(1, id);
            ratingsPstmt.executeUpdate();

//            System.out.println("Inserted Movie - ID: " + id + ", Title: " + title + ", Year: " + year + ", Director: " + director);
        } catch (SQLException e) {
            logError("Error inserting movie: " + title + " - " + e.getMessage());
        }
    }

    private void addGenreToDatabase(String genreName) {
        if (genreCache.containsKey(genreName)) {
            currentMovieGenres.add(genreCache.get(genreName));
        } else {
            int newGenreID = ++lastGeneratedGenreID;
            try {
                genrePstmt.setInt(1, newGenreID);
                genrePstmt.setString(2, genreName);
                genrePstmt.executeUpdate();
//                System.out.println("Inserted Genre - ID: " + newGenreID + ", Name: " + genreName);

                genreCache.put(genreName, newGenreID);
                currentMovieGenres.add(newGenreID);
            } catch (SQLException e) {
                logError("Error handling genre: " + genreName + " - " + e.getMessage());
            }
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

    private void logError(String message) {
        try {
            errorWriter.write(message);
            errorWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}