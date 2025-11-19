package waterfall.net.processor.contract;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import waterfall.net.processor.ProcessorImpl;

public interface Processor {
    Processor IMPL = new ProcessorImpl();

    void process(HttpServletRequest req, HttpServletResponse res) throws Exception;
}
