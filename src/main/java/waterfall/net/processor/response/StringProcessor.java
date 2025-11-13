package waterfall.net.processor.response;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import waterfall.net.processor.response.contract.ResponseProcessor;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class StringProcessor implements ResponseProcessor {
    @Override
    public void process(String url, Method action, Object controller, HttpServletRequest req, HttpServletResponse res) throws Exception {
        try (PrintWriter printWriter = res.getWriter()) {
            res.setContentType("text/plain;charset=UTF-8");
            String actionResult = (String) action.invoke(controller);
            printWriter.print("200 String: " + actionResult);
        } catch (IOException | IllegalAccessException | InvocationTargetException e) {
            throw new Exception(e);
        }
    }
}
