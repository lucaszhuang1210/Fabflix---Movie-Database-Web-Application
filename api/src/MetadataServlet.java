import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.naming.InitialContext;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(name = "MetadataServlet", urlPatterns = "/api/metadata")
public class MetadataServlet extends HttpServlet {

    private DataSource dataSource;

    @Override
    public void init() throws ServletException {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (Exception e) {
            throw new ServletException("Unable to connect to database", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String[] tables = {"creditcards", "customers", "employees", "movies", "stars", "stars_in_movies",
                "genres", "genres_in_movies", "sales", "ratings"};

        try (Connection conn = dataSource.getConnection()) {
            JsonArray tablesJson = new JsonArray();
            for (String table : tables) {
                JsonObject tableJson = new JsonObject();
                tableJson.addProperty("tableName", table);
                System.out.println("current table:" + table);

                String query = "SELECT COLUMN_NAME, DATA_TYPE " +
                        "FROM INFORMATION_SCHEMA.COLUMNS " +
                        "WHERE TABLE_SCHEMA = 'moviedb' AND TABLE_NAME = ? ";
                try (PreparedStatement statement = conn.prepareStatement(query)) {
                    statement.setString(1, table);
                    try (ResultSet rs = statement.executeQuery()) {
                        JsonArray columnsJson = new JsonArray();

                        while (rs.next()) {
                            JsonObject columnJson = new JsonObject();
                            String columnname = rs.getString("COLUMN_NAME");
                            String datatype = rs.getString("DATA_TYPE");
                            columnJson.addProperty("columnName", columnname);
                            columnJson.addProperty("dataType", datatype);

                            System.out.println("columnname"+ columnname);
                            System.out.println("datatype" + datatype);
                            columnsJson.add(columnJson);
                        }
                        tableJson.add("columns", columnsJson);
                    }
                }
                tablesJson.add(tableJson);
            }
            out.print(tablesJson.toString());
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("error", "Error fetching metadata: " + e.getMessage());
            out.print(error.toString());
        } finally {
            out.close();
        }
    }
}



