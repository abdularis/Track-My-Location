package com.github.abdularis.trackmylocation.data.rxfirestore.errors;

public class DocumentNotExistsException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Firebase document not exists";
    }
}
