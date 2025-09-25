package waterfall.plateau.servlet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/")
public class FrontServlet extends HttpServlet {
    private RequestDispatcher contextDefaultDispatcher;

    @Override
    public void init() throws ServletException {
        contextDefaultDispatcher = getServletContext().getNamedDispatcher("default");
        if (contextDefaultDispatcher == null)
            throw new ServletException("The default dispatcher cannot be found");
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String relativePath = getContextRelativePath(request);

        if (getServletContext().getResource(relativePath) != null) contextDefaultDispatcher.forward(request, response);
        else {
            response.setContentType("text/plain;charset=UTF-8");
            try (PrintWriter printWriter = response.getWriter()) {
                printWriter.print(relativePath);
            }
        }
    }

    // TODO security concerns around path traversal with request containing ".." or/and "//"
    private String getContextRelativePath(HttpServletRequest request) {
        String servletPath = request.getServletPath(); // "" for "/"
        String uncheckedPathInfo = request.getPathInfo(); // null for "/"
        String pathInfo = uncheckedPathInfo == null ? "" : uncheckedPathInfo;
        String relativePath = servletPath + pathInfo;
        return relativePath.isEmpty() ? "/" : relativePath;
    }
}
