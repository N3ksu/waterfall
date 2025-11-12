package waterfall.net.handler.http;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.lang.reflect.Method;

public class UrlNotFoundHandler implements HttpHandler {
    @Override
    public void handle(String url , Method action, Object controller, HttpServletRequest req, HttpServletResponse res) throws Exception {
        try (PrintWriter printWriter = res.getWriter()) {
            res.setContentType("text/plain;charset=UTF-8");
            printWriter.print("404 Not Found: " + url);
        }
    }
}
