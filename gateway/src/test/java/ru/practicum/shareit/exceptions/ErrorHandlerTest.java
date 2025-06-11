package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

public class ErrorHandlerTest {
    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void testValidationException() {
        ValidationException exception = new ValidationException("Validation exception");

        ErrorResponse errorResponse = errorHandler.handlerValidation(exception);
        Assertions.assertNotNull(errorResponse);
        Assertions.assertEquals("Validation exception", errorResponse.getError());
    }

    @Test
    void testHandleGenericException() {
        Exception exception = new RuntimeException("Exception");
        ResponseEntity<String> response = errorHandler.handleGenericException(exception);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("Authentication Failed Exception", response.getBody());
    }

}
