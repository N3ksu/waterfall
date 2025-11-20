package waterfall.core.target;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import waterfall.bootstrap.net.route.Route;
import waterfall.component.ui.ModelView;

import java.lang.reflect.Method;
import java.util.Map.Entry;

public class ModelViewTarget implements ITarget {
    @Override
    public void land(Route route, Object controller, HttpServletRequest req, HttpServletResponse res)
            throws Exception {
        Method method = route.getMethod();
        ModelView modelView = (ModelView) method.invoke(controller);

        String view = modelView.getView();
        RequestDispatcher reqDispatcher = req.getRequestDispatcher(view);

        if (reqDispatcher == null) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "The view set for the ModelView inside " + method.getName() + " inside " + controller.getClass().getName() + " cannot be found");
            return;
        }

        for (Entry<String, Object> entry : modelView.getAttributes().entrySet())
            req.setAttribute(entry.getKey(), entry.getValue());

        reqDispatcher.forward(req, res);
    }
}
