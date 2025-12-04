package waterfall.kernel.dispatcher.destination;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import waterfall.kernel.routing.route.Route;
import waterfall.api.ui.ModelView;

import java.lang.reflect.Method;
import java.util.Map.Entry;

public class ModelViewDestination implements Destination {
    @Override
    public void forward(Route route, Object[] args, Object controller, HttpServletRequest req, HttpServletResponse res)
            throws Exception {
        Method method = route.getMethod();
        method.setAccessible(true);

        ModelView modelView = (ModelView) method.invoke(controller, args);

        RequestDispatcher reqDispatcher = req.getRequestDispatcher(modelView.getView());

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
