package waterfall.plateau.servlet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

@WebServlet("/")
public class FrontServlet extends HttpServlet {
    private RequestDispatcher defaultDispatcher;

    @Override
    public void init() {
        defaultDispatcher = findDefaultDispatcher()
                .orElseThrow(() -> new RuntimeException("The default dispatcher cannot be found"));
    }

    private Optional<RequestDispatcher> findDefaultDispatcher() {
        return Optional.ofNullable(getServletContext().getNamedDispatcher("default"));
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String resourcePath = getResourcePath(request);
        Optional<URL> optionalURL = findResource(resourcePath);

        if (optionalURL.isPresent()) {
            delegateToDefaultDispatcher(request, response);
        } else {
            handle(resourcePath, response);
        }
    }

    private void handle(String resourcePath, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            out.print(resourcePath);
        }
    }

    private void delegateToDefaultDispatcher(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        defaultDispatcher.forward(request, response);
    }

    private Optional<URL> findResource(String resourcePath) throws MalformedURLException {
        return Optional.ofNullable(getServletContext().getResource(resourcePath));
    }

    private String getResourcePath(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String resourcePath = requestURI.substring(contextPath.length());

        return resourcePath.isEmpty() ? "/" : resourcePath;
    }
}
