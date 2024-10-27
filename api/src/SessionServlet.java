import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "SessionServlet", urlPatterns = "/api/session/*")
public class SessionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Handles POST requests to save the session state.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();

        // Save parameters to session
        session.setAttribute("title", request.getParameter("title"));
        session.setAttribute("year", request.getParameter("year"));
        session.setAttribute("director", request.getParameter("director"));
        session.setAttribute("star", request.getParameter("star"));
        session.setAttribute("sort", request.getParameter("sort"));
        session.setAttribute("page", Integer.parseInt(request.getParameter("page")));
        session.setAttribute("limit", Integer.parseInt(request.getParameter("limit")));

        response.setStatus(200);
    }

    /**
     * Handles GET requests to load the session state.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        JsonObject savedState = new JsonObject();

        // Retrieve saved session state
        savedState.addProperty("title", (String) session.getAttribute("title"));
        savedState.addProperty("year", (String) session.getAttribute("year"));
        savedState.addProperty("director", (String) session.getAttribute("director"));
        savedState.addProperty("star", (String) session.getAttribute("star"));
        savedState.addProperty("sort", (String) session.getAttribute("sort"));
        savedState.addProperty("page", (Integer) session.getAttribute("page"));
        savedState.addProperty("limit", (Integer) session.getAttribute("limit"));

        // Write JSON response
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.write(savedState.toString());
        out.close();
    }
}

