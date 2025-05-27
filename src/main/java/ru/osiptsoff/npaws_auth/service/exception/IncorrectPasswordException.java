package ru.osiptsoff.npaws_auth.service.exception;

import org.springframework.security.core.AuthenticationException;

import lombok.experimental.StandardException;

@StandardException
public class IncorrectPasswordException extends AuthenticationException {
    
}
