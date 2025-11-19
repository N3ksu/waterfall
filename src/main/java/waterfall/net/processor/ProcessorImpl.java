package waterfall.net.processor;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import waterfall.net.Router;
import waterfall.net.processor.type.contract.TypeProcessor;
import waterfall.net.processor.contract.Processor;
import waterfall.view.ModelView;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class ProcessorImpl implements Processor {
    @Override
    public void process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String path = req.getServletPath();
        ServletContext ctx = req.getServletContext();
        Map<String, Method> routes = Router.getRoutes(ctx);

        try {
            if (!routes.containsKey(path)) {
                TypeProcessor.NOT_MANAGED.process(null, null, req, res);
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
                TypeProcessor.MODEL_VIEW.process(method, controller, req, res);
            } else if (returnType.equals(String.class)) {
                TypeProcessor.STRING.process(method, controller, req, res);
            } else {
               TypeProcessor.VOID.process(method, controller, req, res);
            }

            // TODO what if the return type cannot be used by the framework

        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException |
                 IOException | ServletException e) {
            throw new ServletException(e);
        }
    }
}
