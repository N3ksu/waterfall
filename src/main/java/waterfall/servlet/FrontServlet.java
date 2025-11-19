package waterfall.servlet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import waterfall.constant.WFC;
import waterfall.net.processor.contract.Processor;

import java.io.IOException;

@WebServlet(WFC.FRONT_SERVLET_URL_MAPPING)
public class FrontServlet extends HttpServlet {
    private ServletContext ctx;
    private RequestDispatcher ctxDefaultDispatcher;
    private Processor processor;

    @Override
    public void init()
            throws ServletException {
        processor = Processor.IMPL;
        loadContextDefaultDispatcher();
    }

    private void loadContextDefaultDispatcher() {
        ctx = getServletContext();
        ctxDefaultDispatcher = ctx.getNamedDispatcher(WFC.CTX_DEFAULT_REQ_DISPATCHER_NAME);

        if (ctxDefaultDispatcher == null)
            throw new RuntimeException("The context's default dispatcher cannot be found");
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String path = req.getServletPath();

        if (ctx.getResource(path) != null)
            ctxDefaultDispatcher.forward(req, res);

        else {
            try {
                processor.process(req, res);
            } catch (Exception e) {
                throw new ServletException(e);
            }
        }
    }
}
