package edu.tcu.cs.hogwarts_artifact_online.artifact;

public class ArtifactNotFoundException extends RuntimeException {
    public ArtifactNotFoundException(String artifactId) {
        super("Could not found artifact with Id " + artifactId + " :(");
    }
}
