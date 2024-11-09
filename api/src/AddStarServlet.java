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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(name = "AddStarServlet", urlPatterns = "/api/add-a-star")
public class AddStarServlet extends HttpServlet {
    private DataSource dataSource;

    @Override
    public void init() throws ServletException {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            throw new ServletException("Cannot retrieve database connection", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String starName = request.getParameter("starName");
        String birthYearParam = request.getParameter("birthYear");

        System.out.println("Received request to add star with name: " + starName + " and birth year: " + birthYearParam);

        if (starName == null || starName.trim().isEmpty()) {
            System.err.println("Error: Star name is required but was not provided.");
            JsonObject errorResponse = new JsonObject();
            errorResponse.addProperty("status", "error");
            errorResponse.addProperty("message", "Star name is required.");
            out.write(errorResponse.toString());
            return;
        }

        Integer birthYear = null;
        if (birthYearParam != null && !birthYearParam.isEmpty()) {
            try {
                birthYear = Integer.parseInt(birthYearParam);
                System.out.println("Parsed birth year: " + birthYear);
            } catch (NumberFormatException e) {
                System.err.println("Error parsing birth year: " + e.getMessage());
                JsonObject errorResponse = new JsonObject();
                errorResponse.addProperty("status", "error");
                errorResponse.addProperty("message", "Invalid birth year format.");
                out.write(errorResponse.toString());
                return;
            }
        }

        try (Connection conn = dataSource.getConnection()) {
            System.out.println("Database connection established.");

            // Generate a new star ID by finding the highest current ID and incrementing it
            String newStarId = generateNewStarId(conn);
            System.out.println("Generated new star ID: " + newStarId);

            // Insert the new star into the stars table
            String query = "INSERT INTO stars (id, name, birthYear) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, newStarId);
                ps.setString(2, starName);
                if (birthYear != null) {
                    ps.setInt(3, birthYear);
                } else {
                    ps.setNull(3, java.sql.Types.INTEGER);
                }

                int rowsAffected = ps.executeUpdate();
                System.out.println("Rows affected by insert: " + rowsAffected);

                JsonObject successResponse = new JsonObject();
                successResponse.addProperty("status", "success");
                successResponse.addProperty("message", "Star added successfully with ID: " + newStarId);
                successResponse.addProperty("starId", newStarId); // Include the generated star ID
                out.write(successResponse.toString());
            }
        } catch (SQLException e) {
            System.err.println("SQL error during star insertion: " + e.getMessage());
            JsonObject errorResponse = new JsonObject();
            errorResponse.addProperty("status", "error");
            errorResponse.addProperty("message", "Error inserting star: " + e.getMessage());
            out.write(errorResponse.toString());
        } finally {
            out.close();
            System.out.println("Response sent to client and PrintWriter closed.");
        }
    }

    private String generateNewStarId(Connection conn) throws SQLException {
        System.out.println("Generating new star ID...");
        String query = "SELECT MAX(CAST(SUBSTRING(id, 3) AS UNSIGNED)) AS max_id FROM stars";
        try (PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int maxId = rs.getInt("max_id");
                System.out.println("Max ID in stars table: " + maxId);
                return "nm" + (maxId + 1); // e.g., if maxId is 123, new ID is "nm124"
            } else {
                System.out.println("No existing records found. Starting ID from nm1.");
                return "nm1";
            }
        }
    }
}

