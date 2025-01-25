package edu.tcu.cs.hogwarts_artifact_online.system.exception;

import edu.tcu.cs.hogwarts_artifact_online.system.Result;
import edu.tcu.cs.hogwarts_artifact_online.system.StatusCode;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    Result handleObjectNotFoundException(ObjectNotFoundException exception) {
        return new Result(false, StatusCode.NOT_FOUND, exception.getMessage());
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
}
