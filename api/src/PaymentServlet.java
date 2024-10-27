import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {
    private DataSource dataSource;

    public void init() {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        StringBuilder jsonBody = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                jsonBody.append(line);
            }
        }

        try (Connection conn = dataSource.getConnection()) {
            JsonObject requestData = new com.google.gson.JsonParser().parse(jsonBody.toString()).getAsJsonObject();
            String firstName = requestData.get("first_name").getAsString();
            String lastName = requestData.get("last_name").getAsString();
            String cardNumber = requestData.get("card_number").getAsString();
            String expirationDate = requestData.get("expiration_date").getAsString();

            // Validate the credit card info
            String query = "SELECT * FROM credit_cards WHERE first_name = ? AND last_name = ? AND card_number = ? AND expiration_date = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, cardNumber);
            statement.setString(4, expirationDate);

            ResultSet rs = statement.executeQuery();
            JsonObject responseObject = new JsonObject();

            if (rs.next()) {
                responseObject.addProperty("status", "success");
                // Record transaction in "sales" table (not implemented here)
            } else {
                responseObject.addProperty("status", "error");
                responseObject.addProperty("message", "Invalid payment information.");
            }

            out.write(responseObject.toString());
            response.setStatus(200);

        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", "error");
            jsonObject.addProperty("message", e.getMessage());
            out.write(jsonObject.toString());
            response.setStatus(500);
        } finally {
            out.close();
        }
    }
}
