package waterfall.servlet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import waterfall.constant.WFC;
import waterfall.net.processor.ControllerReturnValueProcessor;

import java.io.IOException;

@WebServlet("/")
public class FrontServlet extends HttpServlet {
    private ServletContext ctx;
    private RequestDispatcher ctxDefaultDispatcher;
    private ControllerReturnValueProcessor processor;

    @Override
    public void init()
            throws ServletException {
        processor = ControllerReturnValueProcessor.IMPL;
        loadContextDefaultDispatcher();
    }

    private void loadContextDefaultDispatcher() {
        ctx = getServletContext();
        ctxDefaultDispatcher = ctx.getNamedDispatcher(WFC.CTX_DEFAULT_REQ_DISPATCHER_NAME);

        if (ctxDefaultDispatcher == null)
            throw new RuntimeException("The context's default dispatcher cannot be found");
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (ctx.getResource(request.getServletPath()) != null)
            ctxDefaultDispatcher.forward(request, response);
        else {
            try {
                processor.process(request, response);
            } catch (Exception e) {
                throw new ServletException(e);
            }
        }
    }
}
