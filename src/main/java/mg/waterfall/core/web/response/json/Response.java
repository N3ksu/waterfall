package mg.waterfall.core.web.response.json;

import mg.waterfall.core.util.serialization.json.JsonMarshaller;

public record Response(boolean success, int code, String message, Object data) {

    public String json() {
        return JsonMarshaller.marshal(this);
    }

    public static Response ok(Object data) {
        return new Response(true, Status.OK, "OK", data);
    }

    public static Response err(Throwable t) {
        return new Response(false, Status.INTERNAL_SERVER_ERROR, t.getMessage(), null);
    }

    private static class Status {
        public static final int OK = 200;
        public static final int INTERNAL_SERVER_ERROR = 500;
    }
}
