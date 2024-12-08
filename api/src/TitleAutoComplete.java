import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
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

@WebServlet("/autocomplete")
public class TitleAutoComplete extends HttpServlet {
    private DataSource dataSource;

    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/ReadOnly");
        } catch (NamingException e) {
            throw new ServletException("Unable to initialize data source", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String title = request.getParameter("title"); // The query parameter from the frontend
        if (title == null || title.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Prepare the full-text search query
        String[] tokens = title.trim().split("\\s+");
        StringBuilder fullTextSearch = new StringBuilder();
        for (String token : tokens) {
            fullTextSearch.append("+").append(token).append("* ");
        }
        String searchQuery = fullTextSearch.toString().trim();

        // Query the database
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT id, title FROM movies WHERE MATCH(title) AGAINST(? IN BOOLEAN MODE) LIMIT 10";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, searchQuery);

                try (ResultSet rs = stmt.executeQuery()) {
                    // Convert results to JSON format
                    JsonArray jsonArray = new JsonArray();

                    while (rs.next()) {
                        String movieId = rs.getString("id");
                        String movieTitle = rs.getString("title");
                        jsonArray.add(generateJsonObject(movieId, movieTitle));
                    }

                    // Write JSON response
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");

                    // Convert to properly formatted JSON string
                    response.getWriter().write(jsonArray.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // Generate a JSON object for each movie
    private static JsonObject generateJsonObject(String movieId, String movieTitle) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", movieTitle);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("movieID", movieId);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }
}