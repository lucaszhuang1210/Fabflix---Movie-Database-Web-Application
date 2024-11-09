import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
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
import java.sql.Statement;

import org.jasypt.util.password.StrongPasswordEncryptor;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        JsonObject responseJsonObject = new JsonObject();

        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);

        // Verify reCAPTCHA
        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e) {
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", "reCAPTCHA VERIFICATION FAILED");
            out.write(responseJsonObject.toString());
            return;
        }

        // Retrieve parameter username and password from url request.
        String costomer_username = request.getParameter("username");
        String costomer_password = request.getParameter("password");

        // The log message can be found in localhost log
        request.getServletContext().log("getting username: " + costomer_username);
        request.getServletContext().log("getting userpassword: " + costomer_password);

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT email, password FROM customers WHERE email = ?;";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the username we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, costomer_username);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String encryptedPassword = rs.getString("password");
                System.out.println("encryptedPassword" + encryptedPassword);
                boolean success = new StrongPasswordEncryptor().checkPassword(costomer_password, encryptedPassword);
                if(success){
                    // set this user into the session
                    request.getSession().setAttribute("user", new User(costomer_username));
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");
                }else {
                    responseJsonObject.addProperty("status", "fail");
                    // Log to localhost log
                    request.getServletContext().log("Login failed");
                    responseJsonObject.addProperty("message", "incorrect password");
                }
            } else {
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("Login failed");
                responseJsonObject.addProperty("message", "user " + costomer_username + " does not exist");

            }
            out.write(responseJsonObject.toString());

        } catch (Exception e) {
            // Set HTTP status code 500 for internal server error
            response.setStatus(500);
            // Write error message JSON object to output
            responseJsonObject.addProperty("errorMessage", e.getMessage());
            out.write(responseJsonObject.toString());
            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Login fail
            responseJsonObject.addProperty("status", "fail");
            request.getServletContext().log("Login failed");
        } finally {
            out.close();
        }
    }
}