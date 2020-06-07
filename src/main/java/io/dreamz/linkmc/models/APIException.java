package io.dreamz.linkmc.models;

public class APIException extends Exception {

    public APIException(int code, String cause) {
        super(String.format("Status %d - %s", code, cause));
    }

    public static class ForbiddenException extends APIException {
        public ForbiddenException(String cause) {
            super(403, cause);
        }
    }

    public static class InternalServerException extends APIException {
        public InternalServerException(String cause) {
            super(500, cause);
        }
    }

    public static class NotFoundException extends APIException {
        public NotFoundException(String cause) {
            super(404, cause);
        }
    }

    public static class BadRequestException extends APIException {
        public BadRequestException(String cause) {
            super(400, cause);
        }
    }

}
