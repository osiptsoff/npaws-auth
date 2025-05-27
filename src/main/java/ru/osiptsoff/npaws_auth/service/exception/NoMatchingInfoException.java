package ru.osiptsoff.npaws_auth.service.exception;

import org.springframework.dao.NonTransientDataAccessException;

import lombok.experimental.StandardException;

@StandardException
public class NoMatchingInfoException extends NonTransientDataAccessException {
    
}
