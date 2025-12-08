package waterfall.kernel.jakarta.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import waterfall.kernel.bootstrap.BootStrap;

@WebListener
public final class ServletContextListenerImpl implements ServletContextListener {
    @Override
    public void contextInitialized(final ServletContextEvent sce) {
        try {
            (new BootStrap(sce.getServletContext())).bootstrap();
        } catch (final Exception e) {
            e.printStackTrace(System.err);
            System.err.flush();
            throw new RuntimeException(e);
        }
    }
}
