import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movie-list")
public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // DataSource for database connection
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles GET requests for movie list.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("MovieListServlet: doGet method is being called.");
        response.setContentType("application/json"); // Response MIME type

        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection()) {

            HttpSession session = request.getSession();

            // Read parameters from request
            String title = request.getParameter("title");
            String year = request.getParameter("year");
            String director = request.getParameter("director");
            String starName = request.getParameter("star");
            String pageStr = request.getParameter("page");
            String sort = request.getParameter("sort");
            String reset = request.getParameter("reset");
            String initial = request.getParameter("initial");
            String genre = request.getParameter("genre");

            int page = Integer.parseInt(request.getParameter("page"));
            int limit = Integer.parseInt(request.getParameter("limit"));

            int resultsPerPage = limit;
            int pageNumber = (pageStr != null) ? Integer.parseInt(pageStr) : 1;

            // Handle reset
            if (reset != null && reset.equals("true")) {
                session.invalidate();
                session = request.getSession(); // Start a new session
            }

            // Store or retrieve parameters in/from session
            if (title != null) session.setAttribute("title", title);
            else title = (String) session.getAttribute("title");

            if (year != null) session.setAttribute("year", year);
            else year = (String) session.getAttribute("year");

            if (director != null) session.setAttribute("director", director);
            else director = (String) session.getAttribute("director");

            if (starName != null) session.setAttribute("star", starName);
            else starName = (String) session.getAttribute("star");

            if (sort != null) session.setAttribute("sort", sort);
            else sort = (String) session.getAttribute("sort");

            if (pageStr != null) session.setAttribute("page", pageNumber);
            else if (session.getAttribute("page") != null)
                pageNumber = (int) session.getAttribute("page");

            if (initial != null && !initial.trim().isEmpty()) {
                session.setAttribute("initial", initial);
            } else {
                initial = (String) session.getAttribute("initial");
            }

            if (genre != null) session.setAttribute("genre", genre);
            else genre = (String) session.getAttribute("genre");


            int offset = (page - 1) * limit;

            // Base query
            String baseQuery = "SELECT DISTINCT m.id, m.title, m.year, m.director, r.rating, "
                    + "(SELECT GROUP_CONCAT(g.name ORDER BY g.name SEPARATOR ', ') "
                    + "FROM genres_in_movies gm JOIN genres g ON gm.genreId = g.id WHERE gm.movieId = m.id LIMIT 3) AS genres, "
                    + "(SELECT GROUP_CONCAT(g.id ORDER BY g.name SEPARATOR ', ') "
                    + "FROM genres_in_movies gm JOIN genres g ON gm.genreId = g.id WHERE gm.movieId = m.id LIMIT 3) AS genre_ids, "
                    + "(SELECT GROUP_CONCAT(s.name ORDER BY s.name SEPARATOR ', ') "
                    + "FROM stars_in_movies sm JOIN stars s ON sm.starId = s.id WHERE sm.movieId = m.id LIMIT 3) AS stars, "
                    + "(SELECT GROUP_CONCAT(s.id ORDER BY s.name SEPARATOR ', ') "
                    + "FROM stars_in_movies sm JOIN stars s ON sm.starId = s.id WHERE sm.movieId = m.id LIMIT 3) AS star_ids "
                    + "FROM movies m JOIN ratings r ON m.id = r.movieId ";

            StringBuilder whereClause = new StringBuilder();
            List<String> paramList = new ArrayList<>();

            // Add conditions based on search criteria
            if (initial != null && !initial.trim().isEmpty()) {
                if (initial.equals("*")) {
                    // Special case for non-alphanumerical characters
                    whereClause.append((whereClause.length() == 0 ? "WHERE " : " AND ") + "m.title REGEXP ?");
                    paramList.add("^[^a-zA-Z0-9]");
                } else {
                    // Regular case for alphanumerical characters
                    whereClause.append((whereClause.length() == 0 ? "WHERE " : " AND ") + "m.title LIKE ?");
                    paramList.add(initial + "%");
                }
            }
            if (genre != null && !genre.trim().isEmpty()) {
                // Filter movies by genre
                whereClause.append((whereClause.length() == 0 ? "WHERE " : " AND ")
                        + "EXISTS (SELECT 1 FROM genres_in_movies gm WHERE gm.movieId = m.id AND gm.genreId = ?)");
                paramList.add(genre);
            }
            if (title != null && !title.trim().isEmpty()) {
                // Tokenize the title into multiple words
                String[] tokens = title.trim().split("\\s+");
                StringBuilder fullTextSearch = new StringBuilder();
                for (String token : tokens) {
                    fullTextSearch.append("+").append(token).append("* ");
                }
                System.out.println("Generated Full-Text Query: " + fullTextSearch.toString().trim());

                String searchQuery = fullTextSearch.toString().trim();
                whereClause.append((whereClause.length() == 0 ? "WHERE " : " AND "))
                        .append("MATCH(m.title) AGAINST(? IN BOOLEAN MODE) ");
                paramList.add(searchQuery);
            }
            if (year != null && !year.trim().isEmpty()) {
                whereClause.append((whereClause.length() == 0 ? "WHERE " : " AND ") + "m.year = ?");
                paramList.add(year);
            }
            if (director != null && !director.trim().isEmpty()) {
                whereClause.append((whereClause.length() == 0 ? "WHERE " : " AND ") + "m.director LIKE ?");
                paramList.add("%" + director + "%");
            }
            if (starName != null && !starName.trim().isEmpty()) {
                baseQuery += " JOIN stars_in_movies sm ON m.id = sm.movieId "
                        + "JOIN stars s ON sm.starId = s.id ";
                whereClause.append((whereClause.length() == 0 ? "WHERE " : " AND ") + "s.name LIKE ?");
                paramList.add("%" + starName + "%");
            }

            // Sorting
            String orderByClause = " ORDER BY ";
            switch (sort) {
                case "title_asc_rating_asc":
                    orderByClause += "m.title ASC, r.rating ASC ";
                    break;
                case "title_asc_rating_desc":
                    orderByClause += "m.title ASC, r.rating DESC ";
                    break;
                case "title_desc_rating_asc":
                    orderByClause += "m.title DESC, r.rating ASC ";
                    break;
                case "title_desc_rating_desc":
                    orderByClause += "m.title DESC, r.rating DESC ";
                    break;
                case "rating_asc_title_asc":
                    orderByClause += "r.rating ASC, m.title ASC ";
                    break;
                case "rating_asc_title_desc":
                    orderByClause += "r.rating ASC, m.title DESC ";
                    break;
                case "rating_desc_title_asc":
                    orderByClause += "r.rating DESC, m.title ASC ";
                    break;
                case "rating_desc_title_desc":
                    orderByClause += "r.rating DESC, m.title DESC ";
                    break;
                default:
                    // Default sort by rating descending and title ascending if no valid sort option is given
                    orderByClause += "r.rating DESC, m.title ASC ";
                    break;
            }

            String limitClause = " LIMIT ? OFFSET ?";

            String finalQuery = baseQuery + whereClause.toString() + orderByClause + limitClause;

            // Prepare the statement
            PreparedStatement statement = conn.prepareStatement(finalQuery);

            int paramIndex = 1;
            // Set parameters for WHERE clause
            for (String param : paramList) {
                statement.setString(paramIndex++, param);
            }

            // Set parameters for LIMIT and OFFSET
            statement.setInt(paramIndex++, resultsPerPage);
            statement.setInt(paramIndex++, offset);

            // Execute the query
            ResultSet rs = statement.executeQuery();

            // Process the results
            JsonArray jsonArray = new JsonArray();

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

            // Calculate total results for pagination
            String countQuery = "SELECT COUNT(DISTINCT m.id) AS total FROM movies m ";

            if (starName != null && !starName.trim().isEmpty()) {
                countQuery += " JOIN stars_in_movies sm ON m.id = sm.movieId "
                        + "JOIN stars s ON sm.starId = s.id ";
            }

            String finalCountQuery = countQuery + whereClause.toString();

            PreparedStatement countStatement = conn.prepareStatement(finalCountQuery);

            paramIndex = 1;
            for (String param : paramList) {
                countStatement.setString(paramIndex++, param);
            }

            ResultSet countRs = countStatement.executeQuery();
            int totalResults = 0;
            if (countRs.next()) {
                totalResults = countRs.getInt("total");
            }
            int totalPages = (int) Math.ceil((double) totalResults / resultsPerPage);

            rs.close();
            statement.close();
            countRs.close();
            countStatement.close();

            // Create the response object
            JsonObject responseObject = new JsonObject();
            responseObject.addProperty("currentPage", pageNumber);
            responseObject.addProperty("totalPages", totalPages);
            responseObject.add("movies", jsonArray);

            // Write JSON string to output
            out.write(responseObject.toString());
            response.setStatus(200);

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            response.setStatus(500);
        } finally {
            out.close();
        }
    }
}
