package edu.tcu.cs.hogwarts_artifact_online.system.exception;

public class ObjectNotFoundException extends RuntimeException {

    public ObjectNotFoundException(String messageObjectNotFound, String objectIdentifier) {
        super("Could not found " + messageObjectNotFound +
                " with Id " + objectIdentifier + " :(");
    }
}
