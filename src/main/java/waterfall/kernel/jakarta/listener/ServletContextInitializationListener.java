package waterfall.kernel.jakarta.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import waterfall.kernel.bootstrap.BootStrap;

@WebListener
public final class ServletContextInitializationListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        // TODO Make this method use our logging library
        try {
            (new BootStrap()).boot(servletContextEvent.getServletContext());
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.err.flush();
            throw new RuntimeException(e);
        }
    }
}
