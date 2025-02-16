package edu.tcu.cs.hogwarts_artifact_online.system.exception;

import edu.tcu.cs.hogwarts_artifact_online.system.Result;
import edu.tcu.cs.hogwarts_artifact_online.system.StatusCode;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.List;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    Result handleObjectNotFoundException(ObjectNotFoundException exception) {
        return new Result(false, StatusCode.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler({
            UsernameNotFoundException.class,
            BadCredentialsException.class,
    })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    Result handleAuthenticationException(Exception exception) {
        return new Result(
                false,
                StatusCode.UNAUTHORIZED,
                "Username or Password is incorrect",
                exception.getMessage());
    }

    @ExceptionHandler(AccountStatusException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    Result handleAccountStatusException(AccountStatusException exception) {
        return new Result(
                false,
                StatusCode.UNAUTHORIZED,
                "User is abnormal.",
                exception.getMessage());
    }

    @ExceptionHandler({
            InvalidBearerTokenException.class,
            InsufficientAuthenticationException.class
    })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    Result handleInvalidBearerTokenException(Exception exception) {
        return new Result(
                false,
                StatusCode.UNAUTHORIZED,
                "The access token provided is expired, revoked, malformed or invalid for other reasons.",
                exception.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    Result handleAccessDeniedException(AccessDeniedException exception) {
        return new Result(
                false,
                StatusCode.FORBIDDEN,
                "No permission.",
                exception.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    Result handleNoHandlerFoundException(NoHandlerFoundException exception) {
        return new Result(
                false,
                StatusCode.NOT_FOUND,
                "This API endpoint is not found.",
                exception.getMessage());
    }

    /**
     * This handles invalid inputs.
     *
     * @param exception
     * @return result
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Result handleValidationException(MethodArgumentNotValidException exception) {
        List<ObjectError> allErrors = exception.getBindingResult().getAllErrors();
        HashMap<String, String> map = new HashMap<>(allErrors.size());
        allErrors.forEach((error) -> {
            String key = ((FieldError) error).getField();
            String value = error.getDefaultMessage();
            map.put(key, value);
        });
        return new Result(false,
                StatusCode.INVALID_ARGUMENT,
                "Provided arguments are invalid, see data for details.",
                map);
    }

    /**
     * This method handles all unhandled exceptions.
     * As fallback unhandled methods.
     *
     * @param exception
     * @return new Result
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    Result handleOtherException(Exception exception) {
        return new Result(
                false,
                StatusCode.INTERNAL_SERVER_ERROR,
                "A server internal error occurs.",
                exception.getMessage());
    }
}
