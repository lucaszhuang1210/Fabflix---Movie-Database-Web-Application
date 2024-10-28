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
            JsonArray cartItems = requestData.getAsJsonArray("cart_items");

            // Ensure the expiration date is in the correct format YYYY-MM-DD
            String formattedExpirationDate = convertDateToDatabaseFormat(expirationDate);
            if (formattedExpirationDate == null) {
                JsonObject responseObject = new JsonObject();
                responseObject.addProperty("status", "error");
                responseObject.addProperty("message", "Invalid date format. Please use YYYY-MM-DD.");
                out.write(responseObject.toString());
                response.setStatus(400);
                return;
            }

            // Validate the credit card info against the database
            String query = "SELECT c.id FROM customers c JOIN creditcards cc ON c.ccId = cc.id " +
                    "WHERE cc.firstName = ? AND cc.lastName = ? AND cc.id = ? AND cc.expiration = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, cardNumber);
            statement.setString(4, formattedExpirationDate);

            ResultSet rs = statement.executeQuery();
            JsonObject responseObject = new JsonObject();

            if (rs.next()) {
                // Credit card is valid; retrieve the customer ID
                int customerId = rs.getInt("id");

                conn.setAutoCommit(false); // Begin transaction

                // Insert sale for each movie in the cart
                String saleQuery = "INSERT INTO sales (customerId, movieId, saleDate) VALUES (?, ?, NOW())";
                PreparedStatement saleStatement = conn.prepareStatement(saleQuery, Statement.RETURN_GENERATED_KEYS);

                JsonArray orderDetails = new JsonArray();
                double totalPrice = 0.0;

                for (int i = 0; i < cartItems.size(); i++) {
                    JsonObject item = cartItems.get(i).getAsJsonObject();
                    String movieId = item.get("movie_id").getAsString();
                    int quantity = item.get("quantity").getAsInt();
                    double price = item.get("price").getAsDouble();
                    totalPrice += price * quantity;

                    // Insert a separate record for each quantity
                    for (int j = 0; j < quantity; j++) {
                        saleStatement.setInt(1, customerId);
                        saleStatement.setString(2, movieId);
                        saleStatement.addBatch();
                    }

                    // For order confirmation
                    JsonObject orderItem = new JsonObject();
                    orderItem.addProperty("movie_id", movieId);
                    orderItem.addProperty("quantity", quantity);
                    orderItem.addProperty("price", price);
                    orderDetails.add(orderItem);
                }

                // Execute batch and commit transaction
                saleStatement.executeBatch();
                conn.commit();

                // Retrieve the generated sale IDs
                ResultSet saleKeys = saleStatement.getGeneratedKeys();
                JsonArray saleIds = new JsonArray();
                while (saleKeys.next()) {
                    saleIds.add(saleKeys.getInt(1));
                }

                // Prepare the response
                responseObject.addProperty("status", "success");
                responseObject.add("sale_ids", saleIds);
                responseObject.add("order_details", orderDetails);
                responseObject.addProperty("total_price", totalPrice);
            } else {
                responseObject.addProperty("status", "error");
                responseObject.addProperty("message", "Invalid payment information.");
            }

            out.write(responseObject.toString());
            response.setStatus(200);

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

    // Utility method to ensure the date is in the correct format (YYYY-MM-DD)
    private String convertDateToDatabaseFormat(String date) {
        try {
            // Assuming the date input might be in 'YYYY-MM' or 'YYYY-MM-DD' format
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            inputFormat.setLenient(false); // Strict parsing
            return inputFormat.format(inputFormat.parse(date));
        } catch (ParseException e) {
            return null; // Return null if the date format is invalid
        }
    }
}
