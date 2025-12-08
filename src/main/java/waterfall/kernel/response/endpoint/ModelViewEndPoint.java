package waterfall.kernel.response.endpoint;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import waterfall.kernel.routing.route.Route;
import waterfall.api.ui.ModelView;

import java.lang.reflect.Method;
import java.util.Map.Entry;

public final class ModelViewEndPoint implements EndPoint {
    @Override
    public void forward(final HttpServletRequest req, final HttpServletResponse res, final Route route, final Object controller, final Object[] args)
            throws Exception {
        final Method method = route.getAction();
        final ModelView modelView = (ModelView) method.invoke(controller, args);
        final RequestDispatcher reqDispatcher = req.getRequestDispatcher(modelView.getView());

        if (reqDispatcher == null) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "The view set for the ModelView inside " + method.getName() + " inside " + controller.getClass().getName() + " cannot be found");
            return;
        }

        for (final Entry<String, Object> entry : modelView.getAttributes().entrySet())
            req.setAttribute(entry.getKey(), entry.getValue());

        reqDispatcher.forward(req, res);
    }
}
