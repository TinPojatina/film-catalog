package com.filmcatalog.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Globalno rukovanje greškama za cijelu aplikaciju
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     *  kada resurs nije pronađen
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        logger.warn("Resource not found: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                "RESOURCE_NOT_FOUND",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     *  kada podaci ne prođu validaciju
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        logger.warn("Validation failed: {}", ex.getMessage());

        List<FieldError> fieldErrors = new ArrayList<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            fieldErrors.add(new FieldError(
                    error.getField(),
                    error.getDefaultMessage(),
                    error.getRejectedValue() != null ? error.getRejectedValue().toString() : null
            ));
        });

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                "VALIDATION_ERROR",
                "Podaci nisu ispravno uneseni",
                LocalDateTime.now(),
                fieldErrors
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Rukovanje greškama integriteta baze podataka
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        logger.error("Data integrity violation: {}", ex.getMessage());

        String userFriendlyMessage = "Operacija nije moguća zbog ograničenja u bazi podataka. " +
                "Možda pokušavate obrisati zapis koji se koristi negdje drugdje.";

        ErrorResponse error = new ErrorResponse(
                "DATA_INTEGRITY_ERROR",
                userFriendlyMessage,
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     *  IllegalArgumentException - neispravni argumenti
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        logger.warn("Illegal argument: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                "INVALID_ARGUMENT",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Rukovanje greške pri parsiranju JSON-a
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonParseError(HttpMessageNotReadableException ex) {
        logger.warn("JSON parse error: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                "INVALID_JSON",
                "Neispravno formatiran JSON. Provjerite sintaksu zahtjeva.",
                LocalDateTime.now()
        );

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Rukovanje greške pri konverziji tipova parametara
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        logger.warn("Type mismatch for parameter '{}': {}", ex.getName(), ex.getMessage());

        String message = String.format("Neispravna vrijednost za parametar '%s'. " +
                        "Očekivani tip: %s",
                ex.getName(),
                ex.getRequiredType().getSimpleName());

        ErrorResponse error = new ErrorResponse(
                "TYPE_MISMATCH",
                message,
                LocalDateTime.now()
        );

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Rukovanje 404 greške - endpoint nije pronađen
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoHandlerFoundException ex) {
        logger.warn("Endpoint not found: {} {}", ex.getHttpMethod(), ex.getRequestURL());

        ErrorResponse error = new ErrorResponse(
                "ENDPOINT_NOT_FOUND",
                "Traženi endpoint nije pronađen: " + ex.getHttpMethod() + " " + ex.getRequestURL(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Rukovanje svih ostalih nepredviđenih grešaka
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("Unexpected error occurred: ", ex);

        ErrorResponse error = new ErrorResponse(
                "INTERNAL_ERROR",
                "Dogodila se neočekivana greška. Molimo kontaktirajte administratora.",
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Standardni format odgovora za greške
     */
    @Schema(description = "Standardni format odgovora za greške")
    public static class ErrorResponse {

        @Schema(description = "Kod greške", example = "RESOURCE_NOT_FOUND")
        private String code;

        @Schema(description = "Poruka greške", example = "Film s ID 123 nije pronađen")
        private String message;

        @Schema(description = "Timestamp kada se greška dogodila")
        private LocalDateTime timestamp;

        public ErrorResponse() {}

        public ErrorResponse(String code, String message, LocalDateTime timestamp) {
            this.code = code;
            this.message = message;
            this.timestamp = timestamp;
        }

        // Getteri i setteri
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

        @Override
        public String toString() {
            return "ErrorResponse{" +
                    "code='" + code + '\'' +
                    ", message='" + message + '\'' +
                    ", timestamp=" + timestamp +
                    '}';
        }
    }

    @Schema(description = "Format odgovora za validation greške")
    public static class ValidationErrorResponse extends ErrorResponse {

        @Schema(description = "Lista grešaka po poljima")
        private List<FieldError> fieldErrors;

        public ValidationErrorResponse() {}

        public ValidationErrorResponse(String code, String message, LocalDateTime timestamp, List<FieldError> fieldErrors) {
            super(code, message, timestamp);
            this.fieldErrors = fieldErrors;
        }

        public List<FieldError> getFieldErrors() { return fieldErrors; }
        public void setFieldErrors(List<FieldError> fieldErrors) { this.fieldErrors = fieldErrors; }
    }

    /**
     * Informacija o grešci u specifičnom polju
     */
    @Schema(description = "Greška u specifičnom polju")
    public static class FieldError {

        @Schema(description = "Naziv polja", example = "naziv")
        private String field;

        @Schema(description = "Poruka greške", example = "Naziv filma je obavezan")
        private String message;

        @Schema(description = "Neispravna vrijednost", example = "")
        private String rejectedValue;

        public FieldError() {}

        public FieldError(String field, String message, String rejectedValue) {
            this.field = field;
            this.message = message;
            this.rejectedValue = rejectedValue;
        }

        // Getteri i setteri
        public String getField() { return field; }
        public void setField(String field) { this.field = field; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getRejectedValue() { return rejectedValue; }
        public void setRejectedValue(String rejectedValue) { this.rejectedValue = rejectedValue; }

        @Override
        public String toString() {
            return "FieldError{" +
                    "field='" + field + '\'' +
                    ", message='" + message + '\'' +
                    ", rejectedValue='" + rejectedValue + '\'' +
                    '}';
        }
    }
}

/**
 * Custom exception za slučajeve kada resurs nije pronađen
 */