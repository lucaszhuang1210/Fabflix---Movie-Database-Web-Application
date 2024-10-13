import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/movie-list?id="
@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movie-list")
public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Declare our statement
            Statement statement = conn.createStatement();

//should all genres and stars
//            String query = "SELECT \n" +
//                    "    m.id, \n" +
//                    "    m.title, \n" +
//                    "    m.year, \n" +
//                    "    m.director, \n" +
//                    "    (SELECT GROUP_CONCAT(g.name ORDER BY g.name SEPARATOR ', ') FROM genres g \n" +  // Change LIMIT to SEPARATOR for correct syntax
//                    "     JOIN genres_in_movies gm ON g.id = gm.genreId WHERE gm.movieId = m.id) AS genres,\n" +
//                    "    (SELECT GROUP_CONCAT(s.name ORDER BY s.name SEPARATOR ', ') FROM stars s \n" +  // Change LIMIT to SEPARATOR for correct syntax
//                    "     JOIN stars_in_movies sm ON s.id = sm.starId WHERE sm.movieId = m.id) AS stars,\n" +
//                    "    r.rating\n" +
//                    "FROM movies m\n" +
//                    "JOIN ratings r ON m.id = r.movieId\n" +
//                    "ORDER BY r.rating DESC\n" +
//                    "LIMIT 20;\n";

            String query = "SELECT \n" +
                    "    m.id, \n" +
                    "    m.title, \n" +
                    "    m.year, \n" +
                    "    m.director, \n" +
                    "    r.rating, \n" +
                    "    (SELECT GROUP_CONCAT(g.name ORDER BY g.name SEPARATOR ', ') \n" +
                    "     FROM genres_in_movies gm \n" +
                    "     JOIN genres g ON gm.genreId = g.id \n" +
                    "     WHERE gm.movieId = m.id \n" +
                    "     LIMIT 3) AS genres, \n" +  // Limit to 3 genres
                    "    (SELECT GROUP_CONCAT(g.id ORDER BY g.name SEPARATOR ', ') \n" + // Fetch genre ids
                    "     FROM genres_in_movies gm \n" +
                    "     JOIN genres g ON gm.genreId = g.id \n" +
                    "     WHERE gm.movieId = m.id \n" +
                    "     LIMIT 3) AS genre_ids, \n" + // Fetch genre ids
                    "    (SELECT GROUP_CONCAT(s.name ORDER BY s.name SEPARATOR ', ') \n" +
                    "     FROM stars_in_movies sm \n" +
                    "     JOIN stars s ON sm.starId = s.id \n" +
                    "     WHERE sm.movieId = m.id \n" +
                    "     LIMIT 3) AS stars, \n" +  // Limit to 3 stars
                    "    (SELECT GROUP_CONCAT(s.id ORDER BY s.name SEPARATOR ', ') \n" +
                    "     FROM stars_in_movies sm \n" +
                    "     JOIN stars s ON sm.starId = s.id \n" +
                    "     WHERE sm.movieId = m.id \n" +
                    "     LIMIT 3) AS star_ids \n" +  // Limit to 3 star ids
                    "FROM movies m \n" +
                    "JOIN ratings r ON m.id = r.movieId \n" +
                    "ORDER BY r.rating DESC \n" +
                    "LIMIT 20;";

            // Perform the query
            ResultSet rs = statement.executeQuery(query);

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                int movie_year = rs.getInt("year");
                String movie_director = rs.getString("director");
                String genres = rs.getString("genres");
                String genre_ids = rs.getString("genre_ids");
                String stars = rs.getString("stars");
                String star_ids = rs.getString("star_ids");
                float movie_rating = rs.getFloat("rating");

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_rating", movie_rating);
                jsonObject.addProperty("movie_genres", genres);
                jsonObject.addProperty("movie_genre_ids", genre_ids);
                jsonObject.addProperty("movie_stars", stars);
                jsonObject.addProperty("movie_star_ids", star_ids);

                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }
}
