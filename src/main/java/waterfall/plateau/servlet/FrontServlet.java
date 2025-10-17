package waterfall.plateau.servlet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class FrontServlet extends HttpServlet {
    private ServletContext servletContext;
    private RequestDispatcher contextDefaultDispatcher;

    @Override
    public void init()
            throws ServletException {
        servletContext = getServletContext();
        contextDefaultDispatcher = servletContext.getNamedDispatcher("default");

        if (contextDefaultDispatcher == null)
            throw new ServletException("The context's default dispatcher cannot be found");
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String servletPath = request.getServletPath();

        if (servletContext.getResource(servletPath) != null)
            contextDefaultDispatcher.forward(request, response);

        else {
            response.setContentType("text/plain;charset=UTF-8");
            try (PrintWriter printWriter = response.getWriter()) {
                printWriter.print(servletPath);
            }
        }
    }
}
