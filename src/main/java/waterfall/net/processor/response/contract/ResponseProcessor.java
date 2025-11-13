package waterfall.net.processor.response.contract;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import waterfall.net.processor.response.ModelViewProcessor;
import waterfall.net.processor.response.NotManagedProcessor;
import waterfall.net.processor.response.StringProcessor;
import waterfall.net.processor.response.VoidProcessor;

import java.lang.reflect.Method;

public interface ResponseProcessor {
    ResponseProcessor VOID = new VoidProcessor();
    ResponseProcessor STRING = new StringProcessor();
    ResponseProcessor MODEL_VIEW = new ModelViewProcessor();
    ResponseProcessor NOT_MANAGED = new NotManagedProcessor();

    void process(String url, Method action, Object controller, HttpServletRequest req, HttpServletResponse res) throws Exception;
}
