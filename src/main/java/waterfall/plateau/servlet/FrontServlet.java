package waterfall.plateau.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class FrontServlet extends HttpServlet {
    @Override
    public void service(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        StringBuffer requestURLBuffer = req.getRequestURL();
        String requestURLString = requestURLBuffer.toString();

        res.setContentType("text/plain");

        PrintWriter printWriter = res.getWriter();
        printWriter.print(requestURLString);
    }
}
