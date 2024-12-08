import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(name = "AddMovieServlet", urlPatterns = "/api/add-movie")
public class AddMovieServlet extends HttpServlet {
    private DataSource dataSource;

    @Override
    public void init() throws ServletException {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/ReadOnly");
        } catch (NamingException e) {
            throw new ServletException("Cannot retrieve database connection", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String title = request.getParameter("title");
        String year = request.getParameter("year");
        String director = request.getParameter("director");
        String starName = request.getParameter("star_name");
        String starBirthYear = request.getParameter("star_birth_year");
        String genreName = request.getParameter("genre_name");

        try (Connection conn = dataSource.getConnection()) {
            // Check for duplicate movies
            String duplicateCheckQuery = "SELECT id FROM movies WHERE title = ? AND year = ? AND director = ?";
            try (PreparedStatement ps = conn.prepareStatement(duplicateCheckQuery)) {
                ps.setString(1, title);
                ps.setString(2, year);
                ps.setString(3, director);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    JsonObject responseJson = new JsonObject();
                    responseJson.addProperty("status", "error");
                    responseJson.addProperty("message", "Movie already exists.");
                    out.write(responseJson.toString());
                    return;
                }
            }

            // Call stored procedure to add movie and get generated IDs
            String callProcedure = "{CALL add_movie(?, ?, ?, ?, ?, ?)}";
            try (CallableStatement cs = conn.prepareCall(callProcedure)) {
                cs.setString(1, title);
                cs.setString(2, year);
                cs.setString(3, director);
                cs.setString(4, starName);
                cs.setString(5, starBirthYear.isEmpty() ? null : starBirthYear);
                cs.setString(6, genreName);

                boolean hasResults = cs.execute();

                // Retrieve generated IDs from the ResultSet
                if (hasResults) {
                    try (ResultSet rs = cs.getResultSet()) {
                        if (rs.next()) {
                            String movieId = rs.getString("movie_id");
                            int genreId = rs.getInt("genre_id");
                            String starId = rs.getString("star_id");

                            JsonObject responseJson = new JsonObject();
                            responseJson.addProperty("status", "success");
                            responseJson.addProperty("message", "Movie added successfully with Movie ID: " + movieId + ", Genre ID: " + genreId + ", Star ID: " + starId);
                            responseJson.addProperty("movie_id", movieId);
                            responseJson.addProperty("genre_id", genreId);
                            responseJson.addProperty("star_id", starId);
                            out.write(responseJson.toString());
                        }
                    }
                }
            }

        } catch (SQLException e) {
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("status", "error");
            responseJson.addProperty("message", "Error adding movie: " + e.getMessage());
            out.write(responseJson.toString());
        } finally {
            out.close();
        }
    }
}


