package waterfall.kernel.response.json;

import waterfall.kernel.serialization.json.JsonMarshaller;

public final class Response {
    private final boolean success;
    private final int code;
    private final String message;
    private final Object data;

    private Response(boolean success, int code, String message, Object data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public String json() {
        return JsonMarshaller.marshal(this);
    }

    private static class Status {
        public static final int OK = 200;
        public static final int INTERNAL_SERVER_ERROR = 500;
    }

    public static Response ok(Object data) {
        return new Response(true, Status.OK, "OK", data);
    }

    public static Response err(Throwable t) {
        return new Response(false, Status.INTERNAL_SERVER_ERROR, t.getMessage(), null);
    }
}
