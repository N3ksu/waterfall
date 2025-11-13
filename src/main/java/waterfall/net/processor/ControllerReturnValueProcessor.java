package waterfall.net.processor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface ControllerReturnValueProcessor {
    ControllerReturnValueProcessor IMPL = new ControllerReturnValueProcessorImpl();

    void process(HttpServletRequest req, HttpServletResponse res) throws Exception;
}
