package waterfall.servlet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import waterfall.annotation.Controller;
import waterfall.annotation.Url;
import waterfall.util.ReflectionUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FrontServlet extends HttpServlet {
    private ServletContext servletContext;
    private RequestDispatcher contextDefaultDispatcher;
    private Map<String, Method> urlsWithMethods;

    @Override
    public void init()
            throws ServletException {
        try {
            loadContextDefaultDispatcher();
            loadUrlsWithMethods();
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

    private void loadUrlsWithMethods()
            throws IOException, URISyntaxException, ClassNotFoundException {
        String controllersBasePackage = getInitParameter("controllers-base-package");
        Set<Method> methods = ReflectionUtil.findAnnotatedMethodsInAnnotatedClasses(controllersBasePackage, Url.class, Controller.class);
        urlsWithMethods = new HashMap<>();

        for (Method method : methods) {
            Url url = method.getAnnotation(Url.class);
            urlsWithMethods.put(url.url(), method);
        }
        
        ServletContext servletContext = getServletContext();
        servletContext.setAttribute("urlsWithMethods", urlsWithMethods);
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
                if (urlsWithMethods.containsKey(servletPath)) {
                    Method method = urlsWithMethods.get(servletPath);

                    Class<?> returnType = method.getReturnType();
                    Class<?> methodClass = method.getDeclaringClass();

                    // The constructor should be public
                    Constructor<?> controllerConstructor = methodClass.getDeclaredConstructor();
                    Object controller = controllerConstructor.newInstance();

                    if (returnType.equals(String.class)) {
                        printWriter.print("200 (String.class):" + method.invoke(controller).toString());
                    } else {
                        method.invoke(controller);
                    }
                }
                else printWriter.print("404: " + servletPath);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new ServletException(e);
            }
        }
    }
}
