package waterfall.net.handler.http;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import waterfall.view.ModelView;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map.Entry;

public class ModelViewHandler implements HttpHandler {
    @Override
    public void handle(String url, Method action, Object controller, HttpServletRequest req, HttpServletResponse res)
            throws Exception {
        try {
            ModelView modelView = (ModelView) action.invoke(controller);

            String view = modelView.getView();
            RequestDispatcher reqDispatcher = req.getRequestDispatcher(view);

            if (reqDispatcher == null) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND,
                        "The view set for the ModelView inside " + action.getName() + " inside " + controller.getClass().getName() + " cannot be found");
                return;
            }

            for (Entry<String, Object> entry : modelView.getAttributes().entrySet())
                req.setAttribute(entry.getKey(), entry.getValue());

            reqDispatcher.forward(req, res);
        } catch (IllegalAccessException | InvocationTargetException | IOException | ServletException e) {
            throw new Exception(e);
        }
    }
}
