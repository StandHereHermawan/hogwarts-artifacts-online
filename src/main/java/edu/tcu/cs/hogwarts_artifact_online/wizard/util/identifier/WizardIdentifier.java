package edu.tcu.cs.hogwarts_artifact_online.wizard.util.identifier;

public class WizardIdentifier {

    private static String identifier;

    public static String getIdentifier() {
        if (identifier == null) increment();
        return identifier;
    }

    public static void increment() {
        if (identifier == null) identifier = "0";
        long value = Long.parseLong(identifier);
        value++;
        identifier = String.valueOf(value);
    }
}
