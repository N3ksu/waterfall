package waterfall.net.processor.response;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import waterfall.net.processor.response.contract.ResponseProcessor;

import java.io.PrintWriter;
import java.lang.reflect.Method;

public class NotManagedProcessor implements ResponseProcessor {
    @Override
    public void process(String url , Method action, Object controller, HttpServletRequest req, HttpServletResponse res) throws Exception {
        try (PrintWriter printWriter = res.getWriter()) {
            res.setContentType("text/plain;charset=UTF-8");
            printWriter.print("404 Not Managed: " + url);
        }
    }
}
