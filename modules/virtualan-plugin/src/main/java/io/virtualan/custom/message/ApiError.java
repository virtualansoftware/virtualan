package io.virtualan.custom.message;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;

public class ApiError {

    private HttpStatus status;
    private String message;
    private String code;
    private List<String> errors;

    //

    public ApiError() {
        super();
    }

    public ApiError(final HttpStatus status, final String code, final String message, final List<String> errors) {
        super();
        this.status = status;
        this.message = message;
        this.code = code;
        this.errors = errors;
    }

    public ApiError(final HttpStatus status, final String code, final String message, final String error) {
        super();
        this.status = status;
        this.message = message;
        this.code = code;
        errors = Arrays.asList(error);
    }

    //

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(final HttpStatus status) {
        this.status = status;
    }

    
    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(final List<String> errors) {
        this.errors = errors;
    }

    public void setError(final String error) {
        errors = Arrays.asList(error);
    }

}