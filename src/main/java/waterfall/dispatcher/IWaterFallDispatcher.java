package waterfall.dispatcher;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface IWaterFallDispatcher {
    IWaterFallDispatcher IMPL = new WaterFallDispatcher();

    void forward(HttpServletRequest req, HttpServletResponse res) throws Exception;
}
