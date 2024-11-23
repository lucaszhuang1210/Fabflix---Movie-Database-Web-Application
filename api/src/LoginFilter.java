import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter(filterName = "LoginFilter", urlPatterns = "/*")
public class LoginFilter implements Filter {
    private final ArrayList<String> allowedURIs = new ArrayList<>();

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        System.out.println("LoginFilter: " + httpRequest.getRequestURI());

        // Check if this URL is allowed to access without logging in
        if (this.isUrlAllowedWithoutLogin(httpRequest.getRequestURI())) {
            // Keep default action: pass along the filter chain
            chain.doFilter(request, response);
            return;
        }

        //String url = httpRequest.getContextPath();
        String url = httpRequest.getRequestURI();
        if(httpRequest.getSession().getAttribute("employee") == null && (url.contains("/_dashboard"))){
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/_dashboard/login.html");
        }

        // Redirect to login page if the "user" attribute doesn't exist in session
        else if (httpRequest.getSession().getAttribute("user") == null && !(url.contains("/_dashboard"))) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.html");
        } else {
            chain.doFilter(request, response);
        }

    }

    private boolean isUrlAllowedWithoutLogin(String requestURI) {
        /*
         Setup your own rules here to allow accessing some resources without logging in
         Always allow your own login related requests(html, js, servlet, etc..)
         You might also want to allow some CSS files, etc..
         */
        return allowedURIs.stream().anyMatch(requestURI.toLowerCase()::endsWith);
    }

    public void init(FilterConfig fConfig) {
        allowedURIs.add("login.html");
        allowedURIs.add("login.js");
        allowedURIs.add("api/login");
        allowedURIs.add("/api/_dashboard-login");

        allowedURIs.add("/styles.css");
        allowedURIs.add("/api/metadata");
        allowedURIs.add("/api/add-movie");
        allowedURIs.add("/api/add-a-star");

        allowedURIs.add("/_dashboard/styles.css");
    }

    public void destroy() {
        // ignored.
    }

}
