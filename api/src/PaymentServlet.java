import com.google.gson.JsonArray;
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
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        System.out.println("before doPost");
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
            System.out.println("before sql");
            JsonObject requestData = new com.google.gson.JsonParser().parse(jsonBody.toString()).getAsJsonObject();
            String firstName = requestData.get("first_name").getAsString();
            String lastName = requestData.get("last_name").getAsString();
            String cardNumber = requestData.get("card_number").getAsString();
            String expirationDate = requestData.get("expiration_date").getAsString();

            System.out.println("before validate credit card");
            // Validate the credit card info against the database
            String query = "SELECT c.id FROM customers c JOIN creditcards cc ON c.ccId = cc.id " +
                    "WHERE cc.firstName = ? AND cc.lastName = ? AND cc.id = ? AND cc.expiration = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, cardNumber);
            statement.setString(4, expirationDate);
            System.out.println("before executeQuery");
            ResultSet rs = statement.executeQuery();
            System.out.println("after executeQuery");
            JsonObject responseObject = new JsonObject();

            System.out.println("rs.next()");
            if (rs.next()) {
                // Credit card is valid; retrieve the customer ID
                int customerId = rs.getInt("id");

                conn.setAutoCommit(false); // Begin transaction

                System.out.println("2nd sql");
                // Insert sale for each movie in the cart

                //String saleQuery = "INSERT INTO sales (customerId, movieId, saleDate) VALUES (?, ?, CURNOW())";
                String saleQuery = "INSERT INTO sales (customerId, movieId, saleDate) VALUES (?, ?, new Date().getTime()))";
                PreparedStatement saleStatement = conn.prepareStatement(saleQuery, Statement.RETURN_GENERATED_KEYS);

                responseObject.addProperty("status", "success");
                response.setStatus(200);
            } else {
                responseObject.addProperty("status", "error");
                responseObject.addProperty("message", "Invalid payment information.");
            }

            out.write(responseObject.toString());

        } catch (Exception e) {
            try {
                response.setStatus(500);
            } catch (Exception rollbackEx) {
                rollbackEx.printStackTrace();
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", "error");
            jsonObject.addProperty("message", e.getMessage());
            out.write(jsonObject.toString());
        } finally {
            out.close();
        }
    }

}
