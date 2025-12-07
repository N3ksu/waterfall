package waterfall.kernel.jakarta.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import waterfall.kernel.bootstrap.WFBootStrap;

@WebListener
public final class WFInitListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            (new WFBootStrap(sce.getServletContext())).boot();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.err.flush();
            throw new RuntimeException(e);
        }
    }
}
