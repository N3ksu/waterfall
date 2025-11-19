package waterfall.net.processor.type;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import waterfall.net.processor.type.contract.TypeProcessor;
import waterfall.view.ModelView;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map.Entry;

public class ModelViewProcessor implements TypeProcessor {
    @Override
    public void process(Method action, Object controller, HttpServletRequest req, HttpServletResponse res)
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
