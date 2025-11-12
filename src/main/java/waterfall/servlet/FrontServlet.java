package waterfall.servlet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import waterfall.constant.WFConst;
import waterfall.net.handler.ServletHandler;

import java.io.IOException;

@WebServlet("/")
public class FrontServlet extends HttpServlet {
    private ServletContext ctx;
    private RequestDispatcher ctxDefaultDispatcher;
    private ServletHandler handler;

    @Override
    public void init()
            throws ServletException {
        handler = ServletHandler.IMPL;
        loadContextDefaultDispatcher();
    }

    private void loadContextDefaultDispatcher() {
        ctx = getServletContext();
        ctxDefaultDispatcher = ctx.getNamedDispatcher(WFConst.CTX_DEFAULT_REQ_DISPATCHER_NAME);

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
                handler.handle(request, response);
            } catch (Exception e) {
                throw new ServletException(e);
            }
        }
    }
}
