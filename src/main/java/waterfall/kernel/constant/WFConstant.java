package waterfall.kernel.constant;

public final class WFConstant {
    // Context
    public static final String ROUTER_CTX_ATTR_NAME = "wf.router";

    // Configuration
    public static final String CONFIG_FILE_URI = "/WEB-INF/waterfall.properties";
    public static final String CONTROLLER_PACKAGE_CONF_PARAM_NAME = "controller.package";

    // Regex
    public static final String JAVA_VAR_NOMENCLATURE_RGX = "[A-Za-z_$][A-Za-z0-9_$]*";

    // Reflection
    public static final String SETTER_DEFAULT_PREFIX = "set";

    // Web
    public static final String SERVLET_CTX_DEFAULT_REQ_DISPATCHER_NAME = "default";
    public static final String FRONT_SERVLET_URL_MAPPING = "/";
}
