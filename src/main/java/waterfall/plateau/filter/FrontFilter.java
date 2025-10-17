package waterfall.plateau.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class FrontFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String servletPath = request.getServletPath();
        ServletContext servletContext = request.getServletContext();

        if(servletContext.getResource(servletPath) != null)
            filterChain.doFilter(servletRequest, servletResponse);

        else {
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.setContentType("text/plain;charset=UTF-8");

            try (PrintWriter printWriter = response.getWriter()) {
                printWriter.print(servletPath);
            }
        }
    }
}
