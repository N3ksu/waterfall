package waterfall.dispatcher;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface IWaterFallDispatcher {
    IWaterFallDispatcher IMPL = new WaterFallDispatcher();

    void forward(HttpServletRequest req, HttpServletResponse res) throws Exception;
}
