package waterfall.kernel.response.json;

import waterfall.kernel.serialization.json.JsonMarshaller;

public final class JsonResponse {
    private final boolean success;
    private final int code;
    private final String message;
    private final Object data;

    private JsonResponse(final boolean success, final int code, final String message, final Object data) {
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

    public static JsonResponse ok(final Object data) {
        return new JsonResponse(true, Status.OK, "OK", data);
    }

    public static JsonResponse err(final Throwable t) {
        return new JsonResponse(false, Status.INTERNAL_SERVER_ERROR, t.getMessage(), null);
    }
}
