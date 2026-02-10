package com.payvance.erp_saas.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class DeviceMismatchException extends RuntimeException {
    public DeviceMismatchException(String message) {
        super(message);
    }
}
