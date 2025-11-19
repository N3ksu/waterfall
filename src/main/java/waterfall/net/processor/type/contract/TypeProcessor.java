package waterfall.net.processor.type.contract;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import waterfall.net.processor.type.ModelViewProcessor;
import waterfall.net.processor.type.NotManagedProcessor;
import waterfall.net.processor.type.StringProcessor;
import waterfall.net.processor.type.VoidProcessor;

import java.lang.reflect.Method;

public interface TypeProcessor {
    TypeProcessor VOID = new VoidProcessor();
    TypeProcessor STRING = new StringProcessor();
    TypeProcessor MODEL_VIEW = new ModelViewProcessor();
    TypeProcessor NOT_MANAGED = new NotManagedProcessor();

    void process(Method action, Object controller, HttpServletRequest req, HttpServletResponse res) throws Exception;
}
