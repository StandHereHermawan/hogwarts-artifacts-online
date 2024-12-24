package edu.tcu.cs.hogwarts_artifact_online.system.exception;

import edu.tcu.cs.hogwarts_artifact_online.artifact.ArtifactNotFoundException;
import edu.tcu.cs.hogwarts_artifact_online.system.Result;
import edu.tcu.cs.hogwarts_artifact_online.system.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(ArtifactNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    Result handleArtifactNotFoundException(ArtifactNotFoundException exception) {
        return new Result(false, StatusCode.NOT_FOUND, exception.getMessage());
    }
}
