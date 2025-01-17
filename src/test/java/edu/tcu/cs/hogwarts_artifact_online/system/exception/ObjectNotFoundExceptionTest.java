package edu.tcu.cs.hogwarts_artifact_online.system.exception;

import edu.tcu.cs.hogwarts_artifact_online.wizard.Wizard;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class ObjectNotFoundExceptionTest {

    private void formatRedConsoleOutput(Object x) {
        System.err.println("\t" + x);
    }

    @Test
    void testOutputName() {

        assertNotNull(new ObjectNotFoundException(Wizard.class.toString(), "1"));

        formatRedConsoleOutput(new ObjectNotFoundException(Wizard.class.getSimpleName().toLowerCase(), "1"));
        formatRedConsoleOutput(new ObjectNotFoundException(Wizard.class.getName().toLowerCase(), "1"));

        formatRedConsoleOutput(Wizard.class.getName());
        formatRedConsoleOutput(Wizard.class.getSimpleName());
    }
}