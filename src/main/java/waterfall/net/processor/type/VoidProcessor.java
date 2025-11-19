package waterfall.net.processor.type;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import waterfall.net.processor.type.contract.TypeProcessor;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class VoidProcessor implements TypeProcessor {
    @Override
    public void process(Method action, Object controller, HttpServletRequest req, HttpServletResponse res) throws Exception {
        try (PrintWriter out = res.getWriter()) {
            res.setContentType("text/plain;charset=UTF-8");
            out.print("200 Invoke: " + action.getName());
            action.invoke(controller);
        } catch (IOException | IllegalAccessException | InvocationTargetException e) {
            throw new Exception(e);
        }
    }
}
