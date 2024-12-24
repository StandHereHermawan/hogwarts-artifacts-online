package edu.tcu.cs.hogwarts_artifact_online.system;

public class StatusCode {

    public static final int SUCCESS = 200; /// Success Request.

    public static final int INVALID_ARGUMENT = 400; /// Bad Request, e.g., invalid parameters.

    public static final int UNAUTHORIZED = 401; /// Username or password incorrect.

    public static final int FORBIDDEN = 403; /// No permission to access.

    public static final int NOT_FOUND = 404; /// Data not found.

    public static final int INTERNAL_SERVER_ERROR = 500; /// Server Internal Error, e.g., Database is dead.
}
