package waterfall.net.handler.http;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.lang.reflect.Method;

public interface HttpHandler {
    HttpHandler VOID = new VoidHandler();
    HttpHandler STRING = new StringHandler();
    HttpHandler MODEL_VIEW = new ModelViewHandler();
    HttpHandler NOT_FOUND = new UrlNotFoundHandler();

    public void handle(String url, Method action, Object controller, HttpServletRequest req, HttpServletResponse res) throws Exception;
}
