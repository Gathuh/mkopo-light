package com.tezzar.mkopo.light.exception;

public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String serviceName, String detail) {
        super(serviceName + " is unavailable: " + detail);
    }
}
