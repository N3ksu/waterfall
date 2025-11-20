package waterfall.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import waterfall.bootstrap.WaterFallBootStrap;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

@WebListener
public class WaterFlowInitListener implements ServletContextListener {
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
