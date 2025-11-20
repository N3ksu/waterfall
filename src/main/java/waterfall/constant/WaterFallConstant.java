package waterfall.constant;

import java.util.regex.Pattern;

public final class WaterFallConstant {
    // Context
    public static final String ROUTER_CTX_ATTR_NAME = "wf.router";

    // Config
    public static final String CONFIG_FILE_URI = "/WEB-INF/waterfall.properties";
    public static final String CONTROLLER_PACKAGE_CONFIG_PARAM_NAME = "controller.package";

    // Regex
    public static final String URI_GRP_RGX = "\\(\\?<([A-Za-z_$][A-Za-z0-9_$]*)>";
    public static final Pattern URI_GRP_RGX_PATTERN = Pattern.compile(URI_GRP_RGX);

    // Misc...
    public static final String CTX_DEFAULT_REQ_DISPATCHER_NAME = "default";
    public static final String FRONT_SERVLET_URL_MAPPING = "/";
}
