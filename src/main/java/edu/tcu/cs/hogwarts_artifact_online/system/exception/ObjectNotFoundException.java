package edu.tcu.cs.hogwarts_artifact_online.system.exception;

public class ObjectNotFoundException extends RuntimeException {

    public ObjectNotFoundException(String objectName, String objectIdentifier) {
        super("Could not found " + objectName +
                " with Id " + objectIdentifier + " :(");
    }

    public ObjectNotFoundException(String objectName, Integer objectIdentifier) {
        super("Could not found " + objectName +
                " with Id " + objectIdentifier + " :(");
    }
}
