package waterfall.servlet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import waterfall.constant.WaterFallConstant;
import waterfall.dispatcher.IWaterFallDispatcher;

import java.io.IOException;

@WebServlet(WaterFallConstant.FRONT_SERVLET_URL_MAPPING)
public class FrontServlet extends HttpServlet {
    private RequestDispatcher ctxDefaultDispatcher;
    private IWaterFallDispatcher waterFallDispatcher;

    @Override
    public void init()
            throws ServletException {
        waterFallDispatcher = IWaterFallDispatcher.IMPL;

        ctxDefaultDispatcher = getServletContext()
                .getNamedDispatcher(WaterFallConstant.CTX_DEFAULT_REQ_DISPATCHER_NAME);

        if (ctxDefaultDispatcher == null)
            throw new ServletException("The context's default dispatcher cannot be found");
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String path = req.getServletPath();

        if (getServletContext().getResource(path) != null)
            ctxDefaultDispatcher.forward(req, res);

        else {
            try {
                waterFallDispatcher.forward(req, res);
            } catch (Exception e) {
                throw new ServletException(e);
            }
        }
    }
}
