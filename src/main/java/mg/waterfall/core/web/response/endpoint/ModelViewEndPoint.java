package mg.waterfall.core.web.response.endpoint;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.waterfall.core.web.routing.route.Route;
import mg.waterfall.api.ui.ModelView;

import java.lang.reflect.Method;
import java.util.Map.Entry;

public final class ModelViewEndPoint implements EndPoint {
    @Override
    public void forward(HttpServletRequest request, HttpServletResponse response, Route route, Object controller, Object[] args)
            throws Exception {
        Method method = route.getAction();
        ModelView modelView = (ModelView) method.invoke(controller, args);
        RequestDispatcher reqDispatcher = request.getRequestDispatcher(modelView.getView());

        if (reqDispatcher == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "The view set for the ModelView inside %s inside %s cannot be found"
                            .formatted(method.getName(), controller.getClass().getName()));
            return;
        }

        for (Entry<String, Object> entry : modelView.getAttributes().entrySet())
            request.setAttribute(entry.getKey(), entry.getValue());

        reqDispatcher.forward(request, response);
    }
}
