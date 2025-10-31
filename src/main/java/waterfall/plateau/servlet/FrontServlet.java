package waterfall.plateau.servlet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import waterfall.plateau.annotation.Controller;
import waterfall.plateau.annotation.Url;
import waterfall.plateau.util.WaterfallUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FrontServlet extends HttpServlet {
    private ServletContext servletContext;
    private RequestDispatcher contextDefaultDispatcher;
    private Map<String, Method> urlWithMethods;

    @Override
    public void init()
            throws ServletException {
        try {
            loadContextDefaultDispatcher();
            loadUrlWithMethods();
        } catch (IOException | URISyntaxException | ClassNotFoundException e) {
            throw new ServletException(e);
        }
    }

    private void loadContextDefaultDispatcher() {
        servletContext = getServletContext();
        contextDefaultDispatcher = servletContext.getNamedDispatcher("default");

        if (contextDefaultDispatcher == null)
            throw new RuntimeException("The context's default dispatcher cannot be found");
    }

    private void loadUrlWithMethods()
            throws IOException, URISyntaxException, ClassNotFoundException {
        String controllersBasePackage = getInitParameter("controllers-base-package");
        Set<Method> methods = WaterfallUtil.findAnnotatedMethodsInAnnotatedClasses(controllersBasePackage, Url.class, Controller.class);
        urlWithMethods = new HashMap<>();

        for (Method method : methods) {
            Url url = method.getAnnotation(Url.class);
            urlWithMethods.put(url.url(), method);
        }
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
                if (urlWithMethods.containsKey(servletPath))
                    printWriter.print("200 OK: " + servletPath + " " + urlWithMethods.get(servletPath).getName());
                else
                    printWriter.print("404 Not Found: " + servletPath);
            }
        }
    }
}
