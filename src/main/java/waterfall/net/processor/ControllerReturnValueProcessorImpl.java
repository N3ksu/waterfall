package waterfall.net.processor;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import waterfall.net.Router;
import waterfall.net.processor.response.contract.ResponseProcessor;
import waterfall.view.ModelView;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class ControllerReturnValueProcessorImpl implements ControllerReturnValueProcessor {
    @Override
    public void process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ServletContext ctx = req.getServletContext();
        String path = req.getServletPath();
        Map<String, Method> routes = Router.getRoutes(ctx);

        try (PrintWriter printWriter = res.getWriter()) {
            if (!routes.containsKey(path)) {
                ResponseProcessor.NOT_MANAGED.process(path, null, null, req, res);
                return;
            }

            // Getting an instance of the controller
            Method method = routes.get(path);
            Class<?> controllerClass = method.getDeclaringClass();
            Constructor<?> controllerConstructor = controllerClass.getDeclaredConstructor();
            controllerConstructor.setAccessible(true); // ! making the constructor public
            Object controller = controllerConstructor.newInstance();

            Class<?> returnType = method.getReturnType();

            // TODO we can make those if statement as a foreach loop
            if (returnType.equals(ModelView.class)) {
                ResponseProcessor.MODEL_VIEW.process(path, method, controller, req, res);
            } else if (returnType.equals(String.class)) {
                ResponseProcessor.STRING.process(path, method, controller, req, res);
            } else {
               ResponseProcessor.VOID.process(path, method, controller, req, res);
            }

            // TODO what if the return type cannot be used by the framework

        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException |
                 IOException | ServletException e) {
            throw new ServletException(e);
        }
    }
}
