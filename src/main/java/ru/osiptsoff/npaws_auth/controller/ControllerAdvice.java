package ru.osiptsoff.npaws_auth.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.jsonwebtoken.JwtException;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler({JwtException.class})
    public ResponseEntity<Map<String, Object>> handle(JwtException e) {
        return getEntity(HttpStatus.FORBIDDEN, e.getMessage());
    }

    private ResponseEntity<Map<String, Object>> getEntity(HttpStatus status, String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("status", status.value());
        result.put("error", status.getReasonPhrase());
        result.put("message", message);

        return new ResponseEntity<>(result, status);
    }
}