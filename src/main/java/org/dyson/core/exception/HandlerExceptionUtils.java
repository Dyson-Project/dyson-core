package org.dyson.core.exception;

import io.grpc.Status;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HandlerExceptionUtils {
    private static final Map<Status.Code, HttpStatus> map = new ConcurrentHashMap<>() {{
        put(Status.Code.OK, HttpStatus.OK);
        put(Status.Code.UNKNOWN, HttpStatus.INTERNAL_SERVER_ERROR);
        put(Status.Code.INVALID_ARGUMENT, HttpStatus.BAD_REQUEST);
        put(Status.Code.DEADLINE_EXCEEDED, HttpStatus.GATEWAY_TIMEOUT);
        put(Status.Code.NOT_FOUND, HttpStatus.NOT_FOUND);
        put(Status.Code.ALREADY_EXISTS, HttpStatus.CONFLICT);
        put(Status.Code.UNAUTHENTICATED, HttpStatus.UNAUTHORIZED);
        put(Status.Code.PERMISSION_DENIED, HttpStatus.FORBIDDEN);
        put(Status.Code.RESOURCE_EXHAUSTED, HttpStatus.TOO_MANY_REQUESTS);
        put(Status.Code.FAILED_PRECONDITION, HttpStatus.BAD_REQUEST);
        put(Status.Code.ABORTED, HttpStatus.CONFLICT);
        put(Status.Code.OUT_OF_RANGE, HttpStatus.BAD_REQUEST);
        put(Status.Code.UNIMPLEMENTED, HttpStatus.NOT_IMPLEMENTED);
        put(Status.Code.INTERNAL, HttpStatus.INTERNAL_SERVER_ERROR);
        put(Status.Code.UNAVAILABLE, HttpStatus.SERVICE_UNAVAILABLE);
        put(Status.Code.DATA_LOSS, HttpStatus.INTERNAL_SERVER_ERROR);
    }};

    public static HttpStatus grpcToHttpStatus(Status.Code code) {
        return map.getOrDefault(code, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static HttpStatus grpcToHttpStatus(Status status) {
        return map.getOrDefault(status.getCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
