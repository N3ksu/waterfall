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
            WaterFallBootStrap bootStrap = new WaterFallBootStrap(sce.getServletContext());
            bootStrap.boot();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
