package waterfall.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@Deprecated
public class FrontFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        String path = request.getServletPath();
        ServletContext ctx = request.getServletContext();

        if(ctx.getResource(path) != null)
            chain.doFilter(req, res);

        else {
            HttpServletResponse response = (HttpServletResponse) res;
            response.setContentType("text/plain;charset=UTF-8");

            try (PrintWriter out = response.getWriter()) {
                out.print(path);
            }
        }
    }
}
