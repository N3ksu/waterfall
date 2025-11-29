package waterfall.web.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import waterfall.bootstrap.WaterFallBootStrap;

import java.util.*;

@WebListener
public final class WaterFlowInitListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            (new WaterFallBootStrap(sce.getServletContext())).boot();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.err.flush();
            throw new RuntimeException(e);
        }
    }
}
